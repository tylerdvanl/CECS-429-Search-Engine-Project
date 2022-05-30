package cecs429.indexes;

import java.util.*;
/*
public class InvertedIndex implements Index {

    private HashMap<String, ArrayList<Posting>> mIndex ;

    public InvertedIndex()
    {
        mIndex = new HashMap<String, ArrayList<Posting>>();
    }

    @Override
    public List<Posting> getPostings(String term) 
    {
        if(mIndex.containsKey(term))
            return mIndex.get(term);
        else
            return new ArrayList<Posting>();
    }

    @Override
    public List<String> getVocabulary() 
    {
        ArrayList<String> vocab = new ArrayList<String>(mIndex.keySet());
        Collections.sort(vocab);
        return vocab;

    }

    /**
     * Adds a term into the inverted index if it does not exist, along with the document's ID.  If the term already exists, but is not found in the 
     * supplied docID, adds that ID to the posting.  Otherwise, does nothing.
     * @param term The term being added to the index.
     * @param docID The ID of the document that the term exists in.  
     */
    /*
    public void addTerm(String term, int docID)
    {       
        //Check if the term is already in the index.
        if(mIndex.containsKey(term))
        {
            //If the term exists in the index, check to see if the document ID exists in the current postings.  If it does, do nothing.  Otherwise, add
            //the new posting to the list with the supplied docID.
            //The postings list is assumed to be already sorted (if it isn't, something incredibly strange is going on), so we can just check the last 
            //item in the postings list to see if the docID lives there. 
            ArrayList<Posting> existingPostings = mIndex.get(term);
            if(existingPostings.get(existingPostings.size() - 1).getDocumentId() != docID)
            {
                existingPostings.add(new Posting(docID));
            }
            //Otherwise, we know the term has been seen in this document before, so we don't need to do anything.
        }
        //If the term is not in the index yet, we can simply add the term and the new posting to the index right away.
        else
        {
            mIndex.put(term, new ArrayList<Posting>());
            ArrayList<Posting> existingPostings = mIndex.get(term);
            existingPostings.add(new Posting(docID));
        }    
    }
    
}*/
