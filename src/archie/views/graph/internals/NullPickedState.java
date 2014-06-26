/**

 ##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
 ##########################

 **/

package archie.views.graph.internals;

import java.awt.event.ItemListener;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.core.ICompilationUnit;

import edu.uci.ics.jung.visualization.picking.PickedState;

/*******************************************************
 * @author Ahmed Fakhry
 * 
 * Defines a null object pattern for the picked state. A null object is an
 * object that performs all the operations of an interface by doing nothing. It
 * safely replaces setting a reference to null and saves us the need to check
 * for null references.
 *******************************************************/
public final class NullPickedState implements PickedState<ICompilationUnit>
{

	/*******************************************************
	 * 
	 * @see java.awt.ItemSelectable#getSelectedObjects()
	 *******************************************************/
	@Override
	public Object[] getSelectedObjects()
	{
		return new Object[] {};
	}

	/*******************************************************
	 * 
	 * @see java.awt.ItemSelectable#addItemListener(java.awt.event.ItemListener)
	 *******************************************************/
	@Override
	public void addItemListener(ItemListener l)
	{
		// Nothing
	}

	/*******************************************************
	 * 
	 * @see java.awt.ItemSelectable#removeItemListener(java.awt.event.ItemListener)
	 *******************************************************/
	@Override
	public void removeItemListener(ItemListener l)
	{
		// Nothing
	}

	/*******************************************************
	 * 
	 * @see edu.uci.ics.jung.visualization.picking.PickedState#pick(java.lang.Object, boolean)
	 *******************************************************/
	@Override
	public boolean pick(ICompilationUnit v, boolean b)
	{
		return false;
	}

	/*******************************************************
	 * 
	 * @see edu.uci.ics.jung.visualization.picking.PickedState#clear()
	 *******************************************************/
	@Override
	public void clear()
	{
		// Nothing
	}

	/*******************************************************
	 * 
	 * @see edu.uci.ics.jung.visualization.picking.PickedState#getPicked()
	 *******************************************************/
	@Override
	public Set<ICompilationUnit> getPicked()
	{
		return new TreeSet<ICompilationUnit>();
	}

	/*******************************************************
	 * 
	 * @see edu.uci.ics.jung.visualization.picking.PickedState#isPicked(java.lang.Object)
	 *******************************************************/
	@Override
	public boolean isPicked(ICompilationUnit v)
	{
		return false;
	}

}
