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

/*******************************************************
 * An interface to define what functionality a vertex of a software architecture
 * component in the graph needs to have.
 * 
 * @author Ahmed Fakhry
 *******************************************************/
interface IArchitectureVertex
{
	/*******************************************************
	 * @return The shape of the vertex (how it should be drawn).
	 *******************************************************/
	public Shape getShape();

	/*******************************************************
	 * @return The color of the vertex.
	 *******************************************************/
	public Color getColor();

	/*******************************************************
	 * Defines what should happen if this vertex was double clicked.
	 *******************************************************/
	public void handleDoubleClick();

	/*******************************************************
	 * @return The string representation of the vertex.
	 *******************************************************/
	@Override
	public String toString();

	/*******************************************************
	 * Tests equality between a vertex and another object.
	 * 
	 * @param obj
	 *            The other object to be compared to.
	 * 
	 * @return true if they're equal, false otherwise.
	 *******************************************************/
	@Override
	public boolean equals(Object obj);

	/*******************************************************
	 * @return The hash code of the vertex object
	 *******************************************************/
	@Override
	public int hashCode();
}
