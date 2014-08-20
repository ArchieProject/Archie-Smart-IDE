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
import java.awt.geom.RoundRectangle2D;

import archie.hierarchy.Goal;

/*******************************************************
 * Defines a vertex object in the system architecture graph that represents an
 * architecture goal.
 * 
 * @author Ahmed Fakhry
 *******************************************************/
final class GoalVertex extends AbstractArchVertex
{
	private static final RoundRectangle2D GOAL_SHAPE = new RoundRectangle2D.Float(-100, -10, 200, 20, 5, 5);
	private static final Color GOAL_COLOR = new Color(24, 82, 251);
	
	/*******************************************************
	 * Constructs a goal vertex.
	 * 
	 * @param goal
	 *            The goal object it will be representing.
	 *******************************************************/
	public GoalVertex(Goal goal)
	{
		super(goal);
	}

	/*******************************************************
	 * 
	 * @see archie.hierarchy.graph.IArchitectureVertex#getShape()
	 *******************************************************/
	@Override
	public Shape getShape()
	{
		return GOAL_SHAPE;
	}

	/*******************************************************
	 * 
	 * @see archie.hierarchy.graph.IArchitectureVertex#getColor()
	 *******************************************************/
	@Override
	public Color getColor()
	{
		return GOAL_COLOR;
	}

}
