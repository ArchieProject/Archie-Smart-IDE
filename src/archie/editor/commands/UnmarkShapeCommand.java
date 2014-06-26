
package archie.editor.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.Command;

import archie.model.shapes.CodeElement;
import archie.model.shapes.Shape;
import archie.monitoring.MonitoringManager;

public class UnmarkShapeCommand extends Command
{
	Shape shape = null;
	List<CodeElement> markedCodeElements = new ArrayList<>();

	public UnmarkShapeCommand()
	{
		super("Accept Changes");
	}

	public void setShape(Shape shape)
	{
		this.shape = shape;
	}

	@Override
	public boolean canExecute()
	{
		return shape.isMarked();
	}

	@Override
	public void execute()
	{
		for (CodeElement e : shape.getCodeElements())
		{
			if (e.isMarked())
			{
				markedCodeElements.add(e);

				// Unmark
				MonitoringManager.getIntance().unmarkAndClearWarnings(e.getAssociatedPath());
			}
		}
	}

	@Override
	public void undo()
	{
		for (CodeElement e : markedCodeElements)
		{
			if (!e.isMarked())
			{
				// Remark
				MonitoringManager.getIntance().markAndGenerateWarnings(e.getAssociatedPath());
			}
		}

		markedCodeElements.clear();
	}
}
