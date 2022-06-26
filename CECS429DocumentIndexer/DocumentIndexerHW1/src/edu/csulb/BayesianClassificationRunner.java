package edu.csulb;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

import cecs429.Statistics.BayesianClassifier;
import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.indexes.DiskIndexWriter;
import cecs429.indexes.DiskPositionalIndex;
import cecs429.indexes.Index;
import cecs429.indexes.InvertedPositionalIndex;
import cecs429.text.EnglishTokenProcessor;
import cecs429.text.EnglishTokenStream;
import cecs429.utilities.TermInformationScorePair;

public class BayesianClassificationRunner 
{
    public static void main(String[] args) 
    {
        try
        {
            Scanner in = new Scanner(System.in);
            EnglishTokenProcessor processor = new EnglishTokenProcessor();
            
            Path hamiltonSave = Paths.get("FederalistPapers\\HAMILTON\\index\\");
            Path jaySave = Paths.get("FederalistPapers\\JAY\\index\\");
            Path madisonSave = Paths.get("FederalistPapers\\MADISON\\index\\");
            Path disputedSave = Paths.get("FederalistPapers\\DISPUTED\\index\\");
            
            DocumentCorpus hamiltonCorpus = DirectoryCorpus.loadDirectory(Paths.get("FederalistPapers\\HAMILTON\\"));
            DocumentCorpus jayCorpus = DirectoryCorpus.loadDirectory(Paths.get("FederalistPapers\\JAY\\"));
            DocumentCorpus madisonCorpus = DirectoryCorpus.loadDirectory(Paths.get("FederalistPapers\\MADISON\\"));
            DocumentCorpus disputedCorpus = DirectoryCorpus.loadDirectory(Paths.get("FederalistPapers\\DISPUTED\\"));

            DiskIndexWriter indexWriter = new DiskIndexWriter();
            Index hamiltonMem = indexCorpus(hamiltonCorpus);
            Index jayMem = indexCorpus(jayCorpus);
            Index madisonMem = indexCorpus(madisonCorpus);
            Index disputedMem = indexCorpus(disputedCorpus);
            indexWriter.writeIndex(hamiltonMem, hamiltonSave, hamiltonCorpus.getCorpusSize());
            Index hamiltonIndex = new DiskPositionalIndex(hamiltonSave);
            indexWriter.writeIndex(jayMem, jaySave, jayCorpus.getCorpusSize());           
            Index jayIndex = new DiskPositionalIndex(jaySave);
            indexWriter.writeIndex(madisonMem, madisonSave, madisonCorpus.getCorpusSize());
            Index madisonIndex = new DiskPositionalIndex(madisonSave);
            indexWriter.writeIndex(disputedMem, disputedSave, disputedCorpus.getCorpusSize());
            Index disputedIndex = new DiskPositionalIndex(disputedSave);

            System.out.println("Hamilton: " + hamiltonIndex.getVocabulary().size());
            System.out.println("Jay: " + jayIndex.getVocabulary().size());
            System.out.println("Madison: " + madisonIndex.getVocabulary().size());
            System.out.println("Disputed: " + disputedIndex.getVocabulary().size());

            ArrayList<Index> trainingSet = new ArrayList<>();
            trainingSet.add(hamiltonIndex);
            trainingSet.add(jayIndex);
            trainingSet.add(madisonIndex);
            BayesianClassifier classifier = new BayesianClassifier(disputedIndex, trainingSet);
            PriorityQueue<TermInformationScorePair> topScores = classifier.mutualInformation(trainingSet);
            /*for(int i = 0; i < topScores.size() && i < 50; i++)
            {
                TermInformationScorePair score = topScores.poll();
                System.out.println(i + ": Term: " + score.getTerm() + " || Score: " + score.getInfoScore());
            }*/
            List<String> tStar = new ArrayList<>();
            tStar = getTopDiscriminatingTerms(50, topScores);
            System.out.println("Top Terms: ");
            System.out.println(tStar);
            System.out.println("Term Frequencies in Hamilton: ");
            for(String term : tStar)
            {
                System.out.println("Term: " + term + " || Occurances: " + hamiltonIndex.getTermFrequency(term));
            }
            System.out.println("Conditional probabilities in Hamilton: ");
            for(String term : tStar)
            {
                System.out.println("Term: " + term + " || Probability: " + classifier.conditionalProbability(term, hamiltonIndex, classifier.getClassWeight(hamiltonIndex, tStar)));
            }


            //boolean exit = false;
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public static Index indexCorpus(DocumentCorpus corpus) 
	{
		EnglishTokenProcessor processor = new EnglishTokenProcessor();
		Instant start = Instant.now();
		// Constuct a TermDocumentMatrix once you know the size of the vocabulary.
		// THEN, do the loop again! But instead of inserting into the HashSet, add terms to the index with addPosting.
		InvertedPositionalIndex positionalIndex = new InvertedPositionalIndex();
		System.out.println("Indexing corpus, please wait.  This may take a few moments!");
		for(Document d : corpus.getDocuments())
		{
			Reader reader = d.getContent();
			EnglishTokenStream englishTokenStream = new EnglishTokenStream(reader);
			Iterable<String> tokens = englishTokenStream.getTokens();
			int positionInDocument = 0;
			for(String token : tokens)
			{
				positionInDocument++;
				ArrayList<String> processingTokens = processor.processToken(token);
				for(String processed : processingTokens)
					positionalIndex.addTerm(processed, d.getId(), positionInDocument);
			}
		}
		Instant finish = Instant.now();
		long timeElapsed = Duration.between(start, finish).toSeconds();
		System.out.println("Indexed in " + timeElapsed + " seconds.");
		return positionalIndex;
	}

    public static void printFirstThousandVocabAndTotal(Index index) throws IOException
	{
		ArrayList<String> vocab = new ArrayList<String>(index.getVocabulary());
		for(int count = 0; count < 1000 && count < vocab.size(); count++)
		{
			System.out.println(vocab.get(count));
		}
		System.out.println("Total vocabulary size: " + vocab.size());
	}

    public static List<String> getTopDiscriminatingTerms(int amount, PriorityQueue<TermInformationScorePair> termsAndScores)
    {
        List<String> topTerms = new ArrayList<>();
        int i = 0;
        while(i < amount)
        {
            String next = termsAndScores.peek().getTerm();
            if(!topTerms.contains(next))
            {
                topTerms.add(termsAndScores.poll().getTerm());
                i++;
            }
            else
                termsAndScores.poll();
        }
        return topTerms;
    }
    
}
