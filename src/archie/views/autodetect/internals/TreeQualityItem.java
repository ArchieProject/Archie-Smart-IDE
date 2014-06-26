/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.views.autodetect.internals;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

/*******************************************************
 * Defines a tree node to hold the parent quality items
 *******************************************************/
public final class TreeQualityItem extends AbstractTreeItem implements Comparable<TreeQualityItem>, Serializable
{
	/*******************************************************
	 * For Serializable
	 *******************************************************/
	private static final long serialVersionUID = -1207003858820697026L;
	
	private final Map<String, TreeFileItem> mChildFileItems = new TreeMap<String, TreeFileItem>();

	/*******************************************************
	 * Constructs a parent tree item for the quality types, which contain a list
	 * of tree file items
	 * 
	 * @param qualityName
	 *            The quality tree item name
	 *******************************************************/
	public TreeQualityItem(String qualityName)
	{
		super(qualityName);
	}

	/*******************************************************
	 * 
	 * @see archie.views.autodetect.internals.ITreeItem#hasChildren()
	 *******************************************************/
	@Override
	public boolean hasChildren()
	{
		return !(mChildFileItems.isEmpty());
	}

	/*******************************************************
	 * 
	 * @see archie.views.autodetect.internals.ITreeItem#getChildren()
	 *******************************************************/
	@Override
	public ITreeItem[] getChildren()
	{
		return mChildFileItems.values().toArray(new ITreeItem[mChildFileItems.size()]);
	}

	/*******************************************************
	 * 
	 * @see archie.views.autodetect.internals.ITreeItem#getParent()
	 *******************************************************/
	@Override
	public ITreeItem getParent()
	{
		// This node has no parent
		return null;
	}

	/*******************************************************
	 * 
	 * @see archie.views.autodetect.internals.ITreeItem#setParent(archie.views.autodetect.internals.ITreeItem)
	 *******************************************************/
	@Override
	public void setParent(ITreeItem parent)
	{
		// This node has no parent
	}

	/*******************************************************
	 * Adds a file item to this tree node
	 * 
	 * @param fileItem
	 *            The file item desired to be added
	 *******************************************************/
	public void addFileItem(TreeFileItem fileItem)
	{
		fileItem.setParent(this);
		mChildFileItems.put(fileItem.getAbsolutePath(), fileItem);
	}

	/*******************************************************
	 * Returns whether the given file item is contained as one of the child items.
	 * 
	 * @param fileAbsolutePath
	 *            The full path of the file.
	 * @return true if the given file is contained here, false otherwise
	 *******************************************************/
	public boolean containsFile(String fileAbsolutePath)
	{
		return mChildFileItems.containsKey(fileAbsolutePath);
	}

	/*******************************************************
	 * Given its full path, it return the {@link TreeFileItem} that corresponds to
	 * that file
	 * 
	 * @param fileAbsolutePath
	 *            The full path of the file.
	 * @return The {@link TreeFileItem} that corresponds to the given file or
	 *         null if its not there.
	 *******************************************************/
	public TreeFileItem getFile(String fileAbsolutePath)
	{
		return mChildFileItems.get(fileAbsolutePath);
	}

	/*******************************************************
	 * Removes the given file from the children list.
	 * 
	 * @param fileAbsolutePath
	 *            The full path of the file.
	 *******************************************************/
	public void removeFile(String fileAbsolutePath)
	{
		mChildFileItems.remove(fileAbsolutePath);
	}

	/*******************************************************
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 *******************************************************/
	@Override
	public int compareTo(TreeQualityItem o)
	{
		return mName.compareTo(o.mName);
	}

	/*******************************************************
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 *******************************************************/
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj.getClass().equals(this.getClass())))
			return false;

		TreeQualityItem other = (TreeQualityItem) obj;
		
		return this.mName.equals(other.mName);
	}
	
	/*******************************************************
	 * 
	 * @see java.lang.Object#hashCode()
	 *******************************************************/
	@Override
	public int hashCode()
	{
		return this.mName.hashCode();
	}
}
