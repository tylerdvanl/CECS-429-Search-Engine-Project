package cecs429.indexes;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.btree.BTree;
import jdbm.helper.Tuple;
import jdbm.helper.TupleBrowser;

public class DiskPositionalIndex implements Index{

    RecordManager recordManager;
    BTree tree;
    long bTreeId;
    Path absolutePathSave;

    //Default Constructor
    public DiskPositionalIndex() throws IOException
    {
        recordManager = RecordManagerFactory.createRecordManager("Terms");
        bTreeId = recordManager.getNamedObject("TermsAndPositions");
        absolutePathSave = Paths.get("index\\");

        
        if(bTreeId == 0)
        {
            System.out.println("Could not load tree");
            throw new IOException();
        }

        tree = BTree.load(recordManager, bTreeId);

    }
    //Constructor for specifying a directory to look for postings in.
    public DiskPositionalIndex(Path savePath) throws IOException
    {
        recordManager = RecordManagerFactory.createRecordManager("Terms");
        bTreeId = recordManager.getNamedObject("TermsAndPositions");
        absolutePathSave = savePath;

        
        if(bTreeId == 0)
        {
            System.out.println("Could not load tree");
            throw new IOException();
        }

        tree = BTree.load(recordManager, bTreeId);

    }

    @Override
    public List<Posting> getPostingsWithPositions(String term) {
        /*  Here, we use the BTree to figure out where in our binary file the information for the term lives.
        *   Next, we store dft, the amount of documents the term appears in.
        *   THEN:
        *       Grab a docID, then tftd, the amount of times the term appears in that document
        *       Grab the next tftd positions, and make a posting out of them.
        *   Do the above loop dft times.
        *   This should give us the postings we require.
        *   Uses seek on a RandomAccessFile object.
        */
        ArrayList<Posting> postings = new ArrayList<>();
        try 
        {
            RandomAccessFile termInfoFile = new RandomAccessFile(new File(absolutePathSave.toString(), "postings.bin"), "r");
            
            int startBytes = (int) tree.find(term); // casting, blegh
            try{
                termInfoFile.seek(startBytes);
                //read the next int: dft, save it.
                int documentFrequency = termInfoFile.readInt();
                
                //Grab the document IDs; recall that they are written as gaps.
                //Then grab wdt
                ArrayList<Integer> documentIds = new ArrayList<>();
                int currentId = 0;
                for(int i = 0; i < documentFrequency; i++)
                {
                    int gap = termInfoFile.readInt();
                    currentId += gap;
                    documentIds.add(currentId);
                    double weightDT = termInfoFile.readDouble();
                    int termFrequency = termInfoFile.readInt();  
                    
                    //Once out of that loop, we have out docIDs, and now we need tftd for each document, and grab that many positions. 
                    ArrayList<Integer> termPositions = new ArrayList<>();
                    int currentPosition = 0;
                    for(int j = 0; j < termFrequency; j++)
                    {
                        int posGap = termInfoFile.readInt();
                        currentPosition += posGap;
                        termPositions.add(currentPosition);
                    }
                    postings.add(new Posting(documentIds.get(i), termPositions, weightDT));
                }
            }
            catch(EOFException e)
            {
                System.out.println("End of File");
            }
            
            termInfoFile.close();

        }
        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        return postings;
    }

    @Override
    public List<Posting> getPostingsNoPositions(String term) 
    {
        /*  Here, we use the BTree to figure out where in our binary file the information for the term lives.
        *   Next, we store dft, the amount of documents the term appears in.
        *   THEN:
        *       Grab a docID, then tftd, the amount of times the term appears in that document
        *       Grab the next tftd positions, and make a posting out of them.
        *   Do the above loop dft times.
        *   This should give us the postings we require.
        *   Uses seek on a RandomAccessFile object.
        */
        ArrayList<Posting> postings = new ArrayList<>();
        try 
        {

            RandomAccessFile termInfoFile = new RandomAccessFile(new File(absolutePathSave.toString(), "postings.bin"), "r");
            int startBytes = (int) tree.find(term); // casting, blegh
            termInfoFile.seek(startBytes);
            //read the next int: dft, save it.
            int documentFrequency = termInfoFile.readInt();
            
            //Grab the document IDs; recall that they are written as gaps.
            ArrayList<Integer> documentIds = new ArrayList<>();
            int currentId = 0;
            for(int i = 0; i < documentFrequency; i++)
            {
                int gap = termInfoFile.readInt();
                currentId += gap;
                documentIds.add(currentId);  
                double weightDT = termInfoFile.readDouble();
                int termFrequency = termInfoFile.readInt();
                //Skip the positions, we're not interested in them.
                termInfoFile.skipBytes(4 * termFrequency);     
                postings.add(new Posting(currentId, weightDT));
            }   
            
            termInfoFile.close();
        }
        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        return postings;
    }

    @Override
    public List<String> getVocabulary() throws IOException {
        
            ArrayList<String> terms = new ArrayList<>();
            Tuple browsedTuple = new Tuple();
            TupleBrowser browser = tree.browse();
            while(browser.getNext(browsedTuple))
            {
                terms.add((String) browsedTuple.getKey()); //I hate casting, but I think I have to do it here.
            }
        return terms;
    }

    @Override
    public int getDocumentFrequency(String term)
    {
        try
        {
            RandomAccessFile termInfoFile =new RandomAccessFile(new File(absolutePathSave.toString(), "postings.bin"), "r");
            
            int startBytes = (int) tree.find(term); // casting, blegh
            termInfoFile.seek(startBytes);
            //read the next int: dft, save it.
            int documentFrequency = termInfoFile.readInt();
            termInfoFile.close();
            return documentFrequency;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    public double getDocWeight(int docID) throws IOException
    {
        //Open the docweights file, skip to the data for the docID (it should be sequential) then read the double and return it.
        double weight = 0.0;
        final int DOUBLE_BYTE_SIZE = 8;
        RandomAccessFile weightInfoFile = new RandomAccessFile(new File(absolutePathSave.toString(), "docWeights.bin"), "r");
        weightInfoFile.skipBytes(DOUBLE_BYTE_SIZE * docID);
        weight = weightInfoFile.readDouble();
        weightInfoFile.close();
        return weight;
    }

    @Override
    public long indexSize() throws IOException 
    {
        RandomAccessFile weightInfoFile = new RandomAccessFile(new File(absolutePathSave.toString(), "docWeights.bin"), "r");
        long length = weightInfoFile.length();
        weightInfoFile.close();
        return length/8;
    }
}
