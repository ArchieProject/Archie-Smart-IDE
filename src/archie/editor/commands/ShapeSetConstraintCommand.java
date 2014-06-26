package archie.editor.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import archie.model.shapes.Shape;



public class ShapeSetConstraintCommand extends Command {
	private final Shape shape;
	private final Rectangle newBounds;
	private Rectangle oldBounds;
	
	public ShapeSetConstraintCommand(Shape shape, Rectangle newBounds) {
		if(shape == null || newBounds == null)
			throw new IllegalArgumentException();
		this.shape = shape;
		this.newBounds = newBounds;
		setLabel("Move or Resize");
	}
	
	@Override
	public void execute() {
		oldBounds = shape.getBounds();
		shape.setBounds(newBounds);
	}
	
	@Override
	public void undo() {
		shape.setBounds(oldBounds);
	}
}
