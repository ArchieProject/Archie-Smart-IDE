package archie.editor.dialogs;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;



public class CodeElementPicker extends TitleAreaDialog {
	private IMember selectedElement;
	private IMember initialSelection;
	
	public CodeElementPicker(Shell parentShell) {
		super(parentShell);
	}
	
	public CodeElementPicker(Shell parentShell, IMember initialSelection) {
		super(parentShell);
		this.initialSelection = initialSelection;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Add Code Element");
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(600, 600);
	}
	
	@Override
	protected boolean isResizable() {
		return true;
	}
	
	@Override
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);
		setTitle("Add Code Element");
		return contents;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite frame = (Composite)super.createDialogArea(parent);
		
		Composite treeComposite = new Composite(frame, SWT.NONE);
		treeComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		treeComposite.setLayout(new FillLayout());
		createTree(treeComposite);
		
		return frame;
	}
	
	@Override
	protected Control createButtonBar(Composite parent) {
		return super.createButtonBar(parent);	// will auto-generate OK and Cancel buttons
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);	// automatically handles OK and Cancel buttons
	}
	
	private void setOkButtonEnabled(boolean enabled) {
		Button okButton = getButton(IDialogConstants.OK_ID);
		okButton.setEnabled(enabled);
	}
	
	private void createTree(Composite parent) {
		TreeViewer viewer = new TreeViewer(parent, SWT.SINGLE);
	    // Provide members of a compilation unit or class file
	    ITreeContentProvider contentProvider = new StandardJavaElementContentProvider(true);
	    viewer.setContentProvider(contentProvider);
	    // There are more flags defined in class JavaElementLabelProvider
	    ILabelProvider labelProvider = new JavaElementLabelProvider( JavaElementLabelProvider.SHOW_DEFAULT );
	    viewer.setLabelProvider(labelProvider);
	    // Using the Java model as the viewers input present Java projects on the first level.
	    viewer.setInput(JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()));
	    viewer.addFilter(new MyJavaElementFilter());
	    viewer.addSelectionChangedListener( new ISelectionChangedListener() {
			@Override 
			public void selectionChanged(SelectionChangedEvent event) {
				ITreeSelection selection = (ITreeSelection)event.getSelection();
				Object selectedObject = selection.getFirstElement();
				if (selectedObject instanceof IType || selectedObject instanceof IMethod) {
					setOkButtonEnabled(true);
					setMessage("You have selected a proper element", IMessageProvider.INFORMATION);
					setSelectedElement((IMember)selectedObject);
				} else {
					setOkButtonEnabled(false);
					setMessage("You have to pick a method or a class", IMessageProvider.ERROR);
					setSelectedElement(null);
				}
			}
		});
	    
	    if (initialSelection != null)
	    	viewer.reveal(initialSelection);
	}
	
	public IMember getSelectedElement() {
		return selectedElement;
	}
	
	private void setSelectedElement(IMember newSelectedElement) {
		selectedElement = newSelectedElement;
	}
}
