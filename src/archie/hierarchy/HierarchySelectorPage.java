/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.hierarchy;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.swt.widgets.TreeItem;

import archie.utils.EclipsePlatformUtils;
import archie.views.autodetect.internals.SimpleImageRegistry;

/*******************************************************
 * Defines a class for the second and third windows of the hierarchy builder
 * wizard. It's flexible enough to be used for both Goals-to-SubGoals
 * relationships, and Sub-Goals-to-Tactics relationships.
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
	private final IComponentTypeBehavior mParentBehavior;
	private final IComponentTypeBehavior mChildBehavior;
	private final Runnable mOnNextHandler;
	private final Runnable mOnFinishHandler;

	// ----------------------------------------
	// Construction
	// ----------------------------------------


	/*******************************************************
	 * 
	 * @param pageNum
	 * @param pagesCount
	 * @param parentBehavior
	 * @param childBehavior
	 * @param onNext
	 * @param onFinish
	 *******************************************************/
	public HierarchySelectorPage(int pageNum, int pagesCount, IComponentTypeBehavior parentBehavior, IComponentTypeBehavior childBehavior,
			Runnable onNext, Runnable onFinish)
	{
		// Validate behaviors
		if (parentBehavior == null || childBehavior == null)
			throw new IllegalArgumentException();

		// Validate that at least one of onNext and onFinish is not null
		// (i.e. they can't be both null).
		if (onNext == null && onFinish == null)
			throw new IllegalArgumentException();

		// They also can't be both not null.
		if (onNext != null && onFinish != null)
			throw new IllegalArgumentException();

		mParentBehavior = parentBehavior;
		mChildBehavior = childBehavior;
		mOnNextHandler = onNext;
		mOnFinishHandler = onFinish;

		// Initialize the shell.
		mShell = new Shell(Display.getDefault(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		mShell.setLayout(new GridLayout(2, true));
		mShell.setText("System Architecture Components Wizard ("+ pageNum +" of " + pagesCount + ")");

		// Initialize the image registry.
		initImageRegistry();

		mShell.setImage(mImageRegistry.getImage("archIcon"));

		// Build the UI
		buildControls(pageNum, pagesCount);

		// Register the event handlers for selecting an item from the combo box
		// or double clicking on an item from the children list.
		registerSelectionHandlers();

		// Register the buttons handlers.
		registerButtonsHandlers();

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
		mImageRegistry.registerImagePath(Goal.GOAL_BEHAVIOR.getComponentType(), "/resources/icons/goalIcon.png");
		mImageRegistry.registerImagePath(SubGoal.SUB_GOAL_BEHAVIOR.getComponentType(),
				"/resources/icons/subGoalIcon.png");
		mImageRegistry.registerImagePath(Tactic.TACTIC_BEHAVIOR.getComponentType(), "/resources/icons/timeditor.png");
	}

	/*******************************************************
	 * Building the UI control of this window.
	 *******************************************************/
	private void buildControls(int pageNum, int pagesCount)
	{
		String parentType = mParentBehavior.getComponentType();
		String childType = mChildBehavior.getComponentType();

		// The instructions label.
		Label label = new Label(mShell, SWT.NONE);
		label.setText("(Step " + pageNum + " of "+ pagesCount +"): Define the " + parentType + "s to " + childType + "s hierarchy relationships.");
		GridData gData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gData.horizontalSpan = 2;
		label.setLayoutData(gData);

		// The parent component group
		Group parentGroup = new Group(mShell, SWT.NONE);
		gData = new GridData(SWT.FILL, SWT.FILL, false, true);
		parentGroup.setLayoutData(gData);
		parentGroup.setText(parentType + "s Selector");
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
		childrenGroup.setText(childType + "s List");
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

		if (mOnNextHandler == null)
			mNextButton.setEnabled(false);
		else if (mOnFinishHandler == null)
			mFinishButton.setEnabled(false);
	}

	/*******************************************************
	 * Register the event handlers for selecting an item from the combo box or
	 * double clicking on an item from the children list.
	 *******************************************************/
	private void registerSelectionHandlers()
	{
		// The selection listener for the combo box.
		mParentCombo.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// First clear the tree.
				mTree.removeAll();

				String selection = mParentCombo.getText();
				IParentArchitectureComponent parentComponent = (IParentArchitectureComponent) mParentBehavior
						.getComponent(selection);

				if (parentComponent != null)
				{
					// Fill the tree based on the selection.
					TreeItem root = new TreeItem(mTree, SWT.NONE);
					root.setText(selection);
					root.setImage(mImageRegistry.getImage(mParentBehavior.getComponentType()));
					root.setData(parentComponent);

					for (IChildArchitectureComponent child : parentComponent)
					{
						TreeItem childItem = new TreeItem(root, SWT.NONE);
						childItem.setText(child.getName());
						childItem.setImage(mImageRegistry.getImage(mChildBehavior.getComponentType()));
						childItem.setData(child);
					}

					// Make the item expanded.
					root.setExpanded(true);
				}
			}
		});

		// The double clicking on a child element in the children list.
		mChildrenList.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDoubleClick(MouseEvent e)
			{
				String[] selections = mChildrenList.getSelection();

				// Get the selected parent
				String parent = mParentCombo.getText();
				int parentIndex = mParentCombo.getSelectionIndex();
				IParentArchitectureComponent parentComponent = (IParentArchitectureComponent) mParentBehavior
						.getComponent(parent);

				if (parentComponent == null)
					return;

				for (String sel : selections)
				{
					IChildArchitectureComponent child = (IChildArchitectureComponent) mChildBehavior.getComponent(sel);

					if (!parentComponent.addChild(child))
					{
						// The child already exists on the parent's list
						EclipsePlatformUtils.showErrorMessage("Error", "Child: " + child.getName()
								+ " is already added to the parent: " + parentComponent.getName());
					}
				}

				// Refresh the tree, re-fire the selection event of the
				// combo-box
				selectParent(parentIndex);
			}
		});

		// Removing a hierarchy connection by double clicking on a child item in
		// the tree.
		mTree.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDoubleClick(MouseEvent e)
			{
				// Only if double clicking on a child component, we remove that
				// component.
				TreeItem[] items = mTree.getSelection();

				// Get the active parent
				String parent = mParentCombo.getText();
				int index = mParentCombo.getSelectionIndex();
				IParentArchitectureComponent parentComponent = (IParentArchitectureComponent) mParentBehavior
						.getComponent(parent);

				for (TreeItem item : items)
				{
					if (item.getData() instanceof IChildArchitectureComponent)
					{
						parentComponent.removeChild((IChildArchitectureComponent) item.getData());
					}
				}

				// Refresh
				selectParent(index);
			}
		});
	}

	/*******************************************************
	 * Registers the next and finish buttons handlers.
	 *******************************************************/
	private void registerButtonsHandlers()
	{
		// The "Next >>" Button.
		mNextButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e)
			{
				// First fire the external runnable.
				if (mOnNextHandler != null)
				{
					mOnNextHandler.run();
				}

				// In all cases we dispose this window.
				mShell.dispose();
			}
		});

		// The same thing for the "Finish" button.
		mFinishButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e)
			{
				// The external runnable for finish
				if (mOnFinishHandler != null)
				{
					mOnFinishHandler.run();
				}

				// We dispose this window
				mShell.dispose();
			}
		});
	}

	/*******************************************************
	 * Fills the combo box with the parent items, and the children list with the
	 * children items from the system's architecture component manager.
	 *******************************************************/
	private void fillLists()
	{
		// Fill in the combo box.
		for (String parent : mParentBehavior.getComponentList())
		{
			mParentCombo.add(parent);
		}

		// Set selection to the first item
		selectParent(0);

		// Fill the list of children.
		for (String child : mChildBehavior.getComponentList())
		{
			mChildrenList.add(child);
		}
	}

	/*******************************************************
	 * Selects a parent component from the parents combo box and fires the
	 * selection event.
	 * 
	 * @param index
	 *            The index of the parent to select.
	 *******************************************************/
	private void selectParent(int index)
	{
		mParentCombo.select(index);
		mParentCombo.notifyListeners(SWT.Selection, null);
	}
}
