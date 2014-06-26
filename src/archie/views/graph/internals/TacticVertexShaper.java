/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.views.graph.internals;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.io.File;

import org.apache.commons.collections15.Transformer;
import org.eclipse.jdt.core.ICompilationUnit;

import archie.views.autodetect.internals.TreeFileItem;
import archie.views.autodetect.internals.TreeQualityItem;

/*******************************************************
 * Defines a vertex shaper based on vertex node (file) probability, which will
 * determine the size of the node
 *******************************************************/
public final class TacticVertexShaper implements Transformer<ICompilationUnit, Shape>
{
	// A small circle as the default shape of the node if its not part of any
	// selected tactic
	private static final Shape DEFAULT_SHAPE = new Ellipse2D.Double(-2.5, -2.5, 5, 5);

	private final TreeQualityItem mTactic;

	/*******************************************************
	 * Constructs a vertex shaper that will draw the vertex (the file node)
	 * based on its probability in the given tactic.
	 * 
	 * @param tactic
	 *            The {@link TreeQualityItem} that represents a particular
	 *            tactic along with its files.
	 *******************************************************/
	public TacticVertexShaper(TreeQualityItem tactic)
	{
		mTactic = tactic;
	}

	/*******************************************************
	 * 
	 * @see org.apache.commons.collections15.Transformer#transform(java.lang.Object)
	 *******************************************************/
	@Override
	public Shape transform(ICompilationUnit vertex)
	{
		Shape result = DEFAULT_SHAPE;
		
		String fullPath = new File(vertex.getResource().getLocationURI()).getAbsolutePath();
		
		TreeFileItem item = mTactic.getFile(fullPath);
		if(item != null)
		{
			// The file exists on this tactic, get its probability
			double prob = item.getFileProbability();
			double size = 5 + (prob * 200);
			result = new Ellipse2D.Double(-size/2, -size/2, size, size);
		}
		
		return result;
	}

}
