/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.monitoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import archie.views.autodetect.internals.AcceptedListManager;
import archie.views.autodetect.internals.TreeFileItem;

/*******************************************************
 * Defines a marked file item (one of the accepted files in the accepted list).
 *******************************************************/
final class MarkedFileItem implements IMarkedItem
{
	/*******************************************************
	 * Serializable ID
	 *******************************************************/
	private static final long serialVersionUID = 2619459052620906962L;

	/**
	 * The marked file item.
	 */
	private final String mFullFilePath;

	/*******************************************************
	 * Constructs a marked file item.
	 * 
	 * @param fullFilePath
	 *            The full path of the file item
	 *******************************************************/
	public MarkedFileItem(String fullFilePath)
	{
		if (fullFilePath == null)
		{
			throw new IllegalArgumentException();
		}

		mFullFilePath = fullFilePath;
	}

	/*******************************************************
	 * 
	 * @see archie.monitoring.IMarkedItem#mark()
	 *******************************************************/
	@Override
	public void mark()
	{
		// Get the resource
		IPath path = new Path(mFullFilePath);
		IFile fileResource = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);

		// Try to find it from the accepted list
		TreeFileItem fileItem = AcceptedListManager.getInstance().containsFileFullPath(mFullFilePath);

		if (fileItem != null && fileResource != null)
		{
			// Mark the file and generate warnings
			WarningsUtils.addWarningToAcceptedFile(fileItem, fileResource);
		}
		// notify observers, an accepted file is now marked.
		AcceptedListManager.getInstance().notifyObservers();
	}

	/*******************************************************
	 * 
	 * @see archie.monitoring.IMarkedItem#unmark()
	 *******************************************************/
	@Override
	public void unmark()
	{
		// Try to find it from the accepted list
		TreeFileItem fileItem = AcceptedListManager.getInstance().containsFileFullPath(mFullFilePath);

		if (fileItem != null)
		{
			// Delete warning and unmark
			WarningsUtils.deleteWarningFromFileItem(fileItem);
		}
		// notify observers, an accepted file is now unmarked.
		AcceptedListManager.getInstance().notifyObservers();
	}

}
