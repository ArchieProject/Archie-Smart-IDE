/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.hierarchy;

import java.util.Set;

/*******************************************************
 * Defines an interface for getting the behavior of each architecture component
 * type, i.e. it's name, how to get the list of its elements, and how to get an
 * element of this type given its string name.
 * 
 * @author Ahmed Fakhry
 *******************************************************/
interface IComponentTypeBehavior
{
	/*******************************************************
	 * Gets the actual item based on its name.
	 * 
	 * @param name
	 *            The name of the item to get its corresponding component.
	 * 
	 * @return The corresponding component of the given name.
	 *******************************************************/
	public IArchitectureComponent getComponent(String name);

	/*******************************************************
	 * @return The list of items in the system listed under this component.
	 *******************************************************/
	public Set<String> getComponentList();

	/*******************************************************
	 * @return The name of the component type, i.e. Goal, Sub Goal, or Tactic.
	 *******************************************************/
	public String getComponentType();
}
