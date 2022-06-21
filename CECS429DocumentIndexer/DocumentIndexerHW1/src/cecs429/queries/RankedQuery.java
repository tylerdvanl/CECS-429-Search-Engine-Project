package cecs429.queries;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import cecs429.indexes.Index;
import cecs429.indexes.Posting;
import cecs429.text.TokenProcessor;
import cecs429.utilities.DocIdScorePair;
import cecs429.utilities.DocIdScorePairSortByScore;

public class RankedQuery{

    private String mQuery;

    public RankedQuery(String query)
    {
        mQuery = query;
    }

    public List<DocIdScorePair> getTopTen(Index index, TokenProcessor processor, int corpusSize) throws IOException {
        //In ranked query mode, you must process a query without any Boolean operators and return the top K = 10
        //documents satisfying the query
        //For each term, get its postings.  Create an accumulator for each document found, and score the document according to 
        //the provided formulas.  Add that score to the document's accumulator score.  At the end, add the documents to a
        //priority queue.  
        HashMap<Integer, Double> docsAndRanks = new HashMap<>();
        PriorityQueue<DocIdScorePair> resultingScoresAndPostings = new PriorityQueue<DocIdScorePair>(new DocIdScorePairSortByScore());
        ArrayList<DocIdScorePair> topTen = new ArrayList<>();
        ArrayList<String> preProcessed = splitQueryOnWhitespace(mQuery);
        ArrayList<String> terms = new ArrayList<>();
        
        for(String processing : preProcessed)
        {
            terms.addAll(processor.processToken(processing));
        }

        for(String term : terms)
        {
            ArrayList<Posting> potentiallyRelevant = new ArrayList<Posting>();
            potentiallyRelevant.addAll(index.getPostingsNoPositions(term));
            for(Posting posting : potentiallyRelevant)
            {
                double acc = 0.0;
                double wdt = posting.getWeight();
                double wqt = calculateQueryWeight(term, index, corpusSize);
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
        }

        //Now, normalize all the values and add them to the priority queue.
        for(int docId : docsAndRanks.keySet())
        {
            docsAndRanks.put(docId, normalizeAccumulatorScore(docId, docsAndRanks.get(docId), index));
            resultingScoresAndPostings.add(new DocIdScorePair(docId, docsAndRanks.get(docId)));
        }

        //Put the top 10 from the priority queue and put them in a list.
        for(int i = 0; i < 10; i++)
        {
            DocIdScorePair popped = resultingScoresAndPostings.poll();
            if(popped != null)
                topTen.add(popped);
        }
        return topTen;
    }

    private ArrayList<String> splitQueryOnWhitespace(String query)
    {
        RankedQueryParser parser = new RankedQueryParser();
        return parser.getTerms(query);
    }

    private double calculateQueryWeight(String term, Index index, int corpusSize) throws IOException
    {
        // ln(1+(N/dft))
        //dft is the first int in the binary file, so should be easy to just jump to it, using the binary tree, and grab from it.
        double dft = index.getDocumentFrequency(term); //int return gets transformed into a double here.
        return Math.log((1 + (double) corpusSize/dft));
    }

    private double normalizeAccumulatorScore(int docId, Double accumulator, Index index) throws IOException
    {
        return accumulator/(index.getDocWeight(docId));
    }
}
