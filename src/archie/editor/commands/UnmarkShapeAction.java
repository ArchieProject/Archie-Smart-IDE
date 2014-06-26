
package archie.editor.commands;

import java.util.*;

import org.eclipse.gef.*;
import org.eclipse.gef.commands.*;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;

import archie.editor.TimEditor;
import archie.model.shapes.Shape;

public class UnmarkShapeAction extends SelectionAction
{
	private static final String ACCEPT_CHANGES_REQUEST = "Accept Changes";
	public static final String ACCEPT_CHANGES = "Accept Changes";

	private Request request;

	public UnmarkShapeAction(IWorkbenchPart part)
	{
		super(part);
		request = new Request(ACCEPT_CHANGES_REQUEST);
		setText(ACCEPT_CHANGES);
		setId(ACCEPT_CHANGES);
		setToolTipText("Accept changes commited to code that affect architectural patterns");
		setImageDescriptor(ImageDescriptor.createFromFile(TimEditor.class, "icons/accept16.png"));
	}

	@Override
	protected boolean calculateEnabled()
	{
		if (getSelectedObjects().isEmpty())
			return false;
		List<?> selectedObjects = getSelectedObjects();
		return !getShapesWithCodeElements(selectedObjects).isEmpty();
	}

	@Override
	public void run()
	{
		List<?> selectedObjects = getSelectedObjects();
		CompoundCommand cc = new CompoundCommand();
		cc.setDebugLabel(ACCEPT_CHANGES);
		for (EditPart part : getEditPartsOfShapesWithCodeElements(selectedObjects))
		{
			Command cmd = part.getCommand(request);
			if (cmd.canExecute())
				cc.add(cmd);
		}
		execute(cc);
	}

	private List<Shape> getShapesWithCodeElements(List<?> selectedObjects)
	{
		List<Shape> result = new ArrayList<>();
		for (Object o : selectedObjects)
		{
			if (o instanceof EditPart)
			{
				EditPart part = (EditPart) o;
				if (part.getModel() instanceof Shape)
					result.add((Shape) part.getModel());
			}
		}
		return result;
	}

	private List<EditPart> getEditPartsOfShapesWithCodeElements(List<?> selectedObjects)
	{
		List<EditPart> result = new ArrayList<>();
		for (Object o : selectedObjects)
		{
			if (o instanceof EditPart)
			{
				EditPart part = (EditPart) o;
				if (part.getModel() instanceof Shape)
					result.add(part);
			}
		}
		return result;
	}
}
