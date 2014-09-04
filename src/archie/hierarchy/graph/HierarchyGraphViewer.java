/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.hierarchy.graph;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Paint;
import java.awt.Shape;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JApplet;

import org.apache.commons.collections15.Transformer;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import archie.hierarchy.ArchitectureComponentsManager;
import archie.hierarchy.ArchitectureHierarchyWizard;
import archie.hierarchy.Goal;
import archie.hierarchy.IChildArchitectureComponent;
import archie.hierarchy.SubGoal;
import archie.hierarchy.Tactic;
import archie.hierarchy.TimComponent;
import archie.model.Tim;
import archie.timstorage.TimsManager;
import archie.utils.EclipsePlatformUtils;
import archie.views.autodetect.internals.IArchieObserver;
import archie.views.autodetect.internals.JavaProjectsListener;
import archie.views.autodetect.internals.ProgressUpdater;
import archie.views.autodetect.internals.SimpleImageRegistry;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

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
	private DirectedGraph<IArchitectureVertex, Edge> mGraph;

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

		// Create the legend composite.
		createLegendComposite();

		// Construct the progress bar.
		mProgress = new ProgressUpdater(mShell);

		// Register event handlers
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
		mEditHierarchyButton.setText("Edit Hierarchy And (Re)Draw");
		mEditHierarchyButton.setToolTipText("Edit the system's hierarchy relationships and draw/redraw the graph.");
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
	 * Creates the composite that contains the legend.
	 *******************************************************/
	private void createLegendComposite()
	{
		Group legend = new Group(mShell, SWT.BORDER);
		legend.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		legend.setText("Legend");
		RowLayout rowLayout = new RowLayout();
		rowLayout.justify = true;
		rowLayout.pack = true;
		rowLayout.wrap = true;
		rowLayout.type = SWT.HORIZONTAL;
		legend.setLayout(rowLayout);

		// A label for each legend element
		Label project = new Label(legend, SWT.BORDER);
		project.setText("Project Node");
		project.setBackground(ColorConstants.red);
		project.setForeground(ColorConstants.black);

		Label goal = new Label(legend, SWT.BORDER);
		goal.setText("Goal Node");
		goal.setBackground(ColorConstants.blue);
		goal.setForeground(ColorConstants.black);

		Label subGoal = new Label(legend, SWT.BORDER);
		subGoal.setText("Sub-Goal Node");
		subGoal.setBackground(ColorConstants.green);
		subGoal.setForeground(ColorConstants.black);

		Label tactic = new Label(legend, SWT.BORDER);
		tactic.setText("Tactic Node");
		tactic.setBackground(ColorConstants.yellow);
		tactic.setForeground(ColorConstants.black);

		Label tim = new Label(legend, SWT.BORDER);
		tim.setText("TIM Node");
		tim.setBackground(ColorConstants.lightGray);
		tim.setForeground(ColorConstants.black);
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
		// The edit hierarchy handler.
		registerEditHierarchyHandler();
		
		// The run button handler
		registerRunHandler();

		// The graph mouse modes
		registerGraphMouseModeButtons();
	}

	/*******************************************************
	 * Registers the handler for the button click on the edit hierarchy button.
	 *******************************************************/
	private void registerEditHierarchyHandler()
	{
		mEditHierarchyButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e)
			{
				editHierarchy();
			}
		});
	}
	
	/*******************************************************
	 * Opens the hierarchy editor wizard.
	 *******************************************************/
	private void editHierarchy()
	{
		// Open the architecture hierarchy wizard.
		new ArchitectureHierarchyWizard(new Runnable()
		{
			@Override
			public void run()
			{
				// Redraw the graph.
				HierarchyGraphViewer.this.run();
			}
		});
	}

	/*******************************************************
	 * Registers the run button handler.
	 *******************************************************/
	private void registerRunHandler()
	{
		// The run button click handler.
		mRunButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e)
			{
				run();
			}
		});
	}

	/*******************************************************
	 * Handles the run button.
	 *******************************************************/
	private void run()
	{
		// Validate that there is a Java project selected.
		int sel = mProjectsCombo.getSelectionIndex();
		if (sel < 1)
		{
			// There is not project selected.
			EclipsePlatformUtils.showErrorMessage("Error", "You must select a Java project first in order to draw the graph!");
			return;
		}

		IJavaProject selectedProject = JavaProjectsListener.getInstance().getJavaProject(
				mProjectsCombo.getText());

		// Validate that the relationships between the architecture
		// components have been already built
		// If not, force the user to build them first.
		if (!ArchitectureComponentsManager.getInstance().isHierarchyBuilt())
		{
			editHierarchy();
		}
		else
		{
			buildAndDrawGraph(selectedProject);
		}
	}
	
	/*******************************************************
	 * Builds the graph and draw it. The operations are done in a separate
	 * thread than that of UI.
	 * 
	 * @param selectedProject
	 *            The selected java project.
	 *******************************************************/
	private void buildAndDrawGraph(final IJavaProject selectedProject)
	{
		if (selectedProject == null)
			throw new IllegalArgumentException();

		// Do everything on a separate thread.
		new Thread("Graph")
		{
			public void run()
			{
				// Each step corresponds to 20% of the overall progress.
				mProgress.setProgressText("Building Graph ...").setProgressValue(0).setVisible(true).run();

				// Create the graph object
				mGraph = new DirectedSparseMultigraph<IArchitectureVertex, Edge>();

				// The following order of creation is very significant.
				addTimComponentVerts(selectedProject);

				// ---- 20% ------
				mProgress.setProgressValue(20).run();

				// ###### TACTICS VERTS ######
				addTacticsVertsAndEdges();

				// ---- 40% ------
				mProgress.setProgressValue(40).run();

				// ###### SUB GOALS VERTS ######
				addSubGoalsVertsAndEdges();

				// ---- 60% ------
				mProgress.setProgressValue(60).run();

				// ###### GOALS VERTS ######
				addGoalsVertsAndEdges(selectedProject);

				// ---- 80% ------
				mProgress.setProgressText("Drawing Graph ...").setProgressValue(80).run();

				// ###### DRAWING ######
				drawGraph();

				// ---- 100% ------
				mProgress.setProgressText("Done").setProgressValue(100).run();

				// ---- Hide progress ----
				mProgress.setProgressText("").setProgressValue(0).setVisible(false).run();
			};

		}.start();
	}

	/*******************************************************
	 * Adds only the TIM component vertices that are marked as open And belong
	 * to the selected project.
	 * 
	 * @param selectedProject
	 *            The selected java project.
	 *******************************************************/
	private void addTimComponentVerts(IJavaProject selectedProject)
	{
		Collection<TimComponent> timComps = ArchitectureComponentsManager.getInstance().getTimComponents();

		for (TimComponent timCom : timComps)
		{
			if (timCom.isOpen())
			{
				// Verify that it belongs to the selected project.
				Tim tim = TimsManager.getInstance().findTimForAbsolutePath(timCom.getName());
				if (tim.getAssociatedFile().getProject().equals(selectedProject.getProject()))
				{
					TimVertex timVert = new TimVertex(timCom);
					mTimVerts.put(timCom, timVert);
					mGraph.addVertex(timVert);
				}
			}
		}
	}

	/*******************************************************
	 * Adds the tactics verts and links them to the TIMs that implement them.
	 *******************************************************/
	private void addTacticsVertsAndEdges()
	{
		Collection<Tactic> tactics = ArchitectureComponentsManager.getInstance().getTactics();
		for (Tactic tactic : tactics)
		{
			// Add the vertex
			TacticVertex tacticVert = new TacticVertex(tactic);
			mTacticVerts.put(tactic, tacticVert);
			mGraph.addVertex(tacticVert);

			// Add the edges of that vertex
			for (IChildArchitectureComponent child : tactic)
			{
				TimVertex startVert = mTimVerts.get(child);

				// It can be null, since it may have not been added as it may be
				// closed.
				if (startVert != null)
				{
					mGraph.addEdge(new Edge(startVert, tacticVert), startVert, tacticVert);
				}
			}
		}
	}

	/*******************************************************
	 * Adds the SubGoals verts and links them to the tactics that implement
	 * them.
	 *******************************************************/
	private void addSubGoalsVertsAndEdges()
	{
		Collection<SubGoal> subGoals = ArchitectureComponentsManager.getInstance().getSubGoals();
		for (SubGoal subGoal : subGoals)
		{
			// Add vertex
			SubGoalVertex subGoalVert = new SubGoalVertex(subGoal);
			mSubGoalVerts.put(subGoal, subGoalVert);

			// Add the edges of the vertex
			for (IChildArchitectureComponent child : subGoal)
			{
				TacticVertex startVert = mTacticVerts.get(child);
				mGraph.addEdge(new Edge(startVert, subGoalVert), startVert, subGoalVert);
			}
		}
	}

	/*******************************************************
	 * Adds the goals verts and links them to the sub goals that contribute to
	 * them.
	 *******************************************************/
	private void addGoalsVertsAndEdges(IJavaProject selectedProject)
	{
		Collection<Goal> goals = ArchitectureComponentsManager.getInstance().getGoals();

		// Create the one and only Project vertex.
		ProjectVertex projVert = new ProjectVertex(selectedProject);

		for (Goal goal : goals)
		{
			// Add vertex
			GoalVertex goalVert = new GoalVertex(goal);

			// Add an edge between each goal vertex and the one and only java
			// project vertex.
			mGraph.addEdge(new Edge(goalVert, projVert), goalVert, projVert);

			// Add the edges
			for (IChildArchitectureComponent child : goal)
			{
				SubGoalVertex startVert = mSubGoalVerts.get(child);
				mGraph.addEdge(new Edge(startVert, goalVert), startVert, goalVert);
			}
		}
	}

	/*******************************************************
	 * Performs the actual drawing of the graph.
	 *******************************************************/
	private void drawGraph()
	{
		// Run the following on the UI thread
		mGraphComposite.getDisplay().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				if (mGraphComposite != null && !mGraphComposite.isDisposed())
				{
					// Drawing the graph
					// Layouts present at package: edu.uci.ics.jung.algorithms.layout.
					mGraphLayout = new FRLayout<IArchitectureVertex, Edge>(mGraph); 

					final Rectangle size = mGraphComposite.getClientArea();

					mGraphLayout.setSize(new Dimension(size.width - 10, size.height - 10));
					mGraphViewer = new VisualizationViewer<IArchitectureVertex, Edge>(mGraphLayout);
					mGraphViewer.setPreferredSize(new Dimension(size.width, size.height));

					mGraphViewer.getRenderContext().setVertexLabelTransformer(
							new ToStringLabeller<IArchitectureVertex>());
					mGraphViewer.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<Edge>());

					// The vertices font.
					mGraphViewer.getRenderContext().setVertexFontTransformer(
							new Transformer<IArchitectureVertex, Font>()
							{
								@Override
								public Font transform(IArchitectureVertex arg0)
								{
									return new Font("Arial", Font.BOLD, 14);
								}
							});

					// The vertices shape.
					mGraphViewer.getRenderContext().setVertexShapeTransformer(
							new Transformer<IArchitectureVertex, Shape>()
							{
								@Override
								public Shape transform(IArchitectureVertex v)
								{
									return v.getShape();
								}
							});

					// The vertices color.
					mGraphViewer.getRenderContext().setVertexFillPaintTransformer(
							new Transformer<IArchitectureVertex, Paint>()
							{

								@Override
								public Paint transform(IArchitectureVertex v)
								{
									return v.getColor();
								}
							});

					mGraphViewer.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);

					// Mouse mode
					mGraphViewer.setGraphMouse(mGraphMouse);

					// The mouse listener ------
					mGraphViewer.addGraphMouseListener(new GraphMouseListener<IArchitectureVertex>()
					{
						@Override
						public void graphClicked(IArchitectureVertex v, java.awt.event.MouseEvent me)
						{
							// The double click event handler.
							if (me.getButton() == java.awt.event.MouseEvent.BUTTON1 && me.getClickCount() == 2)
							{
								v.handleDoubleClick();
							}

							me.consume();
						}

						@Override
						public void graphPressed(IArchitectureVertex v, java.awt.event.MouseEvent me)
						{
							// Nothing here.
						}

						@Override
						public void graphReleased(IArchitectureVertex v, java.awt.event.MouseEvent me)
						{
							// Nothing here.
						}
					});

					// Dispose previous children if any
					Control[] controls = mGraphComposite.getChildren();
					for (Control cont : controls)
					{
						cont.dispose();
					}

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
