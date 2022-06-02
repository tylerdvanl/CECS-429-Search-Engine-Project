package cecs429.queries;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import cecs429.indexes.Index;
import cecs429.indexes.Posting;
import cecs429.text.TokenProcessor;

/**
 * An AndQuery composes other QueryComponents and merges their postings in an intersection-like operation.
 */
public class AndQuery implements QueryComponent {
	private List<QueryComponent> mComponents;
	
	public AndQuery(List<QueryComponent> components) {
		mComponents = components;
	}
	
	@Override
	public List<Posting> getPostings(Index index, TokenProcessor processor) {
		List<Posting> result = new ArrayList<>();
		ArrayList<List<Posting>> potentialMatches = new ArrayList<>();

		//Grab postings from all query components.
		for(QueryComponent literal : mComponents)
		{
			//potentialMatches.add(literal.getPostings(index));
		}

		//Merge the lists of postings.
		//Merge the first 2 lists immediately.  Then, continue merging any additional lists into the results list.
		result = this.mergePostingsAnd(potentialMatches.get(0), potentialMatches.get(1));
		for(int i = 2; i < potentialMatches.size(); i++)
		{
			result = this.mergePostingsAnd(result, potentialMatches.get(i));
		}

		return result;
	}
	
	@Override
	public String toString() {
		return
		 String.join(" ", mComponents.stream().map(c -> c.toString()).collect(Collectors.toList()));
	}

	/**
     * Merges two lists of postings together, using intersection
     * @param postings1 First list of postings.
     * @param postings2 Second list of postings.
     * @return The list of postings as a result of the merge.
     */
    List<Posting> mergePostingsAnd(List<Posting> postings1, List<Posting> postings2)
	{
		List<Posting> merged = new ArrayList<>();
		
		int i = 0;
		int j = 0;

		while(i < postings1.size() && j < postings2.size())
		{
			//Check the two list indicies for matching document IDs, assuming a sorted list.
			//If they both match, increment both i and j and add the postings to the merged list.
			if((postings1.get(i)).getDocumentId() == (postings2.get(j).getDocumentId()))
			{
				//Add a new posting for this document with no positions 
				merged.add(new Posting(postings1.get(i).getDocumentId()));
				i++;
				j++;
			}
			//If they dont match, we need to check which Document ID is smaller, and increment the iterator for THAT list.
			else if((postings1.get(i)).getDocumentId() < (postings2.get(j).getDocumentId()))
			{
				i++;
			}
			else if((postings1.get(i)).getDocumentId() > (postings2.get(j).getDocumentId()))
			{
				j++;
			}
		}
		//If we've fallen off the edge of one of the lists, we can be certain that we will never find another match, and so can return the merged list now.
		//This is possible because this is an AND merge.
		return merged;
	}
}
