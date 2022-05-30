package cecs429.indexes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class InvertedPositionalIndex implements Index
{
    private HashMap<String, ArrayList<Posting>> mIndex;

    public InvertedPositionalIndex()
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
    public List<String> getVocabulary() {
        
        ArrayList<String> vocab = new ArrayList<String>(mIndex.keySet());
        Collections.sort(vocab);
        return vocab;
    }

     /**
     * Adds a term into the inverted index if it does not exist, along with the document's ID and the term's position within the document.
     * If the term already exists, but is not found in the supplied docID, adds that ID to the posting.
     * If the term already exists in this docID as well, adds the new position to the list.
     *   Otherwise, does nothing.
     * @param term The term being added to the index.
     * @param docID The ID of the document that the term was found in.  
     * @param position The position this term was found at.
     */
    public void addTerm(String term, int docID, int position)
    {   
        //Check if the term exists in the index already.
        if(mIndex.containsKey(term))
        {
            //If the term exists in the index:
            //    Check to see if the document ID exists in the current postings. 
            //    If it does, check and add a new position to the postings positions list.
            //    Otherwise, add a brand new posting of this Document, along with the position the term was first found in.
            //
            //The postings list is assumed to be already sorted (if it isn't, something incredibly strange is going on), so we can just check the last 
            //item in the postings list to see if the docID lives there. 
            ArrayList<Posting> existingPostings = mIndex.get(term);
            if(existingPostings.get(existingPostings.size() - 1).getDocumentId() != docID)
            {
                existingPostings.add(new Posting(docID, position));
            }
            //Otherwise, we know we've seen the term in this document before, so we need to add a new position to it.
            else
            {
                //Grab the latest posting
                Posting foundPosting = existingPostings.get(existingPostings.size() - 1);
                //Add the position
                foundPosting.addPosition(position);
            }
        }
        else
        {
            //If the term does NOT exist in the index:
            //    Simply add it to the index, along with this first position.
            mIndex.put(term, new ArrayList<Posting>());
            ArrayList<Posting> existingPostings = mIndex.get(term);
            existingPostings.add(new Posting(docID, position));
        }
    }
}
