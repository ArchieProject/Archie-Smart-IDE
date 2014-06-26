
package archie.editor.parts;

import java.beans.*;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.*;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.*;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.swt.widgets.Display;

import archie.editor.commands.*;
import archie.editor.figures.shapes.CodeElementFigure;
import archie.model.shapes.CodeElement;
import archie.model.shapes.Shape;

public class CodeElementEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener
{

	// =========== MODEL ===========
	@Override
	public CodeElement getModel()
	{
		return (CodeElement) super.getModel();
	}

	// ========== VISUALS ==========
	@Override
	protected IFigure createFigure()
	{
		return new CodeElementFigure(getModel().getDisplayName());
	}

	@Override
	public CodeElementFigure getFigure()
	{
		return (CodeElementFigure) super.getFigure();
	}

	@Override
	protected void refreshVisuals()
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			
			@Override
			public void run()
			{
				getFigure().setJavaElementType(getModel().getJavaElementType());
				getFigure().setText(getModel().getDisplayName());
				getFigure().setMarked(getModel().isMarked());
				getFigure().repaint();
			}
		});
	}

	// =========== EVENTS ==========
	@Override
	public void activate()
	{
		if (!isActive())
		{
			super.activate();
			((CodeElement) getModel()).addPropertyChangeListener(this);
		}
	}

	@Override
	public void deactivate()
	{
		if (isActive())
		{
			((CodeElement) getModel()).removePropertyChangeListener(this);
			super.deactivate();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		String property = event.getPropertyName();
		if (property.equals(CodeElement.JAVA_ELEMENT_TYPE_PROP) || property.equals(CodeElement.QUALIFIED_NAME_PROP)
				|| property.equals(CodeElement.MARKED_PROP))
			refreshVisuals();
	}

	// ======= EDIT POLICIES =======
	@Override
	protected void createEditPolicies()
	{
		// mark & unmark commands
		installEditPolicy("Mark and Unmark", new CodeElementMarkEditPolicy());

		// delete command
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy()
		{
			@Override
			protected Command createDeleteCommand(GroupRequest deleteRequest)
			{
				Object container = getHost().getParent().getModel();
				Object ce = getHost().getModel();
				if (container instanceof Shape && ce instanceof CodeElement)
					return new DeleteCodeElementCommand((Shape) container, (CodeElement) ce);
				return super.createDeleteCommand(deleteRequest);
			}
		});
	}

}
