/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.views.graph.internals;

import java.awt.Color;
import java.awt.Paint;
import java.io.File;
import java.util.Random;

import org.apache.commons.collections15.Transformer;
import org.eclipse.jdt.core.ICompilationUnit;

import archie.views.autodetect.internals.TreeQualityItem;
import edu.uci.ics.jung.visualization.picking.PickedState;

/*******************************************************
 * Defines a Vertex painter to color the vertices on a graph of type
 * Graph<String, E>. The vertices represents file names, and they will be
 * colored based on whether they belong to the given {@link TreeQualityItem}
 *******************************************************/
public final class TacticVertexPainter implements Transformer<ICompilationUnit, Paint>
{
	// -- Random num generator
	private static final Random RANDOM = new Random();
	private static final Color PICKED_COLOR = Color.YELLOW;

	// -- Static initializer
	static
	{
		// Seed the random num gen
		RANDOM.setSeed(System.currentTimeMillis());
	}

	/*******************************************************
	 * @return A random {@link Color}
	 *******************************************************/
	private static Color getRandomColor()
	{
		// [0 : 255]
		int r = RANDOM.nextInt(256);
		int g = RANDOM.nextInt(256);
		int b = RANDOM.nextInt(256);

		return new Color(r, g, b);
	}

	// --------------------
	// Fields
	// --------------------
	private final TreeQualityItem mTactic;
	private final Color mResult;
	private PickedState<ICompilationUnit> mVertexPickedState;

	// --------------------

	/*******************************************************
	 * Constructs a vertex painter for the nodes (the files) that belong to the
	 * given tactics.
	 * 
	 * @param tactic
	 *            The {@link TreeQualityItem} whose files will be painted
	 *******************************************************/
	public TacticVertexPainter(TreeQualityItem tactic)
	{
		mTactic = tactic;
		mResult = TacticVertexPainter.getRandomColor();
		
		mVertexPickedState = new NullPickedState();
	} 

	/*******************************************************
	 * Sets the graph's {@link PickedState} in order to know whether the vertex
	 * is selected or not
	 * 
	 * @param pickedState
	 *            The graph's picked state.
	 *******************************************************/
	public void setPickedState(PickedState<ICompilationUnit> pickedState)
	{
		mVertexPickedState = pickedState;
	}

	/*******************************************************
	 * Given a vertex name (which represents a file), this method will return
	 * the corresponding vertex color if it belongs to this painter tactics
	 * type.
	 * 
	 * @see org.apache.commons.collections15.Transformer#transform(java.lang.Object)
	 *******************************************************/
	@Override
	public Paint transform(ICompilationUnit vertex)
	{
		Color result = null;

		// Is the vertex selected (picked)?
		if (mVertexPickedState.isPicked(vertex))
		{
			result = TacticVertexPainter.PICKED_COLOR;
		}
		else
		{
			// Is the given node (file) name is contained in the tactics?
			String fullPath = new File(vertex.getResource().getLocationURI()).getAbsolutePath();
			
			if (mTactic.containsFile(fullPath))
			{
				result = mResult;
			}
			else
			{
				// Return the default red color
				result = Color.RED;
			}
		}

		return result;
	}
}
