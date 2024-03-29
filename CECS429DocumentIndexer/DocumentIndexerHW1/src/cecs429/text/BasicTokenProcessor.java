package cecs429.text;

import java.util.ArrayList;


/**
 * A BasicTokenProcessor creates terms from tokens by removing all non-alphanumeric characters from the token, and
 * converting it to all lowercase.
 */
public class BasicTokenProcessor implements TokenProcessor {
	@Override
	public ArrayList<String> processToken(String token) {
		ArrayList<String> results = new ArrayList<>();
		token = token.replaceAll("\\W", "").toLowerCase();
		results.add(token);
		return results;
	}

	@Override
	public ArrayList<String> processTokenKeepHyphens(String token) {
		return this.processToken(token);
	}

	@Override
	public String stemSingleString(String token) {
		return this.processToken(token).get(0);
	}
}
