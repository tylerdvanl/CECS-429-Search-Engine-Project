package edu.csulb;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.indexes.Index;
import cecs429.indexes.InvertedPositionalIndex;
import cecs429.indexes.Posting;
//import cecs429.indexes.TermDocumentIndex;
import cecs429.text.BasicTokenProcessor;
import cecs429.text.EnglishTokenProcessor;
import cecs429.text.EnglishTokenStream;
import cecs429.queries.*;

import java.util.Scanner;

import javax.swing.JFileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.io.Reader;

public class InvertedIndexRunner {
	public static void main(String[] args) {
		// Create a DocumentCorpus to load .txt documents from the project directory.
		try 
		{
			Path directory = getPathFromUser();
			EnglishTokenProcessor processor = new EnglishTokenProcessor();
					
			DocumentCorpus corpus = DirectoryCorpus.loadJSONDirectory(directory, ".json");
			// Index the documents of the corpus.
			System.out.println("Indexing corpus, please wait.  This may take a few moments!");
			Index index = indexCorpus(corpus);

			// We aren't ready to use a full query parser; for now, we'll only support single-term queries.
			
			QueryComponent query = getUserInput();
			/*for (Posting p : index.getPostings(query)) {
				System.out.println("Document " + corpus.getDocument(p.getDocumentId()).getTitle() + " at positions: " + p.getPositions());
			}*/

			for(Posting p : query.getPostings(index, processor))
			{
				System.out.println("Document " + corpus.getDocument(p.getDocumentId()).getTitle());
			}
		// TODO: fix this application so the user is asked for a term to search.
		} 
		catch (FileNotFoundException e) 
		{
			System.out.println("File error, exiting program.");
		}
		catch(RuntimeException e)
		{
			System.out.println("File selection cancelled, exiting program.");
		}
	}
	
	private static Index indexCorpus(DocumentCorpus corpus) {
		EnglishTokenProcessor processor = new EnglishTokenProcessor();

		// Constuct a TermDocumentMatrix once you know the size of the vocabulary.
		// THEN, do the loop again! But instead of inserting into the HashSet, add terms to the index with addPosting.
		InvertedPositionalIndex positionalIndex = new InvertedPositionalIndex();
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

		return positionalIndex;
	}

	private static QueryComponent getUserInput()
	{
		Scanner in = new Scanner(System.in);
		System.out.println("Enter a query: ");
		String queryString = in.nextLine();
		BooleanQueryParser queryParser = new BooleanQueryParser();
		QueryComponent query = queryParser.parseQuery(queryString);
		in.close();
		return query;
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
