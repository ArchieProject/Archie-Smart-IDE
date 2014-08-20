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
import java.awt.geom.Rectangle2D;

import archie.hierarchy.SubGoal;

/*******************************************************
 * Defines a vertex for a sub goal architecture component.
 * 
 * @author Ahmed Fakhry
 *******************************************************/
final class SubGoalVertex extends AbstractArchVertex
{
	private static final Rectangle2D SUB_GOAL_SHAPE = new Rectangle2D.Float(-100, -20, 200, 40);
	
	/*******************************************************
	 * Constructs a sub goal vertex.
	 * 
	 * @param subGoal
	 * 			The sub goal component it represents.
	 *******************************************************/
	public SubGoalVertex(SubGoal subGoal)
	{
		super(subGoal);
	}

	/*******************************************************
	 * 
	 * @see archie.hierarchy.graph.IArchitectureVertex#getShape()
	 *******************************************************/
	@Override
	public Shape getShape()
	{
		return SUB_GOAL_SHAPE;
	}

	/*******************************************************
	 * 
	 * @see archie.hierarchy.graph.IArchitectureVertex#getColor()
	 *******************************************************/
	@Override
	public Color getColor()
	{
		return Color.GREEN;
	}

}
