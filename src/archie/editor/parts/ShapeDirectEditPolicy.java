package archie.editor.parts;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;

import archie.editor.commands.ShapeTextEditCommand;
import archie.editor.figures.shapes.ShapeFigure;
import archie.model.shapes.Shape;




public class ShapeDirectEditPolicy extends DirectEditPolicy {

	@Override
	protected Command getDirectEditCommand(DirectEditRequest request) {
		String text = (String)request.getCellEditor().getValue();
		ShapeEditPart editPart = (ShapeEditPart)getHost();
		ShapeTextEditCommand command = new ShapeTextEditCommand( (Shape)editPart.getModel(), text);
		return command;
	}

	@Override
	protected void showCurrentEditValue(DirectEditRequest request) {
		String value = (String) request.getCellEditor().getValue();
		((ShapeFigure)getHostFigure()).setName(value);
		// hack to prevent async layout from placing the cell editor twice.
		getHostFigure().getUpdateManager().performUpdate();
	}

}
