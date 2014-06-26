
package archie.editor.commands;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import archie.Activator;
import archie.editor.TimEditor;
import archie.editor.dialogs.CodeElementPicker;
import archie.model.Tim;
import archie.model.shapes.Attribute;
import archie.model.shapes.CodeElement;
import archie.model.shapes.Component;
import archie.model.shapes.Message;
import archie.model.shapes.Parameter;
import archie.model.shapes.Shape;

public class AddCodeElementAction extends SelectionAction
{
	public static final String ADD_CODE_ELEMENT = "Add Tracked Code";
	private static AddCodeElementAction instance;

	public AddCodeElementAction(IWorkbenchPart part)
	{
		super(part);
		setText(ADD_CODE_ELEMENT);
		setId(ADD_CODE_ELEMENT);
		setImageDescriptor(ImageDescriptor.createFromFile(TimEditor.class, "icons/plus.png"));
		instance = this;
	}

	public static AddCodeElementAction getInstance()
	{
		return instance;
	}

	@Override
	protected boolean calculateEnabled()
	{
		if (getSelectedObjects().size() != 1)
			return false;
		Object o = getSelectedObjects().get(0);
		if (!(o instanceof EditPart))
			return false;
		EditPart part = (EditPart) o;
		if (!(part.getModel() instanceof Shape))
			return false;

		return true;
	}

	@Override
	public void run()
	{
		Shape container = (Shape) ((EditPart) getSelectedObjects().get(0)).getModel();

		if (container instanceof Attribute || container instanceof Component || container instanceof Message
				|| container instanceof Parameter)
		{
			Shell shell = Activator.getShell();

			CodeElementPicker picker = new CodeElementPicker(shell);
			if (picker.open() == CodeElementPicker.OK)
			{
				IMember element = picker.getSelectedElement();
				// System.out.println(element.toString());
				CodeElement ce = new CodeElement(element);
				AddCodeElementCommand cmd = new AddCodeElementCommand(container, ce);
				execute(cmd);
			}
		}
		else
		{
			MessageDialog
					.openInformation(null, "Information",
							"Code element attachement is only allowed for:\nAttribute, Component, Message, and Parameter nodes");
			System.out.println("no code element allowed");
		}

		// IJavaProject project =
		// JavaCore.create(editor.getTim().getAssociatedFile().getProject());
		// CodeElementValidator validator = new CodeElementValidator(project);
		// InputDialog inputDialog = new InputDialog( shell,
		// "", //dialog title
		// "Please specify a fully qualified method/class name:", //dialog
		// prompt
		// "", //default text
		// validator ); //validator to use
		//
		// if (inputDialog.open() == InputDialog.OK) {
		// CodeElement ce = new CodeElement(container, inputDialog.getValue(),
		// validator.getCodeElementType(), validator.getHash());
		// AddCodeElementCommand cmd = new AddCodeElementCommand(container, ce);
		// execute(cmd);
		// }
	}

	public void run2(String path, Shape shape)
	{
		// Shape container =
		// (Shape)((EditPart)getSelectedObjects().get(0)).getModel();
		Shape container = shape;

		File file = new File(path);

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPath location = Path.fromOSString(file.getAbsolutePath());
		IFile myFile = workspace.getRoot().getFile(location);

		ICompilationUnit unit = JavaCore.createCompilationUnitFrom(myFile);
		// IMember element = null;

		if (unit != null)
		{
			CodeElement ce = new CodeElement(unit);
			AddCodeElementCommand cmd = new AddCodeElementCommand(container, ce);
			execute(cmd);
		}
		else
		{
			MessageDialog
					.openInformation(
							null,
							"Error",
							"Failed to find the source to link to the node. Please make sure that the source file is in the scr folder of the project or is include in the build path.");
		}
	}

	public Boolean createLink(File file)
	{
		Boolean passed = true;
		Tim tim = ((TimEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor())
				.getTim();
		this.setSelection(tim.getEditor().getSite().getSelectionProvider().getSelection());// changes
																							// the
																							// selection
																							// to
																							// the
																							// current
																							// view
																							// tim
																							// editor
		
		// Make sure that the project path is a substring of the file path
		// otherwise you are trying to link a file to a TIM in a different project
		String projPath = tim.getAssociatedFile().getProject().getLocation().toFile().getAbsolutePath();
		String filePath = file.getAbsolutePath();
		
		if(! filePath.contains(projPath) )
		{
			throw new IllegalStateException();
		}
		
		Shape container = (Shape) ((EditPart) getSelectedObjects().get(0)).getModel();
		// container.setText(file.getName());

		if (container instanceof Attribute || container instanceof Component || container instanceof Message
				|| container instanceof Parameter)
		{
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IPath location = Path.fromOSString(file.getAbsolutePath());
			IFile myFile = workspace.getRoot().getFileForLocation(location);

			ICompilationUnit unit = JavaCore.createCompilationUnitFrom(myFile);
			// IMember element = null;

			if (unit != null)
			{
				CodeElement ce = new CodeElement(unit);
				AddCodeElementCommand cmd = new AddCodeElementCommand(container, ce);
				execute(cmd);
			}
			else
			{
				passed = false;
				MessageDialog
						.openInformation(
								null,
								"Error",
								"Failed to find the source to link to the node. Please make sure that the source file is in the scr folder of the project or is include in the build path.");
			}
		}
		else
		{
			passed = false;
			MessageDialog
					.openInformation(null, "Information",
							"Code element attachement is only allowed for:\nAttribute, Component, Message, and Parameter nodes");
			System.out.println("code elemenet not allowed");
		}
		return passed;
	}

}
