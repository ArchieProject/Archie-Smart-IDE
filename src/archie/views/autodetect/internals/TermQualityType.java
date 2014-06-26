/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.views.autodetect.internals;

import java.io.Serializable;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;

/*******************************************************
 * Defines a class to encapsulate an IndicatorTerms quality type or a
 * "classification" of a group of indicator terms.
 * 
 * The equality and comparison of two TermQualityTypes depends only on their
 * names. This to prevent any duplicates.
 *******************************************************/
final class TermQualityType implements Comparable<TermQualityType>, Iterable<String>, Serializable
{
	/*******************************************************
	 * For Serializable.
	 *******************************************************/
	private static final long serialVersionUID = -1463508622465455962L;

	/*******************************************************
	 * The name of the quality type
	 *******************************************************/
	private final String mQualityName;

	/*******************************************************
	 * Key: The term word. Value: The corresponding {@link IndicatorTerm}
	 * object.
	 *******************************************************/
	private final Hashtable<String, IndicatorTerm> mTerms = new Hashtable<String, IndicatorTerm>();

	/*******************************************************
	 * The total probability of this terms that belong to this quality type
	 *******************************************************/
	private double mTotalProbability;
	
	/*******************************************************
	 * Constructs a quality type with the specified name to contain a list of
	 * IndicatorTerms that belong to this quality.
	 * 
	 * @param qualityName
	 *            The name of this quality.
	 *******************************************************/
	public TermQualityType(String qualityName)
	{
		mQualityName = qualityName;
		mTotalProbability = 0.0;
	}

	/*******************************************************
	 * Adds the provided term to the list of IndicatorTerms under this quality
	 * type.
	 * 
	 * @param term
	 *            An {@link IndicatorTerm} to be added under this quality type.
	 *******************************************************/
	public void addTerm(IndicatorTerm term)
	{
		mTerms.put(term.getTerm(), term);
		mTotalProbability += term.getProbability();
	}

	/*******************************************************
	 * Gets the {@link IndicatorTerm} that corresponds to the same provided term
	 * word, if it exists on this quality type, or null if it doesn't.
	 * 
	 * @param termWord
	 *            The string term word.
	 * @return The {@link IndicatorTerm} object if found, null otherwise.
	 *******************************************************/
	public IndicatorTerm getTerm(String termWord)
	{
		IndicatorTerm result;

		if (termWord != null)
		{
			result = mTerms.get(termWord);
		}
		else
		{
			result = null;
		}

		return result;
	}

	/*******************************************************
	 * Tests if the specified term word has a corresponding
	 * {@link IndicatorTerm} object on this quality type.
	 * 
	 * @param termWord
	 *            The string term word
	 * @return true if it belongs to this quality type, false otherwise.
	 *******************************************************/
	public boolean containsTerm(String termWord)
	{
		return (this.getTerm(termWord) != null);
	}

	/*******************************************************
	 * Gets the total probability of the terms on this quality type.
	 * 
	 * @return the double total probability.
	 *******************************************************/
	public double getTotalProbability()
	{
		return mTotalProbability;
	}

	/*******************************************************
	 * Returns the string name of this quality type.
	 * 
	 * @return The string name.
	 *******************************************************/
	public String getName()
	{
		return mQualityName;
	}

	/*******************************************************
	 * Equality test. Depends only on the name of the quality term.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 *******************************************************/
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (obj.getClass() != this.getClass())
			return false;

		TermQualityType other = (TermQualityType) obj;

		return this.mQualityName.equals(other.mQualityName);
	}

	/*******************************************************
	 * The hash code of the object. Depends only on the name of the quality.
	 * 
	 * @see java.lang.Object#hashCode()
	 *******************************************************/
	@Override
	public int hashCode()
	{
		return mQualityName.hashCode();
	}

	/*******************************************************
	 * Comparison test. Depends only on the names of the two qualities.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 *******************************************************/
	@Override
	public int compareTo(TermQualityType o)
	{
		return mQualityName.compareTo(o.mQualityName);
	}

	/*******************************************************
	 * Returns a read-only iterator over the terms under this quality type.
	 * 
	 * @see java.lang.Iterable#iterator()
	 *******************************************************/
	@Override
	public Iterator<String> iterator()
	{
		return Collections.unmodifiableCollection(mTerms.keySet()).iterator();
	}

}
