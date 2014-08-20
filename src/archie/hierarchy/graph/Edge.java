/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.hierarchy.graph;

/*******************************************************
 * Defines an edge in the architecture graph.f
 * 
 * @author Ahmed Fakhry
 *******************************************************/
final class Edge
{
	private final IArchitectureVertex mStart;
	private final IArchitectureVertex mEnd;

	/*******************************************************
	 * Constructs and edge to connect two vertices.
	 * 
	 * @param start
	 * 			The start vertex. [Cannot be null].
	 * 
	 * @param end
	 * 			The end vertex. [Cannot be null].
	 *******************************************************/
	public Edge(IArchitectureVertex start, IArchitectureVertex end)
	{
		if (start == null || end == null)
			throw new IllegalArgumentException();

		mStart = start;
		mEnd = end;
	}

	/*******************************************************
	 * By default the edge has no label.
	 * 
	 * @see java.lang.Object#toString()
	 *******************************************************/
	@Override
	public String toString()
	{
		return "";
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
		
		return result;
	}
}
