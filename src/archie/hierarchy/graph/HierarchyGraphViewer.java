/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.hierarchy.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import archie.hierarchy.SubGoal;
import archie.hierarchy.Tactic;
import archie.hierarchy.TimComponent;
import archie.views.autodetect.internals.IArchieObserver;
import archie.views.autodetect.internals.JavaProjectsListener;
import archie.views.autodetect.internals.ProgressUpdater;
import archie.views.autodetect.internals.SimpleImageRegistry;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;

/*******************************************************
 * This will create an display the window that builds and displays the hierarchy
 * graph.
 * 
 * @author Ahmed Fakhry
 *******************************************************/
public final class HierarchyGraphViewer implements IArchieObserver
{
	private final Shell mShell;
	private SimpleImageRegistry mImageRegistry;
	private Composite mToolBarComposite;
	private Combo mProjectsCombo;
	private Button mRunButton;
	private Button mEditHierarchyButton;
	private Button mPickingButton;
	private Button mMoveButton;
	private Composite mGraphComposite;
	private ProgressUpdater mProgress;

	// The actual graph.
	private DirectedGraph<IArchitectureVertex, Edge> mGraph = new DirectedSparseMultigraph<IArchitectureVertex, Edge>();

	// Graph related objects.
	private final DefaultModalGraphMouse<IArchitectureVertex, Edge> mGraphMouse = new DefaultModalGraphMouse<IArchitectureVertex, Edge>();
	private VisualizationViewer<IArchitectureVertex, Edge> mGraphViewer;
	private Layout<IArchitectureVertex, Edge> mGraphLayout;

	// These are needed to keep track of the different nodes.
	private Map<TimComponent, TimVertex> mTimVerts = new HashMap<TimComponent, TimVertex>();
	private Map<Tactic, TacticVertex> mTacticVerts = new HashMap<Tactic, TacticVertex>();
	private Map<SubGoal, SubGoalVertex> mSubGoalVerts = new HashMap<SubGoal, SubGoalVertex>();

	/*******************************************************
	 * Constructs the hierarchy graph viewer.
	 *******************************************************/
	public HierarchyGraphViewer()
	{
		mShell = new Shell();
		initImageRegistry();

		mShell.setText("System Software Architecture Hierarchy Graph Viewer");
		mShell.setImage(mImageRegistry.getImage("tacticsIcon"));

		mShell.setLayout(new GridLayout(1, true));

		// The Graph mouse default mode
		mGraphMouse.setMode(ModalGraphMouse.Mode.PICKING);
		
		// Construct the tool bar.
		createToolBarComposite();

		// Construct the graph composite
		mGraphComposite = new Composite(mShell, SWT.BORDER);
		mGraphComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		mGraphComposite.setLayout(new FillLayout());

		// Construct the progress bar.
		mProgress = new ProgressUpdater(mShell);

		// TODO: Register handlers
		registerHandlers();

		// Register this as an observer to the changes in the java projects
		JavaProjectsListener.getInstance().registerObserver(this);

		// Unregister upon disposal of this window
		mShell.addDisposeListener(new DisposeListener()
		{
			@Override
			public void widgetDisposed(DisposeEvent e)
			{
				JavaProjectsListener.getInstance().removeObserver(HierarchyGraphViewer.this);
			}
		});

		// Display the window.
		mShell.open();
	}

	/*******************************************************
	 * Initializes the image registry of this shell. The shell must have been
	 * created prior to calling this method.
	 *******************************************************/
	private void initImageRegistry()
	{
		// Create the registry
		mImageRegistry = new SimpleImageRegistry(mShell);

		// Attach the needed images for this shell.
		mImageRegistry.registerImagePath("pickIcon", "/resources/icons/pick.png");
		mImageRegistry.registerImagePath("moveIcon", "/resources/icons/move.png");
		mImageRegistry.registerImagePath("runIcon", "/resources/icons/run.png");
		mImageRegistry.registerImagePath("archIcon", "/resources/icons/architecture.png");
		mImageRegistry.registerImagePath("tacticsIcon", "/resources/icons/tactics.png");
	}

	/*******************************************************
	 * Creates the buttons in the tool bar composite.
	 *******************************************************/
	private void createToolBarComposite()
	{
		// First, build the container
		mToolBarComposite = new Composite(mShell, SWT.BORDER);
		mToolBarComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		RowLayout rowLayout = new RowLayout();
		rowLayout.justify = false;
		rowLayout.pack = true;
		rowLayout.wrap = true;
		rowLayout.type = SWT.HORIZONTAL;
		mToolBarComposite.setLayout(rowLayout);

		// Second, build the contents
		mProjectsCombo = new Combo(mToolBarComposite, SWT.NONE);
		mProjectsCombo.setToolTipText("Select one of the Java projects available in the workspace");
		updateProjectsCombo();

		// The run button.
		mRunButton = new Button(mToolBarComposite, SWT.NONE);
		mRunButton.setText("Run");
		mRunButton.setToolTipText("Genrate and display the architecture hierarchy graph.");
		mRunButton.setImage(mImageRegistry.getImage("runIcon"));

		// The edit hierarchy button
		mEditHierarchyButton = new Button(mToolBarComposite, SWT.NONE);
		mEditHierarchyButton.setToolTipText("Edit the system's hierarchy relationships.");
		mEditHierarchyButton.setImage(mImageRegistry.getImage("archIcon"));

		// The picking button
		mPickingButton = new Button(mToolBarComposite, SWT.NONE);
		mPickingButton.setToolTipText("Set graph mouse mode to picking.");
		mPickingButton.setImage(mImageRegistry.getImage("pickIcon"));

		// The move button
		mMoveButton = new Button(mToolBarComposite, SWT.NONE);
		mMoveButton.setToolTipText("Set graph mouse mode to moving.");
		mMoveButton.setImage(mImageRegistry.getImage("moveIcon"));
	}

	/*******************************************************
	 * Updates the list of open java projects in the combo box.
	 *******************************************************/
	private void updateProjectsCombo()
	{
		// Must be done on the UI thread as it's going to be called from other
		// threads
		if (mProjectsCombo != null && !mProjectsCombo.isDisposed())
		{
			mProjectsCombo.getDisplay().asyncExec(new Runnable()
			{
				public void run()
				{
					if (mProjectsCombo != null && !mProjectsCombo.isDisposed())
					{
						// Clear the old list if any
						mProjectsCombo.removeAll();

						// Fill the combo with the list of java projects
						mProjectsCombo.add("Select a Java project ...");
						Set<String> projects = JavaProjectsListener.getInstance().getProjectsNames();
						for (String javaProj : projects)
						{
							mProjectsCombo.add(javaProj);
						}

						// Update the layout of the composites.
						mProjectsCombo.select(0);
						mProjectsCombo.pack(true);
						mProjectsCombo.layout(true, true);
						mToolBarComposite.layout(true, true);
					}
				}
			});
		}

	}

	/*******************************************************
	 * Registers the various events handlers in the tool bar buttons.
	 *******************************************************/
	private void registerHandlers()
	{
		// The graph mouse modes
		registerGraphMouseModeButtons();
	}
	
	/*******************************************************
	 * Registers the handlers for the graph mouse mode buttons.
	 *******************************************************/
	private void registerGraphMouseModeButtons()
	{
		// The picking mode
		mPickingButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e)
			{
				mGraphMouse.setMode(ModalGraphMouse.Mode.PICKING);
			}
		});
		
		// The moving mode
		mMoveButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e)
			{
				mGraphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
			}
		});
	}
	
	/*******************************************************
	 * 
	 * @see archie.views.autodetect.internals.IArchieObserver#notifyMeWithTimsChange()
	 *******************************************************/
	@Override
	public void notifyMeWithTimsChange()
	{
		// Nothing here.
	}

	/*******************************************************
	 * 
	 * @see archie.views.autodetect.internals.IArchieObserver#notifyMeWithAcceptedListChange()
	 *******************************************************/
	@Override
	public void notifyMeWithAcceptedListChange()
	{
		// Nothing here.
	}

	/*******************************************************
	 * 
	 * @see archie.views.autodetect.internals.IArchieObserver#notifyMeWithJavaProjectsChange()
	 *******************************************************/
	@Override
	public void notifyMeWithJavaProjectsChange()
	{
		// Update the combo of the projects
		updateProjectsCombo();
	}
}
