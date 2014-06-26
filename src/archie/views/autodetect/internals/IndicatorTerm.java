/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.views.autodetect.internals;

import java.io.Serializable;

/*******************************************************
 * Defines an abstraction for an indicator term to encapsulate its name and its
 * probability. An object of this class is <b>Immutable</b>
 * 
 * @Note It implements <code>equals()</code> and <code>hashCode()</code> so it's
 *       ready to be used with collections.
 *******************************************************/
final class IndicatorTerm implements Serializable
{
	/*******************************************************
	 * For serializable.
	 *******************************************************/
	private static final long serialVersionUID = -477283899394832257L;
	
	private final String mTerm;
	private final double mProbability;
	
	/*******************************************************
	 * Constructs and IndicatorTerm object.
	 * 
	 * @param term
	 *            The actual string term.
	 * @param probability
	 *            Its probability.
	 *******************************************************/
	public IndicatorTerm(String term, double probability)
	{
		mTerm = term;
		mProbability = probability;
	}

	/*******************************************************
	 * The copy constructor
	 * 
	 * @param other
	 *            The source from which to construct the new copy
	 *******************************************************/
	public IndicatorTerm(final IndicatorTerm other)
	{
		mTerm = new String(other.mTerm);
		mProbability = other.mProbability;
	}

	/*******************************************************
	 * Gets the string term.
	 * 
	 * @return This Indicator term as a string
	 *******************************************************/
	public String getTerm()
	{
		return mTerm;
	}

	/*******************************************************
	 * Gets the probability of this term.
	 * 
	 * @return The double probability of the term.
	 *******************************************************/
	public double getProbability()
	{
		return mProbability;
	}

	/*******************************************************
	 * Determines whether this IndicatorTerm is equal to the provided obj.
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

		IndicatorTerm other = (IndicatorTerm) obj;

		return mTerm.equals(other.mTerm) && (new Double(mProbability).equals(new Double(other.mProbability)));
	}

	/*******************************************************
	 * Returns the hash code of this object.
	 * 
	 * @see java.lang.Object#hashCode()
	 *******************************************************/
	@Override
	public int hashCode()
	{
		int result = 17;
		result = result * 31 + mTerm.hashCode();
		result = result * 31 + (new Double(mProbability).hashCode());

		return result;
	}

	/*******************************************************
	 * The string representation of the this object.
	 * 
	 * @see java.lang.Object#toString()
	 *******************************************************/
	@Override
	public String toString()
	{
		return new String(mTerm + " : " + mProbability);
	}
}
