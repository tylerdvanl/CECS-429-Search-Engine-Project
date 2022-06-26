package cecs429.Statistics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import cecs429.indexes.Index;
import cecs429.utilities.TermInformationScorePair;
import cecs429.utilities.TermInformationScorePairSortByScore;

public class BayesianClassifier 
{

    Index targetIndex;
    List<Index> trainingIndexes;

    public BayesianClassifier(Index target, List<Index> training)
    {
        targetIndex = target;
        trainingIndexes = training;
    }

    public PriorityQueue<TermInformationScorePair> mutualInformation(List<Index> classes) throws FileNotFoundException, IOException
    {
        //TODO: Give this a type
        PriorityQueue<TermInformationScorePair> informationScores = new PriorityQueue<>(new TermInformationScorePairSortByScore());
        //TODO: Mutual Information algorithm; big giant equation for each term/class pairing.
        Double totalDocuments = 0.0;
        ArrayList<Integer> indexSizes = new ArrayList<>();
        for(Index index : classes)
        {
            int indexSize = (int) index.indexSize();
            indexSizes.add(indexSize);
            totalDocuments += indexSize;
        }
        //Go through each term in each index, calculating I(T,C) for each, and adding that information to a priority queue.
        for(int i = 0; i < classes.size(); i++)
        {
            //Target index
            Index index = classes.get(i);
            //Documents in the index
            Double docsInIndex = (double) index.indexSize();
            
            //Documents in other indexes
            Double totalDocsInOtherIndexes = 0.0;
            for(int j = 0; j < classes.size(); j++)
            {
                if(j != i)
                    totalDocsInOtherIndexes += classes.get(j).indexSize();
            }
            for(String term : index.getVocabulary())
            {
                //Documents with the term in other indexes.
                Double docsWithTermInOtherIndexes = 0.0;
                for(int k = 0; k < classes.size(); k++)
                {
                    if(k != i)
                        docsWithTermInOtherIndexes += classes.get(k).getDocumentFrequency(term);
                }
                double score = calculateInformationScore(totalDocuments, (double) index.getDocumentFrequency(term), docsWithTermInOtherIndexes, 
                    docsInIndex -index.getDocumentFrequency(term), totalDocsInOtherIndexes - docsWithTermInOtherIndexes);
                
                if(Double.isNaN(score))
                    informationScores.add(new TermInformationScorePair(term, 0.0));
                else    
                    informationScores.add(new TermInformationScorePair(term, score));
            }
        }
        return informationScores;
    }

    public double getClassWeight(Index index, List<String> tStar)
    {
        double classWeight = 0.0;
        for(String term : tStar)
        {
            classWeight += (index.getTermFrequency(term) + 1);
        }
        return classWeight;
    }

    public double conditionalProbability(String term, Index index, double classWeight)
    {
        return ((index.getTermFrequency(term) + 1)/(classWeight));
    }

    private double calculateInformationScore(Double totalDocuments, Double n11, Double n10, Double n01, Double n00)
    {
        return ((n11/totalDocuments) * (log2(totalDocuments*n11) - log2((n11 + n10)*(n11 + n01)))
            + (n10/totalDocuments) * (log2(totalDocuments*n10) - log2((n11 + n10)*(n10 + n00)))
            + (n01/totalDocuments) * (log2(totalDocuments*n01) - log2((n01 + n00)*(n11 + n01)))
            + (n00/totalDocuments) * (log2(totalDocuments*n00) - log2((n01 + n00)*(n10 + n00))));
    }

    private double log2(Double argument)
    {
        //This is probably improper, but I'm unsure of how to handle this in this specific use case.
        if(argument == 0)
            return 0;

        return Math.log(argument)/Math.log(2);
    }
}
