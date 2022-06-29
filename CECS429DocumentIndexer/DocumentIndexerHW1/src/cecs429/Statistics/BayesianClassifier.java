package cecs429.Statistics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import cecs429.indexes.Index;
import cecs429.indexes.Posting;
import cecs429.utilities.TermInformationScorePair;
import cecs429.utilities.TermInformationScorePairSortByScore;

public class BayesianClassifier 
{

    private Index targetIndex;
    private List<Index> trainingIndexes;

    public BayesianClassifier(Index target, List<Index> training)
    {
        targetIndex = target;
        trainingIndexes = training;
    }

    public PriorityQueue<TermInformationScorePair> mutualInformation(List<Index> classes) throws FileNotFoundException, IOException
    {
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
                double score = calculateInformationScore(
                    totalDocuments, 
                    (double) index.getDocumentFrequency(term), 
                    docsWithTermInOtherIndexes, 
                    (double) docsInIndex -index.getDocumentFrequency(term), 
                    totalDocsInOtherIndexes - docsWithTermInOtherIndexes);
                
                if(Double.isNaN(score))
                    informationScores.add(new TermInformationScorePair(term, 0.0));
                else    
                    informationScores.add(new TermInformationScorePair(term, score));
            }
        }
        return informationScores;
    }

    //TODO: This is some disastrously bad OOP, fix it if I have time.
    public List<Integer> classify(Index targetIndex, List<Index> trainingSet, List<String> tStar) throws FileNotFoundException, IOException
    {
        List<Integer> topClasses = new ArrayList<Integer>((int) targetIndex.indexSize());
        double trainingSetSize = indexSetTotalDocuments(trainingSet);
        List<HashMap<String, List<Posting>>> postingsWithTermsInIndexes = new ArrayList<>();
        for(Index index : trainingSet)
        {
            HashMap<String, List<Posting>> termToPostings = new HashMap<>();
            for(String term : tStar)
                termToPostings.put(term, index.getPostingsNoPositions(term));
            
            postingsWithTermsInIndexes.add(termToPostings);
        }
        //Now we have the postings for each term, for each class.
        //For each new document, check to see if it contains a t* term, then calculate the probability of that term in each training class.
        HashMap<String, List<Posting>> newDocumentsTermsToPostings = new HashMap<>();
        for(String term : tStar)
            newDocumentsTermsToPostings.put(term, targetIndex.getPostingsNoPositions(term));

        for(int docId = 0; docId < targetIndex.indexSize(); docId++)
        {

            HashMap<Integer, Double> classNumToTotalProbability = new HashMap<>();
            for(int classNum = 0; classNum < trainingSet.size(); classNum++)
            {
                double weight = getClassWeight(trainingSet.get(classNum), tStar);
                //Grab all the factors that will be multiplied together for this class.
                //These will be the conditional probability of the term in this class, if the document has the term.
                List<Double> factors = new ArrayList<Double>();
                for(String term : tStar)
                {
                    List<Posting> newDocsWithTerm = newDocumentsTermsToPostings.get(term);
                    for(Posting posting : newDocsWithTerm)
                        {
                            if(posting.getDocumentId() == docId)
                            {
                                factors.add(Math.log(conditionalProbability(term, trainingSet.get(classNum), weight)));
                                break; //There will never be a duplicate docId in a postings list.
                            }
                        }
                }
                double probabilityOfClass = ((double) trainingSet.get(classNum).indexSize())/((double) trainingSetSize);
                //Multiply all the factors, and put the product into the map with the classNum.
                classNumToTotalProbability.put(classNum, (Math.log(probabilityOfClass) + sumAll(factors)));
            }
            //////////////
            //This is for demo purposes, not a functional piece of code!
            /*//////////////////////
            if(docId == 0)
                printRequestedDemoInformation(docId, classNumToTotalProbability, trainingSet);
            *//////////////////////
            //Now we can grab the class with the maximum probability value in our map.
            int classWithMaxProbability = 0;
            for(int i = 1; i < trainingSet.size(); i++)
            {
                if(classNumToTotalProbability.get(i) > classNumToTotalProbability.get(classWithMaxProbability))
                    classWithMaxProbability = i;
            }
            topClasses.add(classWithMaxProbability);
        }
        return topClasses;
    }

    public double getClassWeight(Index index, List<String> tStar)
    {
        double classWeight = 0.0;
        for(String term : tStar)
            classWeight += ((double) index.getDocumentFrequency(term) + 1.0);

        return classWeight;
    }

    public double conditionalProbability(String term, Index index, double classWeight)
    {
        int frequency = index.getDocumentFrequency(term);
        return ((frequency + 1.0)/(classWeight));
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
        return Math.log(argument)/Math.log(2);
    }

    private double sumAll(List<Double> factors)
    {
        double product = 0;
        for(Double factor : factors)
            product += factor;

        return product;
    }

    private int indexSetTotalDocuments(List<Index> set) throws FileNotFoundException, IOException
    {
        int sum = 0;
        for(Index index : set)
            sum += (int) index.indexSize();

        return sum;
    }

    private void printRequestedDemoInformation(int newDocId, HashMap<Integer, Double> classToScore, List<Index> indexes)
    {
        System.out.println("New Document ID: " + newDocId + " Score for each potential index follows:");
        for(int i = 0; i < indexes.size(); i++)
            System.out.println("Score for Index in " + indexes.get(i).getSavePath() + " || " + classToScore.get(i));

    }
}
