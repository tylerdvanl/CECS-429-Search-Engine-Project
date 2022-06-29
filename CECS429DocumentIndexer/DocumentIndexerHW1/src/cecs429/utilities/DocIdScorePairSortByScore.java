package cecs429.utilities;

import java.util.Comparator;

public class DocIdScorePairSortByScore implements Comparator<DocIdScorePair>
{
    @Override
    public int compare(DocIdScorePair o1, DocIdScorePair o2) 
    {
        return -Double.compare(o1.getScore(), o2.getScore());
    }
    
}
