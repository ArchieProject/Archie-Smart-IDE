package archie.editor.parts;

import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;

import archie.editor.commands.*;
import archie.model.shapes.Shape;




public class ShapeWithCodeElementsMarkEditPolicy extends ComponentEditPolicy {
	private static final String ACCEPT_CHANGES_REQUEST = "Accept Changes";
	
	@Override
	public Command getCommand(Request request) {
		if (request.getType().equals(ACCEPT_CHANGES_REQUEST))
			return getAcceptChangesCommand();
		return super.getCommand(request);
	}
	
	private Command getAcceptChangesCommand() {
		UnmarkShapeCommand command = new UnmarkShapeCommand();
		command.setShape((Shape)getHost().getModel());
		return command;
	}
}
