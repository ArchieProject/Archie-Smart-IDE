/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.views.autodetect.internals;

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;

/*******************************************************
 * Defines a leaf tree item to describe a file that belongs to a particular
 * quality type in the results tree view
 *******************************************************/
public final class TreeFileItem extends AbstractTreeItem implements Iterable<String>, Comparable<TreeFileItem>,
		Serializable
{
	/*******************************************************
	 * For Serializable.
	 *******************************************************/
	private static final long serialVersionUID = -699004938721009053L;

	private final File mFile;
	private final TermQualityType mFoundTerms;
	private ITreeItem mParent;
	private boolean mMarked;

	/*******************************************************
	 * Constructs a tree item that represents a found file that belongs to a
	 * particular quality type.
	 * 
	 * @param file
	 *            The file (Ex. "audit.java")
	 * @param foundTerms
	 *            The indicator terms that were found in this file under a
	 *            particular quality type.
	 *******************************************************/
	public TreeFileItem(File file, TermQualityType foundTerms)
	{
		super(file.getName());
		mFile = file;
		mFoundTerms = foundTerms;
		mMarked = false;
	}

	/*******************************************************
	 * Gets the status of the file item.
	 * 
	 * @return True if marked, false otherwise.
	 *******************************************************/
	public boolean isMarked()
	{
		return mMarked;
	}

	/*******************************************************
	 * Marks the file item.
	 *******************************************************/
	public void mark()
	{
		mMarked = true;
	}

	/*******************************************************
	 * Unmarks the file item.
	 *******************************************************/
	public void unMark()
	{
		mMarked = false;
	}

	/*******************************************************
	 * 
	 * @see archie.views.autodetect.internals.ITreeItem#hasChildren()
	 *******************************************************/
	@Override
	public boolean hasChildren()
	{
		// It's a leaf item always returns false
		return false;
	}

	/*******************************************************
	 * 
	 * @see archie.views.autodetect.internals.ITreeItem#getChildren()
	 *******************************************************/
	@Override
	public ITreeItem[] getChildren()
	{
		// A leaf item, return an array of size 0
		return new ITreeItem[0];
	}

	/*******************************************************
	 * 
	 * @see archie.views.autodetect.internals.ITreeItem#getParent()
	 *******************************************************/
	@Override
	public ITreeItem getParent()
	{
		return mParent;
	}

	/*******************************************************
	 * 
	 * @see archie.views.autodetect.internals.ITreeItem#setParent(archie.views.autodetect.internals.ITreeItem)
	 *******************************************************/
	@Override
	public void setParent(ITreeItem parent)
	{
		mParent = parent;
	}

	/*******************************************************
	 * @return The name of the quality type to which this file belongs
	 *******************************************************/
	public String getQualityName()
	{
		return mFoundTerms.getName();
	}

	/*******************************************************
	 * @return The absolute path of the file
	 *******************************************************/
	public String getAbsolutePath()
	{
		return mFile.getAbsolutePath();
	}

	/*******************************************************
	 * @return The name part of the file
	 *******************************************************/
	public String getFileName()
	{
		return mFile.getName();
	}

	/*******************************************************
	 * @return The probability of this file that it matches the quality type
	 *         under which it is listed
	 *******************************************************/
	public double getFileProbability()
	{
		return mFoundTerms.getTotalProbability();
	}

	/*******************************************************
	 * Returns an iterator over the terms found in this file under this
	 * particular quality type.
	 * 
	 * @see java.lang.Iterable#iterator()
	 *******************************************************/
	@Override
	public Iterator<String> iterator()
	{
		return mFoundTerms.iterator();
	}

	/*******************************************************
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 *******************************************************/
	@Override
	public int compareTo(TreeFileItem o)
	{
		// This could be changed later depending on how I intend to use it
		// currently it's just a place holder implementation
		int res = mFile.getAbsolutePath().compareTo(o.mFile.getAbsolutePath());

		if (res != 0)
		{
			return res;
		}

		return mFoundTerms.getName().compareTo(o.mFoundTerms.getName());
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
		
		TreeFileItem other = (TreeFileItem) obj;
		
		return mFile.equals(other.mFile);
	}
	
	/*******************************************************
	 * 
	 * @see java.lang.Object#hashCode()
	 *******************************************************/
	@Override
	public int hashCode()
	{
		return mFile.hashCode();
	}
}
