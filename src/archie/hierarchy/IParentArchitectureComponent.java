/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.hierarchy;

import java.util.Iterator;

/*******************************************************
 * Defines an interface for components that can have children, such as goals and
 * sub-goals.
 *******************************************************/
interface IParentArchitectureComponent extends IArchitectureComponent, Iterable<IChildArchitectureComponent>
{
	/*******************************************************
	 * Adds a child component to this component in the archie.hierarchy.
	 * 
	 * @param child
	 *            The component to be added as a child.
	 *******************************************************/
	/*******************************************************
	 * Adds a child component to this component in the archie.hierarchy.
	 * 
	 * @param child
	 *            The component to be added as a child.
	 * 
	 * @return true if the child was not already on the list (and successfully
	 *         added), false otherwise.
	 *******************************************************/
	public boolean addChild(IChildArchitectureComponent child);

	/*******************************************************
	 * Removes the given component from the list of children.
	 * 
	 * @param child
	 *            The child to be removed.
	 *******************************************************/
	public void removeChild(IChildArchitectureComponent child);

	/*******************************************************
	 * @return An iterator over the children of this component.
	 *******************************************************/
	public Iterator<IChildArchitectureComponent> iterator();

	/*******************************************************
	 * Removes all children in this component.
	 *******************************************************/
	public void clearChildren();
}
