package archie.editor.commands;

import org.eclipse.gef.commands.Command;

import archie.model.shapes.CodeElement;
import archie.model.shapes.Shape;



public class ReparentCodeElementCommand extends Command {
	private final Shape oldContainer;
	private int oldIndex;
	private final Shape newContainer;
	private int newIndex = 0;
	private final CodeElement ce;
	
	
	public ReparentCodeElementCommand(Shape oldContainer, Shape newContainer, CodeElement ce) {
		super("Move Code Element");
		this.oldContainer = oldContainer;
		this.newContainer = newContainer;
		this.ce = ce;
	}
	
	/**
	 * @param ce the CodeElement after which reparented CodeElement will be inserted
	 */
	public void putAfter(CodeElement ce) {
		newIndex = newContainer.getCodeElements().indexOf(ce) + 1;
	}
	
	@Override
	public void execute() {
		oldIndex = oldContainer.getCodeElements().indexOf(ce);
		oldContainer.removeCodeElement(ce);
		newContainer.addCodeElement(newIndex, ce);
	}
	
	@Override
	public void undo() {
		newContainer.removeCodeElement(ce);
		oldContainer.addCodeElement(oldIndex, ce);
	}
}
