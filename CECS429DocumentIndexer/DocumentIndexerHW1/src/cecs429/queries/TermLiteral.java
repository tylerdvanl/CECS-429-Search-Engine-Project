package cecs429.queries;

import java.util.ArrayList;
import java.util.List;

import cecs429.indexes.Index;
import cecs429.indexes.Posting;
import cecs429.text.EnglishTokenProcessor;
import cecs429.text.TokenProcessor;


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
	public List<Posting> getPostings(Index index, TokenProcessor processor) {
		//Grab the first string that results from processing the term.
		String literal = (processor.processToken(mTerm)).get(0);
		return index.getPostings(literal);
	}
	
	@Override
	public String toString() {
		return mTerm;
	}
}
