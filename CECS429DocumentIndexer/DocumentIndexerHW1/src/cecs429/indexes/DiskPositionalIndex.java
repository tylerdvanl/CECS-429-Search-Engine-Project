package cecs429.indexes;

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

        try 
        {
            ArrayList<Posting> postings = new ArrayList<>();
            RandomAccessFile termInfoFile = new RandomAccessFile("postings.bin", "r");

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
            for(int i = 0; i < documentFrequency - 1; i++)
            {
                int gap = termInfoFile.readInt();
                currentId += gap;
                documentIds.add(currentId);              
            }
            //Once out of that loop, we have out docIDs, and now we need tftd for each document, and grab that many positions.  
            for(int docNum = 0; docNum < documentFrequency - 1; docNum++)
            {
                int termFrequency = termInfoFile.readInt();
                ArrayList<Integer> termPositions = new ArrayList<>();
                int currentPosition = 0;
                for(int j = 0; j < termFrequency - 1; j++)
                {
                    int posGap = termInfoFile.readInt();
                    currentPosition += posGap;
                    termPositions.add(currentPosition);
                }
                postings.add(new Posting(documentIds.get(docNum), termPositions));
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
            RandomAccessFile termInfoFile = new RandomAccessFile("postings.bin", "r");

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
            for(int i = 0; i < documentFrequency - 1; i++)
            {
                int gap = termInfoFile.readInt();
                currentId += gap;
                documentIds.add(currentId);              
            }
            //Once out of that loop, we have our docIDs.
            for(int id : documentIds)  
                postings.add(new Posting(id));
            
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

    public double getDocWeight(int docID) throws IOException
    {
        //Open the docweights file, skip to the data for the docID (it should be sequential) then read the double and return it.
        double weight = 0.0;
        final int DOUBLE_BYTE_SIZE = 8;
        RandomAccessFile weightInfoFile = new RandomAccessFile("docWeights.bin", "r");
        weightInfoFile.skipBytes(DOUBLE_BYTE_SIZE * docID);
        weight = weightInfoFile.readDouble();
        weightInfoFile.close();
        return weight;
    }

    public double getTermWeightInDocument(String term, List<Integer> termStartBytes) throws IOException
    {
        //TODO: THIS IS COMPLETELY WRONG.
        double termWeight = 0.0;
        ArrayList<String> vocabularyList = new ArrayList<>();
        vocabularyList.addAll(this.getVocabulary());
        int bytes = termStartBytes.get(vocabularyList.indexOf(term));

        RandomAccessFile indexFile = new RandomAccessFile("postings.bin", "r");
        //We're at the beginning of the term; now we need to see how many documents have the term.
        indexFile.seek(bytes);
        int docFrequency = indexFile.readInt();
        //For each doc, 
        for(int i = 0; i < docFrequency; i++)
        {

        }

        return termWeight;
    } 

}
