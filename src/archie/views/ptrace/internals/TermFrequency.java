/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.views.ptrace.internals;

/*******************************************************
 * Container of a Term frequency
 *******************************************************/
final class TermFrequency
{
	/*******************************************************
	 * The frequency value.
	 *******************************************************/
	private int mFrequency;

	/*******************************************************
	 * Constructs the object.
	 * 
	 * @param freq
	 *            The initial frequency value.
	 *******************************************************/
	public TermFrequency(int freq)
	{
		mFrequency = freq;
	}

	/*******************************************************
	 * Increments the frequency value by 1.
	 *******************************************************/
	public void increment()
	{
		++mFrequency;
	}

	/*******************************************************
	 * Gets the frequency value.
	 * 
	 * @return The frequency value.
	 *******************************************************/
	public int getValue()
	{
		return mFrequency;
	}
}
