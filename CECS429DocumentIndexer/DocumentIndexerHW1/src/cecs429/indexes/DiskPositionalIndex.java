package cecs429.indexes;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.btree.BTree;
import jdbm.helper.Tuple;
import jdbm.helper.TupleBrowser;

public class DiskPositionalIndex implements Index{

    @Override
    public List<Posting> getPostingsWithPositions(String term) {
        /*  TODO: Here, we use the BTree to figure out where in our binary file the information for the term lives.
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
            RandomAccessFile termInfoFile = new RandomAccessFile("mobyDickCorpus\\index\\postings.bin", "r");
            RecordManager recordManager = RecordManagerFactory.createRecordManager("Terms");
            long bTreeId = recordManager.getNamedObject("TermsAndPositions");
            BTree tree;

            //If the btree could not load, just return an empty arraylist.
            if(bTreeId == 0)
            {
                System.out.println("Could not load tree");
                termInfoFile.close();
                return new ArrayList<Posting>();
            }
            
            tree = BTree.load(recordManager, bTreeId);
            System.out.println("Debug: Loaded tree with nodes: " + tree.size());
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
                    
                    //TODO: this is also wrong, positions come immediately after the docID.
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
                System.out.println("End of FIle");
            }
            
            termInfoFile.close();

        }
        catch (FileNotFoundException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return postings;
    }

    @Override
    public List<Posting> getPostingsNoPositions(String term) 
    {
        /*  TODO: Here, we use the BTree to figure out where in our binary file the information for the term lives.
        *   Next, we store dft, the amount of documents the term appears in.
        *   THEN:
        *       Grab a docID, then tftd, the amount of times the term appears in that document
        *       Grab the next tftd positions, and make a posting out of them.
        *   Do the above loop dft times.
        *   This should give us the postings we require.
        *   Uses seek on a RandomAccessFile object.
        */

        try 
        {
            ArrayList<Posting> postings = new ArrayList<>();
            RandomAccessFile termInfoFile = new RandomAccessFile("mobyDickCorpus\\index\\postings.bin", "r");

            RecordManager recordManager = RecordManagerFactory.createRecordManager("Terms");
            long bTreeId = recordManager.getNamedObject("TermsAndPositions");
            BTree tree;

            //If the btree could not load, just return an empty arraylist.
            if(bTreeId == 0)
            {
                System.out.println("Could not load tree");
                termInfoFile.close();
                return new ArrayList<Posting>();
            }
            
            tree = BTree.load(recordManager, bTreeId);
            System.out.println("Debug: Loaded tree with nodes: " + tree.size());
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
            return postings;
        }
        catch (FileNotFoundException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> getVocabulary() throws IOException {
        
        RecordManager recordManager = RecordManagerFactory.createRecordManager("Terms");
        long bTreeId = recordManager.getNamedObject("TermsAndPositions");
        BTree tree;
        ArrayList<String> terms = new ArrayList<>();

        if(bTreeId == 0)
        {
            System.out.println("Could not find vocabulary from index");
        }
        else
        {
            Tuple browsedTuple = new Tuple();
            tree = BTree.load(recordManager, bTreeId);
            System.out.println("Debug: Loaded tree with nodes: " + tree.size());
            TupleBrowser browser = tree.browse();
            while(browser.getNext(browsedTuple))
            {
                terms.add((String) browsedTuple.getKey()); //I hate casting, but I think I have to do it here.
            }
        }
        return terms;
    }

    @Override
    public int getDocumentFrequency(String term)
    {
        try
        {
            RandomAccessFile termInfoFile = new RandomAccessFile("mobyDickCorpus\\index\\postings.bin", "r");
            RecordManager recordManager = RecordManagerFactory.createRecordManager("Terms");
            long bTreeId = recordManager.getNamedObject("TermsAndPositions");
            BTree tree;
    
            //If the btree could not load, just return an empty arraylist.
            if(bTreeId == 0)
            {
                System.out.println("Could not load tree");
                termInfoFile.close();
                return 0;
            }
            
            tree = BTree.load(recordManager, bTreeId);
            System.out.println("Debug: Loaded tree with nodes: " + tree.size());
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
        RandomAccessFile weightInfoFile = new RandomAccessFile("mobyDickCorpus\\index\\docWeights.bin", "r");
        weightInfoFile.skipBytes(DOUBLE_BYTE_SIZE * docID);
        weight = weightInfoFile.readDouble();
        weightInfoFile.close();
        return weight;
    }
}
