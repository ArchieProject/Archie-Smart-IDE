/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.timstorage;

import java.io.File;

import archie.editor.commands.AddCodeElementAction;
import archie.utils.EclipsePlatformUtils;

/*******************************************************
 * A class that contains a static method to be used to link a file to a TIM.
 * 
 * @author Ahmed Fakhry
 *******************************************************/
public final class LinkToTim
{

	/*******************************************************
	 * Private non-instantiable class
	 *******************************************************/
	private LinkToTim()
	{
	}

	/*******************************************************
	 * Links the CompilationUnit whose absolute file path is given to the
	 * currently active element in the currently active TIM. It will handle all the
	 * exceptions (if any) and report errors as error dialogs.
	 * 
	 * @param filePath
	 *            The absolute file path of the CompilationUnit to link to the
	 *            TIM.
	 *******************************************************/
	public static void linkToActiveTIM(String filePath)
	{
		try
		{
			// Create the link
			AddCodeElementAction act = AddCodeElementAction.getInstance();
			act.createLink(new File(filePath));
		}
		catch (IllegalArgumentException e)
		{
			EclipsePlatformUtils.showErrorMessage("Error", "No item is selected to link!");
		}
		catch (NullPointerException e)
		{
			EclipsePlatformUtils.showErrorMessage("Error", "Could not find an active TIM editor!");
		}
		catch (ClassCastException e)
		{
			EclipsePlatformUtils.showErrorMessage("Error",
					"Could not find an active TIM editor or no node is highlighted!");
		}
		catch (IllegalStateException e)
		{
			EclipsePlatformUtils.showErrorMessage("Error",
					"You can't link a source file to a TIM in a different project!");
		}
	}

}
