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
		String processedTerm = (processor.processToken(mTerm)).get(0);
		

		return index.getPostings(mTerm);
	}
	
	@Override
	public String toString() {
		return mTerm;
	}
}
