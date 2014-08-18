/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.hierarchy;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

import archie.views.autodetect.internals.SimpleImageRegistry;

/*******************************************************
 * 
 * @author Ahmed Fakhry
 *******************************************************/
final class HierarchySelectorPage
{
	// ----------------------------------------
	// Fields
	// ----------------------------------------

	private Shell mShell;
	private SimpleImageRegistry mImageRegistry;
	private Button mNextButton;
	private Button mFinishButton;
	private Combo mParentCombo;
	private Tree mTree;
	private List mChildrenList;
	private final String mParentType;
	private final String mChildType;
	private final Runnable mOnNextHandler;
	private final Runnable mOnFinishHandler;

	// ----------------------------------------
	// Construction
	// ----------------------------------------

	public HierarchySelectorPage(String parentType, String childType, Runnable onNext, Runnable onFinish)
	{
		// Validate types
		if(parentType == null || childType == null)
			throw new IllegalArgumentException();
		
		if(parentType.isEmpty() || childType.isEmpty())
			throw new IllegalArgumentException();
		
		// Validate that at least one of onNext and onFinish is not null
		// (i.e. they can't be both null).
		if (onNext == null && onFinish == null)
			throw new IllegalArgumentException();

		// They also can't be both not null.
		if (onNext != null && onFinish != null)
			throw new IllegalArgumentException();

		mParentType = parentType;
		mChildType = childType;
		mOnNextHandler = onNext;
		mOnFinishHandler = onFinish;

		// Initialize the shell.
		mShell = new Shell(Display.getDefault(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		mShell.setLayout(new GridLayout(2, true));
		mShell.setText("System Architecture Components Wizard (2 of 3)");

		// Initialize the image registry.
		initImageRegistry();

		mShell.setImage(mImageRegistry.getImage("archIcon"));

		// Build the UI
		buildControls();

		// Fill list with initial contents.
		fillLists();
		
		// Open the shell
		mShell.open();
	}

	// ----------------------------------------
	// Private Methods.
	// ----------------------------------------

	/*******************************************************
	 * Initializes the image registry.
	 *******************************************************/
	private void initImageRegistry()
	{
		// The shell must have already been initialized.
		if (mShell == null)
			throw new IllegalStateException();

		mImageRegistry = new SimpleImageRegistry(mShell);

		mImageRegistry.registerImagePath("archIcon", "/resources/icons/architecture.png");
	}

	private void buildControls()
	{
		// The instructions label.
		Label label = new Label(mShell, SWT.NONE);
		label.setText("(Step 2 of 3): Define the " + mParentType + " to " + mChildType + " hierarchy relationships.");
		GridData gData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gData.horizontalSpan = 2;
		label.setLayoutData(gData);
		
		// The parent component group
		Group parentGroup = new Group(mShell, SWT.NONE);
		gData = new GridData(SWT.FILL, SWT.FILL, false, true);
		parentGroup.setLayoutData(gData);
		parentGroup.setText(mParentType + " Selector");
		parentGroup.setLayout(new GridLayout(1, true));
		
		// The combo box for the parent component list.
		mParentCombo = new Combo(parentGroup, SWT.NONE);
		gData = new GridData(SWT.FILL, SWT.FILL, true, false);
		mParentCombo.setLayoutData(gData);
		
		// The tree of parent to children relationships.
		mTree = new Tree(parentGroup, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		gData = new GridData(SWT.FILL, SWT.FILL, true, true);
		mTree.setLayoutData(gData);
		
		// The children list group
		Group childrenGroup = new Group(mShell, SWT.NONE);
		gData = new GridData(SWT.FILL, SWT.FILL, false, true);
		childrenGroup.setLayoutData(gData);
		childrenGroup.setText(mChildType + " List");
		childrenGroup.setLayout(new GridLayout(1, true));
		
		// The list of available children
		mChildrenList = new List(childrenGroup, SWT.V_SCROLL | SWT.H_SCROLL);
		gData = new GridData(SWT.FILL, SWT.FILL, true, true);
		mChildrenList.setLayoutData(gData);
		
		// The next and finish buttons
		mNextButton = new Button(mShell, SWT.NONE);
		gData = new GridData(SWT.FILL, SWT.FILL, true, false);
		mNextButton.setLayoutData(gData);
		mNextButton.setText("Next >>");
		
		mFinishButton = new Button(mShell, SWT.NONE);
		gData = new GridData(SWT.FILL, SWT.FILL, true, false);
		mFinishButton.setLayoutData(gData);
		mFinishButton.setText("Finish");
		
		if(mOnNextHandler == null)
			mNextButton.setEnabled(false);
		else if(mOnFinishHandler == null)
			mFinishButton.setEnabled(false); 
	}

	private void fillLists()
	{

	}
}
