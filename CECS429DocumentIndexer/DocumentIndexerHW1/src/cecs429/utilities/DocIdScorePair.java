package cecs429.utilities;

public class DocIdScorePair {

    int mDocId;
    double mScore;
    
    public DocIdScorePair(int docId, double score)
    {
        mDocId = docId;
        mScore = score;
    }
    
    public int getId()
    {   
        return mDocId;
    }

    public double getScore()
    {
        return mScore;
    }


}
