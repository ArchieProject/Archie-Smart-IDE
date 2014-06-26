
package archie.editor.commands;

import org.eclipse.gef.commands.Command;

import archie.model.shapes.CodeElement;
import archie.model.shapes.Shape;
import archie.monitoring.MonitoringManager;

public class DeleteCodeElementCommand extends Command
{
	private final Shape container;
	private int oldIndex;
	private final CodeElement ce;
	private String oldSourcePath;

	private boolean mWasThereWarnings;

	public DeleteCodeElementCommand(Shape container, CodeElement ce)
	{
		if (container == null || ce == null)
			throw new IllegalArgumentException();
		this.container = container;
		this.ce = ce;
		setLabel("Delete Associated Code Element");
	}

	@Override
	public void execute()
	{
		// Delete any associated warnings (Must be done before the actual deletion)
		mWasThereWarnings = ce.isMarked();
		
		MonitoringManager.getIntance().unmarkAndClearWarnings(ce.getAssociatedPath());

		oldIndex = container.getCodeElements().indexOf(ce);
		container.removeCodeElement(ce);
		oldSourcePath = ce.getAssociatedPath();
	}

	@Override
	public void undo()
	{
		container.addCodeElement(oldIndex, ce);
		ce.setSourcePath(oldSourcePath);

		if (mWasThereWarnings)
		{
			// Must be done after the addition
			MonitoringManager.getIntance().markAndGenerateWarnings(ce.getAssociatedPath());
		}

	}
}
