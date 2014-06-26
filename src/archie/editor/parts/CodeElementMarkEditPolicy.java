package archie.editor.parts;

import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;

import archie.editor.commands.*;
import archie.model.shapes.CodeElement;




public class CodeElementMarkEditPolicy extends ComponentEditPolicy{
		
	@Override
	public Command getCommand(Request request) {
		if (request.getType().equals(MarkCodeElementAction.MARK))
			return getMarkCommand();
		if (request.getType().equals(UnmarkCodeElementAction.UNMARK))
			return getAcceptChangesCommand();
		return super.getCommand(request);
	}
	
	private Command getMarkCommand() {
		MarkCodeElementCommand command = new MarkCodeElementCommand();
		command.setShape((CodeElement)getHost().getModel());
		return command;
	}
	
	private Command getAcceptChangesCommand() {
		UnmarkCodeElementCommand command = new UnmarkCodeElementCommand();
		command.setShape((CodeElement)getHost().getModel());
		return command;
	}
}
