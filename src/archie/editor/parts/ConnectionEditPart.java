package archie.editor.parts;

import java.beans.*;

import org.eclipse.gef.*;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.*;
import org.eclipse.gef.editpolicies.*;
import org.eclipse.gef.requests.GroupRequest;

import archie.editor.commands.ConnectionDeleteCommand;
import archie.editor.figures.connections.ConnectionFigure;
import archie.model.connections.Connection;



public class ConnectionEditPart extends AbstractConnectionEditPart implements PropertyChangeListener {

	// ============ MODEL ============
	public Connection getModel() {
		return (Connection) super.getModel();
	}
	
	// =========== VISUALS ===========
	@Override
	public ConnectionFigure getFigure() {
		return (ConnectionFigure)super.getFigure();
	}
	
	@Override
	protected void refreshVisuals() {
		String text = getModel().getText();
		((ConnectionFigure)getFigure()).setText(text);
	}
	
	// =========== EVENTS ============
	@Override
	public void activate() {
		if(!isActive()) {
			super.activate();
			getModel().addPropertyChangeListener(this);
		}
	}
	
	@Override
	public void deactivate() {
		if(isActive()) {
			getModel().removePropertyChangeListener(this);
			super.deactivate();
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getPropertyName();
		if(property.equals(Connection.TEXT_PROP))
			refreshVisuals();
	}

	// ========= EDIT PARTS ==========
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new ConnectionEndpointEditPolicy());
		installEditPolicy(EditPolicy.CONNECTION_ROLE, 
				new ConnectionEditPolicy() {
					protected Command getDeleteCommand(GroupRequest request) {
						return new ConnectionDeleteCommand(getModel());
					}
				});
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new ConnectionDirectEditPolicy());
	}
	
	@Override
	public void performRequest(Request request) {
		if (request.getType() == RequestConstants.REQ_DIRECT_EDIT)
			new ConnectionEditManager(this, new ConnectionCellEditorLocator(getFigure())).show();
	}
}
