package archie.editor.parts;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;

import archie.editor.commands.ConnectionEditCommand;
import archie.editor.figures.connections.ConnectionFigure;
import archie.model.connections.Connection;




public class ConnectionDirectEditPolicy extends DirectEditPolicy {

	@Override
	protected Command getDirectEditCommand(DirectEditRequest request) {
		String text = (String)request.getCellEditor().getValue();
		ConnectionEditPart editPart = (ConnectionEditPart)getHost();
		ConnectionEditCommand command = new ConnectionEditCommand( (Connection)editPart.getModel(), text);
		return command;
	}

	@Override
	protected void showCurrentEditValue(DirectEditRequest request) {
		String text = (String)request.getCellEditor().getValue();
		((ConnectionFigure)getHostFigure()).setText(text);
		// hack to prevent async layout from placing the cell editor twice.
		getHostFigure().getUpdateManager().performUpdate();
	}
}
