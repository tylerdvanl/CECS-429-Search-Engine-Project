package cecs429.queries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cecs429.indexes.Index;
import cecs429.indexes.Posting;
import cecs429.text.EnglishTokenProcessor;
import cecs429.text.TokenProcessor;

/**
 * Represents a phrase literal consisting of one or more terms that must occur in sequence.
 */
public class PhraseLiteral implements QueryComponent {
	// The list of individual terms in the phrase.
	private List<String> mTerms = new ArrayList<>();
	
	/**
	 * Constructs a PhraseLiteral with the given individual phrase terms.
	 */
	public PhraseLiteral(List<String> terms) {
		mTerms.addAll(terms);
	}
	
	/**
	 * Constructs a PhraseLiteral given a string with one or more individual terms separated by spaces.
	 */
	public PhraseLiteral(String terms) {
		mTerms.addAll(Arrays.asList(terms.split(" ")));
	}
	
	@Override
	public List<Posting> getPostings(Index index, TokenProcessor processor) 
	{
		List<Posting> result = new ArrayList<>();
		ArrayList<List<Posting>> potentialMatches = new ArrayList<>();
		for(String term : mTerms)
		{
			potentialMatches.add(index.getPostings(processor.processToken(term).get(0)));
		}
		
		int targetDistance = 1;
		result = this.positionalMerge(potentialMatches.get(0), potentialMatches.get(1), targetDistance);
		for(int i = 2; i < potentialMatches.size(); i++)
		{
			targetDistance++;
			result = this.positionalMerge(result, potentialMatches.get(i), targetDistance);
		}
		return result;
	}
	
	@Override
	public String toString() {
		return "\"" + String.join(" ", mTerms) + "\"";
	}

	//TODO: Positional merge for phrase queries.  Duplicates are getting added for some reason, fix it.  Also, these queries seem to take a long time.
	List<Posting> positionalMerge(List<Posting> postings1, List<Posting> postings2, int targetDistance)
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
				//Check to see if any positions in the postings are offset by targetDistance 
				int a = 0;
				int b = 0;

				ArrayList<Integer> positions1 = postings1.get(i).getPositions();
				ArrayList<Integer> positions2 = postings2.get(j).getPositions();

				while(a < positions1.size() && b < positions2.size())
				{
					if(positions2.get(b) - positions1.get(a) == targetDistance)
					{
						merged.add(postings1.get(i));
						a++;
						b++;
						break;
					}
					else if(positions1.get(a) < positions2.get(b))
						a++;
					else if(positions1.get(a) > positions2.get(b))
						b++;
					else if(positions1.get(a).equals(positions2.get(b)))
					{
						a++;
						b++;
					}
				}
				
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
