package cecs429.queries;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import cecs429.indexes.Index;
import cecs429.indexes.Posting;
import cecs429.text.TokenProcessor;

/**
 * An OrQuery composes other QueryComponents and merges their postings with a union-type operation.
 */
public class OrQuery implements QueryComponent {
	// The components of the Or query.
	private List<QueryComponent> mComponents;
	
	public OrQuery(List<QueryComponent> components) {
		mComponents = components;
	}
	
	@Override
	public List<Posting> getPostings(Index index, TokenProcessor processor) {
		List<Posting> result = new ArrayList<>();
		ArrayList<List<Posting>> potentialMatches = new ArrayList<>();

		//Grab postings from all query components.
		for(QueryComponent literal : mComponents)
		{
			potentialMatches.add(literal.getPostings(index, processor));
		}

		//Merge the lists of postings.
		//Merge the first 2 lists immediately.  Then, continue merging any additional lists into the results list.
		result = this.mergePostingsOr(potentialMatches.get(0), potentialMatches.get(1));
		for(int i = 2; i < potentialMatches.size(); i++)
		{
			result = this.mergePostingsOr(result, potentialMatches.get(i));
		}

		return result;
	}
	
	@Override
	public String toString() {
		// Returns a string of the form "[SUBQUERY] + [SUBQUERY] + [SUBQUERY]"
		return "(" +
		 String.join(" + ", mComponents.stream().map(c -> c.toString()).collect(Collectors.toList()))
		 + " )";
	}

	/**
     * Merges two lists of postings together, using union
     * @param postings1 First list of postings.
     * @param postings2 Second list of postings.
     * @return The list of postings as a result of the merge.
     */
	List<Posting> mergePostingsOr(List<Posting> postings1, List<Posting> postings2)
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
			//Since this is an OR merge, we add the smaller document ID as well.
			else if((postings1.get(i)).getDocumentId() < (postings2.get(j).getDocumentId()))
			{
				merged.add(new Posting(postings1.get(i).getDocumentId()));
				i++;
			}
			else if((postings1.get(i)).getDocumentId() > (postings2.get(j).getDocumentId()))
			{
				merged.add(new Posting(postings2.get(j).getDocumentId()));
				j++;
			}
		}

		//If we fall off the end of end of a list, simply add the rest of the other list to the resulting list.
		//Fallen off postings1:
		if(i >= postings1.size())
		{
			while(j < postings2.size())
			{
				merged.add(new Posting(postings2.get(j).getDocumentId()));
				j++;
			}
		}
		//Fallen off postings2: 
		else if(j >= postings2.size())
		{
			while(i < postings1.size())
			{
				merged.add(new Posting(postings1.get(i).getDocumentId()));
				i++;
			}
		}
		return merged;
	}
}
