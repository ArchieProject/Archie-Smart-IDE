/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.views.graph.internals;

import org.eclipse.jdt.core.ICompilationUnit;

/*******************************************************
 * Defines an edge wrapper for the graph
 *******************************************************/
public final class EdgeWrapper
{
	/*******************************************************
	 * Enum for the possible graph types
	 *******************************************************/
	public enum GraphEdgeType
	{
		MACRO, MICRO;
	}

	final ICompilationUnit mStart;
	final ICompilationUnit mEnd;
	final String mMethod;
	final GraphEdgeType mType;

	/*******************************************************
	 * Creates an edge wrapper for the graph. Wraps the start, and end java
	 * files and the method that connects both
	 * 
	 * @param s
	 *            The file that calls method
	 * @param e
	 *            The file that includes the method that gets called
	 * @param m
	 *            The name of the method that gets called
	 * @param type
	 *            The type of the graph on which this edge will be added
	 * 
	 * @throws IllegalArgumentException
	 *             If any of the parameters is null
	 *******************************************************/
	public EdgeWrapper(ICompilationUnit s, ICompilationUnit e, String m, GraphEdgeType type)
	{
		if (s == null || e == null || m == null || type == null)
			throw new IllegalArgumentException();

		mStart = s;
		mEnd = e;
		mMethod = m;
		mType = type;
	}

	/*******************************************************
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 *******************************************************/
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!(obj instanceof EdgeWrapper))
			return false;

		EdgeWrapper other = (EdgeWrapper) obj;
		return mStart.equals(other.mStart) && mEnd.equals(other.mEnd) && mMethod.equals(other.mMethod)
				&& mType == other.mType;
	}

	/*******************************************************
	 * Returns the name of the edge
	 * 
	 * @see java.lang.Object#toString()
	 *******************************************************/
	@Override
	public String toString()
	{
		if (mType == GraphEdgeType.MACRO)
		{
			// In case of a macro graph, no label
			return "";
		}
		else
		{
			// In case of a micro graph, the name of the method
			return mMethod + "()";
		}
	}

	/*******************************************************
	 * 
	 * @see java.lang.Object#hashCode()
	 *******************************************************/
	@Override
	public int hashCode()
	{
		int result = 17;
		result = result * 37 + mStart.hashCode();
		result = result * 37 + mEnd.hashCode();

		if (mType == GraphEdgeType.MICRO)
		{
			result = result * 37 + mMethod.hashCode();
		}

		return result;
	}
}
