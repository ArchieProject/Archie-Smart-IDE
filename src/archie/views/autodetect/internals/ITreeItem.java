/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.views.autodetect.internals;

/*******************************************************
 * Defines an interface for a composite tree item. This tree will be used as
 * input to the results tree view.
 *******************************************************/
public interface ITreeItem
{
	/*******************************************************
	 * Gets the name of the tree item.
	 * 
	 * @return The string name of the item.
	 *******************************************************/
	public String getName();

	/*******************************************************
	 * Tests whether the item has child items.
	 * 
	 * @return True if item has children, false otherwise.
	 *******************************************************/
	public boolean hasChildren();

	/*******************************************************
	 * Gets an array of the immediate child items
	 * 
	 * @return An array of children
	 *******************************************************/
	public ITreeItem[] getChildren();

	/*******************************************************
	 * Gets the parent of this item
	 * 
	 * @return The parent item.
	 *******************************************************/
	public ITreeItem getParent();

	/*******************************************************
	 * Sets the parent of this item
	 * 
	 * @param parent
	 *            The item that will be the parent of this item.
	 *******************************************************/
	public void setParent(ITreeItem parent);
}
