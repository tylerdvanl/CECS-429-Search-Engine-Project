package edu.csulb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFileChooser;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.indexes.DiskIndexWriter;
import cecs429.indexes.DiskPositionalIndex;
import cecs429.indexes.Index;
import cecs429.indexes.InvertedPositionalIndex;
import cecs429.indexes.Posting;
import cecs429.queries.RankedQuery;
import cecs429.text.EnglishTokenProcessor;
import cecs429.text.EnglishTokenStream;
import cecs429.utilities.DocIdScorePair;

public class FileWritingRunner 
{
 
    public static void main(String[] args) 
    {
        try 
        {
            Scanner in = new Scanner(System.in);
            Path directory;
            Path savePath = Paths.get("mobyDickCorpus\\index\\");
            directory = getPathFromUser();
            EnglishTokenProcessor processor = new EnglishTokenProcessor();
            DiskIndexWriter indexWriter = new DiskIndexWriter();

            DocumentCorpus corpus = DirectoryCorpus.loadDirectory(directory);
            // Index the documents of the corpus.
            Index index = indexCorpus(corpus);
			Index diskIndex = new DiskPositionalIndex();

            ArrayList<Integer> bytePositions = indexWriter.writeIndex(index, savePath);
			System.out.println("Finished Indexing");

			System.out.println(diskIndex.getVocabulary().size());

			ArrayList<DocIdScorePair> IdsAndScores = new ArrayList<>();
			RankedQuery testQuery = new RankedQuery("whale");
			IdsAndScores.addAll(testQuery.getTopTen(diskIndex, processor, corpus));
			System.out.println("done!");

			for(DocIdScorePair pair : IdsAndScores)
			{
				System.out.println("Document ID " + pair.getId() + " : " + corpus.getDocument(pair.getId()).getTitle() + " || Score : " + pair.getScore());
			}
        }
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		catch (IOException e){
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
    
    private static Path getPathFromUser() throws FileNotFoundException
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int choice = chooser.showOpenDialog(null);
		if(choice == JFileChooser.APPROVE_OPTION)
		{
			File selectedFile = chooser.getSelectedFile();
			return selectedFile.toPath();
		}
		else if(choice == JFileChooser.CANCEL_OPTION)
		{
			throw new RuntimeException();
		}
		else
			throw new FileNotFoundException();	
	}

}
