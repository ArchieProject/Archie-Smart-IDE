package archie.editor.commands;

import org.eclipse.gef.commands.Command;

import archie.model.shapes.CodeElement;
import archie.model.shapes.Shape;



public class AddCodeElementCommand extends Command {
	private final Shape parent;
	private final CodeElement codeElement;
	
	public AddCodeElementCommand(Shape parent, CodeElement codeElement) {
		super("Add Code Element");
		this.parent = parent;
		this.codeElement = codeElement;
	}
	
	@Override
	public void execute() {
		parent.addCodeElement(codeElement);
	}
	
	@Override
	public void undo() {
		parent.removeCodeElement(codeElement);
	}
}
