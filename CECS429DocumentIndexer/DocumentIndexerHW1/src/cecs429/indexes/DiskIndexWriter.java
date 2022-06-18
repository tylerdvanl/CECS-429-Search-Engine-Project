package cecs429.indexes;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
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
        System.out.println("Saving to disk...");
        Instant start = Instant.now();
        try 
        {
            FileOutputStream postingsOut = new FileOutputStream("mobyDickCorpus\\index\\postings.bin");
            DataOutputStream postingsDataOut = new DataOutputStream(postingsOut);
            FileOutputStream weightsOut = new FileOutputStream("mobyDickCorpus\\index\\docWeights.bin");
            DataOutputStream weightsDataOut = new DataOutputStream(weightsOut);
            RecordManager recordManager = RecordManagerFactory.createRecordManager("Terms");
            BTree termsToBytesTree = createBTree(recordManager);
            List<Double> termWeights = new ArrayList<>();
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
                    OPTIONAL ADDITION:
                    I've added wdt to the postings in the file, fulfilling the DSP Index module.
             */

            for(String term : vocabulary)
            {
                //System.out.println("New Term: " + Instant.now().toEpochMilli());
                int currentStartBytes = postingsDataOut.size();
                startBytes.add(currentStartBytes);
                termsToBytesTree.insert(term, currentStartBytes, false);
                //System.out.println("Tree Insert: " + Instant.now().toEpochMilli());
                List<Posting> postings = index.getPostingsWithPositions(term);
                int documentFrequency = postings.size();
                postingsDataOut.writeInt(documentFrequency);
                //System.out.println("Document Frequency: " + Instant.now().toEpochMilli());
                int previousID = 0;
                for(Posting posting : postings) //surely that's not confusing//
                {
                    int currentID = posting.getDocumentId();
                    postingsDataOut.writeInt(currentID - previousID);
                    //System.out.println("Document: " + Instant.now().toEpochMilli());
                    List<Integer> positions = posting.getPositions();
                    //KEEP IN MIND: Doubles are 8 Bytes!  Ints are 4!
                    int termFrequency = positions.size();
                    Double termWeight = calculateTermWeight(termFrequency);
                    postingsDataOut.writeDouble(termWeight);
                    //System.out.println("wdt: " + Instant.now().toEpochMilli());
                    termWeights.add(termWeight);
                    postingsDataOut.writeInt(termFrequency);
                    //System.out.println("Term Frequency: " + Instant.now().toEpochMilli());
                    int previousPosition = 0;
                    for(int position : positions)
                    {
                        postingsDataOut.writeInt(position - previousPosition);
                        //System.out.println("Position: " + Instant.now().toEpochMilli());
                        previousPosition = position;
                    }
                    previousID = currentID;
                    //weightsDataOut.writeDouble(calculateDocumentWeight(termWeights));
                    //System.out.println("Document Weight: " + Instant.now().toEpochMilli());
                }
            }
            postingsDataOut.close(); 
            weightsDataOut.close();
            recordManager.commit();
        } 
        
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        Instant finish = Instant.now();
		long timeElapsed = Duration.between(start, finish).toSeconds();
		System.out.println("Saved to disk in " + timeElapsed + " seconds.");        
        return startBytes;
    }

    //TODO: See if this thing actually works.
    private BTree createBTree(RecordManager recordManager) throws IOException
    {
        BTree tree = BTree.createInstance(recordManager, new StringComparator());
        recordManager.setNamedObject("TermsAndPositions", tree.getRecid());
        return tree;
    }

    private double calculateTermWeight(int termFrequency)
    {
        // 1+ ln([tftd])
        return 1 + Math.log(termFrequency);
    }

    private double calculateDocumentWeight(List<Double> termWeights)
    {
        // Sqrt(SUM([wdt]^2))
        double sum = 0.0;
        for(Double weight : termWeights)
            sum += Math.pow(weight, 2);
        
        return Math.sqrt(sum);
    }

}
