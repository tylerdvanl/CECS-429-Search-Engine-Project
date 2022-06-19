package cecs429.queries;

import java.util.ArrayList;
import java.util.Arrays;

public class RankedQueryParser 
{
    public ArrayList<String> getTerms(String query)
    {
        ArrayList<String> terms = new ArrayList<>();
        terms.addAll(Arrays.asList(query.split(" ")));
        return terms;
    }


}
