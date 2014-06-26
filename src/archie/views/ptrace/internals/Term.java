/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.views.ptrace.internals;

import java.util.HashSet;
import java.util.Set;

/*******************************************************
 * Defines an abstraction of a term in one or more documents.
 *******************************************************/
final class Term
{
	/*******************************************************
	 * The literal term string.
	 *******************************************************/
	private final String mTerm;

	/*******************************************************
	 * The term's inverse document frequency.
	 *******************************************************/
	private double mIdf;

	/*******************************************************
	 * The list of documents that contain this term.
	 *******************************************************/
	private final Set<String> mContainingDocs = new HashSet<String>();

	/*******************************************************
	 * The number of documents that contain this term.
	 *******************************************************/
	private int mNumDocsContainingTerm;

	/*******************************************************
	 * Constructs a {@link Term} object.
	 * 
	 * @param term
	 *            The literal string of the term.
	 *******************************************************/
	public Term(final String term)
	{
		mTerm = term;
		mIdf = 0.0;
		mNumDocsContainingTerm = 0;
	}

	/*******************************************************
	 * Adds the given document name to the list of documents on which this term
	 * exists.
	 * 
	 * @param docFileName
	 *            The document file name that contains this term. Use absolute
	 *            file paths.
	 *******************************************************/
	public void addContainingDoc(final String docFileName)
	{
		if (!mContainingDocs.contains(docFileName))
		{
			// Document name doesn't exist in the list
			// Add it
			mContainingDocs.add(docFileName);
			
			// Increment the counter
			mNumDocsContainingTerm++;
		}
	}

	/*******************************************************
	 * Calculates this term Inverse document frequency.
	 * 
	 * @param numDocumentsInCorpus
	 *            The total number of the documents in the corpus.
	 *******************************************************/
	public void calculateIdf(int numDocumentsInCorpus)
	{
		if (mNumDocsContainingTerm == 0)
		{
			mIdf = 1;
		}
		else
		{
			mIdf = Math.log(((double) numDocumentsInCorpus / mNumDocsContainingTerm));
		}
	}

	/*******************************************************
	 * Gets the term's inverse document frequency.
	 * 
	 * @return The term's Idf.
	 *******************************************************/
	public double getIdf()
	{
		return mIdf;
	}

	/*******************************************************
	 * Gets the term's literal string.
	 * 
	 * @return The term's literal string.
	 *******************************************************/
	public String getTerm()
	{
		return mTerm;
	}
	
	/*******************************************************
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 *******************************************************/
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this)
			return true;
		
		if(! (obj instanceof Term) )
			return false;
		
		Term other = (Term) obj;
		
		return this.mTerm.equals(other.mTerm);
	}
	
	/*******************************************************
	 * 
	 * @see java.lang.Object#hashCode()
	 *******************************************************/
	@Override
	public int hashCode()
	{
		return mTerm.hashCode();
	}
	
	/*******************************************************
	 * 
	 * @see java.lang.Object#toString()
	 *******************************************************/
	@Override
	public String toString()
	{
		return mTerm;
	}
}
