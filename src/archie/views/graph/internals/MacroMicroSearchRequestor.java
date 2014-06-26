/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.views.graph.internals;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchRequestor;

import archie.views.graph.internals.EdgeWrapper.GraphEdgeType;
import edu.uci.ics.jung.graph.DirectedGraph;

/*******************************************************
 * Defines a search requestor to handle a search match while searching for the
 * declarations of the methods invoked by a particular file and hence filling
 * the macro and micro graphs with the needed edges.
 *******************************************************/
public final class MacroMicroSearchRequestor extends SearchRequestor
{
	private final ICompilationUnit mInvokingFile;
	private final DirectedGraph<ICompilationUnit, EdgeWrapper> mMacroGraph;
	private final DirectedGraph<ICompilationUnit, EdgeWrapper> mMicroGraph;

	/*******************************************************
	 * Creates a new instance of this class
	 * 
	 * @param invokingFile
	 *            The file that invokes methods whose declarations will be
	 *            searched
	 * @param macroGraph
	 *            The macro graph to be filled
	 * @param microGraph
	 *            The micro graph to be filled
	 * @throws IllegalArgumentException
	 *             If any of the parameters is null
	 *******************************************************/
	public MacroMicroSearchRequestor(ICompilationUnit invokingFile, DirectedGraph<ICompilationUnit, EdgeWrapper> macroGraph,
			DirectedGraph<ICompilationUnit, EdgeWrapper> microGraph)
	{
		if (invokingFile == null || macroGraph == null || microGraph == null)
		{
			throw new IllegalArgumentException();
		}

		
		mInvokingFile = invokingFile;
		mMacroGraph = macroGraph;
		mMicroGraph = microGraph;

		// Add the invoking file vertices
		mMacroGraph.addVertex(mInvokingFile);
		mMicroGraph.addVertex(mInvokingFile);
	}

	/*******************************************************
	 * 
	 * @see org.eclipse.jdt.core.search.SearchRequestor#acceptSearchMatch(org.eclipse.jdt.core.search.SearchMatch)
	 *******************************************************/
	@Override
	public void acceptSearchMatch(SearchMatch match) throws CoreException
	{
		// The found method declaration
		IJavaElement element = (IJavaElement) match.getElement();

		if (element instanceof IMethod)
		{
			IMethod foundMethod = (IMethod) element;
			ICompilationUnit calledFile = foundMethod.getCompilationUnit();

			if (calledFile != null)
			{
				if (!mInvokingFile.equals(calledFile))
				{
					// Only care about inter-file dependencies

					// --- Add vertices if they don't exist
					if (!mMacroGraph.containsVertex(calledFile))
					{
						mMacroGraph.addVertex(calledFile);
					}

					if (!mMicroGraph.containsVertex(calledFile))
					{
						mMicroGraph.addVertex(calledFile);
					}

					String methodName = foundMethod.getElementName();

					// --- Add the edges
					// 1 - Macro
					EdgeWrapper e1 = new EdgeWrapper(mInvokingFile, calledFile, methodName, GraphEdgeType.MACRO);
					if (!mMacroGraph.containsEdge(e1))
					{
						// In the macro graph, only one edge between any two
						// files
						mMacroGraph.addEdge(e1, mInvokingFile, calledFile);
					}

					// 2 - Micro
					EdgeWrapper e2 = new EdgeWrapper(mInvokingFile, calledFile, methodName, GraphEdgeType.MICRO);
					mMicroGraph.addEdge(e2, mInvokingFile, calledFile);
				}
			}
		}
	}

}
