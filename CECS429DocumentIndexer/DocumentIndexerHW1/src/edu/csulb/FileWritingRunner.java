package edu.csulb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import cecs429.queries.BooleanQueryParser;
import cecs429.queries.QueryComponent;
import cecs429.queries.RankedQuery;
import cecs429.text.EnglishTokenProcessor;
import cecs429.text.EnglishTokenStream;
import cecs429.text.TokenProcessor;
import cecs429.utilities.DocIdScorePair;

public class FileWritingRunner 
{
 
    public static void main(String[] args) 
    {
        try 
        {
			Scanner in = new Scanner(System.in);
            Path directory;
            Path savePath = Paths.get("index\\");
			Index diskIndex = new DiskPositionalIndex();
			EnglishTokenProcessor processor = new EnglishTokenProcessor();
			directory = getPathFromUser();
			DocumentCorpus corpus = DirectoryCorpus.loadDirectory(directory);
			boolean exit = false;
			
			while(!exit)
			{
				System.out.println("1.) Index a corpus");
				System.out.println("2.) Perform queries");
				String modeChoice = getUserInput(in);
				//Index a corpus and write it out to file.
				if(modeChoice.matches("1"))
				{
					DiskIndexWriter indexWriter = new DiskIndexWriter();
	
					// Index the documents of the corpus.
					Index index = indexCorpus(corpus);
					ArrayList<Integer> bytePositions = indexWriter.writeIndex(index, savePath);
					System.out.println("Finished Indexing");
				}
				else if(modeChoice.matches("2"))
				{
					while(!exit)
					{
						//Ask to do ranked query or boolean query.  Both modes should allow the special queries to work.
						System.out.println("1.) Ranked Query");
						System.out.println("2.) Boolean Query");
						String queryChoice = getUserInput(in);

						if(queryChoice.matches("1"))
						{
							//Do Ranked Queries
							int corpusSize = corpus.getCorpusSize();	
							ArrayList<DocIdScorePair> IdsAndScores = new ArrayList<>();
							RankedQuery testQuery = new RankedQuery("devils postpile");
							IdsAndScores.addAll(testQuery.getTopTen(diskIndex, processor, corpusSize));
							System.out.println("done!");
				
							for(DocIdScorePair pair : IdsAndScores)
							{
								System.out.println("Document ID " + pair.getId() + " : " + corpus.getDocument(pair.getId()).getTitle() + " || Score : " + pair.getScore());
							}
							exit = true;
						}
						else if(queryChoice.matches("2"))
						{
							//Do boolean queries, same as previous milestone
							int corpusSize = corpus.getCorpusSize();
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
								menuStemToken(input, processor);
							}
							else if(input.startsWith(":index"))
							{
								//corpus = createNewCorpusFromInput(input);
								//diskIndex = indexCorpus(corpus);
							}
							else if(input.matches(":vocab"))
							{
								printFirstThousandVocabAndTotal(diskIndex);
							}
							else
							{
								QueryComponent query = createQuery(input);
								/*for (Posting p : index.getPostings(query)) {
									System.out.println("Document " + corpus.getDocument(p.getDocumentId()).getTitle() + " at positions: " + p.getPositions());
								}*/
								int returnedPostings = 0;
								for(Posting p : query.getPostings(diskIndex, processor))
								{
									returnedPostings++;
									System.out.println("Document ID " + p.getDocumentId() + ": " + corpus.getDocument(p.getDocumentId()).getTitle());
								}
								System.out.println("Number of documents found: " + returnedPostings);
								//Select and print document?
								if(returnedPostings > 0)
									selectAndPrintDocument(in, corpus);
							}
						}
					}
				}
			}
			System.out.println("Exiting.");
        }
        catch (FileNotFoundException e) {
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
	private static void menuStemToken(String token, TokenProcessor processor)
	{
		//Grab the string that results after the first space.
		List<String> tokens = Arrays.asList(token.split("\\s", 2));
		token = tokens.get(1);
		token = processor.stemSingleString(token);
		System.out.println("Stemmed: " + token);
	}
	
	//Index special query
	private static DocumentCorpus createNewCorpusFromInput(String directoryInput)
	{
		List<String> inputs = Arrays.asList(directoryInput.split("\\s", 2));
		String directory = inputs.get(1);
		Path path = Paths.get(directory);
		return DirectoryCorpus.loadDirectory(path);

	}

	private static void printFirstThousandVocabAndTotal(Index index) throws IOException
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
		"Enter its ID to view, or type \"n\" to do another query.");
		String selection = getUserInput(inputScanner);
		//If the input starts with a digit, parse an int and print the doc.
		if(selection.substring(0, 1).matches("[\\d]"))
		{
			int numSelection = Integer.parseInt(selection);
			if(numSelection > 0)
			{
				Document selectedDoc = corpus.getDocument(numSelection);
				BufferedReader reader = new BufferedReader(selectedDoc.getContent());
				boolean finished = false;
				while(!finished)
				{
					String line = reader.readLine();
					if(line != null)
						System.out.println(line);
					else if(line == null)
						finished = true;
				}
			}
		}
	}
}
