/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.views.ptrace.internals;

import java.util.Comparator;

/*******************************************************
 * @author Ahmed Fakhry
 * 
 *         A wrapper for the results expected by the panoramic trace view
 *******************************************************/
public final class PTraceResult implements Comparable<PTraceResult>
{
	/*******************************************************
	 * A comparator that can be used to sort results by
	 * increasing order of the scores.
	 *******************************************************/
	public static final Comparator<PTraceResult> BY_INCREASING_SCORES = new Comparator<PTraceResult>()
	{
		@Override
		public int compare(PTraceResult o1, PTraceResult o2)
		{
			if(o1.mScore < o2.mScore)
			{
				return -1;
			}
			else if(o1.mScore > o2.mScore)
			{
				return 1;
			}
			else
			{
				return 0;
			}
		}
	};
	
	/*******************************************************
	 * A comparator that can be used to sort the results in a
	 * decreasing order of their scores.
	 *******************************************************/
	public static final Comparator<PTraceResult> BY_DECREASING_SCORES = new Comparator<PTraceResult>()
	{
		@Override
		public int compare(PTraceResult o1, PTraceResult o2)
		{
			if(o1.mScore < o2.mScore)
			{
				return 1;
			}
			else if(o1.mScore > o2.mScore)
			{
				return -1;
			}
			else
			{
				return 0;
			}
		}
	};
	
	/*******************************************************
	 * Fields
	 *******************************************************/
	
	private final String mFileName;
	private final String mFilePath;
	private final String mSampleContent;
	private final double mScore;

	/*******************************************************
	 * Creates a panoramic view result wrapper.
	 * 
	 * @param fileName
	 *            The name of the target comparison file.
	 * @param sampleContent
	 *            The first paragraph of the target comparison file.
	 * @param score
	 *            The similarity score.
	 *******************************************************/
	public PTraceResult(String fileName, String filePath, String sampleContent, double score)
	{
		mFileName = fileName;
		mFilePath = filePath;
		mSampleContent = sampleContent;
		mScore = score;
	}

	/*******************************************************
	 * @return The name of the file.
	 *******************************************************/
	public String getFileName()
	{
		return mFileName;
	}

	/*******************************************************
	 * @return The path of the file.
	 *******************************************************/
	public String getFilePath()
	{
		return mFilePath;
	}

	/*******************************************************
	 * @return The first paragraph of the target comparison file.
	 *******************************************************/
	public String getSampleContent()
	{
		return mSampleContent;
	}

	/*******************************************************
	 * @return The similarity score.
	 *******************************************************/
	public double getScore()
	{
		return mScore;
	}

	/*******************************************************
	 * Score-based comparison
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 *******************************************************/
	@Override
	public int compareTo(PTraceResult other)
	{
		if (this.mScore < other.mScore)
		{
			return -1;
		}
		else if(this.mScore > other.mScore)
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
}
