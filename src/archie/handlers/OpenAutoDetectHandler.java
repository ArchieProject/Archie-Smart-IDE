/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;

import archie.utils.EclipsePlatformUtils;
import archie.views.AutoDetectView;

/*******************************************************
 * Defines a class to handle the command that opens the new
 * UI for the automatic detection view.
 *******************************************************/
public final class OpenAutoDetectHandler implements IHandler
{

	/*******************************************************
	 * 
	 * @see org.eclipse.core.commands.IHandler#addHandlerListener(org.eclipse.core.commands.IHandlerListener)
	 *******************************************************/
	@Override
	public void addHandlerListener(IHandlerListener handlerListener)
	{
		// Nothing
	}

	/*******************************************************
	 * 
	 * @see org.eclipse.core.commands.IHandler#dispose()
	 *******************************************************/
	@Override
	public void dispose()
	{
		// Nothing
	}

	/*******************************************************
	 * Handles the associated command by opening the new automatic detection
	 * view.
	 * 
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 *******************************************************/
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		// Use the Utility static method that I wrote to handle
		// Opening views
		EclipsePlatformUtils.openView(AutoDetectView.VIEW_ID);

		// The return value is reserved for future use as per the documents
		// and it must be null
		return null;
	}

	/*******************************************************
	 * Returns true since this handler is enabled.
	 * 
	 * @see org.eclipse.core.commands.IHandler#isEnabled()
	 *******************************************************/
	@Override
	public boolean isEnabled()
	{
		return true;
	}

	/*******************************************************
	 * Returns true since is really capable of handling delegation.
	 * 
	 * @see org.eclipse.core.commands.IHandler#isHandled()
	 *******************************************************/
	@Override
	public boolean isHandled()
	{
		return true;
	}

	/*******************************************************
	 * 
	 * @see org.eclipse.core.commands.IHandler#removeHandlerListener(org.eclipse.core.commands.IHandlerListener)
	 *******************************************************/
	@Override
	public void removeHandlerListener(IHandlerListener handlerListener)
	{
		// Does nothing
	}

}
