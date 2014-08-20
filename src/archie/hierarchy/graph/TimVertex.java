/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.hierarchy.graph;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import org.eclipse.ui.PlatformUI;

import archie.hierarchy.TimComponent;
import archie.model.Tim;
import archie.timstorage.TimsManager;
import archie.utils.EclipsePlatformUtils;

/*******************************************************
 * Defines a vertex for TIM components.
 * 
 * @author Ahmed Fakhry
 *******************************************************/
final class TimVertex extends AbstractArchVertex
{
	private static final Ellipse2D TIM_SHAPE = new Ellipse2D.Float(-30, -30, 60, 60);

	/*******************************************************
	 * Constructs a vertex for the given TIM component.
	 * 
	 * @param component
	 *            The {@link TimComponent} that will be represented by this
	 *            vertex.
	 *******************************************************/
	public TimVertex(TimComponent component)
	{
		super(component);
	}

	/*******************************************************
	 * 
	 * @see archie.hierarchy.graph.IArchitectureVertex#getShape()
	 *******************************************************/
	@Override
	public Shape getShape()
	{
		return TIM_SHAPE;
	}

	/*******************************************************
	 * 
	 * @see archie.hierarchy.graph.IArchitectureVertex#getColor()
	 *******************************************************/
	@Override
	public Color getColor()
	{
		return Color.LIGHT_GRAY;
	}

	/*******************************************************
	 * Overriding toString() to return just the name of the TIM rather than the
	 * whole path.
	 * 
	 * @see archie.hierarchy.graph.AbstractArchVertex#toString()
	 *******************************************************/
	@Override
	public String toString()
	{
		Tim tim = TimsManager.getInstance().findTimForAbsolutePath(mComponent.getName());

		if (tim != null)
			return tim.getAssociatedFile().getName();
		else
			return mComponent.getName();
	}

	/*******************************************************
	 * Opens the associated TIM in the editor.
	 * 
	 * @see archie.hierarchy.graph.AbstractArchVertex#handleDoubleClick()
	 *******************************************************/
	@Override
	public void handleDoubleClick()
	{
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				// Open the file in the editor.
				EclipsePlatformUtils.openFileInDefaultEditor(mComponent.getName(), true);
			}
		});
	}
}
