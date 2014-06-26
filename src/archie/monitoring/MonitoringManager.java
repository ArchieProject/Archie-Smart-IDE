/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.monitoring;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import archie.globals.ArchieSettings;
import archie.timstorage.TimsManager;

/*******************************************************
 * Defines the manager for the monitoring behavior.
 *******************************************************/
public final class MonitoringManager
{
	/*******************************************************
	 * The singleton instance.
	 *******************************************************/
	private static final MonitoringManager INSTANCE = new MonitoringManager();

	/*******************************************************
	 * The list of the marked items.
	 *******************************************************/
	HashMap<String, List<IMarkedItem>> mMarkedItems;

	/*******************************************************
	 * @return The singleton instance of the manager.
	 *******************************************************/
	public static MonitoringManager getIntance()
	{
		return INSTANCE;
	}

	/*******************************************************
	 * private constructor for the singleton pattern.
	 *******************************************************/
	private MonitoringManager()
	{

	}

	/*******************************************************
	 * Reads the list from the database file
	 *******************************************************/
	@SuppressWarnings("unchecked")
	public void initialize()
	{
		// Deserialize from the DB file.
		try
		{
			ObjectInputStream is = new ObjectInputStream(new FileInputStream(new File(ArchieSettings.getInstance()
					.getMonitoringListFilePath())));

			Object map = is.readObject();
			boolean failed = false;

			if (map instanceof HashMap<?, ?>)
			{
				mMarkedItems = (HashMap<String, List<IMarkedItem>>) map;
			}
			else
			{
				failed = true;
			}

			is.close();

			if (failed)
			{
				throw new IOException();
			}
		}
		catch (IOException | ClassNotFoundException e)
		{
			System.err.println("Failed to read the monitoring list from database file!");
			e.printStackTrace();

			// Create a new results container
			mMarkedItems = new HashMap<String, List<IMarkedItem>>();
		}
	}

	/*******************************************************
	 * Marks all monitored items related to the given full file name and
	 * generate warnings for them. (The monitored items are the accepted files,
	 * and the files added to the TIMs).
	 * 
	 * @param fullFilePath
	 *            The full file name.
	 *******************************************************/
	public void markAndGenerateWarnings(final String fullFilePath)
	{
		if (fullFilePath == null)
		{
			throw new IllegalArgumentException();
		}

		// Mark the item and add it to the list
		// File Item
		IMarkedItem markedFileItem = new MarkedFileItem(fullFilePath);
		markedFileItem.mark();
		
		// --- Mark and add to the list
		// Code Element Item
		IMarkedItem markedCEItem = new MarkedCodeElement(fullFilePath);
		markedCEItem.mark();

		List<IMarkedItem> items = mMarkedItems.get(fullFilePath);
		if (items == null)
		{
			items = new LinkedList<IMarkedItem>();
			mMarkedItems.put(fullFilePath, items);
		}
		items.add(markedFileItem);
		items.add(markedCEItem);

		// Make sure any changes to the TIMs are saved
		TimsManager.getInstance().saveAll();
	}

	/*******************************************************
	 * Unmarks and clears the warnings on all monitored items related to the
	 * given file name. (The monitored items are the accepted files, and the
	 * files added to the TIMs).
	 * 
	 * @param fullFilePath
	 *            The full file name.
	 *******************************************************/
	public void unmarkAndClearWarnings(final String fullFilePath)
	{
		if (fullFilePath == null)
		{
			throw new IllegalArgumentException();
		}

		// Get the list of marked items associated with this file name (if any)
		// Unmark them all and clear the list.
		List<IMarkedItem> items = mMarkedItems.get(fullFilePath);

		if (items != null)
		{
			for (IMarkedItem item : items)
			{
				item.unmark();
			}

			items.clear();
		}

		// Make sure any changes to the TIMs are saved
		TimsManager.getInstance().saveAll();
	}

	/*******************************************************
	 * Saves the accepted results to the engine's database.
	 *******************************************************/
	public void saveToDatabase()
	{
		try
		{
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File(ArchieSettings.getInstance()
					.getMonitoringListFilePath())));

			// Serialize the results container.
			os.writeObject(mMarkedItems);

			os.close();
		}
		catch (IOException e)
		{
			System.err.println("Failed to save the monitoring list to the database file!");
			e.printStackTrace();
		}
	}
}
