package cecs429.text;

import java.util.ArrayList;
import java.util.Arrays;
import org.tartarus.snowball.ext.englishStemmer;


public class EnglishTokenProcessor implements TokenProcessor
{

    /**
     * Normalizes a token into one or more terms.  Several steps occur, detailed in the function code.
     * @param token The token being processed
     * @return The strings that pop out as a result of processing the token.  
     */
    @Override
    public ArrayList<String> processToken(String token) 
    {
        // Remove all non-alphanumeric characters from the beginning and end of the token, but not the middle
        token = removeNonAlphanumeric(token);
        if(token.isEmpty())
            return new ArrayList<String>();

        //Remove all apostropes or quotation marks (single or double quotes) from anywhere in the string.
        token = token.replaceAll("[\'\"]", "");
        if(token.isEmpty())
            return new ArrayList<String>();

        //For hyphens in words, do both:
        //  (a) Remove the hyphens from the token and then proceed with the modfied token;
        //  (b) Split the original hyphenated token into multiple tokens without a hyphen, and proceed with all split tokens.
        //      (So the token Hewlett-Packard-Computing would turn into HewlettPackardComputing, Hewlett,
        //      Packard, and Computing.)
        ArrayList<String> processed = hyphenModification(token);

        //Convert the token to lowercase.
        for(String word : processed)
        {
            processed.set(processed.indexOf(word), word.toLowerCase());
        }
        
        //Stem the token using an implementation of the Porter2 stemmer. Please do not code this yourself; find
        //an implementation with a permissible license and integrate it with your solution.
        processed = stemStrings(processed);
        return processed;
    }

    public ArrayList<String> processTokenKeepHyphens(String token)
    {
        //TODO: Process a token, but keep the hyphens.
        return null;
    }

    /**
     * Removes all non-alphanumeric characters from the beginning and end of a
     * string, and returns it.  If the string ever becomes empty, should immediately return the empty string.
     * @param token
     * @return The string without any alphanumeric characters orignally at the beginning or end.
     */
    private String removeNonAlphanumeric(String token)
    {
        // Remove all non-alphanumeric characters from the beginning and end of the token, but not the middle
        if(token.substring(0, 1).matches("\\W"))
        {
            token = token.substring(1);
            //If replacing the first character results in the string being empty, return an empty arraylist.
            if(token.isEmpty())
                return "";
        }
        if(token.substring(token.length() - 1, token.length()).matches("\\W"))
        {
            token = token.substring(0, token.length() - 1);
            //If replacing the last character results in the string being empty, return an empty arraylist.
            if(token.isEmpty())
                return "";
        }

        return token;
    }

    /**
     * Does a couple things: 1. Removes hyphens from the token
     * AND 2. Splits the token on the hyphens, resulting in multiple tokens.
     * The results get added to an arraylist and returned.
     * @param token The original token to modify.
     * @return An arraylist with a hyphen-removed token, and other tokens that were split on the hyphen.
     */
    private ArrayList<String> hyphenModification(String token)
    {
        //TODO: If the orignal string has no hyphens, simply return it alone in the list.  In this case, there should be only one string in the return list.
        //For hyphens in words, do both:
        //  (a) Remove the hyphens from the token and then proceed with the modfied token;
        //  (b) Split the original hyphenated token into multiple tokens without a hyphen, and proceed with all split tokens.
        //      (So the token Hewlett-Packard-Computing would turn into HewlettPackardComputing, Hewlett,
        //      Packard, and Computing.)
        ArrayList<String> modified = new ArrayList<>();
        modified.add(token.replaceAll("[-]", ""));
        modified.addAll(Arrays.asList(token.split("[-]")));
        return modified;
    }

    /**
     * Stems all strings in a given arraylist using a Snowball Stemmer, which implements Porter2.
     * @param tokens A list of strings to be stemmed.
     * @return A list of the strings as a result of stemming the argument list.
     */
    private ArrayList<String> stemStrings(ArrayList<String> tokens)
    {
        englishStemmer stemmer = new englishStemmer();
        for(String word : tokens)
        {
            //TODO: This usage seems proper, and gives correct results (I think)
            stemmer.setCurrent(word);
            stemmer.stem();
            String newWord = stemmer.getCurrent();
            tokens.set(tokens.indexOf(word), newWord);
        }
        return tokens;
    }
    
}
