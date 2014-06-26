
package archie.editor.commands;

import org.eclipse.core.resources.IMarker;
import org.eclipse.gef.commands.Command;

import archie.model.Tim;
import archie.model.shapes.CodeElement;
import archie.monitoring.MonitoringManager;

public class MarkCodeElementCommand extends Command
{
	CodeElement ce = null;
	Tim tim = null;
	IMarker marker;

	public MarkCodeElementCommand()
	{
		super(MarkCodeElementAction.MARK);
	}

	public void setShape(CodeElement ce)
	{
		this.ce = ce;
	}

	public void setTim(Tim inTim)
	{
		this.tim = inTim;
	}

	@Override
	public boolean canExecute()
	{
		return !ce.isMarked();
	}

	@Override
	public void execute()
	{
		// Add marker
		MonitoringManager.getIntance().markAndGenerateWarnings(ce.getAssociatedPath());
	}

	@Override
	public void undo()
	{
		// Unmark
		MonitoringManager.getIntance().unmarkAndClearWarnings(ce.getAssociatedPath());
	}
}
