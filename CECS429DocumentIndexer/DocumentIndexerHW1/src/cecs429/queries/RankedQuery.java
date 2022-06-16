package cecs429.queries;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import cecs429.documents.DocumentCorpus;
import cecs429.indexes.DiskPositionalIndex;
import cecs429.indexes.Index;
import cecs429.indexes.Posting;
import cecs429.text.TokenProcessor;

public class RankedQuery{

    private List<String> mTerms = new ArrayList<>();

    public RankedQuery(List<String> terms)
    {
        mTerms.addAll(terms);
    }

    public PriorityQueue<HashMap<Integer, Double>> getTopTen(DiskPositionalIndex index, List<String> terms, DocumentCorpus corpus) throws IOException {
        //TODO: In ranked query mode, you must process a query without any Boolean operators and return the top K = 10
        //documents satisfying the query
        //For each term, get its postings.  Create an accumulator for each document found, and score the document according to 
        //the provided formulas.  Add that score to the document's accumulator score.  At the end, add the documents to a
        //priority queue.  
        HashMap<Integer, Double> docsAndRanks = new HashMap<>();
        PriorityQueue<HashMap<Posting, Double>> resultingScoresAndPostings = new PriorityQueue<>();

        for(String term : terms)
        {
            ArrayList<Posting> potentiallyRelevant = new ArrayList<Posting>();
            potentiallyRelevant.addAll(index.getPostingsWithPositions(term));
            for(Posting posting : potentiallyRelevant)
            {
                double acc = 0.0;
                double wdt = posting.getWeight();
                double wqt = calculateQueryWeight(term, index, corpus);
                //If the map contains the document already, update it's accumulator(score) value
                if(docsAndRanks.containsKey(posting.getDocumentId()))
                {
                    acc = docsAndRanks.get(posting.getDocumentId());
                    acc += wdt * wqt;
                    docsAndRanks.put(posting.getDocumentId(), acc);
                }
                //Otherwise, add the document to the map with the newly calculated accumulator score.
                else
                {
                    acc = wdt * wqt;
                    docsAndRanks.put(posting.getDocumentId(), acc);
                }
            }

            //Now, normalize all the values and add them to the priority queue.
            for(int docId : docsAndRanks.keySet())
            {
                docsAndRanks.put(docId, normalizeAccumulatorScore(docId, docsAndRanks.get(docId), index));
                
            }
            

        }
        return null;
    }

    private double calculateQueryWeight(String term, Index index, DocumentCorpus corpus) throws IOException
    {
        // ln(1+(N/dft))
        //dft is the first int in the binary file, so should be easy to just jump to it, using the binary tree, and grab from it.
        double dft = index.getDocumentFrequency(term); //int return gets transformed into a double here.
        return Math.log((1 + (corpus.getCorpusSize()/dft)));
    }

    private double normalizeAccumulatorScore(int docId, Double accumulator, DiskPositionalIndex index) throws IOException
    {
        return accumulator/(index.getDocWeight(docId));
    }

    
    
}
