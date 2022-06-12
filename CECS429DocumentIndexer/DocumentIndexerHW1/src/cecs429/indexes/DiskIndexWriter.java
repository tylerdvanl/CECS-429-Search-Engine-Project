package cecs429.indexes;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.btree.BTree;
import jdbm.helper.StringComparator;

public class DiskIndexWriter 
{

    public ArrayList<Integer> writeIndex(Index index, Path absolutePathSave)
    {
        ArrayList<Integer> startBytes = new ArrayList<>();
        
        try 
        {
            FileOutputStream fOut = new FileOutputStream("mobyDickCorpus\\index\\postings.bin");
            DataOutputStream dOut = new DataOutputStream(fOut);
            RecordManager recordManager = RecordManagerFactory.createRecordManager("Terms");
            BTree termsToBytesTree = createBTree(recordManager);

            List<String> vocabulary = index.getVocabulary();

            /*
             *  For each term in the vocabulary:
                    i. Write df t to the file as a 4-byte integer.
                    ii. Retrieve the index postings for the term.
                    iii. For each posting:
                        A. Write the posting's document ID as a 4-byte gap. (The first document in a list is written
                            as-is. All the rest are gaps from the previous value.)
                        B. Write tf t,d as a 4-byte integer.
                        C. Write the list of positions, each a 4-byte gap. (The first position is written as-is. All the
                        rest are gaps from the previous value.)
                    iv. Repeat for each term in the vocabulary.
             */
            for(String term : vocabulary)
            {
                startBytes.add(dOut.size());
                termsToBytesTree.insert(term, dOut.size(), false);
                List<Posting> postings = index.getPostings(term);
                dOut.writeInt(postings.size());
                int previousID = 0;
                for(Posting posting : postings) //surely that's not confusing//
                {
                    int currentID = posting.getDocumentId();
                    dOut.writeInt(currentID - previousID);
                    List<Integer> positions = posting.getPositions();
                    dOut.writeInt(positions.size());
                    int previousPosition = 0;
                    for(int position : positions)
                    {
                        dOut.writeInt(position - previousPosition);
                        previousPosition = position;
                    }
                    previousID = currentID;
                }
            }
            dOut.close();  
            recordManager.commit();
        } 
        
        catch (IOException e) 
        {
            e.printStackTrace();
        }        
        return startBytes;
    }

    //TODO: See if this thing actually works.
    private BTree createBTree(RecordManager recordManager) throws IOException
    {
        BTree tree = BTree.createInstance(recordManager, new StringComparator());
        recordManager.setNamedObject("TermsAndPositions", tree.getRecid());
        return tree;
    }

}
