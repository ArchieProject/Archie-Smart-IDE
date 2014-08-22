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

import org.eclipse.jdt.core.IJavaProject;

/*******************************************************
 * Defines a vertex in the architecture graph that represents a Java project.
 * 
 * @author Ahmed Fakhry
 *******************************************************/
final class ProjectVertex implements IArchitectureVertex
{
	private static final Shape PROJECT_SHAPE = new Rectangle2D.Float(-100, -50, 200, 100);

	private final IJavaProject mProject;

	/*******************************************************
	 * Constructs a java project vertex in the architecture hierarchy graph.
	 * 
	 * @param project
	 *            The reference to the Java project that this vertex will
	 *            represent in the graph. [Cannot be null].
	 *******************************************************/
	public ProjectVertex(IJavaProject project)
	{
		if (project == null)
			throw new IllegalArgumentException();

		mProject = project;
	}

	/*******************************************************
	 * 
	 * @see archie.hierarchy.graph.IArchitectureVertex#getShape()
	 *******************************************************/
	@Override
	public Shape getShape()
	{
		return PROJECT_SHAPE;
	}

	/*******************************************************
	 * 
	 * @see archie.hierarchy.graph.IArchitectureVertex#getColor()
	 *******************************************************/
	@Override
	public Color getColor()
	{
		return Color.RED;
	}

	/*******************************************************
	 * 
	 * @see archie.hierarchy.graph.IArchitectureVertex#handleDoubleClick()
	 *******************************************************/
	@Override
	public void handleDoubleClick()
	{
		// Doesn't do anything.
	}

	/*******************************************************
	 * 
	 * @see IArchitectureVertex#toString()
	 *******************************************************/
	@Override
	public String toString()
	{
		return mProject.getElementName();
	}

	/*******************************************************
	 * 
	 * @see IArchitectureVertex#equals(Object)
	 *******************************************************/
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
			return false;

		if (this == obj)
			return true;

		if (this.getClass() != obj.getClass())
			return false;

		ProjectVertex vert = (ProjectVertex) obj;

		return this.mProject.equals(vert.mProject);
	}

	/*******************************************************
	 * 
	 * @see IArchitectureVertex#hashCode()
	 *******************************************************/
	@Override
	public int hashCode()
	{
		return mProject.hashCode();
	}
}
