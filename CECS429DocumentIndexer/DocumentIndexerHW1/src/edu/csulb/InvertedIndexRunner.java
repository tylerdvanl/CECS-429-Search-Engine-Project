package edu.csulb;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.indexes.Index;
import cecs429.indexes.InvertedIndex;
import cecs429.indexes.Posting;
//import cecs429.indexes.TermDocumentIndex;
import cecs429.text.BasicTokenProcessor;
import cecs429.text.EnglishTokenStream;

import java.util.Scanner;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.HashSet;
import java.io.Reader;

public class InvertedIndexRunner {
	public static void main(String[] args) {
		// Create a DocumentCorpus to load .txt documents from the project directory.
		DocumentCorpus corpus = DirectoryCorpus.loadJSONDirectory(Paths.get("NPScorpus").toAbsolutePath(), ".json");
		// Index the documents of the corpus.
		Index index = indexCorpus(corpus);

		// We aren't ready to use a full query parser; for now, we'll only support single-term queries.
		
		String query = getUserInput();
		for (Posting p : index.getPostings(query)) {
			System.out.println("Document " + corpus.getDocument(p.getDocumentId()).getTitle());
		}
		// TODO: fix this application so the user is asked for a term to search.
	}
	
	private static Index indexCorpus(DocumentCorpus corpus) {
		BasicTokenProcessor processor = new BasicTokenProcessor();
		
		
		// Constuct a TermDocumentMatrix once you know the size of the vocabulary.
		// THEN, do the loop again! But instead of inserting into the HashSet, add terms to the index with addPosting.
		InvertedIndex tDIndex = new InvertedIndex();
		for(Document d : corpus.getDocuments())
		{
			Reader reader = d.getContent();
			EnglishTokenStream englishTokenStream = new EnglishTokenStream(reader);
			Iterable<String> tokens = englishTokenStream.getTokens();
			for(String token : tokens)
			{
				token = processor.processToken(token);
				tDIndex.addTerm(token, d.getId());
			}
		}
		
		return tDIndex;
	}

	private static String getUserInput()
	{
		Scanner in = new Scanner(System.in);
		System.out.println("Enter a single search term: ");
		String queryTerm = in.nextLine();
		queryTerm.toLowerCase();
		in.close();
		return queryTerm;
	}
}
