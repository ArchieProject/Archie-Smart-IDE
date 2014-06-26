package archie.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;

import archie.globals.ArchieSettings;

public final class ToggleMonitoringHandler implements IHandler
{
	
	@Override
	public void addHandlerListener(IHandlerListener handlerListener)
	{
		// Nothing
	}

	@Override
	public void dispose()
	{
		// Nothing
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		// Toggle the monitoring flag
		ArchieSettings.MONITORING = !ArchieSettings.MONITORING;
		
		return null;
	}

	@Override
	public boolean isEnabled()
	{
		return true;
	}

	@Override
	public boolean isHandled()
	{
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener)
	{
		// Nothing
	}

}
