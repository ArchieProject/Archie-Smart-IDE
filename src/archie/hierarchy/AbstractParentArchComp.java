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
abstract class AbstractParentArchComp extends AbstractArchitectureComponent implements IParentArchitectureComponent
{
	/*******************************************************
	 * For Serialization.
	 *******************************************************/
	private static final long serialVersionUID = -8895527846156195788L;

	/*******************************************************
	 * The sorted list of children.
	 *******************************************************/
	protected final Set<IChildArchitectureComponent> mChildren = new TreeSet<IChildArchitectureComponent>();

	/*******************************************************
	 * Constructs an abstract parent architecture component.
	 * 
	 * @param name
	 *            The unique name of the component.
	 *******************************************************/
	AbstractParentArchComp(String name)
	{
		super(name);
	}

	/*******************************************************
	 * 
	 * @see archie.hierarchy.IParentArchitectureComponent#addChild(archie.hierarchy.IChildArchitectureComponent)
	 *******************************************************/
	@Override
	public boolean addChild(IChildArchitectureComponent child)
	{
		return mChildren.add(child);
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
}
