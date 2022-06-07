package cecs429.queries;

import java.util.ArrayList;
import java.util.List;

import cecs429.indexes.Index;
import cecs429.indexes.Posting;
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
		List<String> literals = processor.processToken(mTerm);
		if(literals.size() == 1)
			return index.getPostings(literals.get(0));
		
		else
		{
			List<QueryComponent> terms = new ArrayList<>();
			for (String literal : literals)
			{
				terms.add(new TermLiteral(literal));
			}
			QueryComponent newQuery = new OrQuery(terms);
			return newQuery.getPostings(index, processor);
		}
	}
	
	@Override
	public String toString() {
		return mTerm;
	}
}
