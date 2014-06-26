/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.views.autodetect.internals;

import java.io.Serializable;


abstract class AbstractTreeItem implements ITreeItem, Serializable
{	
	/*******************************************************
	 * For Serializable
	 *******************************************************/
	private static final long serialVersionUID = -5257281490639396517L;
	
	protected final String mName;
	
	/*******************************************************
	 * Constructs an abstract tree item with the given name.
	 * 
	 * @param itemName
	 *            The name of the item
	 *******************************************************/
	public AbstractTreeItem(String itemName)
	{
		mName = itemName;
	}

	/*******************************************************
	 * 
	 * @see archie.views.autodetect.internals.ITreeItem#getName()
	 *******************************************************/
	@Override
	public String getName()
	{
		return mName;
	}

	/*******************************************************
	 * 
	 * @see java.lang.Object#toString()
	 *******************************************************/
	@Override
	public String toString()
	{
		return getName();
	}
}
