package cecs429.utilities;

public class TermInformationScorePair 
{
    private String term;
    private double infoScore;

    public TermInformationScorePair(String term, double score)
    {
        this.term = term;
        infoScore = score;
    }

    public String getTerm()
    {
        return term;
    }

    public double getInfoScore()
    {
        return infoScore;
    }

}
