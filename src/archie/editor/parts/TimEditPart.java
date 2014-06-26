
package archie.editor.parts;

import java.beans.*;
import java.util.*;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.*;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.*;
import org.eclipse.gef.requests.*;

import archie.editor.commands.*;
import archie.model.*;
import archie.model.shapes.Shape;

public class TimEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener
{
	/*******************************************************
	 * This is the unique id of this editor, so that it can be used to open and
	 * query this editor from anywhere.
	 * 
	 * @note Remember to change it if you happen to change the id of this editor
	 *       in the MANIFEST.MF file.
	 *******************************************************/
	public static final String TIM_EDITOR_ID = new String("archie.editors.TimEditor");

	// ============ MODEL ===========
	@Override
	public Tim getModel()
	{
		return (Tim) super.getModel();
	}

	@Override
	protected List<Shape> getModelChildren()
	{
		return getModel().getChildren();
	}

	// =========== VISUALS ===========
	@Override
	protected IFigure createFigure()
	{
		Figure figure = new FreeformLayer();
		figure.setLayoutManager(new FreeformLayout());

		ConnectionLayer connectionLayer = (ConnectionLayer) getLayer(LayerConstants.CONNECTION_LAYER);
		connectionLayer.setConnectionRouter(new ShortestPathConnectionRouter(figure));

		return figure;
	}

	// ============ EVENTS ===========
	@Override
	public void activate()
	{
		if (!isActive())
		{
			super.activate();
			getModel().addPropertyChangeListener(this);
			// TimView timView = TimView.getInstance();
			// timView.addTimModel(getModel());
		}
	}

	@Override
	public void deactivate()
	{
		if (isActive())
		{
			getModel().removePropertyChangeListener(this);
			// TimView timView = TimView.getInstance();
			// timView.refreshDataOnCloseNoSave(getModel());
			// TimViewEventsHandler.refreshDataOnClosedNoSave(getModel());
			super.deactivate();

		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		String prop = event.getPropertyName();
		if (Tim.CHILDREN_PROP.equals(prop))
			refreshChildren();
	}

	// =========== EDIT POLICIES ===========
	@Override
	protected void createEditPolicies()
	{
		// disallows the removal of this edit part from its parent
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
		// handles moving,resizing,creation of model elements
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new TimXYLayoutEditPolicy());
	}

	private static class TimXYLayoutEditPolicy extends XYLayoutEditPolicy
	{
		@Override
		protected Command createChangeConstraintCommand(ChangeBoundsRequest request, EditPart child, Object constraint)
		{
			return new ShapeSetConstraintCommand((Shape) child.getModel(), (Rectangle) constraint);
		}

		@Override
		protected Command getCreateCommand(CreateRequest request)
		{
			Object o = request.getNewObject();
			if (o instanceof Shape)
				return new ShapeCreateCommand((Shape) request.getNewObject(), (Tim) getHost().getModel(),
						(Rectangle) getConstraintFor(request));
			return null;
		}
	}
}
