
package archie.timstorage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;

import archie.model.Tim;
import archie.model.TimPersister;
import archie.resourcesbinding.ResourcesFinder;
import archie.utils.EclipsePlatformUtils;
import archie.views.autodetect.internals.IArchieObserver;

/**
 * [Singleton] Manages Tims from the solution.
 */
public class TimsManager
{
	private static TimsManager singleton;

	private Map<String, Tim> tims = new HashMap<String, Tim>();

	/*******************************************************
	 * The list of observers (0 or many).
	 *******************************************************/
	private List<IArchieObserver> mObservers = new LinkedList<IArchieObserver>();

	// --------------------------------------------------------

	private TimsManager()
	{
	}

	/**
	 * (Singleton) Returns the only instance of the class
	 * 
	 * @return the only instance of the class
	 */
	public static TimsManager getInstance()
	{
		if (singleton == null)
		{
			synchronized (TimsManager.class)
			{
				if (singleton == null)
					singleton = new TimsManager();
			}
		}
		return singleton;
	}

	// --------------------------------------------------------------------------
	// Observable code: Added by Ahmed Fakhry
	// --------------------------------------------------------------------------

	/*******************************************************
	 * Adds the specified observer to the list of observers that will be
	 * notified of any changes.
	 * 
	 * @param observer
	 *            The observer to be added to the list.
	 *******************************************************/
	public void registerTimsObserver(IArchieObserver observer)
	{
		if (observer == null)
		{
			throw new IllegalArgumentException();
		}

		mObservers.add(observer);
	}

	/*******************************************************
	 * Removes the specified observer from the list of observers.
	 * 
	 * @param observer
	 *            The observer that wishes to unsubscribe.
	 *******************************************************/
	public void removeTimsObserver(IArchieObserver observer)
	{
		if (observer == null)
		{
			throw new IllegalArgumentException();
		}

		mObservers.remove(observer);
	}

	/*******************************************************
	 * Notifies all the observers that something has changed so that they can
	 * check this TimsManager for the list of Tims again.
	 * 
	 * This will be called whenever a TIM is added or removed. Observers upon
	 * being notified, should query the list of TIMs managed by this manager.
	 *******************************************************/
	private void notifyTimsObservers()
	{
		for (IArchieObserver observer : mObservers)
		{
			observer.notifyMeWithTimsChange();
		}
	}
	
	/*******************************************************
	 * Gets a TIM whose absolute path is given, if any exists, null otherwise.
	 * 
	 * @param path
	 * 			The absolute path of the TIM.
	 * 
	 * @return A TIM whose absolute path is given, if any exists, null otherwise.
	 *******************************************************/
	public Tim findTimForAbsolutePath(String path)
	{
		if (path == null || path.isEmpty())
			throw new IllegalArgumentException();

		return tims.get(path);
	}

	// --------------------------------------------------------------------------

	/**
	 * Scans the entire solution in search of files with *.tim extension and
	 * adds them to the managed pool.
	 */
	public void scanSolution()
	{
		for (IFile timFile : ResourcesFinder.findAllFilesInSolutionWithProvidedExtension("tim"))
		{
			add(timFile);
		}
	}

	/**
	 * Returns all Tims managed by this TimsManager
	 * 
	 * @return all Tims managed by this TimsManager
	 */
	public List<Tim> getAll()
	{
		return new ArrayList<Tim>(tims.values());
	}

	public ArrayList<String> getAllNames()
	{
		return new ArrayList<String>(tims.keySet());
	}

	/**
	 * Returns a first Tim object that is associated with the provided IFile
	 * 
	 * @param file
	 *            an IFile that a desired tim is associated with
	 * @return a first Tim object that is associated with the provided IFile;
	 *         null if no such Tim exists
	 */
	public Tim findTimFor(IFile file)
	{
		if (file == null)
			throw new IllegalArgumentException("Provided IFile is null");

		String path = new File(file.getLocationURI()).getAbsolutePath();

		return tims.get(path);
	}

	/**
	 * Returns a first Tim object that is associated with a file that located in
	 * the provided IPath
	 * 
	 * @param path
	 *            an IPath that represents a location of an IFile that a desired
	 *            tim is associated with
	 * @return a first Tim object that is associated with a file that located in
	 *         the provided IPath; null if no such Tim exists
	 */
	public Tim findTimFor(IPath path)
	{
		if (path == null)
			throw new IllegalArgumentException("Provided IPath is null");

		String strPath = path.toFile().getAbsolutePath();

		return tims.get(strPath);
	}

	/*******************************************************
	 * Saves all the TIMs
	 *******************************************************/
	public void saveAll()
	{
		for (Tim tim : tims.values())
		{
			try
			{
				TimPersister.save(tim, tim.getAssociatedFile());
			}
			catch (IOException e)
			{
				System.err.println("Couldn't save the file " + tim.getAssociatedFile().getName());
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param file
	 */
	public void add(IFile file)
	{
		// Prevent the addition of a file that already exists
		Tim tim = findTimFor(file);

		if (tim == null)
		{
			try
			{
				tim = TimPersister.loadFrom(file);
				tims.put(new File(file.getLocationURI()).getAbsolutePath(), tim);

				// Notify
				this.notifyTimsObservers();

				System.out.println("Added tim: " + file.getName());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param file
	 * @return
	 */
	public boolean remove(IFile file)
	{
		String strPath = new File(file.getLocationURI()).getAbsolutePath();
		
		Tim tim = tims.remove(strPath);

		// Notify
		if (tim != null)
		{
			// Close it if it's open in an editor without saving
			EclipsePlatformUtils.closeFileEditor(strPath, false);
			
			this.notifyTimsObservers();

			System.out.println("Removed tim: " + file.getName());

			return true;
		}

		return false;
	}

	/**
	 * 
	 * @param path
	 * @return
	 */
	public boolean remove(IPath path)
	{
		String strPath = path.toFile().getAbsolutePath();
		
		Tim tim = tims.remove(strPath);

		if (tim != null)
		{
			// Close it if it's open in an editor without saving
			EclipsePlatformUtils.closeFileEditor(strPath, false);
			
			this.notifyTimsObservers();

			System.out.println("Removed tim path: " + path.toString());

			return true;
		}

		return false;
	}

	/**
	 * 
	 * @param file
	 */
	// public void reload(IFile file)
	// {
	// remove(file);
	// add(file);
	// }
}
