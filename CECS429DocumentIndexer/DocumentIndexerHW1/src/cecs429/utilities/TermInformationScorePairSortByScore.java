package cecs429.utilities;

import java.util.Comparator;

public class TermInformationScorePairSortByScore implements Comparator<TermInformationScorePair>
{

    @Override
    public int compare(TermInformationScorePair o1, TermInformationScorePair o2) 
    {
        return -Double.compare(o1.getInfoScore(), o2.getInfoScore());
    }
    
}
