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
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JApplet;

import org.apache.commons.collections15.Transformer;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import archie.hierarchy.ArchitectureComponentsManager;
import archie.hierarchy.Goal;
import archie.hierarchy.IChildArchitectureComponent;
import archie.hierarchy.SubGoal;
import archie.hierarchy.Tactic;
import archie.hierarchy.TimComponent;
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
 * Builds and displays the software architecture hierarchy graph.
 * 
 * @author Ahmed Fakhry
 *******************************************************/
public final class HierarchyGraph
{
	// The actual graph.
	DirectedGraph<IArchitectureVertex, Edge> mGraph = new DirectedSparseMultigraph<IArchitectureVertex, Edge>();

	// These are needed to keep track of the different nodes.
	Map<TimComponent, TimVertex> mTimVerts = new HashMap<TimComponent, TimVertex>();
	Map<Tactic, TacticVertex> mTacticVerts = new HashMap<Tactic, TacticVertex>();
	Map<SubGoal, SubGoalVertex> mSubGoalVerts = new HashMap<SubGoal, SubGoalVertex>();

	/*******************************************************
	 * Creates and displays the architecture hierarchy graph of the currently
	 * opened projects.
	 *******************************************************/
	public HierarchyGraph()
	{
		// first build the graph to be drawn.
		buildGraph();

		// draw it.
		drawGraph();
		
		// For testing purposes
		new HierarchyGraphViewer();
	}

	/*******************************************************
	 * Builds the graph by extracting the verts and edges from the system.
	 *******************************************************/
	private void buildGraph()
	{
		// This exact order or creation is very significant.
		addTimComponentVerts();

		addTacticsVertsAndEdges();

		addSubGoalsVertsAndEdges();

		addGoalsVertsAndEdges();
	}

	/*******************************************************
	 * Adds only the TIM component vertices that are marked as open.
	 *******************************************************/
	private void addTimComponentVerts()
	{
		Collection<TimComponent> timComps = ArchitectureComponentsManager.getInstance().getTimComponents();
		for (TimComponent timCom : timComps)
		{
			if (timCom.isOpen())
			{
				TimVertex timVert = new TimVertex(timCom);
				mTimVerts.put(timCom, timVert);
				mGraph.addVertex(timVert);
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
	private void addGoalsVertsAndEdges()
	{
		Collection<Goal> goals = ArchitectureComponentsManager.getInstance().getGoals();
		for (Goal goal : goals)
		{
			// Add vertex
			GoalVertex goalVert = new GoalVertex(goal);

			// Add the edges
			for (IChildArchitectureComponent child : goal)
			{
				SubGoalVertex startVert = mSubGoalVerts.get(child);
				mGraph.addEdge(new Edge(startVert, goalVert), startVert, goalVert);
			}
		}
	}

	/*******************************************************
	 * Creates a window in which to draw the graph.
	 *******************************************************/
	private void drawGraph()
	{
		// Drawing the graph
		Layout<IArchitectureVertex, Edge> layout =  new edu.uci.ics.jung.algorithms.layout.ISOMLayout<IArchitectureVertex, Edge>(mGraph);
		layout.setSize(new Dimension(1014, 758));
		final VisualizationViewer<IArchitectureVertex, Edge> vv = new VisualizationViewer<IArchitectureVertex, Edge>(
				layout);
		vv.setPreferredSize(new Dimension(1024, 768));

		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<IArchitectureVertex>());
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<Edge>());

		vv.getRenderContext().setVertexFontTransformer(new Transformer<IArchitectureVertex, Font>()
		{
			@Override
			public Font transform(IArchitectureVertex arg0)
			{
				return new Font("Monospaced", Font.BOLD, 12);
			}
		});

		vv.getRenderContext().setVertexShapeTransformer(new Transformer<IArchitectureVertex, Shape>()
		{
			@Override
			public Shape transform(IArchitectureVertex v)
			{
				return v.getShape();
			}
		});

		vv.getRenderContext().setVertexFillPaintTransformer(new Transformer<IArchitectureVertex, Paint>()
		{

			@Override
			public Paint transform(IArchitectureVertex v)
			{
				return v.getColor();
			}
		});

		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);

		// Mouse picking mode
		final DefaultModalGraphMouse<IArchitectureVertex, Edge> gm = new DefaultModalGraphMouse<IArchitectureVertex, Edge>();
		gm.setMode(ModalGraphMouse.Mode.PICKING);
		vv.setGraphMouse(gm);

		// The mouse listener ------
		vv.addGraphMouseListener(new GraphMouseListener<IArchitectureVertex>()
		{
			@Override
			public void graphClicked(IArchitectureVertex v, MouseEvent me)
			{
				// The double click event handler.
				if (me.getButton() == MouseEvent.BUTTON1 && me.getClickCount() == 2)
				{
					v.handleDoubleClick();
				}

				me.consume();
			}

			@Override
			public void graphPressed(IArchitectureVertex v, MouseEvent me)
			{
				// Nothing here.
			}

			@Override
			public void graphReleased(IArchitectureVertex v, MouseEvent me)
			{
				// Nothing here.
			}
		});

		/*****************************************************************************
		 * Quick And Dirty View --------------------
		 *****************************************************************************/

		Shell shell = new Shell();
		shell.setText("Software Architecture Component Graph");
		shell.setLayout(new FillLayout());
		shell.setSize(1024, 768);
		shell.setImage(ImageDescriptor.createFromURL(this.getClass().getResource("/resources/icons/tactics.png"))
				.createImage());

		Composite graphComp = new Composite(shell, SWT.EMBEDDED | SWT.NO_BACKGROUND | SWT.BORDER);

		Frame frame = SWT_AWT.new_Frame(graphComp);
		JApplet ja = new JApplet();
		ja.add(vv);
		frame.add(ja);

		shell.open();
	}
}
