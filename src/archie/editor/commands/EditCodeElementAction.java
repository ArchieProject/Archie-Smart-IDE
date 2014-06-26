
package archie.editor.commands;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;

import archie.editor.TimEditor;
import archie.editor.parts.CodeElementEditPart;
import archie.utils.EclipsePlatformUtils;

public class EditCodeElementAction extends SelectionAction
{
	public static final String EDIT_CODE_ELEMENT = "Edit Tracked Code";

	public EditCodeElementAction(IWorkbenchPart part)
	{
		super(part);
		// request = new Request(ADD_CODE_ELEMENT);
		setText(EDIT_CODE_ELEMENT);
		setId(EDIT_CODE_ELEMENT);
		setImageDescriptor(ImageDescriptor.createFromFile(TimEditor.class, "icons/edit.png"));
	}

	@Override
	protected boolean calculateEnabled()
	{
		if (getSelectedObjects().size() != 1)
			return false;
		Object o = getSelectedObjects().get(0);
		if (!(o instanceof CodeElementEditPart))
			return false;

		return true;
	}

	@Override
	public void run()
	{
		CodeElementEditPart part = (CodeElementEditPart) getSelectedObjects().get(0);
		String path = part.getModel().getAssociatedPath();
		EclipsePlatformUtils.openFileInDefaultEditor(path, true);
	}
}
