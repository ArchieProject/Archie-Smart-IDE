/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.hierarchy;

import java.io.Serializable;

/*******************************************************
 * Defines an interface for an architecture component which
 * can be either a goal, a sub-goal, or a tactic.
 *******************************************************/
interface IArchitectureComponent extends Serializable
{
	/*******************************************************
	 * @return The unique name of the component.
	 *******************************************************/
	public String getName();
}
