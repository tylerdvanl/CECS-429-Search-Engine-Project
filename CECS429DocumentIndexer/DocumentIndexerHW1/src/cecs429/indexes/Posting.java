package cecs429.indexes;

import java.util.ArrayList;

/**
 * A Posting encapulates a document ID associated with a search query component.
 */
public class Posting {
	private int mDocumentId;
	private ArrayList<Integer> mPositions;
	private double mWeight;
	
	public Posting(int documentId, int position) {
		mDocumentId = documentId;
		mPositions = new ArrayList<Integer>();
		mPositions.add(position);
		mWeight = 0.0;
	}

	public Posting(int documentId, int position, double docWeight) {
		mDocumentId = documentId;
		mPositions = new ArrayList<Integer>();
		mPositions.add(position);
		mWeight = docWeight;
	}

	public Posting(int documentId, ArrayList<Integer> positions) {
		mDocumentId = documentId;
		mPositions = new ArrayList<Integer>();
		mPositions.addAll(positions);
		mWeight = 0.0;
	}

	public Posting(int documentId, ArrayList<Integer> positions, double docWeight) {
		mDocumentId = documentId;
		mPositions = new ArrayList<Integer>();
		mPositions.addAll(positions);
		mWeight = docWeight;
	}

	public Posting(int documentId)
	{
		mDocumentId = documentId;
		mPositions = new ArrayList<Integer>();
		mWeight = 0.0;
	}

	public Posting(int documentId, double docWeight)
	{
		mDocumentId = documentId;
		mPositions = new ArrayList<Integer>();
		mWeight = docWeight;
	}
	
	public int getDocumentId() 
	{
		return mDocumentId;
	}

	public double getWeight()
	{
		return mWeight;
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
