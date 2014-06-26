
package archie.editor.commands;

import org.eclipse.draw2d.geometry.*;
import org.eclipse.gef.commands.Command;

import archie.model.*;
import archie.model.shapes.Shape;

public class ShapeCreateCommand extends Command
{
	private Shape newShape;
	private final Tim parent;
	private Rectangle bounds;

	public ShapeCreateCommand(Shape newShape, Tim parent, Rectangle bounds)
	{
		this.newShape = newShape;
		this.parent = parent;
		if (bounds.width <= 0 && bounds.height <= 0)
			bounds = new Rectangle(bounds.x, bounds.y, 60, 40);
		this.bounds = bounds;
		setLabel("shape creation");
	}

	@Override
	public boolean canExecute()
	{
		return newShape != null && parent != null && bounds != null;
	}

	@Override
	public void execute()
	{
		newShape.setBounds(bounds);
		parent.addChild(newShape);
	}

	@Override
	public void undo()
	{
		parent.removeChild(newShape);
	}
}
