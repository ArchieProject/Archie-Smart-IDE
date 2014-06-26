/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.monitoring;

import java.io.Serializable;

/*******************************************************
 * An interface for a monitored marked item for which 
 * warnings have been generated.
 *******************************************************/
interface IMarkedItem extends Serializable
{
	/*******************************************************
	 * Marks the element and generate warnings.
	 *******************************************************/
	public void mark();
	
	/*******************************************************
	 * Unmarks and clears the warnings on this element.
	 *******************************************************/
	public void unmark();
}
