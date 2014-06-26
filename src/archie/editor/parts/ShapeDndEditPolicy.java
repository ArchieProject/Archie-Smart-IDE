package archie.editor.parts;

import org.eclipse.gef.*;
import org.eclipse.gef.commands.Command;

import archie.editor.commands.ShapeTextEditCommand;
import archie.editor.dnd.NativeDropRequest;
import archie.model.shapes.Shape;



public class ShapeDndEditPolicy extends ShapeComponentEditPolicy {
	
	public Command getCommand(Request request) {
		if (NativeDropRequest.ID.equals(request.getType()))
			return getDropTextCommand((NativeDropRequest) request);
		return super.getCommand(request);
	}

	protected Command getDropTextCommand(NativeDropRequest request) {
		ShapeTextEditCommand command = new ShapeTextEditCommand((Shape)getHost().getModel(), (String)request.getData());
		return command;
	}

	public EditPart getTargetEditPart(Request request) {
		if (NativeDropRequest.ID.equals(request.getType()))
			return getHost();
		return super.getTargetEditPart(request);
	}
}
