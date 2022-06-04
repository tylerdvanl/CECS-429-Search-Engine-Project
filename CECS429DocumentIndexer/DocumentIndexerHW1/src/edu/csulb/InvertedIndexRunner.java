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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.io.Reader;

public class InvertedIndexRunner {
	public static void main(String[] args) {
		// Create a DocumentCorpus to load .txt documents from the project directory.
		try 
		{
			Scanner in = new Scanner(System.in);
			Path directory = getPathFromUser();
			EnglishTokenProcessor processor = new EnglishTokenProcessor();
					
			DocumentCorpus corpus = DirectoryCorpus.loadJSONDirectory(directory, ".json");
			// Index the documents of the corpus.
			Index index = indexCorpus(corpus);
			boolean exit = false;
			
			while(!exit)
			{
				System.out.println("Enter a query: ");
				String input = getUserInput(in);
				//Special user inputs:
				// :q quits the program.
				if(input.matches(":q"))
				{
					exit = true;
				}
				else if(input.startsWith(":stem"))
				{
					System.out.println("Placeholder special stem function");
				}
				else if(input.startsWith(":index"))
				{
					System.out.println("Placeholder new index directory funtion.");
				}
				else if(input.matches(":vocab"))
				{
					printFirstThousandVocabAndTotal(index);
				}
				else
				{
					QueryComponent query = createQuery(input);
					/*for (Posting p : index.getPostings(query)) {
						System.out.println("Document " + corpus.getDocument(p.getDocumentId()).getTitle() + " at positions: " + p.getPositions());
					}*/
					int returnedPostings = 0;
					for(Posting p : query.getPostings(index, processor))
					{
						returnedPostings++;
						System.out.println("Document ID " + p.getDocumentId() + ": " + corpus.getDocument(p.getDocumentId()).getTitle());
					}
					System.out.println("Number of documents found: " + returnedPostings);
					//Select and print document?
					selectAndPrintDocument(in, corpus);
				}
			}
			in.close();
			System.out.println("Exiting, good bye!");
		} 
		catch (FileNotFoundException e) 
		{
			System.out.println("File error, exiting program.");
		}
		catch(RuntimeException e)
		{
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static Index indexCorpus(DocumentCorpus corpus) {
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

	private static String getUserInput(Scanner inputScanner)
	{
		

		System.out.print("> ");
		String userInput = inputScanner.nextLine();
		return userInput;
	}

	private static QueryComponent createQuery(String queryString)
	{
		BooleanQueryParser queryParser = new BooleanQueryParser();
		QueryComponent query = queryParser.parseQuery(queryString);
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

	//Stem special query
	//Index special query
	private static void printFirstThousandVocabAndTotal(Index index)
	{
		ArrayList<String> vocab = new ArrayList<String>(index.getVocabulary());
		for(int count = 0; count < 1000 && count < vocab.size(); count++)
		{
			System.out.println(vocab.get(count));
		}
		System.out.println("Total vocabulary size: " + vocab.size());
	}

	private static void selectAndPrintDocument(Scanner inputScanner, DocumentCorpus corpus) throws IOException
	{
		System.out.println("Would you like to view one of these documents? \n" + 
		"Enter it's ID to view, or type \"n\" to do another query.");
		String selection = getUserInput(inputScanner);
		//If the input starts with a digit, parse an int and print the doc.
		if(selection.substring(0, 1).matches("[\\d]"))
		{
			int numSelection = Integer.parseInt(selection);
			if(numSelection > 0)
			{
				Document selectedDoc = corpus.getDocument(numSelection);
				BufferedReader reader = new BufferedReader(selectedDoc.getContent());
				System.out.println(reader.readLine());
			}
		}
	}
}
