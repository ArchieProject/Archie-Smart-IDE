/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.hierarchy.graph;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import archie.hierarchy.Tactic;

/*******************************************************
 * Defines a vertex object for the tactic architecture component.
 * 
 * @author Ahmed Fakhry
 *******************************************************/
final class TacticVertex extends AbstractArchVertex
{
	private static final Ellipse2D TACTIC_SHAPE = new Ellipse2D.Float(-100, -20, 200, 40);

	/*******************************************************
	 * Constructs a tactic vertex for the given tactic component.
	 * 
	 * @param tactic
	 *            The tactic that this vertex will represent.
	 *******************************************************/
	public TacticVertex(Tactic tactic)
	{
		super(tactic);
	}

	/*******************************************************
	 * 
	 * @see archie.hierarchy.graph.IArchitectureVertex#getShape()
	 *******************************************************/
	@Override
	public Shape getShape()
	{
		return TACTIC_SHAPE;
	}

	/*******************************************************
	 * 
	 * @see archie.hierarchy.graph.IArchitectureVertex#getColor()
	 *******************************************************/
	@Override
	public Color getColor()
	{
		return Color.YELLOW;
	}

}
