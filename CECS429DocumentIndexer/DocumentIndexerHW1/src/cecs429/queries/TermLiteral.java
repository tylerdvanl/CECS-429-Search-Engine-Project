package cecs429.queries;

import java.util.ArrayList;
import java.util.List;

import cecs429.indexes.Index;
import cecs429.indexes.Posting;
import cecs429.text.EnglishTokenProcessor;


/**
 * A TermLiteral represents a single term in a subquery.
 */
public class TermLiteral implements QueryComponent {
	private String mTerm;
	
	public TermLiteral(String term) {
		mTerm = term;
	}
	
	public String getTerm() {
		return mTerm;
	}
	
	@Override
	public List<Posting> getPostings(Index index) {
		EnglishTokenProcessor processor = new EnglishTokenProcessor();
		List<String> processedTerms = processor.processToken(mTerm);
		List<Posting> postings = new ArrayList<>();
		List<QueryComponent> newLiterals = new ArrayList<>();
		for(String term : processedTerms)
		{
			newLiterals.add(new TermLiteral(term));
		}
		if(processedTerms.size() == 1)
		{
			postings.addAll(index.getPostings(processedTerms.get(0)));
			return postings;
		}
		else
		{
			postings = new AndQuery(newLiterals).getPostings(index);
			return postings;
		}			
	}
	
	@Override
	public String toString() {
		return mTerm;
	}
}
