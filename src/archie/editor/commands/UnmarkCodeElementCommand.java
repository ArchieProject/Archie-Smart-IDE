
package archie.editor.commands;

import org.eclipse.gef.commands.Command;

import archie.model.Tim;
import archie.model.shapes.CodeElement;
import archie.monitoring.MonitoringManager;

public class UnmarkCodeElementCommand extends Command
{
	CodeElement ce = null;
	Tim tim = null;

	public UnmarkCodeElementCommand()
	{
		super(UnmarkCodeElementAction.UNMARK);
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
		return ce.isMarked();
	}

	@Override
	public void execute()
	{
		// Unmark
		MonitoringManager.getIntance().unmarkAndClearWarnings(ce.getAssociatedPath());
	}

	@Override
	public void undo()
	{
		// Remark
		MonitoringManager.getIntance().markAndGenerateWarnings(ce.getAssociatedPath());
	}
}
