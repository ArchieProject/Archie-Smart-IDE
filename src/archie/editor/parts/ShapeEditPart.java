
package archie.editor.parts;

import java.beans.*;
import java.util.List;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.*;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.*;
import org.eclipse.gef.requests.*;
import org.eclipse.swt.widgets.Display;

import archie.editor.commands.*;
import archie.editor.figures.shapes.ShapeFigure;
import archie.model.connections.Connection;
import archie.model.shapes.CodeElement;
import archie.model.shapes.Shape;

public abstract class ShapeEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener, NodeEditPart
{

	// =========== MODEL ============
	@Override
	public Shape getModel()
	{
		return (Shape) super.getModel();
	}

	@Override
	protected List<?> getModelChildren()
	{
		return getModel().getCodeElements();
	}

	@Override
	protected List<Connection> getModelSourceConnections()
	{
		return getModel().getSourceConnections();
	}

	@Override
	protected List<Connection> getModelTargetConnections()
	{
		return getModel().getTargetConnections();
	}

	// =========== VISUALS ===========
	@Override
	public ShapeFigure getFigure()
	{
		return (ShapeFigure) super.getFigure();
	}

	@Override
	public IFigure getContentPane()
	{
		return getFigure().getCodeElementsContainer();
	}

	@Override
	protected void refreshVisuals()
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			
			@Override
			public void run()
			{
				GraphicalEditPart parentEditPart = (GraphicalEditPart) getParent();
				ShapeFigure figure = getFigure();
				
				String text = getModel().getText();
				figure.setName(text);
				Rectangle bounds = getModel().getBounds();
				parentEditPart.setLayoutConstraint(ShapeEditPart.this, figure, bounds);
				figure.setMarked(getModel().isMarked());
				figure.repaint();
			}
		});
		
		
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection)
	{
		return getFigure().getConnectionAnchor();
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection)
	{
		return getFigure().getConnectionAnchor();
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(Request request)
	{
		return getFigure().getConnectionAnchor();
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request request)
	{
		return getFigure().getConnectionAnchor();
	}

	// ============ EVENTS ============
	@Override
	public void activate()
	{
		if (!isActive())
		{
			super.activate();
			getModel().addPropertyChangeListener(this);
		}
	}

	@Override
	public void deactivate()
	{
		if (isActive())
		{
			getModel().removePropertyChangeListener(this);
			super.deactivate();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		String property = event.getPropertyName();
		if (property.equals(Shape.BOUNDS_PROP) || property.equals(Shape.TEXT_PROP)
				|| property.equals(Shape.MARKED_PROP))
			refreshVisuals();
		else if (property.equals(Shape.SOURCE_CONNECTIONS_PROP))
			refreshSourceConnections();
		else if (property.equals(Shape.TARGET_CONNECTIONS_PROP))
			refreshTargetConnections();
		else if (property.equals(Shape.CODE_ELEMENTS_PROP))
		{
			if (event.getNewValue() == null)
			{ // removed
				Object part = getViewer().getEditPartRegistry().get(event.getOldValue());
				if (part instanceof EditPart)
					removeChild((EditPart) part);
				refreshVisuals();
			}
			else
			{
				addChild(createChild((CodeElement) event.getNewValue()), (int) event.getOldValue());
				refreshVisuals();
			}
		}
	}

	// =========== EDIT POLICIES ===========
	@Override
	protected void createEditPolicies()
	{
		// delete command
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ShapeComponentEditPolicy());

		// edit name
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new ShapeDirectEditPolicy());

		// connections
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new GraphicalNodeEditPolicy()
		{
			@Override
			protected Command getConnectionCreateCommand(CreateConnectionRequest request)
			{
				Shape source = (Shape) getHost().getModel();
				ConnectionCreateCommand command = new ConnectionCreateCommand(source, (Class<?>) request
						.getNewObjectType());
				request.setStartCommand(command);
				return command;
			}

			@Override
			protected Command getConnectionCompleteCommand(CreateConnectionRequest request)
			{
				ConnectionCreateCommand command = (ConnectionCreateCommand) request.getStartCommand();
				command.setTarget((Shape) getHost().getModel());
				return command;
			}

			@Override
			protected Command getReconnectSourceCommand(ReconnectRequest request)
			{
				Connection connection = (Connection) request.getConnectionEditPart().getModel();
				Shape newSource = (Shape) getHost().getModel();
				ConnectionReconnectCommand command = new ConnectionReconnectCommand(connection);
				command.setNewSource(newSource);
				return command;
			}

			@Override
			protected Command getReconnectTargetCommand(ReconnectRequest request)
			{
				Connection connection = (Connection) request.getConnectionEditPart().getModel();
				Shape newTarget = (Shape) getHost().getModel();
				ConnectionReconnectCommand command = new ConnectionReconnectCommand(connection);
				command.setNewTarget(newTarget);
				return command;
			}
		});

		// mark & unmark commands
		installEditPolicy("Mark and Unmark", new ShapeWithCodeElementsMarkEditPolicy());

		// code elements
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new OrderedLayoutEditPolicy()
		{

			/**
			 * Returns CreateCodeElementCommand that creates a CodeElement
			 * inside this Shape
			 */
			@Override
			protected Command getCreateCommand(CreateRequest request)
			{
				if (request.getNewObjectType() == CodeElement.class)
				{
					CodeElement ce = (CodeElement) request.getNewObject();
					return new AddCodeElementCommand(getModel(), ce);
				}
				return null;
			}

			/**
			 * Invoked when a CodeElement is dragged from one Shape to another.
			 * Returns null when no changes are required.
			 */
			@Override
			protected Command createAddCommand(EditPart child, EditPart after)
			{
				if (!(child instanceof CodeElementEditPart))
					return null;
				Shape oldContainer = (Shape) child.getParent().getModel();
				if (getModel() == oldContainer) // if moving to the same Shape,
												// return null
					return null;
				CodeElement ce = (CodeElement) child.getModel();
				ReparentCodeElementCommand cmd = new ReparentCodeElementCommand(oldContainer, getModel(), ce);
				if (after != null)
					cmd.putAfter((CodeElement) after.getModel());
				return cmd;
			}

			/** Invoked when a CodeElements is dragged within the same Shape */
			@Override
			protected Command createMoveChildCommand(EditPart child, EditPart after)
			{
				if (child == after || getChildren().size() == 1)
					return null;
				int index = getChildren().indexOf(child);
				if (index == 0)
				{
					if (after == null)
						return null;
				}
				else
				{
					if (after == getChildren().get(index - 1))
						return null;
				}
				ReorderCodeElementCommand cmd = new ReorderCodeElementCommand(getModel(), (CodeElement) child
						.getModel());
				if (after != null)
					cmd.putAfter((CodeElement) after.getModel());
				return cmd;
			}

			/**
			 * Invoked when CodeElement is reparented or reordered Returns the
			 * insertion point
			 */
			@Override
			protected EditPart getInsertionReference(Request request)
			{
				int y = ((ChangeBoundsRequest) request).getLocation().y;
				List<?> codeElements = getChildren();
				CodeElementEditPart afterPart = null;
				for (Object o : codeElements)
				{
					CodeElementEditPart part = (CodeElementEditPart) o;
					Rectangle bounds = part.getFigure().getBounds();
					if (y < bounds.y)
						return afterPart;
					afterPart = part;
				}
				return afterPart;
			}
		});
	}

	@Override
	public void performRequest(Request request)
	{
		if (request.getType() == RequestConstants.REQ_DIRECT_EDIT)
			new ShapeEditManager(this, new ShapeCellEditorLocator(getFigure())).show();
	}

}
