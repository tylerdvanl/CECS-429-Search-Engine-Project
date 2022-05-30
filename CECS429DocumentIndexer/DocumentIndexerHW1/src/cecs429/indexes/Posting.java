package cecs429.indexes;

import java.util.ArrayList;

/**
 * A Posting encapulates a document ID associated with a search query component.
 */
public class Posting {
	private int mDocumentId;
	private ArrayList<Integer> mPositions;
	
	public Posting(int documentId, int position) {
		mDocumentId = documentId;
		mPositions = new ArrayList<Integer>();
		mPositions.add(position);
	}
	
	public int getDocumentId() {
		return mDocumentId;
	}

	/**
	 * Adds another position to the list stored by this posting.
	 * @param position  The additional position to add to the posting.
	 */
	public void addPosition(int position)
	{
		//We should check to see if this position exists in the arraylist first.
		if(!mPositions.contains(position))
			mPositions.add(position);
	}

	/**
	 * Retrieves all the positions stored within this posting.
	 * @return The positions stored within this posting.
	 */
	public ArrayList<Integer> getPositions()
	{
		return mPositions;
	}
}
