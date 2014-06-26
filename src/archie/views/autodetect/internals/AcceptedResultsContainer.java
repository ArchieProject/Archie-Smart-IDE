
package archie.views.autodetect.internals;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

final class AcceptedResultsContainer implements Serializable
{
	/*******************************************************
	 * The list of accepted results.
	 *******************************************************/
	Map<String, TreeQualityItem> mResults = new TreeMap<String, TreeQualityItem>();

	/*******************************************************
	 * For Serializable.
	 *******************************************************/
	private static final long serialVersionUID = -2960344388889801409L;

	/*******************************************************
	 * Constructor
	 *******************************************************/
	public AcceptedResultsContainer()
	{
	}

	/*******************************************************
	 * Adds the given quality item to the list of accepted results.
	 * 
	 * @param item
	 *            The accepted quality item.
	 *******************************************************/
	public void acceptQualityItem(TreeQualityItem item)
	{
		// Is it already on the accepted list?
		TreeQualityItem acceptedQuality = mResults.get(item.getName());
		if (acceptedQuality != null)
		{
			// Yes, add all the remaining files in it to
			// the accepted quality item in the accepted list
			for (ITreeItem fileItem : item.getChildren())
			{
				acceptedQuality.addFileItem((TreeFileItem) fileItem);
			}
		}
		else
		{
			// No, then add that whole quality to the accepted list
			mResults.put(item.getName(), (TreeQualityItem) item);
		}
	}

	/*******************************************************
	 * Adds the given file to the list of accepted results.
	 * 
	 * @param item
	 *            The accepted file item.
	 *******************************************************/
	public void acceptFileItem(TreeFileItem item)
	{
		TreeQualityItem parent = (TreeQualityItem) item.getParent();

		TreeQualityItem acceptedQuality = mResults.get(parent.getName());
		if (acceptedQuality != null)
		{
			// This Quality is already present in the accepted list.
			// We get it and add this file to it
			acceptedQuality.addFileItem(item);
		}
		else
		{
			// We need to create a new Quality and add it to the
			// accepted list
			// and add this file to it.
			acceptedQuality = new TreeQualityItem(parent.getName());
			acceptedQuality.addFileItem(item);

			// Add to the accepted list
			mResults.put(acceptedQuality.getName(), acceptedQuality);
		}
	}

	/*******************************************************
	 * Removes the given quality item from the accepted list.
	 * 
	 * @param item
	 *            The rejected quality item.
	 *******************************************************/
	public void rejectQualityItem(TreeQualityItem item)
	{
		// Remove it from the accepted results.
		mResults.remove(item.getName());
	}

	/*******************************************************
	 * Removes the given file item from the accepted list.
	 * 
	 * @param item
	 *            The rejected file item.
	 *******************************************************/
	public void rejectFileItem(TreeFileItem item)
	{
		TreeQualityItem parent = (TreeQualityItem) item.getParent();

		// Remove it from the old parent
		parent.removeFile(item.getAbsolutePath());

		// Was it the last file removed?
		if (!parent.hasChildren())
		{
			// Yes, remove this quality from the accepted
			// results
			mResults.remove(parent.getName());
		}
	}

	/*******************************************************
	 * Tests if the file whose full absolute path is given is one of the
	 * accepted files.
	 * 
	 * @param fullFilePath
	 *            The full absolute path of the file to test.
	 * @return The found {@link TreeFileItem} object, or null if not found.
	 *******************************************************/
	public TreeFileItem containsFileFullPath(String fullFilePath)
	{
		for (TreeQualityItem qItem : mResults.values())
		{
			TreeFileItem fItem = qItem.getFile(fullFilePath);

			if (fItem != null && fItem.getAbsolutePath().equals(fullFilePath))
			{
				return fItem;
			}
		}

		return null;
	}

	/*******************************************************
	 * Tests if a certain quality whose name is specified is contained in the
	 * list of results.
	 * 
	 * @param qualityName
	 *            The name of the quality to test.
	 * @return True if the quality exists, false otherwise.
	 *******************************************************/
	public boolean containsQualityName(String qualityName)
	{
		return mResults.containsKey(qualityName);
	}

	/*******************************************************
	 * Gets the quality item whose name is specified if it exists, returns null
	 * otherwise.
	 * 
	 * @param qualityName
	 *            The name of the quality to get.
	 * 
	 * @return The {@link TreeQualityItem} whose name is specified, null
	 *         otherwise.
	 *******************************************************/
	public TreeQualityItem getQualityItem(String qualityName)
	{
		return mResults.get(qualityName);
	}

	/*******************************************************
	 * Gets the list of the results as an array.
	 * 
	 * @return The list of the results as an array.
	 *******************************************************/
	public TreeQualityItem[] toArray()
	{
		return mResults.values().toArray(new TreeQualityItem[mResults.size()]);
	}

	/*******************************************************
	 * Clears the accepted results.
	 *******************************************************/
	public void clear()
	{
		mResults.clear();
	}
}
