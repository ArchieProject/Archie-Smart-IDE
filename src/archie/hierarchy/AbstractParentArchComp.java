/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.hierarchy;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/*******************************************************
 * An abstract parent software architecture component. It contains the common
 * code for the components that can be parents.
 *******************************************************/
abstract class AbstractParentArchComp implements IParentArchitectureComponent
{
	/*******************************************************
	 * For Serialization.
	 *******************************************************/
	private static final long serialVersionUID = -8895527846156195788L;

	/*******************************************************
	 * The unique name of the goal.
	 *******************************************************/
	protected final String mName;

	/*******************************************************
	 * The sorted list of children.
	 *******************************************************/
	private final Set<IChildArchitectureComponent> mChildren = new TreeSet<IChildArchitectureComponent>();

	/*******************************************************
	 * Constructs an abstract parent architecture component.
	 * 
	 * @param name
	 *            The unique name of the component.
	 *******************************************************/
	AbstractParentArchComp(String name)
	{
		if (name == null || name.isEmpty())
			throw new IllegalArgumentException();

		mName = name;
	}

	/*******************************************************
	 * 
	 * @see archie.hierarchy.IArchitectureComponent#getName()
	 *******************************************************/
	@Override
	public String getName()
	{
		return mName;
	}

	/*******************************************************
	 * 
	 * @see archie.hierarchy.IParentArchitectureComponent#addChild(archie.hierarchy.IChildArchitectureComponent)
	 *******************************************************/
	@Override
	public void addChild(IChildArchitectureComponent child)
	{
		mChildren.add(child);
	}

	/*******************************************************
	 * 
	 * @see archie.hierarchy.IParentArchitectureComponent#removeChild(archie.hierarchy.IChildArchitectureComponent)
	 *******************************************************/
	@Override
	public void removeChild(IChildArchitectureComponent child)
	{
		mChildren.remove(child);
	}

	/*******************************************************
	 * 
	 * @see archie.hierarchy.IParentArchitectureComponent#iterator()
	 *******************************************************/
	@Override
	public Iterator<IChildArchitectureComponent> iterator()
	{
		return Collections.unmodifiableCollection(mChildren).iterator();
	}

	/*******************************************************
	 * 
	 * @see archie.hierarchy.IParentArchitectureComponent#clearChildren()
	 *******************************************************/
	@Override
	public void clearChildren()
	{
		mChildren.clear();
	}

	/*******************************************************
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 *******************************************************/
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		// Must the same class.
		if (obj.getClass() != this.getClass())
			return false;
		
		AbstractParentArchComp other = (AbstractParentArchComp) obj;
		
		return mName.equals(other.mName);
	}
	
	/*******************************************************
	 * 
	 * @see java.lang.Object#hashCode()
	 *******************************************************/
	@Override
	public int hashCode()
	{
		return mName.hashCode();
	}
}
