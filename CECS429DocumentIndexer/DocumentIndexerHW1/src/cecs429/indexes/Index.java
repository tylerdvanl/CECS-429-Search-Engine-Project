package cecs429.indexes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * An Index can retrieve postings for a term from a data structure associating terms and the documents
 * that contain them.
 */
public interface Index {
	/**
	 * Retrieves a list of Postings of documents that contain the given term and its positions.
	 */
	List<Posting> getPostingsWithPositions(String term);

	/**
	 * Retrieves a list of Postings of documents that contain the given term.
	 */
	List<Posting> getPostingsNoPositions(String term);
	
	int getDocumentFrequency(String term);
	
	/**
	 * A (sorted) list of all terms in the index vocabulary.
	 * @throws IOException
	 */
	List<String> getVocabulary() throws IOException;

	double getDocWeight(int docId) throws IOException;

	long indexSize() throws FileNotFoundException, IOException;
}
