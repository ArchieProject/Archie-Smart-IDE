/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.views.autodetect.internals;

/*******************************************************
 * Defines an interface for a runnable command 
 * (The Command Pattern) to update the progress bar
 * when scanning
 *******************************************************/
public interface IProgressCommand
{
	public void run(double progress);
}
