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
        if(token.substring(0, 1).matches("\\W"))
        {
            token = token.substring(1);
            //If replacing the first character results in the string being empty, return an empty arraylist.
            if(token.isEmpty())
                return new ArrayList<>();
        }
        if(token.substring(token.length() - 1, token.length()).matches("\\W"))
        {
            token = token.substring(0, token.length() - 1);
            //If replacing the last character results in the string being empty, return an empty arraylist.
            if(token.isEmpty())
                return new ArrayList<>();
        }

        //Remove all apostropes or quotation marks (single or double quotes) from anywhere in the string.
        token = token.replaceAll("[\'\"]", "");
        if(token.isEmpty())
            return new ArrayList<>();

        //For hyphens in words, do both:
        //  (a) Remove the hyphens from the token and then proceed with the modfied token;
        //  (b) Split the original hyphenated token into multiple tokens without a hyphen, and proceed with all split tokens.
        //      (So the token Hewlett-Packard-Computing would turn into HewlettPackardComputing, Hewlett,
        //      Packard, and Computing.)
        ArrayList<String> processed = new ArrayList<>();
        processed.add(token.replaceAll("[-]", ""));
        processed.addAll(Arrays.asList(token.split("[-]")));

        //Convert the token to lowercase.
        for(String word : processed)
        {
            processed.set(processed.indexOf(word), word.toLowerCase());
        }
            

        //Stem the token using an implementation of the Porter2 stemmer. Please do not code this yourself; find
        //an implementation with a permissible license and integrate it with your solution.
        englishStemmer stemmer = new englishStemmer();
        for(String word : processed)
        {
            //TODO: I have no idea if this is the proper usage, there is no documentation for Snowball.
            stemmer.setCurrent(word);
            stemmer.stem();
            String newWord = stemmer.getCurrent();
            System.out.println("stemmed: " + newWord);
            processed.set(processed.indexOf(word), newWord);
        }
        //System.out.println("Tokens: " + processed);
        return processed;
    }
    
}
