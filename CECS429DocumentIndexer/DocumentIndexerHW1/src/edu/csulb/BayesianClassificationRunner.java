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

            ArrayList<Index> trainingSet = new ArrayList<>();
            trainingSet.add(hamiltonIndex);
            trainingSet.add(jayIndex);
            trainingSet.add(madisonIndex);
            BayesianClassifier classifier = new BayesianClassifier();
            PriorityQueue<TermInformationScorePair> topScores = classifier.mutualInformation(trainingSet);
            /*for(int i = 0; i < topScores.size() && i < 50; i++)
            {
                TermInformationScorePair score = topScores.poll();
                System.out.println(i + ": Term: " + score.getTerm() + " || Score: " + score.getInfoScore());
            }*/
            List<String> tStar = new ArrayList<>();
            int cutoff = 50;
            tStar = getTopDiscriminatingTerms(cutoff, topScores);

            System.out.println("\nTop " + cutoff + " Discriminating Terms:");
            System.out.println(tStar);
            List<Integer> topClasses = classifier.classify(disputedIndex, trainingSet, tStar);
            System.out.println("\nClass decisions per document: ");
            for(int i = 0; i < disputedIndex.indexSize(); i++)
            {
                System.out.println("Document " + i + ": " + disputedCorpus.getDocument(i).getTitle() + " most likely belongs to index in " +
                trainingSet.get(topClasses.get(i)).getSavePath());
            }
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

    public static List<String> getTopDiscriminatingTerms(int amount, PriorityQueue<TermInformationScorePair> termsAndScores)
    {
        List<String> topTerms = new ArrayList<>();
        int i = 0;
        while(i < amount)
        {
            String next = termsAndScores.peek().getTerm();
            if(!topTerms.contains(next))
            {
                /*DEMO CODE! NOT A FUNCTIONAL PIECE OF THE PROGRAM 
                if(i < 10)
                    System.out.println("Term: " + next + " || Mutual Information Score: " + termsAndScores.peek().getInfoScore());
                */
                topTerms.add(termsAndScores.poll().getTerm());
                i++;
            }
            else
                termsAndScores.poll();
        }
        return topTerms;
    }

    
    
}
