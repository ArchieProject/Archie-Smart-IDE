package archie.editor.commands;

import org.eclipse.gef.commands.Command;

import archie.model.shapes.Shape;


public class ShapeTextEditCommand extends Command {
	private String newText, oldText;
	private Shape shape;
	
	public ShapeTextEditCommand(Shape shape, String newText) {
		this.shape = shape;
		if(newText == null)
			this.newText = "";
		else
			this.newText = newText;
	}
	
	@Override
	public void execute() {
		oldText = shape.getText();
		shape.setText(newText);
	}
	
	@Override
	public void undo() {
		shape.setText(oldText);
	}
}
