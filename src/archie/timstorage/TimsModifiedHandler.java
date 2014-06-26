
package archie.timstorage;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

/**
 * 
 * @author Mateusz Wieloch
 */
public class TimsModifiedHandler implements IResourceChangeListener
{
	private static volatile TimsModifiedHandler singleton;

	private TimsModifiedHandler()
	{
	}

	/**
	 * 
	 * @return
	 */
	public static TimsModifiedHandler getInstance()
	{
		if (singleton == null)
		{
			synchronized (TimsModifiedHandler.class)
			{
				if (singleton == null)
					singleton = new TimsModifiedHandler();
			}
		}
		return singleton;
	}

	/**
	 * 
	 */
	@Override
	public void resourceChanged(IResourceChangeEvent event)
	{
		try
		{
			if (event.getDelta() != null)
				event.getDelta().accept(new ResourceDeltaVisitor());

		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
	}

	private class ResourceDeltaVisitor implements IResourceDeltaVisitor
	{
		@Override
		public boolean visit(IResourceDelta delta)
		{
			IResource resource = delta.getResource();

			if (isFileWithExt(resource, "tim"))
			{
				IFile file = (IFile) resource;
				if (contentChanged(delta))
				{
					// This caused a huge bug:
					// TimsManager.getInstance().reload(file);
				}
				if (moved(delta))
				{
					TimsManager.getInstance().remove(delta.getMovedFromPath());
					TimsManager.getInstance().add(file);
				}
				if (added(delta))
				{
					TimsManager.getInstance().add(file);
				}
				if (removed(delta))
				{
					TimsManager.getInstance().remove(file);
				}
			}

			return true;
		}

		private boolean contentChanged(IResourceDelta delta)
		{
			return delta.getKind() == IResourceDelta.CHANGED && (delta.getFlags() & IResourceDelta.CONTENT) != 0;
		}

		private boolean moved(IResourceDelta delta)
		{
			return delta.getKind() == IResourceDelta.ADDED && (delta.getFlags() & IResourceDelta.MOVED_FROM) != 0;
		}

		private boolean added(IResourceDelta delta)
		{
			return delta.getKind() == IResourceDelta.ADDED && (delta.getFlags() & IResourceDelta.MOVED_FROM) == 0;
		}

		private boolean removed(IResourceDelta delta)
		{
			return delta.getKind() == IResourceDelta.REMOVED;
		}

		private boolean isFileWithExt(IResource resource, String extension)
		{
			return resource.getType() == IResource.FILE && extension.equalsIgnoreCase(resource.getFileExtension());
		}
	};
}
