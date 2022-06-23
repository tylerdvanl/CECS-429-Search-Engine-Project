package edu.csulb;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Scanner;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.indexes.DiskIndexWriter;
import cecs429.indexes.DiskPositionalIndex;
import cecs429.indexes.Index;
import cecs429.indexes.InvertedPositionalIndex;
import cecs429.text.EnglishTokenProcessor;
import cecs429.text.EnglishTokenStream;

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
            
            Index hamiltonIndex = new DiskPositionalIndex();
            Index jayIndex = new DiskPositionalIndex();
            Index madisonIndex = new DiskPositionalIndex();
            Index disputedIndex = new DiskPositionalIndex();

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
            indexWriter.writeIndex(jayMem, jaySave, jayCorpus.getCorpusSize());
            indexWriter.writeIndex(madisonMem, madisonSave, madisonCorpus.getCorpusSize());
            indexWriter.writeIndex(disputedMem, disputedSave, disputedCorpus.getCorpusSize());


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
    
}
