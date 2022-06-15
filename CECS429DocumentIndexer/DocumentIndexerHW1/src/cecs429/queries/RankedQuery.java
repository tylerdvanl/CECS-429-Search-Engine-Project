package cecs429.queries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import cecs429.indexes.Index;
import cecs429.indexes.Posting;
import cecs429.text.TokenProcessor;

public class RankedQuery{

    private List<String> mTerms = new ArrayList<>();
    private PriorityQueue<HashMap<Integer, Double>> docIdsAndRanks = new PriorityQueue<>();

    public RankedQuery(List<String> terms)
    {
        mTerms.addAll(terms);
    }

    public PriorityQueue<HashMap<Integer, Double>> getTopTen(Index index, List<String> terms) {
        //TODO: In ranked query mode, you must process a query without any Boolean operators and return the top K = 10
        //documents satisfying the query
        //For each term, get its postings.  Create an accumulator for each document found, and score the document according to 
        //the provided formulas.  Add that score to the document's accumulator score.  At the end, add the documents to a
        //priority queue.  

        for(String term : terms)
        {
            ArrayList<Posting> potentiallyRelevant = new ArrayList<Posting>();
            potentiallyRelevant.addAll(index.getPostingsWithPositions(term));
        }
        return null;
    }

    
    
}
