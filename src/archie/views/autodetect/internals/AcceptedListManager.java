/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.views.autodetect.internals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import archie.globals.ArchieSettings;

/*******************************************************
 * A singleton class to manage the accepted tactics results.
 *******************************************************/
public final class AcceptedListManager
{
	// -------------------------------------------------------------------------
	// Fields
	// -------------------------------------------------------------------------

	/*******************************************************
	 * The singleton instance.
	 *******************************************************/
	private static AcceptedListManager mInstance;

	/*******************************************************
	 * The serializable container of the accepted results
	 *******************************************************/
	private AcceptedResultsContainer mResultsContainer;

	/*******************************************************
	 * The list of observers who need to be notified of any changes in the
	 * accepted list.
	 *******************************************************/
	private List<IArchieObserver> mObservers = new ArrayList<IArchieObserver>();

	// -------------------------------------------------------------------------

	/*******************************************************
	 * Gets the singleton instance.
	 * 
	 * @return The singleton instance.
	 *******************************************************/
	public static AcceptedListManager getInstance()
	{
		if (mInstance == null)
		{
			mInstance = new AcceptedListManager();
		}

		return mInstance;
	}

	/*******************************************************
	 * Private constructor for singleton
	 *******************************************************/
	private AcceptedListManager()
	{
		// Deserialize from the DB file.
		try
		{
			ObjectInputStream is = new ObjectInputStream(new FileInputStream(new File(ArchieSettings.getInstance()
					.getDatabaseFilePath())));

			mResultsContainer = (AcceptedResultsContainer) is.readObject();

			is.close();
		}
		catch (IOException | ClassNotFoundException e)
		{
			System.err.println("Failed to read accepted results from database file!");
			e.printStackTrace();

			// Create a new results container
			mResultsContainer = new AcceptedResultsContainer();
		}
	}

	/*******************************************************
	 * Registers the given observer.
	 * 
	 * @param observer
	 *            The observer that will be notified of any change in the
	 *            accepted list.
	 *******************************************************/
	public void registerObserver(IArchieObserver observer)
	{
		mObservers.add(observer);
	}

	/*******************************************************
	 * Unregisters the given observer.
	 * 
	 * @param observer
	 *            The observer to be unregistered.
	 *******************************************************/
	public void removeObserver(IArchieObserver observer)
	{
		mObservers.remove(observer);
	}

	/*******************************************************
	 * Adds the given quality item to the list of accepted results.
	 * 
	 * @param item
	 *            The accepted quality item.
	 *******************************************************/
	public void acceptQualityItem(TreeQualityItem item)
	{
		mResultsContainer.acceptQualityItem(item);
		notifyObservers();
	}

	/*******************************************************
	 * Adds the given file to the list of accepted results.
	 * 
	 * @param item
	 *            The accepted file item.
	 *******************************************************/
	public void acceptFileItem(TreeFileItem item)
	{
		mResultsContainer.acceptFileItem(item);
		notifyObservers();
	}

	/*******************************************************
	 * Removes the given quality item from the accepted list.
	 * 
	 * @param item
	 *            The rejected quality item.
	 *******************************************************/
	public void rejectQualityItem(TreeQualityItem item)
	{
		mResultsContainer.rejectQualityItem(item);
		notifyObservers();
	}

	/*******************************************************
	 * Removes the given file item from the accepted list.
	 * 
	 * @param item
	 *            The rejected file item.
	 *******************************************************/
	public void rejectFileItem(TreeFileItem item)
	{
		mResultsContainer.rejectFileItem(item);
		notifyObservers();
	}

	/*******************************************************
	 * Clears the accepted results list
	 *******************************************************/
	public void clear()
	{
		mResultsContainer.clear();
		notifyObservers();
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
		return mResultsContainer.containsFileFullPath(fullFilePath);
	}

//	/*******************************************************
//	 * Tests if a file whose name (ex. "source.java", i.e. non full path) exists
//	 * on the accepted list.
//	 * 
//	 * @param fileName
//	 *            The non-full-path name of the file to test.
//	 * 
//	 * @return The found {@link TreeFileItem} object, or null if not found.
//	 *******************************************************/
//	public TreeFileItem containsFileName(String fileName)
//	{
//		return mResultsContainer.containsFileName(fileName);
//	}

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
		return mResultsContainer.getQualityItem(qualityName);
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
		return mResultsContainer.containsQualityName(qualityName);
	}
	
	/*******************************************************
	 * Gets the list of the results as an array.
	 * 
	 * @return The list of the results as an array.
	 *******************************************************/
	public TreeQualityItem[] toArray()
	{
		return mResultsContainer.toArray();
	}

	/*******************************************************
	 * Saves the accepted results to the engine's database.
	 *******************************************************/
	public void saveToDatabase()
	{
		try
		{
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File(ArchieSettings.getInstance()
					.getDatabaseFilePath())));

			// Serialize the results container.
			os.writeObject(mResultsContainer);

			os.close();
		}
		catch (IOException e)
		{
			System.err.println("Failed to save to the database file!");
			e.printStackTrace();
		}
	}
	
	/*******************************************************
	 * Notifies the observers of the change.
	 *******************************************************/
	public void notifyObservers()
	{
		for(IArchieObserver obs : mObservers)
		{
			obs.notifyMeWithAcceptedListChange();
		}
	}
}
