/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.views.graph.internals;

import java.io.File;

import org.apache.commons.collections15.Predicate;
import org.eclipse.jdt.core.ICompilationUnit;

import archie.views.autodetect.internals.TreeQualityItem;
import edu.uci.ics.jung.algorithms.filters.VertexPredicateFilter;
import edu.uci.ics.jung.graph.Graph;

/*******************************************************
 * Defines a graph filter to get a filtered graph that includes only the nodes
 * in a given tactics.
 *******************************************************/
public final class TacticVertexFilter
{
	private final VertexPredicateFilter<ICompilationUnit, EdgeWrapper> mFilter;

	/*******************************************************
	 * Constructor.
	 * 
	 * @param tactic
	 *            The {@link TreeQualityItem} that represents a tactic.
	 *******************************************************/
	public TacticVertexFilter(final TreeQualityItem tactic)
	{
		mFilter = new VertexPredicateFilter<ICompilationUnit, EdgeWrapper>(new Predicate<ICompilationUnit>()
		{
			private final TreeQualityItem mTactic = tactic;

			/*******************************************************
			 * This predicate will determine whether to include the vertex in
			 * the filtered graph or not.
			 * 
			 * @param vertex
			 *            The vertex currently being processed
			 * @return ture if the vertex to be included in the filtered graph,
			 *         false otherwise.
			 *******************************************************/
			@Override
			public boolean evaluate(ICompilationUnit vertex)
			{
				boolean result = false;

				String fullPath = new File(vertex.getResource().getLocationURI()).getAbsolutePath();
				
				if (mTactic.containsFile(fullPath))
				{
					result = true;
				}

				return result;
			}
		});
	}

	/*******************************************************
	 * Filters the given graph, the resulting graph will include only nodes in
	 * this filter's tactic
	 * 
	 * @param graph
	 *            The input graph to filter
	 * @return The filtered graph
	 *******************************************************/
	public Graph<ICompilationUnit, EdgeWrapper> filterGraph(Graph<ICompilationUnit, EdgeWrapper> graph)
	{
		return mFilter.transform(graph);
	}
}
