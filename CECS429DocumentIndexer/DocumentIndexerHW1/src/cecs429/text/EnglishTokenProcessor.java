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

    /**
     * Processes a string in the same way as processToken() but skips the hyphen modification step.
     * @param token The string to be processed.
     * @return  A list containing the processed string.  This method is intended to only return one (or 0, possibly) string.
     */
    public ArrayList<String> processTokenKeepHyphens(String token)
    {
        // Remove all non-alphanumeric characters from the beginning and end of the token, but not the middle
        token = removeNonAlphanumeric(token);
        if(token.isEmpty())
            return new ArrayList<String>();

        //Remove all apostropes or quotation marks (single or double quotes) from anywhere in the string.
        token = token.replaceAll("[\'\"]", "");
        if(token.isEmpty())
            return new ArrayList<String>();

        ArrayList<String> processed = new ArrayList<>();
        processed.add(token);
        
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

    /**
     * Removes all non-alphanumeric characters from the beginning and end of a
     * string, and returns it.  If the string ever becomes empty, should immediately return the empty string.
     * @param token
     * @return The string without any alphanumeric characters orignally at the beginning or end.
     */
    private String removeNonAlphanumeric(String token)
    {
        // Remove all non-alphanumeric characters from the beginning and end of the token, but not the middle

        int firstIndex = 0;
        int lastIndex = token.length() - 1;


        while(firstIndex < token.length() && !Character.isLetterOrDigit(token.charAt(firstIndex)))
        {
            firstIndex++;
        }

        while(lastIndex >= firstIndex && !Character.isLetterOrDigit(token.charAt(lastIndex)))
        {
            lastIndex--;
        }

        if(firstIndex >= token.length())
            return "";
        //System.out.println("token: " + token.substring(firstIndex, lastIndex));
        return token.substring(firstIndex, lastIndex + 1);
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
        //For hyphens in words, do both:
        //  (a) Remove the hyphens from the token and then proceed with the modfied token;
        //  (b) Split the original hyphenated token into multiple tokens without a hyphen, and proceed with all split tokens.
        //      (So the token Hewlett-Packard-Computing would turn into HewlettPackardComputing, Hewlett,
        //      Packard, and Computing.)
        ArrayList<String> modified = new ArrayList<>();
        if(!token.contains("-"))
        {
            modified.add(token);
            return modified;
        }
        else
        {
            modified.add(token.replaceAll("[-]", ""));
            modified.addAll(Arrays.asList(token.split("[-]")));
            for(int i = 0; i < modified.size(); i++)
            {
                modified.set(i, removeNonAlphanumeric(modified.get(i)));
            }
            return modified;
        }  
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
            stemmer.setCurrent(word);
            stemmer.stem();
            String newWord = stemmer.getCurrent();
            tokens.set(tokens.indexOf(word), newWord);
        }
        //System.out.println("Tokens: " + tokens);
        return tokens;
    }

    public String stemSingleString(String token)
    {
        englishStemmer stemmer = new englishStemmer();
        stemmer.setCurrent(token);
        stemmer.stem();
        String newWord = stemmer.getCurrent();
        return newWord;
    }
}
