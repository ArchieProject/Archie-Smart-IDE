/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.views;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.io.File;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JApplet;

import org.apache.commons.collections15.Transformer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.part.ViewPart;

import archie.utils.EclipsePlatformUtils;
import archie.views.autodetect.internals.FileQualityScanner;
import archie.views.autodetect.internals.IArchieObserver;
import archie.views.autodetect.internals.IProgressCommand;
import archie.views.autodetect.internals.ITreeItem;
import archie.views.autodetect.internals.JavaProjectsListener;
import archie.views.autodetect.internals.ProgressUpdater;
import archie.views.autodetect.internals.SimpleImageRegistry;
import archie.views.autodetect.internals.TreeQualityItem;
import archie.views.graph.internals.EdgeWrapper;
import archie.views.graph.internals.MacroMicroSearchRequestor;
import archie.views.graph.internals.TacticVertexFilter;
import archie.views.graph.internals.TacticVertexPainter;
import archie.views.graph.internals.TacticVertexShaper;
import archie.views.graph.internals.TacticVertexTransformers;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

/*******************************************************
 * @author Ahmed Fakhry
 * 
 *         Defines a class for the "Draw Me a Picture" View, simply called the
 *         GraphView.
 *******************************************************/
public final class GraphView extends ViewPart implements IArchieObserver
{
	/*******************************************************
	 * The ID of the view as specified by the extension.
	 *******************************************************/
	public static final String VIEW_ID = "archie.views.graphView";

	// -- Image registry
	private SimpleImageRegistry mImageRegistry;

	// -- Java files in the selected java project
	private Queue<ICompilationUnit> mJavaFiles = new LinkedList<ICompilationUnit>();

	// -- The graphs
	private DirectedGraph<ICompilationUnit, EdgeWrapper> mMacroGraph;
	private DirectedGraph<ICompilationUnit, EdgeWrapper> mMicroGraph;

	// -- Controls
	private ToolItem mPickButton;
	private ToolItem mMoveButton;
	private Button mRunButton;
	private Combo mJavaProjectsCombo;
	private ExpandItem mMacroExpandItem;
	private Button mEnableMacroGraph;
	private Group mMacroTactics;
	private Spinner mThresholdSpinner;
	private ExpandItem mMicroExpandItem;
	private Button mEnableMicroGraph;
	private Group mMicroTactics;
	private Composite mGraphComposite;

	// -- The threshold value
	private double mThreshold;

	private final DefaultModalGraphMouse<ICompilationUnit, EdgeWrapper> mGraphMouse = new DefaultModalGraphMouse<ICompilationUnit, EdgeWrapper>();
	private VisualizationViewer<ICompilationUnit, EdgeWrapper> mGraphViewer;
	private Layout<ICompilationUnit, EdgeWrapper> mGraphLayout;

	private Map<ITreeItem, TacticVertexTransformers> mTactics = new TreeMap<ITreeItem, TacticVertexTransformers>();

	// The progress updater
	private ProgressUpdater mProgressUpdater;

	private ToolItem mComboSeparator;

	/*******************************************************
	 * The constructor
	 *******************************************************/
	public GraphView()
	{
		// The default mouse mode for the graph view is to transform the graph
		mGraphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
	}

	/*******************************************************
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 *******************************************************/
	@Override
	public void createPartControl(Composite parent)
	{
		// The image resource management code:
		mImageRegistry = new SimpleImageRegistry(this, parent);

		// Registering images that will be used
		mImageRegistry.registerImagePath("pickIcon", "/resources/icons/pick.png");
		mImageRegistry.registerImagePath("moveIcon", "/resources/icons/move.png");
		mImageRegistry.registerImagePath("runIcon", "/resources/icons/run.png");
		mImageRegistry.registerImagePath("sepIcon", "/resources/icons/separator.png");
		mImageRegistry.registerImagePath("timIcon", "/resources/icons/timeditor.png");

		// The root composite
		Composite root = new Composite(parent, SWT.NONE);
		root.setLayout(new GridLayout());

		// The tool bar composite
		this.createToolBar(root);

		// Vertical sash for the graph view:
		// - Left: The graph control panel
		// - Right: The graph itself
		SashForm sash = new SashForm(root, SWT.HORIZONTAL);
		sash.setLayout(new FillLayout());
		sash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// Create the content of the left control panel
		this.createLeftControlPanel(sash);

		// Create the content of the right panel that contains the graph itself
		this.createRightGraphPanel(sash);

		// Set the weights of the children left and right panels
		// Left : Right = 1 : 4
		sash.setWeights(new int[] { 1, 4 });

		// Create the progress composite
		this.createProgressComposite(root);

		// Register the event handlers for the left control panel
		this.registerLeftPanelHandlers();

		// Register the event handlers for the tool bar buttons
		this.registerToolBarEventHandlers();

		// Register the tool bar buttons related to the graph (the edit and move
		// buttons)
		this.registerGraphToolbarButtonsHandlers();

		// Register yourself as a listener for projects change
		JavaProjectsListener.getInstance().registerObserver(this);
	}

	/*******************************************************
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 *******************************************************/
	@Override
	public void dispose()
	{
		// Unregister yourself as a listener for projects change
		JavaProjectsListener.getInstance().removeObserver(this);
	}

	/*******************************************************
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 *******************************************************/
	@Override
	public void setFocus()
	{
		// Focus on the selection of the java projects
		mJavaProjectsCombo.setFocus();
	}

	/*******************************************************
	 * 
	 * @see archie.views.autodetect.internals.IArchieObserver#notifyMeWithTimsChange()
	 *******************************************************/
	@Override
	public void notifyMeWithTimsChange()
	{
		// Does nothing, not interested in this.
	}

	/*******************************************************
	 * 
	 * @see archie.views.autodetect.internals.IArchieObserver#notifyMeWithAcceptedListChange()
	 *******************************************************/
	@Override
	public void notifyMeWithAcceptedListChange()
	{
		// Does nothing, not interested in this.
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

	// --------------------------------------------------------------------------------------
	// Private Methods
	// --------------------------------------------------------------------------------------

	/*******************************************************
	 * Creates the view tool bar
	 * 
	 * @param parent
	 *            The parent composite of the tool bar to be created
	 *******************************************************/
	private void createToolBar(Composite parent)
	{
		Composite toolBarParent = new Composite(parent, SWT.BORDER);
		toolBarParent.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		toolBarParent.setLayout(new GridLayout());

		// The tool bar
		ToolBar bar = new ToolBar(toolBarParent, SWT.BORDER | SWT.FLAT | SWT.HORIZONTAL | SWT.WRAP);
		bar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// A tool bar separator to contain the combo box for the list of java
		// projects
		mComboSeparator = new ToolItem(bar, SWT.SEPARATOR);
		mJavaProjectsCombo = new Combo(bar, SWT.READ_ONLY);
		mJavaProjectsCombo.setToolTipText("Select one of the Java projects available in the workspace");

		updateProjectsCombo();

		// -------
		// A tool bar separator for the threshold spinner
		ToolItem sep = new ToolItem(bar, SWT.SEPARATOR);
		mThresholdSpinner = new Spinner(bar, SWT.NONE);
		mThresholdSpinner.setToolTipText("Set the classifier threshold");
		// The number of decimal places -- only 1
		mThresholdSpinner.setDigits(1);
		// The maximum value (1 * 10 = 10) Maximum must be set before minimum
		mThresholdSpinner.setMaximum(10);
		// The minimum value (0.1 * 10 = 1)
		mThresholdSpinner.setMinimum(1);
		// The increment (0.1 * 10 = 1)
		mThresholdSpinner.setIncrement(1);
		// The default value (0.2 * 10 = 2)
		mThresholdSpinner.setSelection(2);

		// Set the default threshold value
		mThreshold = 0.2;

		mThresholdSpinner.pack();
		sep.setWidth(mThresholdSpinner.getSize().x);
		sep.setControl(mThresholdSpinner);
		// -------

		// Separator bar
		sep = new ToolItem(bar, SWT.SEPARATOR);
		sep.setImage(mImageRegistry.getImage("sepIcon"));

		// The run button
		sep = new ToolItem(bar, SWT.SEPARATOR);
		mRunButton = new Button(bar, SWT.PUSH);
		mRunButton.setText("Run");
		mRunButton.setImage(mImageRegistry.getImage("runIcon"));
		mRunButton.setToolTipText("Generate the graphs");
		mRunButton.pack();
		sep.setWidth(mRunButton.getSize().x);
		sep.setControl(mRunButton);

		// Separator bar
		sep = new ToolItem(bar, SWT.SEPARATOR);
		sep.setImage(mImageRegistry.getImage("sepIcon"));

		// The pick button
		mPickButton = new ToolItem(bar, SWT.PUSH);
		mPickButton.setImage(mImageRegistry.getImage("pickIcon"));
		mPickButton.setToolTipText("Pick and move nodes");

		// The move button
		mMoveButton = new ToolItem(bar, SWT.PUSH);
		mMoveButton.setImage(mImageRegistry.getImage("moveIcon"));
		mMoveButton.setToolTipText("Pan the graph");

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
						mJavaProjectsCombo.pack();
						mComboSeparator.setWidth(mJavaProjectsCombo.getSize().x);
						mComboSeparator.setControl(mJavaProjectsCombo);
					}
				}
			});
		}

	}

	/*******************************************************
	 * Creates the left control panel of the graph view
	 * 
	 * @param parent
	 *            The {@link SashForm} parent of this panel
	 *******************************************************/
	private void createLeftControlPanel(Composite parent)
	{
		// The expand bar
		ExpandBar bar = new ExpandBar(parent, SWT.V_SCROLL);

		// Create the composites of the Macro/Micro Expand Items:

		// --- 1 - The Macro composite
		Composite macroComp = new Composite(bar, SWT.BORDER | SWT.SHADOW_ETCHED_IN);
		macroComp.setLayout(new GridLayout(1, true));
		macroComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// The enable button of the macro graph
		mEnableMacroGraph = new Button(macroComp, SWT.RADIO);
		mEnableMacroGraph.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		mEnableMacroGraph.setText("Enable Macro Graph");

		// The list of tactics for the macro graph: contains check boxes
		mMacroTactics = new Group(macroComp, SWT.BORDER | SWT.SHADOW_ETCHED_IN);
		mMacroTactics.setLayout(new GridLayout(1, true));
		mMacroTactics.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		mMacroTactics.setText("Select Tactics");

		// --- 2 - The Micro composite
		Composite microComp = new Composite(bar, SWT.BORDER | SWT.SHADOW_ETCHED_IN);
		microComp.setLayout(new GridLayout(1, true));
		microComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// The enable button of the micro graph
		mEnableMicroGraph = new Button(microComp, SWT.RADIO);
		mEnableMicroGraph.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		mEnableMicroGraph.setText("Enable Micro Graph");

		// The list of tactics for the macro graph: contains check boxes
		mMicroTactics = new Group(microComp, SWT.BORDER | SWT.SHADOW_ETCHED_IN);
		mMicroTactics.setLayout(new GridLayout(1, true));
		mMicroTactics.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		mMicroTactics.setText("Select a Tactic");

		// Create the expand items:

		// 1 - The Macro Graph Expand Item
		mMacroExpandItem = new ExpandItem(bar, SWT.NONE);
		mMacroExpandItem.setText("Macro Graph");
		mMacroExpandItem.setImage(mImageRegistry.getImage("timIcon"));
		// Set the control of the expand item
		mMacroExpandItem.setHeight(macroComp.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		mMacroExpandItem.setControl(macroComp);

		// 2 - The Macro Graph Expand Item
		mMicroExpandItem = new ExpandItem(bar, SWT.NONE);
		mMicroExpandItem.setText("Micro Graph");
		mMicroExpandItem.setImage(mImageRegistry.getImage("timIcon"));
		// Set the control of the expand item
		mMicroExpandItem.setHeight(microComp.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		mMicroExpandItem.setControl(microComp);

		// Expand the macro item by default
		mMacroExpandItem.setExpanded(true);
		mMicroExpandItem.setExpanded(false);
	}

	/*******************************************************
	 * Creates the right panel that contains the graph itself
	 * 
	 * @param parent
	 *            The {@link SashForm} parent of this panel
	 *******************************************************/
	private void createRightGraphPanel(Composite parent)
	{
		// The composite to embed the AWT graph
		mGraphComposite = new Composite(parent, SWT.BORDER);
		mGraphComposite.setLayout(new FillLayout());
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
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		// The progress bar
		ProgressBar progBar = new ProgressBar(progressComp, SWT.NONE);
		progBar.setMaximum(100);
		progBar.setMinimum(0);
		progBar.setSelection(0);
		progBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// Initially hide them
		label.setVisible(false);
		progBar.setVisible(false);

		// Create the progress updater
		mProgressUpdater = new ProgressUpdater(progBar, label);
	}

	/*******************************************************
	 * Registers the event handlers for the left control panel
	 *******************************************************/
	private void registerLeftPanelHandlers()
	{
		// Internal class to do the update
		class SelectionUpdater
		{
			/*******************************************************
			 * Enables the macro graph
			 *******************************************************/
			public void enableMacroButton()
			{
				// Enable macro graph
				mEnableMacroGraph.setSelection(true);
				mMacroTactics.setEnabled(true);

				// Disable micro graph
				mEnableMicroGraph.setSelection(false);
				mMicroTactics.setEnabled(false);

				// Draw the enabled graph
				drawEnabledGraph();
			}

			/*******************************************************
			 * Enables the micro graph
			 *******************************************************/
			public void enableMicroButton()
			{
				// Enable micro graph
				mEnableMicroGraph.setSelection(true);
				mMicroTactics.setEnabled(true);

				// Disable macro graph
				mEnableMacroGraph.setSelection(false);
				mMacroTactics.setEnabled(false);

				// Draw the enabled graph
				drawEnabledGraph();
			}
		}

		final SelectionUpdater updater = new SelectionUpdater();

		// Clicking the macro radio button
		mEnableMacroGraph.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e)
			{
				updater.enableMacroButton();
			}
		});

		// Clicking the micro radio button
		mEnableMicroGraph.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e)
			{
				updater.enableMicroButton();
			}
		});

		// By default set the selected graph type to the macro graph
		updater.enableMacroButton();
	}

	/*******************************************************
	 * Registers the event handlers for the tool bar buttons
	 *******************************************************/
	private void registerToolBarEventHandlers()
	{
		// -----------------------------------
		// The handler for the run button
		mRunButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
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

					try
					{
						runAndGenerate(javaProjName);
					}
					catch (MalformedURLException e1)
					{
						EclipsePlatformUtils.showErrorMessage("Error", "Couldn't generate graphs.");
						e1.printStackTrace();
					}
				}
			}
		});

		// -----------------------------------
		// The threshold spinner event handler
		mThresholdSpinner.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				mThreshold = Double.parseDouble(mThresholdSpinner.getText());
			}
		});
	}

	/*******************************************************
	 * Start scanning and graph generation
	 * 
	 * @param selectedJavaProjName
	 *            The selected java project name from the combo box
	 * @throws MalformedURLException
	 *******************************************************/
	private void runAndGenerate(String selectedJavaProjName) throws MalformedURLException
	{
		final IJavaProject javaProj = JavaProjectsListener.getInstance().getJavaProject(selectedJavaProjName);

		if (javaProj != null)
		{

			// Create a command to update the progress
			final IProgressCommand progCmd = new IProgressCommand()
			{
				@Override
				public void run(final double progress)
				{
					mProgressUpdater.setProgressValue((int) (0.5 * progress * 100)).run();
				}
			};

			// Initialize the progress updater and use to update the progress
			mProgressUpdater.setVisible(true).setProgressText("Classifying the project -- Extracting tactics ...")
					.setProgressValue(0).run();

			// Extract the tactics
			final String projPath = new File(javaProj.getProject().getLocationURI()).getAbsolutePath();

			// Scanning and graph creation must be done on a different thread
			// than
			// that of the UI
			new Thread("GraphGenerator")
			{
				@Override
				public void run()
				{
					LinkedList<ITreeItem> tactics = FileQualityScanner.scanDirectory(projPath, mThreshold, progCmd);

					// Update the progress status -- has to be done on the UI
					// thread
					// (this ensured by the our progress updater)
					mProgressUpdater.setProgressText("Generating Graphs ... ").run();

					// Store the tactics and associate on color for each
					// Clear first
					mTactics.clear();
					for (ITreeItem tactic : tactics)
					{
						TacticVertexPainter painter = new TacticVertexPainter((TreeQualityItem) tactic);
						TacticVertexShaper shaper = new TacticVertexShaper((TreeQualityItem) tactic);
						TacticVertexFilter filter = new TacticVertexFilter((TreeQualityItem) tactic);

						TacticVertexTransformers transformers = new TacticVertexTransformers(painter, shaper, filter);

						mTactics.put(tactic, transformers);
					}

					// Generate the graphs
					generateGraphs(javaProj);

					// Update the progress status -- has to be done on the UI
					// thread
					// (this ensured by the our progress updater)
					mProgressUpdater.setProgressText("").setProgressValue(0).setVisible(false).run();
				}
			}.start();

		}
	}

	/*******************************************************
	 * Generates the macro and micro graphs for the provided Java project
	 * 
	 * @param javaProj
	 *            The {@link IJavaProject} for which the graphs will be
	 *            generated
	 *******************************************************/
	private void generateGraphs(IJavaProject javaProj)
	{
		// Clear the list first
		mJavaFiles.clear();
		// Clear the graphs
		mMacroGraph = new DirectedSparseMultigraph<ICompilationUnit, EdgeWrapper>();
		mMicroGraph = new DirectedSparseMultigraph<ICompilationUnit, EdgeWrapper>();

		// Extract the list of java files in the project
		try
		{
			// Get all the packages in the project
			IPackageFragment[] packages = javaProj.getPackageFragments();

			for (IPackageFragment pack : packages)
			{
				// Get all the compilation units (Java Files)
				ICompilationUnit[] javaFiles = pack.getCompilationUnits();

				for (ICompilationUnit javaFile : javaFiles)
				{
					// Fill the java files queue
					mJavaFiles.add(javaFile);
				}
			}

			// --- Done adding all the vertices
			// Start adding edges:
			addGraphsEdges();

			// Update the progress status -- has to be done on the UI thread
			// (this ensured by the our progress updater)
			mProgressUpdater.setProgressText("Drawing Graphs ... ").run();

			// Initialize the graph viewer
			initGraphViewer(mMacroGraph);

			// Fill the tactics list
			fillTacticsComposites();

			// --- Draw graphs
			drawEnabledGraph();

		}
		catch (CoreException e)
		{
			EclipsePlatformUtils.showErrorMessage("Error", "Could not generate the graphs.");
			e.printStackTrace();
		}
	}

	/*******************************************************
	 * Adds the edges of both macro and micro graphs
	 * 
	 * @throws CoreException
	 *             If searching for methods references fails
	 *******************************************************/
	private void addGraphsEdges() throws CoreException
	{
		SearchEngine searchEngine = new SearchEngine();

		// process all methods
		int size = mJavaFiles.size();
		int remaining = size;
		while (remaining > 0)
		{
			// dequeue
			ICompilationUnit javaFile = mJavaFiles.poll();
			final String javaFileName = javaFile.getElementName();
			final double progress = 1.0 - ((double) remaining / size);

			// Update the progress status -- has to be done on the UI thread
			// (this ensured by the our progress updater)
			mProgressUpdater
					.setProgressText(
							"Generating Graphs --- Searching for declarations of methods in file : " + javaFileName
									+ " ...").setProgressValue(50 + (int) (0.5 * progress * 100)).run();

			// Start searching
			searchEngine.searchDeclarationsOfSentMessages(javaFile, new MacroMicroSearchRequestor(javaFile,
					mMacroGraph, mMicroGraph), null);

			// push it back (enqueue)
			mJavaFiles.add(javaFile);
			remaining--;
		}
	}

	/*******************************************************
	 * Draws the enabled graph (Macro or Micro)
	 *******************************************************/
	private void drawEnabledGraph()
	{
		mGraphComposite.getDisplay().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				if (mGraphComposite != null && !mGraphComposite.isDisposed())
				{
					// Draw the enabled graph
					if (mEnableMacroGraph.getSelection())
					{
						drawGraph(mMacroGraph);

						// Apply the selected tactic
						Control[] radios = mMacroTactics.getChildren();
						for (Control radio : radios)
						{
							if (((Button) radio).getSelection())
							{
								radio.notifyListeners(SWT.Selection, new Event());
							}
						}
					}
					else
					{
						drawGraph(mMicroGraph);

						// Apply the selected tactic
						Control[] radios = mMicroTactics.getChildren();
						for (Control radio : radios)
						{
							if (((Button) radio).getSelection())
							{
								radio.notifyListeners(SWT.Selection, new Event());
							}
						}
					}
				}
			}
		});
	}

	/*******************************************************
	 * Initializes the graph viewer with the given graph
	 * 
	 * @param graph
	 *            The graph to initialize the drawer with
	 *******************************************************/
	private void initGraphViewer(final Graph<ICompilationUnit, EdgeWrapper> graph)
	{
		if (graph == null)
			return;

		if (mGraphViewer == null)
		{
			// First time to create the graph viewer

			// Run the following on the UI thread
			mGraphComposite.getDisplay().asyncExec(new Runnable()
			{
				@Override
				public void run()
				{
					if (mGraphComposite != null && !mGraphComposite.isDisposed())
					{
						// Drawing the graph
						mGraphLayout = new FRLayout<ICompilationUnit, EdgeWrapper>(graph);

						final Rectangle size = mGraphComposite.getClientArea();

						mGraphLayout.setSize(new Dimension(size.width - 10, size.height - 10));
						mGraphViewer = new VisualizationViewer<ICompilationUnit, EdgeWrapper>(mGraphLayout);
						mGraphViewer.setPreferredSize(new Dimension(size.width, size.height));

						mGraphViewer.getRenderContext().setVertexLabelTransformer(new Transformer<ICompilationUnit, String>()
						{
							@Override
							public String transform(ICompilationUnit arg0)
							{
								return arg0.getElementName();
							}
							
						});
						mGraphViewer.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<EdgeWrapper>());

						mGraphViewer.getRenderContext().setVertexFontTransformer(new Transformer<ICompilationUnit, Font>()
						{

							@Override
							public Font transform(ICompilationUnit arg0)
							{
								return new Font("Monospaced", Font.BOLD, 12);
							}
						});

						mGraphViewer.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);

						// Mouse mode
						mGraphViewer.setGraphMouse(mGraphMouse);

						final Composite comp = new Composite(mGraphComposite, SWT.EMBEDDED | SWT.NO_BACKGROUND);
						comp.setLayout(new FillLayout());
						Frame frame = SWT_AWT.new_Frame(comp);

						// Create the JApplet for the graph
						JApplet ja = new JApplet();
						ja.add(mGraphViewer);
						frame.add(ja);

						comp.setSize(size.width, size.height);
						comp.layout(true, true);

						mGraphComposite.layout(true, true);
					}
				}
			});
		}
	}

	/*******************************************************
	 * Draws the provided graph
	 * 
	 * @param graph
	 *            The graph to be drawn
	 *******************************************************/
	private void drawGraph(Graph<ICompilationUnit, EdgeWrapper> graph)
	{
		if (graph == null)
			return;

		if (mGraphViewer != null)
		{
			mGraphLayout.setGraph(graph);
			mGraphViewer.repaint();
		}
	}

	/*******************************************************
	 * Register the tool bar buttons related to controlling the graph view
	 *******************************************************/
	private void registerGraphToolbarButtonsHandlers()
	{
		// The pick tool bar button
		mPickButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// Change the mouse mode to picking
				mGraphMouse.setMode(ModalGraphMouse.Mode.PICKING);
			}
		});

		// The move tool bar button
		mMoveButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// Change the mouse mode to transforming
				mGraphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
			}
		});

	}

	/*******************************************************
	 * Fills the left panel with the list of tactics extracted from the scan
	 *******************************************************/
	private void fillTacticsComposites()
	{
		// Run everything here on the UI thread
		mMacroTactics.getDisplay().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				if (mMacroTactics != null && !mMacroTactics.isDisposed())
				{
					// Dispose old children
					Control[] children = mMacroTactics.getChildren();
					for (Control child : children)
					{
						child.dispose();
					}

					children = mMicroTactics.getChildren();
					for (Control child : children)
					{
						child.dispose();
					}

					// Fill the macro/micro tactics composites with their
					// buttons

					for (final ITreeItem item : mTactics.keySet())
					{
						// Button for the macro (Radios)
						final Button b1 = new Button(mMacroTactics, SWT.RADIO);
						b1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
						b1.setText(item.getName());
						b1.addSelectionListener(new SelectionAdapter()
						{
							@Override
							public void widgetSelected(SelectionEvent e)
							{
								if (b1.getSelection() && mGraphViewer != null)
								{
									// Get the graph's render context
									RenderContext<ICompilationUnit, EdgeWrapper> context = mGraphViewer.getRenderContext();

									// Get the transformers for this tactic
									TacticVertexTransformers transformers = mTactics.get(item);

									// Set the painter
									TacticVertexPainter vertexPainter = transformers.getVertexPainter();
									vertexPainter.setPickedState(mGraphViewer.getPickedVertexState());
									context.setVertexFillPaintTransformer(vertexPainter);

									// Set the shaper
									TacticVertexShaper shaper = transformers.getVertexShaper();
									context.setVertexShapeTransformer(shaper);

									// Redraw
									mGraphViewer.repaint();
								}
							}
						});

						// Button for the micro (radio button)
						final Button b2 = new Button(mMicroTactics, SWT.RADIO);
						b2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
						b2.setText(item.getName());
						b2.addSelectionListener(new SelectionAdapter()
						{
							@Override
							public void widgetSelected(SelectionEvent e)
							{
								if (b2.getSelection() && mGraphViewer != null)
								{
									// Get the graph's render context
									RenderContext<ICompilationUnit, EdgeWrapper> context = mGraphViewer.getRenderContext();

									// Get the transformers for this tactic
									TacticVertexTransformers transformers = mTactics.get(item);

									// Set the painter
									TacticVertexPainter vertexPainter = transformers.getVertexPainter();
									vertexPainter.setPickedState(mGraphViewer.getPickedVertexState());
									context.setVertexFillPaintTransformer(vertexPainter);

									// Set the shaper
									TacticVertexShaper shaper = transformers.getVertexShaper();
									context.setVertexShapeTransformer(shaper);

									// Filter the graph
									TacticVertexFilter filter = transformers.getVertexFilter();

									// Draw the filtered graph
									drawGraph(filter.filterGraph(mMicroGraph));
								}
							}
						});
					}

					// Select the first radio button in the macro/micro tactics
					// (if
					// any)
					Control[] radios = mMacroTactics.getChildren();
					if (radios.length > 0)
					{
						((Button) radios[0]).setSelection(true);
					}

					radios = mMicroTactics.getChildren();
					if (radios.length > 0)
					{
						((Button) radios[0]).setSelection(true);
					}

					// Done adding the buttons
					// Update the expand items size
					Composite comp = mMacroTactics.getParent();
					comp.pack();
					comp.layout(true, true);
					mMacroExpandItem.setHeight(mMacroTactics.getParent().getSize().y);

					comp = mMicroTactics.getParent();
					comp.pack();
					comp.layout(true, true);
					mMicroExpandItem.setHeight(mMicroTactics.getParent().getSize().y);

					comp = mMacroExpandItem.getParent();
					comp.layout(true, true);
					comp.redraw();
				}
			}
		});
	}
}
