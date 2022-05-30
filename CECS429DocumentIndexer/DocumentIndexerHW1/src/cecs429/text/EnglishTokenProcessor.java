package cecs429.text;

public class EnglishTokenProcessor implements TokenProcessor
{

    /**
     * Normalizes a token into one or more terms.  Several steps occur, detailed in the function code.
     * @param token The token being processed
     * @return The strings that pop out as a result of processing the token.  
     */
    @Override
    public String processToken(String token) 
    {
        // Remove all non-alphanumeric characters from the beginning and end of the token, but not the middle
        if(token.substring(0, 1) == "\\W")
        {
            token = token.substring(1);
        }
        if(token.substring(token.length() - 1, token.length()) == "\\W")
        {
            token = token.substring(0, token.length() - 2);
        }

        //Remove all apostropes or quotation marks (single or double quotes) from anywhere in the string.
        token.replaceAll("[\'\"]", "");

        //For hyphens in words, do both:
        //  (a) Remove the hyphens from the token and then proceed with the modied token;
        //  (b) Split the original hyphenated token into multiple tokens without a hyphen, and proceed with all split tokens.
        //      (So the token Hewlett-Packard-Computing would turn into HewlettPackardComputing, Hewlett,
        //      Packard, and Computing.)

        //TODO: This will require a list, will come back to it.

        
        return token;
    }
    
}
