/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.views;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;

import archie.utils.EclipsePlatformUtils;
import archie.views.autodetect.internals.ProgressUpdater;
import archie.views.autodetect.internals.SimpleImageRegistry;
import archie.views.ptrace.internals.PTraceResult;
import archie.views.ptrace.internals.PanoramicTracer;

/*******************************************************
 * @author Ahmed Fakhry
 * 
 * A class that defines the UI for the panoramic trace view.
 *******************************************************/
public final class PanoramicTraceView extends ViewPart
{

	/*******************************************************
	 * The view ID. Must match the view's ID in the manifest file.
	 *******************************************************/
	public static final String VIEW_ID = "archie.views.panoramicTrace";

	/*******************************************************
	 * The image registry
	 *******************************************************/
	private SimpleImageRegistry mImageRegistry;

	/*******************************************************
	 * The results list.
	 *******************************************************/
	private ArrayList<PTraceResult> mResults = new ArrayList<PTraceResult>();
	private TreeSet<PTraceResult> mAscendingResults = new TreeSet<PTraceResult>(PTraceResult.BY_INCREASING_SCORES);
	private TreeSet<PTraceResult> mDescendingResults = new TreeSet<PTraceResult>(PTraceResult.BY_DECREASING_SCORES);

	/*******************************************************
	 * The unique folders list. Meant to be fed to the back end. This provides
	 * faster search.
	 *******************************************************/
	private Set<String> mFolders = new HashSet<String>();

	// The progress updater
	private ProgressUpdater mProgressUpdater;

	/*******************************************************
	 * Controls
	 *******************************************************/
	private Button mAddNewFolderButton;
	private Button mRemoveFoldersButton;
	private List mFoldersList; // <-- This is the view folders list
	private Button mRunButton;
	private Table mResultsTable;
	private Button mSortAscending;
	private Button mSortDescending;

	/*******************************************************
	 * The default constructor
	 *******************************************************/
	public PanoramicTraceView()
	{
	}

	/*******************************************************
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 *******************************************************/
	@Override
	public void createPartControl(Composite parent)
	{
		// The image resource management code:
		this.mImageRegistry = new SimpleImageRegistry(parent);

		// Registering images that will be used
		this.mImageRegistry.registerImagePath("addIcon", "/resources/icons/addIcon.png");
		this.mImageRegistry.registerImagePath("deleteIcon", "/resources/icons/deleteIcon.png");
		this.mImageRegistry.registerImagePath("runIcon", "/resources/icons/run.png");
		this.mImageRegistry.registerImagePath("ascendIcon", "/resources/icons/ascending.png");
		this.mImageRegistry.registerImagePath("descendIcon", "/resources/icons/descending.png");

		// How the parent lays out its children
		parent.setLayout(new FillLayout(SWT.VERTICAL));

		// Introducing a sash to stack the two groups vertically
		SashForm sash = new SashForm(parent, SWT.VERTICAL);
		sash.setLayout(new FillLayout(SWT.VERTICAL));
		// Initialize the group that manages the list of folders to compare to
		initCompareFoldersGroup(sash);

		Composite bottomComposite = new Composite(sash, SWT.BORDER);
		bottomComposite.setLayout(new GridLayout(1, true));
		bottomComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// Initialize the results group
		initResultsGroup(bottomComposite);

		// Initialize the progress component
		createProgressComposite(bottomComposite);

		// Register the event handlers
		registerEventHandlers();
	}

	/*******************************************************
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 *******************************************************/
	@Override
	public void setFocus()
	{
		mFoldersList.setFocus();
	}

	/*******************************************************
	 * Initializes the group that contains the list of folders that contain
	 * files to which the comparison will be made.
	 * 
	 * @param parent
	 *            The parent composite of this group.
	 *******************************************************/
	private void initCompareFoldersGroup(Composite parent)
	{
		Group compareFolderGroup = new Group(parent, SWT.NONE);
		compareFolderGroup.setText("Folders To Compare To");
		compareFolderGroup.setLayout(new GridLayout(4, true));
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		compareFolderGroup.setLayoutData(gridData);

		// Button to add a new Folder
		mAddNewFolderButton = new Button(compareFolderGroup, SWT.NONE);
		mAddNewFolderButton.setImage(mImageRegistry.getImage("addIcon"));
		gridData = new GridData(GridData.FILL, GridData.FILL, false, false);
		mAddNewFolderButton.setLayoutData(gridData);
		mAddNewFolderButton.setToolTipText("Add a new folder.");
		mAddNewFolderButton.setText("Add Folder");

		// Button to remove the selected TIMs
		mRemoveFoldersButton = new Button(compareFolderGroup, SWT.NONE);
		mRemoveFoldersButton.setImage(mImageRegistry.getImage("deleteIcon"));
		gridData = new GridData(GridData.FILL, GridData.FILL, false, false);
		mRemoveFoldersButton.setLayoutData(gridData);
		mRemoveFoldersButton.setToolTipText("Delete the selected folders.");
		mRemoveFoldersButton.setText("Remove Folder(s)");

		// The run button
		mRunButton = new Button(compareFolderGroup, SWT.NONE);
		mRunButton.setImage(mImageRegistry.getImage("runIcon"));
		gridData = new GridData(GridData.FILL, GridData.FILL, false, false);
		gridData.horizontalSpan = 2;
		mRunButton.setLayoutData(gridData);
		mRunButton.setToolTipText("Run the scanner");
		mRunButton.setText("Run");

		// The list of TIMs
		mFoldersList = new List(compareFolderGroup, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 4;
		mFoldersList.setLayoutData(gridData);
	}

	/*******************************************************
	 * Initializes the group that will contain the results
	 * 
	 * @param parent
	 *            The parent composite of this group.
	 *******************************************************/
	private void initResultsGroup(Composite parent)
	{
		// The group that will contain the results
		Group resultsGroup = new Group(parent, SWT.NONE);
		resultsGroup.setText("Results");
		resultsGroup.setLayout(new GridLayout(2, true));
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		resultsGroup.setLayoutData(gridData);

		// The ascending / descending buttons
		mSortAscending = new Button(resultsGroup, SWT.NONE);
		mSortAscending.setImage(this.mImageRegistry.getImage("ascendIcon"));
		mSortAscending.setText("Sort Ascending");
		mSortAscending.setToolTipText("Sort results by scores in an ascending order");
		gridData = new GridData(GridData.FILL, GridData.FILL, false, false);
		mSortAscending.setLayoutData(gridData);

		mSortDescending = new Button(resultsGroup, SWT.NONE);
		mSortDescending.setImage(this.mImageRegistry.getImage("descendIcon"));
		mSortDescending.setText("Sort Descending");
		mSortDescending.setToolTipText("Sort results by scores in a descending order");
		gridData = new GridData(GridData.FILL, GridData.FILL, false, false);
		mSortDescending.setLayoutData(gridData);

		// The results table
		mResultsTable = new Table(resultsGroup, SWT.BORDER | SWT.V_SCROLL | SWT.VIRTUAL);
		mResultsTable.setHeaderVisible(true);
		mResultsTable.setLinesVisible(true);
		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 2;
		mResultsTable.setLayoutData(gridData);

		// The table's columns
		// 1- The "File" column
		TableColumn col = new TableColumn(mResultsTable, SWT.NONE);
		col.setText("File Name");
		col.setWidth(200);
		// 2- The file path
		col = new TableColumn(mResultsTable, SWT.NONE);
		col.setText("File Path");
		col.setWidth(300);

		// 3- The "Sample Content" column
		col = new TableColumn(mResultsTable, SWT.NONE);
		col.setText("Sample Content");
		col.setWidth(400);
		// 4- The "Score" column
		final TableColumn scoreCol = new TableColumn(mResultsTable, SWT.NONE);
		scoreCol.setText("Similarity Score %");
		scoreCol.setWidth(100);

		// The scores drawing mechanism
		/*
		 * NOTE: MeasureItem, PaintItem and EraseItem are called repeatedly.
		 * Therefore, it is critical for performance that these methods be as
		 * efficient as possible.
		 */
		mResultsTable.addListener(SWT.PaintItem, new Listener()
		{
			@Override
			public void handleEvent(Event event)
			{
				// Only for the fourth column
				if (event.index == 3)
				{
					GC gc = event.gc;
					TableItem item = (TableItem) event.item;
					int index = mResultsTable.indexOf(item);

					if (index != -1 && index < mResults.size())
					{
						// Get the corresponding result item
						PTraceResult result = mResults.get(index);

						// Get the score value, that will be used to draw the
						// bar chart
						double score = result.getScore();

						Display display = Display.getDefault();
						Color foreground = gc.getForeground();
						Color background = gc.getBackground();
						gc.setForeground(display.getSystemColor(SWT.COLOR_DARK_CYAN));
						gc.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
						int width = (scoreCol.getWidth() - 1) * (int) score / 100;

						gc.fillGradientRectangle(event.x, event.y, width, event.height, true);
						Rectangle rect2 = new Rectangle(event.x, event.y, width - 1, event.height - 1);
						gc.drawRectangle(rect2);
						gc.setForeground(display.getSystemColor(SWT.COLOR_LIST_FOREGROUND));
						String text = String.format("%.2f%%", score);
						Point size = gc.textExtent(text);
						int offset = Math.max(0, (event.height - size.y) / 2);

						// 3 here is the index of the score column --- (The
						// fourth column)
						Rectangle textBounds = item.getTextBounds(3);

						gc.drawText(text, textBounds.x + 1, textBounds.y + offset, true);
						gc.setForeground(background);
						gc.setBackground(foreground);
					}
				}
			}
		});

		/**
		 * Double click listener for the table.
		 */
		mResultsTable.addListener(SWT.MouseDoubleClick, new Listener()
		{

			@Override
			public void handleEvent(Event event)
			{
				if (event != null)
				{
					Table tab = (Table) event.widget;
					int index = tab.getSelectionIndex();

					// Get the corresponding result item
					PTraceResult result = mResults.get(index);

					EclipsePlatformUtils.openFileInDefaultEditor(result.getFilePath(), false);
				}
			}

		});
	}

	/*******************************************************
	 * Creates the progress bar composite. This will help the user know that the
	 * tool is working and making progress
	 * 
	 * @param parent
	 *            The parent composite
	 *******************************************************/
	private void createProgressComposite(Composite parent)
	{
		// The container of this group
		Composite progressComp = new Composite(parent, SWT.BORDER);
		progressComp.setLayout(new GridLayout(1, true));
		progressComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		// The label to tell the user about the current state
		Label label = new Label(progressComp, SWT.NONE);
		label.setText("Testing ...");
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		// The progress bar
		ProgressBar progBar = new ProgressBar(progressComp, SWT.NONE);
		progBar.setMaximum(100);
		progBar.setMinimum(0);
		progBar.setSelection(0);
		progBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		// Initially hide them
		label.setVisible(false);
		progBar.setVisible(false);

		// Create the progress updater
		mProgressUpdater = new ProgressUpdater(progBar, label);
	}

	/*******************************************************
	 * Registers all the event handlers in this view
	 *******************************************************/
	private void registerEventHandlers()
	{
		// The add button
		registerAddButtonEventHandler();

		// The remove button
		registerRemoveButtonEventHandler();

		// The run button
		registerRunButtonEventHandler();

		// The sort buttons
		registerSortButtonsEventHandlers();
	}

	/*******************************************************
	 * Registers the event handler for the add button
	 *******************************************************/
	private void registerAddButtonEventHandler()
	{
		// This button opens the folder browse window
		// to let the user choose target folders
		mAddNewFolderButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e)
			{
				DirectoryDialog dd = new DirectoryDialog(e.display.getActiveShell(), SWT.NONE);
				dd.setText("Select a target folder to add to the list of target folders ...");

				String result = dd.open();
				if (result != null && !result.isEmpty())
				{
					// Make sure that the folder is not a duplicate
					if (mFolders.contains(result))
					{
						// Yup, it's a duplicate
						EclipsePlatformUtils.showErrorMessage("Error", "Folder already exists on the list.");
					}
					else
					{
						// Add it to the view list
						mFoldersList.add(result);
						// Add it to the back-end list
						mFolders.add(result);
					}
				}
			}
		});
	}

	/*******************************************************
	 * Registers the event handler for the remove button
	 *******************************************************/
	private void registerRemoveButtonEventHandler()
	{
		// The mouse click event handler for the remove selected TIMs
		mRemoveFoldersButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e)
			{
				// Is there at least one folder selected?
				int[] selection = mFoldersList.getSelectionIndices();
				if (selection.length < 1)
				{
					// Give the user a message and return
					EclipsePlatformUtils.showMessage("Remove Selected Folders",
							"You must select at least one folder to be removed.");
					return;
				}
				else
				{
					// User must confirm the delete first
					if (EclipsePlatformUtils
							.showConfirmMessage("Confirm",
									"Do you really want to remove the selected folder(s)? They will not be part of the target folders in the scan."))
					{
						// Remove the folders from the lists
						for (String sel : mFoldersList.getSelection())
						{
							mFolders.remove(sel);
						}

						mFoldersList.remove(selection);
					}
				}
			}
		});
	}

	/*******************************************************
	 * Registers the event handler for the run button
	 *******************************************************/
	private void registerRunButtonEventHandler()
	{
		mRunButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e)
			{
				// Make sure that the folders list is not empty
				if (mFoldersList.getItemCount() < 1)
				{
					// Error message
					EclipsePlatformUtils.showErrorMessage("Error", "You must select folders to scan first!");
				}
				else
				{
					IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
							.getActiveEditor();
					if (editor != null)
					{

						// Clear the old results in the table
						clearTable();

						// Running on a separate thread

						IEditorInput input = editor.getEditorInput();
						final IPath path = ((FileEditorInput) input).getPath();

						new Thread("Scanner")
						{
							public void run()
							{
								try
								{

									mProgressUpdater.setVisible(true).setProgressText("Starting ...")
											.setProgressValue(0).run();

									// The returned results are sorted by descending order by default
									mDescendingResults = PanoramicTracer.start(mFolders, path.toString(),
											mProgressUpdater);
									
									// Build the ascending results
									buildAscendingResults();

									// Add the results to the table
									addResults(mDescendingResults);

									// Done!
									mProgressUpdater.setProgressText("Done!").setProgressValue(100).run();

									// Hide the progress bar
									mProgressUpdater.setProgressValue(0).setVisible(false).run();

								}
								catch (FileNotFoundException e1)
								{
									// Error message
									EclipsePlatformUtils.showErrorMessage("Error", "Failed to run, check your input.");
									e1.printStackTrace();
									mProgressUpdater.setVisible(false).setProgressValue(0).run();
								}
							};

						}.start();

					}
					else
					{
						EclipsePlatformUtils.showErrorMessage("Error",
								"You must open a source file in an active editor to be the comparison source file.");
					}

				}
			}
		});
	}

	/*******************************************************
	 * Registers the event handlers for the sort ascending / descending buttons.
	 *******************************************************/
	private void registerSortButtonsEventHandlers()
	{
		// The ascending button event handler
		mSortAscending.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e)
			{
				// Clear the table with old results
				clearTable();

				// Add the new results
				addResults(mAscendingResults);
			}
		});

		// The descending button event handler
		mSortDescending.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e)
			{
				// Clear the table with old results
				clearTable();

				// Add the new results
				addResults(mDescendingResults);
			}
		});
	}

	/*******************************************************
	 * Builds the ascending results from the descending one received
	 * by default from the scanner.
	 *******************************************************/
	private void buildAscendingResults()
	{
		mAscendingResults.clear();
		
		for(PTraceResult res : mDescendingResults)
		{
			mAscendingResults.add(res);
		}
	}

	/*******************************************************
	 * Clears the results table and the results list used to draw the bars.
	 *******************************************************/
	private void clearTable()
	{
		mResultsTable.clearAll();

		for (TableItem item : mResultsTable.getItems())
		{
			item.dispose();
		}

		mResults.clear();

		mResultsTable.redraw();
		mResultsTable.update();
	}

	/*******************************************************
	 * Fills in the results table with the supplied results. Runs on the UI
	 * thread.
	 * 
	 * @param results
	 *            The results to be added to the table.
	 *******************************************************/
	private void addResults(final TreeSet<PTraceResult> results)
	{
		if (mResultsTable != null && !mResultsTable.isDisposed())
		{
			mResultsTable.getDisplay().asyncExec(new Runnable()
			{

				@Override
				public void run()
				{
					for (PTraceResult resultItem : results)
					{
						// Create the table item that represent the given result
						// item.
						TableItem item = new TableItem(mResultsTable, SWT.NONE);
						// The score is added as an empty string "". This is
						// because
						// it will
						// be drawn by the paint listener.
						item.setText(new String[] { resultItem.getFileName(), resultItem.getFilePath(),
								resultItem.getSampleContent(), "" });

						// Add the result to the list
						// Unfortunately, we have to use an extra array list to
						// be able to
						// index into it when we draw the bars
						mResults.add(resultItem);
					}
				}
			});
		}

	}

}
