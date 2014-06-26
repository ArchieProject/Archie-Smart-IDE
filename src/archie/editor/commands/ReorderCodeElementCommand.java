package archie.editor.commands;

import org.eclipse.gef.commands.Command;

import archie.model.shapes.CodeElement;
import archie.model.shapes.Shape;



public class ReorderCodeElementCommand extends Command {
	private final Shape container;
	private final CodeElement ce;
	private int newIndex;
	private int oldIndex;
	
	
	public ReorderCodeElementCommand(Shape newContainer, CodeElement ce) {
		super("Change order of a CodeElement");
		this.container = newContainer;
		this.ce = ce;
	}
	
	public void putAfter(CodeElement ce) {
		newIndex = container.getCodeElements().indexOf(ce)+1;
	}
	
	@Override
	public void execute() {
		oldIndex = container.getCodeElements().indexOf(ce);
		container.removeCodeElement(ce);
		container.addCodeElement(newIndex <= oldIndex ? newIndex : newIndex-1, ce);
		System.out.println("adding to position: " + (newIndex <= oldIndex ? newIndex : newIndex-1));
	}
	
	@Override
	public void undo() {
		container.removeCodeElement(ce);
		container.addCodeElement(oldIndex <= newIndex ? oldIndex : oldIndex-1, ce);
	}
}
