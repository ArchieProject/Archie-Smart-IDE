/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.hierarchy.graph;

import archie.hierarchy.IArchitectureComponent;

/*******************************************************
 * Defines an abstract object to implement the common functionality between all
 * types of architecture vertices.
 * 
 * @author Ahmed Fakhry
 *******************************************************/
abstract class AbstractArchVertex implements IArchitectureVertex
{
	/*******************************************************
	 * The component linked to this vertex.
	 *******************************************************/
	protected final IArchitectureComponent mComponent;

	/*******************************************************
	 * Constructs an abstract vertex given the architecture component it
	 * represents.
	 * 
	 * @param component
	 *            The component represented by this vertex.
	 *******************************************************/
	public AbstractArchVertex(IArchitectureComponent component)
	{
		if (component == null)
			throw new IllegalArgumentException();

		mComponent = component;
	}

	/*******************************************************
	 * 
	 * @see archie.hierarchy.graph.IArchitectureVertex#handleDoubleClick()
	 *******************************************************/
	@Override
	public void handleDoubleClick()
	{
		// By default they do nothing, unless some derived class would like to
		// override this default behavior.
	}

	/*******************************************************
	 * 
	 * @see IArchitectureVertex#toString()
	 *******************************************************/
	@Override
	public String toString()
	{
		return mComponent.getName();
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

		AbstractArchVertex vert = (AbstractArchVertex) obj;

		return this.mComponent.equals(vert.mComponent);
	}

	/*******************************************************
	 * 
	 * @see IArchitectureVertex#hashCode()
	 *******************************************************/
	@Override
	public int hashCode()
	{
		return mComponent.hashCode();
	}
}
