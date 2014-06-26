package archie.editor.parts;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import archie.editor.commands.ShapeDeleteCommand;
import archie.model.*;
import archie.model.shapes.Shape;



public class ShapeComponentEditPolicy extends ComponentEditPolicy {
	@Override
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		Object parent = getHost().getParent().getModel();
		Object child = getHost().getModel();
		if(parent instanceof Tim && child instanceof Shape)
			return new ShapeDeleteCommand((Tim)parent, (Shape)child);
		return super.createDeleteCommand(deleteRequest);
	}
}
