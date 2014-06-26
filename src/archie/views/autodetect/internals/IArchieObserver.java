/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.views.autodetect.internals;

/*******************************************************
 * Defines an interface for any object that would like
 * to be notified of any changes in the TimsManager, so that
 * they can ask the TimsManager for the updated lists of TIMs
 *******************************************************/
public interface IArchieObserver
{
	/*******************************************************
	 * Called by the TimsManager to notify the observer that
	 * something has changed in the managed list of TIMs.
	 *******************************************************/
	public void notifyMeWithTimsChange();
	
	/*******************************************************
	 * Called by the {@link AcceptedListManager} to notify its
	 * observers that the accepted list has changed.
	 *******************************************************/
	public void notifyMeWithAcceptedListChange();
	
	/*******************************************************
	 * Called by the {@link JavaProjectsListener} to notify its
	 * observers that the list of java projects may have changed.
	 *******************************************************/
	public void notifyMeWithJavaProjectsChange();
}
