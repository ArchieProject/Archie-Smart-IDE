/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.views.ptrace.internals;

import java.util.HashMap;
import java.util.Map;

/*******************************************************
 * A singleton object that manages all the {@link Term}'s in all documents.
 *******************************************************/
final class TermsManager
{
	/**
	 * The singleton instance.
	 */
	private static final TermsManager INSTANCE = new TermsManager();

	/**
	 * The list of all terms in all documents.
	 */
	private final Map<String, Term> mTerms = new HashMap<String, Term>();

	/*******************************************************
	 * Gets the singleton instance.
	 * 
	 * @return The only instance of the {@link TermsManager}.
	 *******************************************************/
	public static TermsManager getInstance()
	{
		return INSTANCE;
	}

	/*******************************************************
	 * Private singleton constructor
	 *******************************************************/
	private TermsManager()
	{
	}

	/*******************************************************
	 * Clears all the terms in the manager.
	 *******************************************************/
	public void clearAllTerms()
	{
		mTerms.clear();
	}

	/*******************************************************
	 * Adds a term to the managed list. If the term already exists, it will be
	 * updated so that it knows the document referred to by docFileName contains
	 * it (if it doesn't know that already). Otherwise, it will be added as a
	 * new term, with the supplied document as a containing document.
	 * 
	 * @param term
	 *            The literal term string.
	 * @param docFileName
	 *            The absolute document file path.
	 * @return The {@link Term} after it has been either added or updated if it
	 *         already existed.
	 *******************************************************/
	public Term addTermToDocument(String term, String docFileName)
	{
		if (term == null || docFileName == null)
			throw new IllegalArgumentException();

		Term t = mTerms.get(term);

		if (t == null)
		{
			// This is a new term.
			t = new Term(term);
			mTerms.put(term, t);
		}

		// Update it to know that this document contains it.
		t.addContainingDoc(docFileName);

		return t;
	}

	/*******************************************************
	 * Calculates the Inverse Document Frequencies of all the managed terms.
	 * 
	 * @param numDocumentsInCorpus
	 *            The total number of the documents in the corpus.
	 *******************************************************/
	public void calculateAllTermsIdfs(int numDocumentsInCorpus)
	{
		for (Term term : mTerms.values())
		{
			term.calculateIdf(numDocumentsInCorpus);
		}
	}
}
