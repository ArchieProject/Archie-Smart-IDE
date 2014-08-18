/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.hierarchy;

import java.io.Serializable;
import java.util.Set;

/*******************************************************
 * Defines an interface for an architecture component which can be either a
 * goal, a sub-goal, or a tactic.
 *******************************************************/
interface IArchitectureComponent extends Serializable
{
	/*******************************************************
	 * @return The unique name of the component.
	 *******************************************************/
	public String getName();

	/*******************************************************
	 * @return The string name of the component type, such as "Goal",
	 *         "Sub-Goal", and "Tactic".
	 *******************************************************/
	public String getComponentType();

	/*******************************************************
	 * @return The list of items in the concrete component defined in the
	 *         system. For example, if the concrete component is a Goal, then
	 *         this method returns the list of goals defined in the system.
	 *******************************************************/
	public Set<String> getComponentList();
}
