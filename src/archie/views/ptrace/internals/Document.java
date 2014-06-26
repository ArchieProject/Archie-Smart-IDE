/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.views.ptrace.internals;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/*******************************************************
 * Defines an abstraction of a document for which we would like to calculate the
 * Tf-Idf
 *******************************************************/
final class Document
{
	/*******************************************************
	 * The file name.
	 *******************************************************/
	private final String mFileName;

	/*******************************************************
	 * The absolute file path.
	 *******************************************************/
	private final String mFilePath;

	/*******************************************************
	 * Some sample content from the file.
	 *******************************************************/
	private final String mSampleContent;

	/*******************************************************
	 * The list of terms contained in this document. Key:- The {@link Term}
	 * object. Value:- The term's frequency within this document.
	 *******************************************************/
	private final Map<Term, TermFrequency> mTerms = new HashMap<Term, TermFrequency>();

	/*******************************************************
	 * Constructs a document.
	 * 
	 * @param fileName
	 *            The file name.
	 * @param filePath
	 *            The absolute file path.
	 * @param sampleContent
	 *            Sample content from the document.
	 *******************************************************/
	public Document(String fileName, String filePath, String sampleContent)
	{
		if (fileName == null || filePath == null || sampleContent == null)
			throw new IllegalArgumentException();

		mFileName = fileName;
		mFilePath = filePath;
		mSampleContent = sampleContent;
	}

	/*******************************************************
	 * Adds a term to this document.
	 * 
	 * @param term
	 *            The term literal string.
	 *******************************************************/
	public void addTerm(final String term)
	{
		Term t = TermsManager.getInstance().addTermToDocument(term, mFilePath);

		TermFrequency frequency = mTerms.get(t);

		if (frequency == null)
		{
			frequency = new TermFrequency(0);
			mTerms.put(t, frequency);
		}

		frequency.increment();
	}

	/*******************************************************
	 * Builds the Tf-Idf vector of this document for the supplied list of terms.
	 * Usually the list of terms given in the parameter are the list of terms of
	 * the query (source) document.
	 * 
	 * @param terms
	 *            The list of terms that will be used to build this document's
	 *            Tf-Idf Vector.
	 * @return The newly-built {@link TfIdfVect}.
	 *******************************************************/
	public TfIdfVect buildTfIdfVect(Iterable<Term> terms)
	{
		TfIdfVect vect = new TfIdfVect(mFileName, mFilePath, mSampleContent);

		// This will be needed to normalize the frequency.
		double inverseNumTerms = 1.0 / (double) mTerms.size();

		for (Term t : terms)
		{
			TermFrequency freq = mTerms.get(t);
			double finalFreq = 0.0;

			if (freq != null)
			{
				// This term exists in this document.
				// Otherwise 0.0 will be used as its frequency.
				finalFreq = (freq.getValue() * inverseNumTerms);
			}

			vect.addTfIdf(finalFreq * t.getIdf());
		}

		return vect;
	}

	/*******************************************************
	 * Gets a sample content of this document.
	 * 
	 * @return The sample content of this document.
	 *******************************************************/
	public String getSampleContent()
	{
		return mSampleContent;
	}

	/*******************************************************
	 * Gets an unmodifiable collection of the this document's terms.
	 * 
	 * @return The list of terms in this document.
	 *******************************************************/
	public Iterable<Term> getTermsList()
	{
		return Collections.unmodifiableCollection(mTerms.keySet());
	}
}
