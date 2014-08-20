/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.views;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;

import archie.globals.ArchieSettings;
import archie.hierarchy.ArchitectureHierarchyWizard;
import archie.model.Tim;
import archie.monitoring.MonitoringManager;
import archie.timstorage.LinkToTim;
import archie.timstorage.TimsManager;
import archie.utils.EclipsePlatformUtils;
import archie.views.autodetect.internals.AcceptedListManager;
import archie.views.autodetect.internals.FileQualityScanner;
import archie.views.autodetect.internals.IArchieObserver;
import archie.views.autodetect.internals.IProgressCommand;
import archie.views.autodetect.internals.ITreeItem;
import archie.views.autodetect.internals.JavaProjectsListener;
import archie.views.autodetect.internals.SimpleImageRegistry;
import archie.views.autodetect.internals.TreeContentProvider;
import archie.views.autodetect.internals.TreeFileItem;
import archie.views.autodetect.internals.TreeLabelProvider;
import archie.views.autodetect.internals.TreeQualityItem;
import archie.wizards.newtim.NewTimWizard;
import archie.wizards.newtim.TimTemplateSaver;

/*******************************************************
 * Defines the class for the Automatic Detection View
 *******************************************************/
public final class AutoDetectView extends ViewPart implements IArchieObserver
{
	/*******************************************************
	 * The ID of the view as specified by the extension.
	 *******************************************************/
	public static final String VIEW_ID = "archie.views.autoDetect";

	// -- Image registry
	private SimpleImageRegistry mImageRegistry;

	// -- The Font registry
	private FontRegistry mFontRegistry;

	private LinkedList<ITreeItem> mResults = new LinkedList<ITreeItem>();

	// -- Controls
	private TreeViewer mResultsTree;
	private TreeViewer mAcceptedListTree;
	private TreeViewer mCurrentSelectedTree;

	private Button mScanButton;
	private Slider mThresholdSlider;
	private Label mThresholdValueLabel;
	private Combo mRawResultsFilter;
	private Combo mAcceptedResultsFilter;

	private Combo mJavaProjectsCombo;

	private ProgressBar mProgressBar;
	private Button mAddNewTIMButton;
	private Button mRemoveTIMsButton;
	private Button mSaveTIMAsTemplateButton;
	private Button mArchitectureButton;
	private List mTIMsList;

	// -- Menu Actions
	private DrillDownAdapter mDrillDownAdapter;
	private Action mPreviewFileAction;
	private Action mLinkToTimAction;
	private Action mAcceptChangeAction;

	/*******************************************************
	 * Constructor
	 *******************************************************/
	public AutoDetectView()
	{
	}

	/*******************************************************
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 *******************************************************/
	public void createPartControl(Composite parent)
	{
		// The image resource management code:
		this.mImageRegistry = new SimpleImageRegistry(parent);

		// The font registry
		this.mFontRegistry = JFaceResources.getFontRegistry();

		// Registering images that will be used
		this.mImageRegistry.registerImagePath("timIcon", "/resources/icons/timeditor.png");

		this.mImageRegistry.registerImagePath("searchIcon", "/resources/icons/search.png");

		this.mImageRegistry.registerImagePath("addIcon", "/resources/icons/addIcon.png");

		this.mImageRegistry.registerImagePath("deleteIcon", "/resources/icons/deleteIcon.png");

		this.mImageRegistry.registerImagePath("fileWarningIcon", "/resources/icons/file-warning.png");

		this.mImageRegistry.registerImagePath("acceptIcon", "/resources/icons/accept.png");

		this.mImageRegistry.registerImagePath("templateIcon", "/resources/icons/template.png");

		this.mImageRegistry.registerImagePath("archIcon", "/resources/icons/architecture.png");

		// ---------------------------------------------------------
		// The sash vertical splitter sash container (two composites are laid
		// out side-by-side horizontally)
		SashForm verticalSash = new SashForm(parent, SWT.HORIZONTAL);
		verticalSash.setLayout(new FillLayout());

		// Left Side
		Composite rootLeft = new Composite(verticalSash, SWT.NONE);
		rootLeft.setLayout(new FillLayout());

		// Right side has another sash ... the splitter is horizontal, hence the
		// child
		// composites are laid out vertically
		SashForm horizontalSash = new SashForm(verticalSash, SWT.VERTICAL);
		horizontalSash.setLayout(new FillLayout());

		// Right Side + Up -- For results
		Composite rootRightUp = new Composite(horizontalSash, SWT.NONE);
		rootRightUp.setLayout(new FillLayout());

		// Right Side + Down -- For accepted list
		Composite rootRightDown = new Composite(horizontalSash, SWT.NONE);
		rootRightDown.setLayout(new FillLayout());

		// Create the upper right side results group
		this.createResultsComposite(rootRightUp);

		// Create the lower right side accepted list group
		this.createAcceptedListComposite(rootRightDown);

		// Create the left side for the scan configuration
		this.createScanConfigComposite(rootLeft);

		// Register the combo box of the projects list event handler.
		this.registerProjectsComboEventHandler();

		// Register the event handlers for the scan and browse buttons
		this.registerButtonsEventsHandlers();

		// Register the event handlers for the threshold slider
		this.registerThresholdEventsHandlers();

		// Register the event handlers for the classification filter combo boxes
		this.registerClassFilterEvenetsHandlers();

		// Register double click handlers for the tree views (results and
		// accepted)
		this.registerDoubleClickEventsHandlers();

		// Register the event handlers for the TIM management group
		this.registerTimManagementGroupHandlers();

		// Fill the TIMs management list with the available TIMs in the TIM
		// manager
		this.fillTimManagementList();

		// Initialize menu actions
		this.initMenuActions();

		// Hook the context menu
		this.hookContextMenus();

		// Register yourself as a TIMs observer
		TimsManager.getInstance().registerTimsObserver(this);

		// Register yourself as an Accepted List observer
		AcceptedListManager.getInstance().registerObserver(this);

		// Register yourself as a listener for projects change
		JavaProjectsListener.getInstance().registerObserver(this);
	}

	/*******************************************************
	 * Passing the focus request to the viewer's control.
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 *******************************************************/
	public void setFocus()
	{
		mResultsTree.getControl().setFocus();
	}

	/*******************************************************
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 *******************************************************/
	@Override
	public void dispose()
	{
		// Must unsubscribe from the TimsManager
		TimsManager.getInstance().removeTimsObserver(this);

		// Must unsubscribe from the Accepted List Manager
		AcceptedListManager.getInstance().registerObserver(this);

		// Serialize the accepted results if any
		AcceptedListManager.getInstance().saveToDatabase();

		// Unregister yourself as a listener for projects change
		JavaProjectsListener.getInstance().removeObserver(this);

		super.dispose();
	}

	/*******************************************************
	 * 
	 * @see archie.views.autodetect.internals.IArchieObserver#notifyMeWithTimsChange()
	 *******************************************************/
	@Override
	public void notifyMeWithTimsChange()
	{
		fillTimManagementList();
	}

	/*******************************************************
	 * 
	 * @see archie.views.autodetect.internals.IArchieObserver#notifyMeWithJavaProjectsChange()
	 *******************************************************/
	@Override
	public void notifyMeWithJavaProjectsChange()
	{
		updateProjectsCombo();
	}

	/*******************************************************
	 * 
	 * @see archie.views.autodetect.internals.IArchieObserver#notifyMeWithAcceptedListChange()
	 *******************************************************/
	@Override
	public void notifyMeWithAcceptedListChange()
	{
		refreshAcceptedTree();
	}

	// --------------------------------------------------------------------------------------
	// Private Methods
	// --------------------------------------------------------------------------------------

	/*******************************************************
	 * Creates the results group of the view
	 * 
	 * @param parent
	 *            The parent composite of this group
	 *******************************************************/
	private void createResultsComposite(Composite parent)
	{
		// The results group
		Group resultsGroup = new Group(parent, SWT.SHADOW_ETCHED_OUT);
		resultsGroup.setText("Results");
		resultsGroup.setLayout(new GridLayout(1, true));
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		resultsGroup.setLayoutData(gridData);

		// A composite for the filters
		Composite filterComposite = new Composite(resultsGroup, SWT.NONE);
		filterComposite.setLayout(new GridLayout(2, true));
		filterComposite.setLayoutData(gridData);

		// The label for the classification filter
		Label labelClassFilter = new Label(filterComposite, SWT.NONE);
		labelClassFilter.setText("Classification Filter");

		// The drop down for the classification filter
		mRawResultsFilter = new Combo(filterComposite, SWT.READ_ONLY);
		mRawResultsFilter.setLayoutData(gridData);

		// Composite for the results tree view
		Composite resultsListComposite = new Composite(resultsGroup, SWT.NONE);
		resultsListComposite.setLayout(new GridLayout(1, true));

		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;

		resultsListComposite.setLayoutData(gridData);

		mResultsTree = new TreeViewer(resultsListComposite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		mResultsTree.setLabelProvider(new DelegatingStyledCellLabelProvider(new TreeLabelProvider(this.mImageRegistry,
				this.mFontRegistry)));
		mResultsTree.setContentProvider(new TreeContentProvider());
		mResultsTree.setInput(new Object[0]);

		mResultsTree.getControl().setLayoutData(gridData);

		// Set the drill down adapter of the results tree
		mDrillDownAdapter = new DrillDownAdapter(mResultsTree);
	}

	/*******************************************************
	 * Creates the accepted list group
	 * 
	 * @param parent
	 *            The parent composite of this group
	 *******************************************************/
	private void createAcceptedListComposite(Composite parent)
	{
		Group acceptedListGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		acceptedListGroup.setText("Accepted Results");
		acceptedListGroup.setLayout(new GridLayout(1, true));
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		acceptedListGroup.setLayoutData(gridData);

		// A composite for the filters
		Composite filterComposite = new Composite(acceptedListGroup, SWT.NONE);
		filterComposite.setLayout(new GridLayout(2, true));
		filterComposite.setLayoutData(gridData);

		// The label for the classification filter
		Label labelClassFilter = new Label(filterComposite, SWT.NONE);
		labelClassFilter.setText("Classification Filter");

		// The drop down for the classification filter
		mAcceptedResultsFilter = new Combo(filterComposite, SWT.READ_ONLY);
		mAcceptedResultsFilter.setLayoutData(gridData);

		// Composite for the accepted results tree view
		Composite resultsListComposite = new Composite(acceptedListGroup, SWT.NONE);
		resultsListComposite.setLayout(new GridLayout(1, true));

		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);

		resultsListComposite.setLayoutData(gridData);

		// Another tree view for the accepted list
		mAcceptedListTree = new TreeViewer(resultsListComposite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		mAcceptedListTree.setLabelProvider(new DelegatingStyledCellLabelProvider(new TreeLabelProvider(mImageRegistry,
				mFontRegistry)));
		mAcceptedListTree.setContentProvider(new TreeContentProvider());
		mAcceptedListTree.getControl().setLayoutData(gridData);

		refreshAcceptedTree();
	}

	/*******************************************************
	 * Creates the scan configuration group of the view
	 * 
	 * @param parent
	 *            The parent composite of this group
	 *******************************************************/
	private void createScanConfigComposite(Composite parent)
	{
		Composite leftSideStack = new Composite(parent, SWT.NONE);
		leftSideStack.setLayout(new GridLayout(1, false));
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		leftSideStack.setLayoutData(gridData);

		// -------------------------------------
		// 1 - The Auto Detect Group
		// -------------------------------------
		Group scanConfigGroup = new Group(leftSideStack, SWT.NONE);
		scanConfigGroup.setLayout(new GridLayout(3, false));
		scanConfigGroup.setText("Automatic Detection Configuration");
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		scanConfigGroup.setLayoutData(gridData);

		// The "Scan Directory" label
		Label scanDirLabel = new Label(scanConfigGroup, SWT.NONE);
		scanDirLabel.setText("Scan Open Java Project:");
		gridData = new GridData(GridData.FILL, GridData.FILL, false, false);
		scanDirLabel.setLayoutData(gridData);

		// The projects combo box
		mJavaProjectsCombo = new Combo(scanConfigGroup, SWT.READ_ONLY);
		mJavaProjectsCombo.setToolTipText("Select one of the Java projects available in the workspace");
		gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData.horizontalSpan = 2;
		mJavaProjectsCombo.setLayoutData(gridData);

		updateProjectsCombo();

		// The threshold label
		Label thresholdLabel = new Label(scanConfigGroup, SWT.NONE);
		thresholdLabel.setText("Classification Threshold:");
		gridData = new GridData(GridData.FILL, GridData.CENTER, false, false);
		thresholdLabel.setLayoutData(gridData);

		// The threshold slider
		mThresholdSlider = new Slider(scanConfigGroup, SWT.HORIZONTAL);
		mThresholdSlider.setMinimum(10);
		mThresholdSlider.setMaximum(110);
		mThresholdSlider.setPageIncrement(10);
		mThresholdSlider.setThumb(10);
		mThresholdSlider.setSelection(20);
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		mThresholdSlider.setLayoutData(gridData);

		// The threshold value Label
		mThresholdValueLabel = new Label(scanConfigGroup, SWT.NONE);
		mThresholdValueLabel.setText("0.2");
		gridData = new GridData(GridData.CENTER, GridData.FILL, false, false);
		mThresholdValueLabel.setLayoutData(gridData);

		// The scan button
		mScanButton = new Button(scanConfigGroup, SWT.BOLD);
		mScanButton.setText("Scan");
		mScanButton.setImage(this.mImageRegistry.getImage("searchIcon"));
		gridData = new GridData(GridData.FILL, GridData.FILL, false, false);
		gridData.horizontalSpan = 3;
		mScanButton.setLayoutData(gridData);
		mScanButton.setToolTipText("Scan the chosen directory with the chosen threshold.");

		// The initially invisible progress bar
		mProgressBar = new ProgressBar(scanConfigGroup, SWT.NONE);
		mProgressBar.setMaximum(100);
		mProgressBar.setMinimum(0);
		mProgressBar.setSelection(0);
		gridData = new GridData(GridData.FILL, GridData.FILL, false, false);
		gridData.horizontalSpan = 3;
		mProgressBar.setLayoutData(gridData);
		mProgressBar.setVisible(false);

		// -------------------------------------
		// 2 - The TIM management
		// -------------------------------------
		Group timManagementGroup = new Group(leftSideStack, SWT.NONE);
		timManagementGroup.setText("TIM Management");
		timManagementGroup.setLayout(new GridLayout(4, true));
		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		timManagementGroup.setLayoutData(gridData);

		// Button to add a new TIM
		mAddNewTIMButton = new Button(timManagementGroup, SWT.NONE);
		mAddNewTIMButton.setImage(mImageRegistry.getImage("addIcon"));
		gridData = new GridData(GridData.FILL, GridData.FILL, false, false);
		mAddNewTIMButton.setLayoutData(gridData);
		mAddNewTIMButton.setToolTipText("Add a new TIM file.");

		// Button to remove the selected TIMs
		mRemoveTIMsButton = new Button(timManagementGroup, SWT.NONE);
		mRemoveTIMsButton.setImage(mImageRegistry.getImage("deleteIcon"));
		gridData = new GridData(GridData.FILL, GridData.FILL, false, false);
		mRemoveTIMsButton.setLayoutData(gridData);
		mRemoveTIMsButton.setToolTipText("Delete the selected TIM files.");

		// Button to save a TIM as a template
		mSaveTIMAsTemplateButton = new Button(timManagementGroup, SWT.NONE);
		mSaveTIMAsTemplateButton.setImage(mImageRegistry.getImage("templateIcon"));
		gridData = new GridData(GridData.FILL, GridData.FILL, false, false);
		mSaveTIMAsTemplateButton.setLayoutData(gridData);
		mSaveTIMAsTemplateButton.setToolTipText("Save the selected TIM as a user template file.");

		// The architecture visualization button.
		mArchitectureButton = new Button(timManagementGroup, SWT.NONE);
		mArchitectureButton.setImage(mImageRegistry.getImage("archIcon"));
		gridData = new GridData(GridData.FILL, GridData.FILL, false, false);
		mArchitectureButton.setLayoutData(gridData);
		mArchitectureButton
				.setToolTipText("Define System architecture componenets, their hierarchies, and visualize your architecture.");

		// The list of TIMs
		mTIMsList = new List(timManagementGroup, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 4;
		mTIMsList.setLayoutData(gridData);
	}

	/*******************************************************
	 * Updates the list of open java projects in the combo box.
	 *******************************************************/
	private void updateProjectsCombo()
	{
		// Must be done on the UI thread as it's going to be called from other
		// threads
		if (mJavaProjectsCombo != null && !mJavaProjectsCombo.isDisposed())
		{
			mJavaProjectsCombo.getDisplay().asyncExec(new Runnable()
			{
				public void run()
				{
					if (mJavaProjectsCombo != null && !mJavaProjectsCombo.isDisposed())
					{
						// Clear the old list if any
						mJavaProjectsCombo.removeAll();

						// Fill the combo with the list of java projects
						mJavaProjectsCombo.add("Select a Java project ...");
						Set<String> projects = JavaProjectsListener.getInstance().getProjectsNames();
						for (String javaProj : projects)
						{
							mJavaProjectsCombo.add(javaProj);
						}

						mJavaProjectsCombo.select(0);
					}
				}
			});
		}

	}

	/*******************************************************
	 * Registers the event handler of the projects list combo box
	 *******************************************************/
	private void registerProjectsComboEventHandler()
	{

	}

	/*******************************************************
	 * Registers the mouse click event handlers for the scan and browse buttons
	 *******************************************************/
	private void registerButtonsEventsHandlers()
	{
		// Register the mouse handler for the scan button
		// All it does is it fills the results list with mock data
		mScanButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e)
			{
				// Get the currently selected java project
				int sel = mJavaProjectsCombo.getSelectionIndex();
				if (sel < 1)
				{
					// No java project is selected, display error
					EclipsePlatformUtils.showErrorMessage("Error", "No Java project is selected.");
				}
				else
				{
					String javaProjName = mJavaProjectsCombo.getItem(sel);
					final IJavaProject javaProj = JavaProjectsListener.getInstance().getJavaProject(javaProjName);

					// Clear the lists
					mResults.clear();

					// Initialize the progress bar
					mProgressBar.setSelection(0);
					mProgressBar.setVisible(true);

					// Define the command to update the progress bar
					final IProgressCommand onProgressCommand = new IProgressCommand()
					{

						@Override
						public void run(final double progress)
						{
							if (mProgressBar != null && !mProgressBar.isDisposed())
							{
								mProgressBar.getDisplay().asyncExec(new Runnable()
								{
									public void run()
									{
										mProgressBar.setSelection((int) (progress * 100));
										mProgressBar.redraw();
										mProgressBar.update();
									}
								});
							}
						}
					};

					// Clear the raw results tree
					mResultsTree.setInput(new Object[0]);

					// Start the scan

					final String path = new File(javaProj.getProject().getLocationURI()).getAbsolutePath();

					final double threshold = Double.parseDouble(mThresholdValueLabel.getText());

					// Run on a different thread than that of the UI
					new Thread("Scanner")
					{
						@Override
						public void run()
						{
							mResults = FileQualityScanner.scanDirectory(path, threshold, onProgressCommand);

							// Refresh the trees and the combo box
							refreshRawResultsTrees();

							// Hide the progress bar when done
							hideProgressBar();
						}
					}.start();

				}
			}
		});
	}

	/*******************************************************
	 * Registers the events handlers for the threshold slider
	 *******************************************************/
	private void registerThresholdEventsHandlers()
	{
		mThresholdSlider.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// Get the selected value and set it in the
				// threshold label
				int val = mThresholdSlider.getSelection();
				// The value of the label is a percentage
				// Whereas the one in the slider is an int from 10 to 100
				float realVal = (float) val / 100;
				mThresholdValueLabel.setText(String.format("%.1f", realVal));
			}
		});
	}

	/*******************************************************
	 * Registers the classification filter combo box events handlers
	 *******************************************************/
	private void registerClassFilterEvenetsHandlers()
	{
		// Selection Listener for the two different filters
		class FilterSelectionListener extends SelectionAdapter
		{
			private final Combo mFilter;
			private final TreeViewer mTree;

			/*******************************************************
			 * Construct the listener with the appropriate controls.
			 * 
			 * @param filter
			 *            The combo box to which this filter will be attached.
			 * @param tree
			 *            The TreeViewer that will be updated based on the
			 *            selection of the filter.
			 *******************************************************/
			public FilterSelectionListener(Combo filter, TreeViewer tree)
			{
				if (filter == null || tree == null)
				{
					throw new IllegalArgumentException();
				}

				mFilter = filter;
				mTree = tree;
			}

			/*******************************************************
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 *******************************************************/
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// Get the current selected text
				String selected = mFilter.getText();

				// This text could be empty, "All", or one of the classification
				if (selected.isEmpty())
				{
					// Clear selection
					mTree.setInput(new Object[0]);
				}
				else if (selected.equals("All"))
				{
					// Display all results
					if (mTree == mResultsTree)
					{
						refreshRawResultsTrees();
					}
					else if (mTree == mAcceptedListTree)
					{
						refreshAcceptedTree();
					}
				}
				else
				{
					// Determine which one to display
					if (mTree == mResultsTree)
					{
						filterRawResults(selected);
					}
					else if (mTree == mAcceptedListTree)
					{
						filterAcceptedResults(selected);
					}
				}
			}

			/*******************************************************
			 * Determines which item to filter based on the selection.
			 * 
			 * @param selected
			 *            The selected item.
			 *******************************************************/
			private void filterRawResults(String selected)
			{
				for (ITreeItem item : mResults)
				{
					if (item.getName().equals(selected))
					{
						mTree.setInput(new ITreeItem[] { item });
					}
				}
			}

			private void filterAcceptedResults(String selected)
			{
				TreeQualityItem item = AcceptedListManager.getInstance().getQualityItem(selected);

				if (item != null)
				{
					mTree.setInput(new TreeQualityItem[] { item });
				}
			}
		}

		// Register it for the raw results filter
		mRawResultsFilter.addSelectionListener(new FilterSelectionListener(mRawResultsFilter, mResultsTree));

		// Register it for the accepted results filter.
		mAcceptedResultsFilter.addSelectionListener(new FilterSelectionListener(mAcceptedResultsFilter,
				mAcceptedListTree));

	}

	/*******************************************************
	 * Register the double clicks events handlers for the results and accepted
	 * files tree views
	 *******************************************************/
	private void registerDoubleClickEventsHandlers()
	{
		// The double click event handler for the raw results tree.
		// +++ ACCEPTING RESULTS +++
		mResultsTree.addDoubleClickListener(new IDoubleClickListener()
		{
			@Override
			public void doubleClick(DoubleClickEvent event)
			{
				// Get the item that was double clicked
				ISelection selection = mResultsTree.getSelection();
				Object selObj = ((IStructuredSelection) selection).getFirstElement();

				// Is it one of the classifications?
				if (selObj instanceof TreeQualityItem)
				{
					TreeQualityItem selQuality = (TreeQualityItem) selObj;

					// Add it to the accepted list.
					AcceptedListManager.getInstance().acceptQualityItem(selQuality);

					// Remove it from the raw results.
					mResults.remove((TreeQualityItem) selObj);
				}
				else if (selObj instanceof TreeFileItem)
				{
					// It's a file item.

					TreeFileItem item = (TreeFileItem) selObj;
					TreeQualityItem parent = (TreeQualityItem) item.getParent();

					// Add it to the accepted list.
					AcceptedListManager.getInstance().acceptFileItem(item);

					// Remove it from the old parent
					parent.removeFile(item.getAbsolutePath());

					// Was it the last file removed?
					if (!parent.hasChildren())
					{
						// Yes, remove this quality from the raw results
						mResults.remove(parent);
					}
				}

				// Refresh the tree views
				AutoDetectView.this.refreshRawResultsTrees();
			}
		});

		// The double click event handler for the accepted list tree
		// --- REJECTING RESULTS ---
		mAcceptedListTree.addDoubleClickListener(new IDoubleClickListener()
		{
			@Override
			public void doubleClick(DoubleClickEvent event)
			{
				// Get the item that was double clicked
				ISelection selection = mAcceptedListTree.getSelection();
				Object selObj = ((IStructuredSelection) selection).getFirstElement();

				// Is it one of the classifications?
				if (selObj instanceof TreeQualityItem)
				{
					TreeQualityItem selQuality = (TreeQualityItem) selObj;

					// Is it already on the raw list?
					int index = mResults.indexOf(selQuality);
					if (index != -1)
					{
						// Yes, add all the remaining files in it to
						// the quality item in the raw list
						TreeQualityItem rawQuality = (TreeQualityItem) mResults.get(index);
						for (ITreeItem item : selQuality.getChildren())
						{
							rawQuality.addFileItem((TreeFileItem) item);
						}
					}
					else
					{
						// No, then add that whole quality to the raw list
						mResults.add((TreeQualityItem) selObj);
					}

					// Remove it from the accepted results.
					AcceptedListManager.getInstance().rejectQualityItem(selQuality);
				}
				else if (selObj instanceof TreeFileItem)
				{
					// It's a file item
					TreeFileItem item = (TreeFileItem) selObj;
					TreeQualityItem parent = (TreeQualityItem) item.getParent();

					// Remove it from the accepted parent
					// IMPORTANT: The following must be done before
					// anything else, as adding it to raw list will change the
					// file item parent.
					AcceptedListManager.getInstance().rejectFileItem(item);

					// Is it already present in the raw results?
					int index = mResults.indexOf(parent);
					if (index != -1)
					{
						// Yes
						// We get it and add this file to it
						((TreeQualityItem) mResults.get(index)).addFileItem(item);
					}
					else
					{
						// We need to create a new Quality and add it to the
						// raw list
						// and add this file to it.
						TreeQualityItem newRawQuality = new TreeQualityItem(parent.getName());
						newRawQuality.addFileItem(item);

						// Add to the rejected list
						mResults.add(newRawQuality);
					}
				}

				// Refresh the tree views
				AutoDetectView.this.refreshRawResultsTrees();
			}
		});
	}

	/*******************************************************
	 * Registers the event handlers for the components in the TIM management
	 * group: - The add button. - The remove selected button.
	 *******************************************************/
	private void registerTimManagementGroupHandlers()
	{
		// The mouse click event handler for the add new TIM button
		mAddNewTIMButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e)
			{
				// Trying to open the new TIM wizard
				NewTimWizard wiz = new NewTimWizard();

				// Open the wizard
				EclipsePlatformUtils.openAlreadyCreatedWizard(wiz);
			}
		});

		// The mouse click event handler for the remove selected TIMs
		mRemoveTIMsButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e)
			{
				// Is there at least one TIM selected?
				int[] selection = mTIMsList.getSelectionIndices();
				if (selection.length < 1)
				{
					// Give the user a message and return
					EclipsePlatformUtils.showMessage("Remove Selected TIMs",
							"You must select at least one TIM file to be removed.");
					return;
				}
				else
				{
					// User must confirm the delete first
					if (EclipsePlatformUtils
							.showConfirmMessage("Confirm",
									"Do you really want to remove the selected TIM(s)? They will also be removed from the project."))
					{
						// List of selected files to delete
						String[] files = mTIMsList.getSelection();
						// Workspace folder
						// String workspacePath =
						// EclipsePlatformUtils.getWorkspacePath();
						for (String filePath : files)
						{
							// Close it if it's open in an editor without saving
							EclipsePlatformUtils.closeFileEditor(filePath, false);

							// Delete the file
							File fileToDelete = new File(filePath);
							fileToDelete.delete();
						}

						// Refresh projects after the above deletes
						EclipsePlatformUtils.refreshAllProjectsInWorkspace();

						// Remove the TIMs from the list
						mTIMsList.remove(selection);
					}
				}
			}
		});

		// The save as template.
		mSaveTIMAsTemplateButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e)
			{
				// Is there EXACTLY ONE TIM selected?
				int[] selection = mTIMsList.getSelectionIndices();
				if (selection.length != 1)
				{
					// Give the user a message and return
					EclipsePlatformUtils.showMessage("Save TIM as template", "You must select EXACTLY ONE TIM file.");
					return;
				}
				else
				{
					// The selected file.
					String file = mTIMsList.getSelection()[0];
					new TimTemplateSaver(file);
				}
			}
		});
		
		// The architecture visualization button.
		mArchitectureButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e)
			{
				// Open the architecture hierarchy wizard for now.
				new ArchitectureHierarchyWizard();
			}
		});

		// The double-click on a TIM file in the list.
		mTIMsList.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDoubleClick(MouseEvent e)
			{
				String[] selections = mTIMsList.getSelection();

				if (selections != null && selections.length > 0)
				{
					String file = selections[0];

					EclipsePlatformUtils.openFileInDefaultEditor(file, true);
				}
			}
		});
	}

	/*******************************************************
	 * Fills the TIM management list with the available TIM files in the
	 * TimManager
	 *******************************************************/
	private void fillTimManagementList()
	{
		// Since this can be called by notifyMe(), which will be called
		// from the TimsManager (outside the UI thread), we must make sure
		// that the following code is run on the UI, thread.

		mTIMsList.getDisplay().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				if (!mTIMsList.isDisposed())
				{
					// First, remove the list
					mTIMsList.removeAll();

					// Get the list of TIMs in the manager
					java.util.List<Tim> Tims = TimsManager.getInstance().getAll();
					for (Tim tim : Tims)
					{
						// Add them one by one
						mTIMsList.add(new File(tim.getAssociatedFile().getLocationURI()).getAbsolutePath());
					}
				}
			}
		});

	}

	/*******************************************************
	 * Initializes the menu actions
	 *******************************************************/
	private void initMenuActions()
	{
		// Initialize the file preview action
		mPreviewFileAction = new Action()
		{
			@Override
			public void run()
			{
				if (mCurrentSelectedTree != null)
				{
					Object[] selected = ((IStructuredSelection) mCurrentSelectedTree.getSelection()).toArray();

					for (Object sel : selected)
					{
						if (sel instanceof TreeFileItem)
						{
							TreeFileItem selectedFileItem = (TreeFileItem) sel;
							String path = selectedFileItem.getAbsolutePath();
							IEditorPart editorPart = EclipsePlatformUtils.openFileInDefaultEditor(path, true);

							if (editorPart != null)
							{
								// Only if internal editor
								// Start highlighting the indicator terms
								AutoDetectView.this.highlightIndicatorTerms(editorPart, selectedFileItem);
							}
						}
					}
				}
			}
		};
		mPreviewFileAction.setText("Preview File");
		mPreviewFileAction.setImageDescriptor(ImageDescriptor.createFromImage(PlatformUI.getWorkbench()
				.getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE)));

		// ---------------------
		// Accept change action
		mAcceptChangeAction = new Action()
		{
			@Override
			public void run()
			{
				if (mCurrentSelectedTree != null)
				{
					Object[] selected = ((IStructuredSelection) mCurrentSelectedTree.getSelection()).toArray();

					for (Object sel : selected)
					{
						if (sel instanceof TreeFileItem)
						{
							TreeFileItem selectedFileItem = (TreeFileItem) sel;

							// Delete warning and unmark
							MonitoringManager.getIntance().unmarkAndClearWarnings(selectedFileItem.getAbsolutePath());
						}
					}
				}
			}
		};
		mAcceptChangeAction.setText("Accept Changes");
		mAcceptChangeAction.setImageDescriptor(ImageDescriptor.createFromImage(mImageRegistry.getImage("acceptIcon")));

		// ---------------------

		// Initialize the link to TIM action
		mLinkToTimAction = new Action()
		{
			@Override
			public void run()
			{
				if (mCurrentSelectedTree != null)
				{
					Object[] selected = ((IStructuredSelection) mCurrentSelectedTree.getSelection()).toArray();

					for (Object sel : selected)
					{
						if (sel instanceof TreeFileItem)
						{
							// Get the path to the file
							String path = ((TreeFileItem) sel).getAbsolutePath();

							// Create the link
							LinkToTim.linkToActiveTIM(path);
						}
					}
				}
			}
		};
		mLinkToTimAction.setText("Link To Highlighted Node in The TIM");
		mLinkToTimAction.setImageDescriptor(ImageDescriptor.createFromImage(mImageRegistry.getImage("timIcon")));
	}

	/*******************************************************
	 * After a file is selected to be previewed from the results tree, this
	 * method will highlight all the indicator terms in the editor that
	 * qualified this file to be listed under the selected quality type.
	 * 
	 * @param editor
	 *            The editor in which the file was opened.
	 * @param selectedFileItem
	 *            The selected file item from the results tree.
	 * @throws IllegalArgumentException
	 *             If any of the parameters is null, or if the editor is not an
	 *             instance of {@link ITextEditor}
	 *******************************************************/
	private void highlightIndicatorTerms(IEditorPart editor, TreeFileItem selectedFileItem)
			throws IllegalArgumentException
	{
		// Argument validation
		if (editor == null || selectedFileItem == null)
		{
			throw new IllegalArgumentException();
		}

		if (!(editor instanceof ITextEditor))
		{
			throw new IllegalArgumentException();
		}

		// Start the scanning and highlighting of the file
		IEditorInput input = editor.getEditorInput();

		if (input instanceof FileEditorInput)
		{
			IFile file = ((FileEditorInput) input).getFile();
			try
			{
				// Scan the file
				InputStream is = file.getContents();
				Scanner scan = new Scanner(is);
				ArrayList<String> lines = new ArrayList<String>();

				String wholeFile = scan.useDelimiter("\\A").next();

				String[] rawLines = wholeFile.split(System.lineSeparator());

				for (String line : rawLines)
				{
					// Get a platform-dependent new line character
					lines.add(line + System.lineSeparator());
				}

				scan.close();

				// Find all occurrences of the term in all lines

				String qualityName = selectedFileItem.getQualityName();

				// A regex pattern to be used to match separator character, in
				// order to highlight full words instead of stemmed indicator
				// terms
				Pattern pattern = Pattern.compile("[^\\dA-Za-z]");

				int lineIndex = 0; // The total index of the starting character
									// of the current line
				for (int i = 0; i < lines.size(); ++i)
				{
					// Get the current line
					String originalLine = lines.get(i);

					// Lower case line for comparisons.
					String lowerCaseLine = originalLine.toLowerCase();

					// Iterate over the found terms in this file under
					// the selected quality type
					for (String term : selectedFileItem)
					{
						// The index of the found term within the current line
						int index = -1;

						// Attempt to find the term.
						index = lowerCaseLine.indexOf(term);

						while (index != -1)
						{
							if (index != 0)
							{
								if (Character.isLowerCase(originalLine.charAt(index))
										&& originalLine.charAt(index - 1) != ' ')
								{
									// We do not highlight a fake term, for
									// example "keeping", "ping" here is a
									// fake term. How do we know? we assume this
									// naming convention, that is, if "ping" was
									// actually
									// meant, it would have been written as
									// "Ping". (camelCase).

									// Skip and go next
									index = lowerCaseLine.indexOf(term, index + 1);
									continue;
								}
							}

							int realIndex = lineIndex + index;

							// The annotation ending position
							int endPos;

							Matcher matcher = pattern.matcher(lowerCaseLine.substring(index));
							if (matcher.find())
							{
								endPos = realIndex + matcher.start() - 1;
							}
							else
							{
								endPos = realIndex + term.length();
							}

							// mark it
							IMarker marker = EclipsePlatformUtils.addTextMarker(file, ArchieSettings.TEXT_MARKER_TYPE,
									"Indicator Term : " + term + " -- Quality Type : " + qualityName, (i + 1),
									realIndex, endPos);

							// Annotate it
							EclipsePlatformUtils.addAnnotation((ITextEditor) editor, marker,
									ArchieSettings.TERM_ANNOTATION_TYPE, realIndex, (endPos - realIndex + 1));

							// Does it occur multiple times?
							index = lowerCaseLine.indexOf(term, index + 1);
						}
					}

					// Update the next line total index of its starting
					// character
					lineIndex += lowerCaseLine.length();
				}

			}
			catch (CoreException e)
			{
				System.err.println("Unable to mark the indicator terms!");
				e.printStackTrace();
			}
		}
	}

	/*******************************************************
	 * Sets up the context menus for the results tree
	 *******************************************************/
	private void hookContextMenus()
	{
		this.createTreeViewerMenuManager(mResultsTree);
		this.createTreeViewerMenuManager(mAcceptedListTree);
	}

	/*******************************************************
	 * Creates a menu manager and a context menu for the provided
	 * {@link TreeViewer} item. This is to be used for creating menus for the
	 * results and accepted results tree viewers.
	 * 
	 * @param tv
	 *            The {@link TreeViewer} to register the menu for
	 *******************************************************/
	private void createTreeViewerMenuManager(final TreeViewer tv)
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener()
		{
			public void menuAboutToShow(IMenuManager manager)
			{
				// Set the current selected tree viewer
				mCurrentSelectedTree = tv;
				// The context menu to fill if a file is selected
				Object[] selected = ((IStructuredSelection) tv.getSelection()).toArray();

				// Only if all selected items are instances of TreeFileItem.
				boolean areAllFileItems = true;

				for (Object sel : selected)
				{
					if (!(sel instanceof TreeFileItem))
					{
						areAllFileItems = false;
						break;
					}
				}

				if (areAllFileItems)
				{
					// Fill the menu with actions to preview file, or link it to
					// an open TIM editor
					AutoDetectView.this.fillFileContextMenu(manager);
				}
			}
		});
		Menu menu = menuMgr.createContextMenu(tv.getControl());
		tv.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, tv);
	}

	/*******************************************************
	 * Fills the menu manager with the menu items when a file is selected in the
	 * results tree
	 * 
	 * @param manager
	 *            The menu manager to be filled
	 *******************************************************/
	private void fillFileContextMenu(IMenuManager manager)
	{
		manager.add(mPreviewFileAction);
		manager.add(mLinkToTimAction);
		manager.add(mAcceptChangeAction);
		manager.add(new Separator());
		mDrillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	/*******************************************************
	 * Refreshes the raw results and the classifications filter combo box
	 *******************************************************/
	private void refreshRawResultsTrees()
	{
		// Make sure to run on the UI thread
		if (mRawResultsFilter != null && !mRawResultsFilter.isDisposed())
		{
			mRawResultsFilter.getDisplay().asyncExec(new Runnable()
			{
				@Override
				public void run()
				{
					mResultsTree.setInput(mResults.toArray(new ITreeItem[mResults.size()]));

					// Update the combo box

					mRawResultsFilter.removeAll();
					mRawResultsFilter.add("All");

					for (ITreeItem item : mResults)
					{
						mRawResultsFilter.add(item.getName());
					}

					mRawResultsFilter.select(0);

					// Refresh the tree views
					mResultsTree.refresh();
				}
			});

		}
	}

	/*******************************************************
	 * Hides the progress bar on the UI thread.
	 *******************************************************/
	private void hideProgressBar()
	{
		if (mProgressBar != null && !mProgressBar.isDisposed())
		{
			mProgressBar.getDisplay().asyncExec(new Runnable()
			{
				@Override
				public void run()
				{
					mProgressBar.setVisible(false);
				}
			});
		}

	}

	/*******************************************************
	 * Refreshes only the accepted list tree.
	 *******************************************************/
	private void refreshAcceptedTree()
	{
		// Make sure to run on the UI thread
		if (mRawResultsFilter != null && !mRawResultsFilter.isDisposed())
		{
			mRawResultsFilter.getDisplay().asyncExec(new Runnable()
			{
				@Override
				public void run()
				{
					TreeQualityItem[] acceptedResult = AcceptedListManager.getInstance().toArray();

					mAcceptedListTree.setInput(acceptedResult);

					// Update the combo box
					mAcceptedResultsFilter.removeAll();

					mAcceptedResultsFilter.add("All");

					for (TreeQualityItem qualityItem : acceptedResult)
					{
						mAcceptedResultsFilter.add(qualityItem.getName());
					}

					mAcceptedResultsFilter.select(0);

					// Refresh the tree views
					mAcceptedListTree.refresh();
				}
			});

		}
	}
}
