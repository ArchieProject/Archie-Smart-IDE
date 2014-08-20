/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.hierarchy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import archie.globals.ArchieSettings;
import archie.timstorage.TimsManager;
import archie.views.autodetect.internals.IArchieObserver;

/*******************************************************
 * Defines the singleton manager that manages the storage of all architecture
 * components defined in the system.
 * 
 * @author Ahmed Fakhry
 *******************************************************/
public final class ArchitectureComponentsManager implements IArchieObserver
{
	/*******************************************************
	 * The singleton instance.
	 *******************************************************/
	private static final ArchitectureComponentsManager INSTANCE = new ArchitectureComponentsManager();

	/*******************************************************
	 * A small class that acts as a wrapper for the storage of the components.
	 *******************************************************/
	private static class ArchCompStorage implements Serializable
	{
		/*******************************************************
		 * For Serialization.
		 *******************************************************/
		private static final long serialVersionUID = 1L;

		// The storage for the various components
		Map<String, Goal> mGoals = new HashMap<String, Goal>();
		Map<String, SubGoal> mSubGoals = new HashMap<String, SubGoal>();
		Map<String, Tactic> mTactics = new HashMap<String, Tactic>();
		Map<String, TimComponent> mTimComponents = new HashMap<String, TimComponent>();

		// Tests whether a archie.hierarchy for the system has been defined and
		// built.
		boolean mIsBuilt = false;

		/*******************************************************
		 * Clears all the components.
		 *******************************************************/
		void clearAll()
		{
			mGoals.clear();
			mSubGoals.clear();
			mTactics.clear();
			mTimComponents.clear();
			mIsBuilt = false;
		}
		
		/*******************************************************
		 * Validates that the storage is actually built.
		 *******************************************************/
		void validate()
		{
			if (mGoals.size() < 1 || mSubGoals.size() < 1 || mTactics.size() < 1 || mTimComponents.size() < 1)
			{
				mIsBuilt = false;
			}
		}
	}

	// The storage instance
	private ArchCompStorage mStorage;

	/*******************************************************
	 * @return The singleton instance of this class.
	 *******************************************************/
	public static ArchitectureComponentsManager getInstance()
	{
		return INSTANCE;
	}

	/*******************************************************
	 * Clears all the stored components.
	 *******************************************************/
	public void clearAll()
	{
		mStorage.clearAll();
	}

	/*******************************************************
	 * @return Whether the archie.hierarchy of the architecture goals has been
	 *         built.
	 *******************************************************/
	public boolean isHierarchyBuilt()
	{
		return (mStorage.mIsBuilt == true);
	}

	// -------------------------------

	/*******************************************************
	 * @return The number of goals defined in the system.
	 *******************************************************/
	public int goalsSize()
	{
		return mStorage.mGoals.size();
	}

	/*******************************************************
	 * @return The number of sub goals defined in the system.
	 *******************************************************/
	public int subGoalsSize()
	{
		return mStorage.mSubGoals.size();
	}

	/*******************************************************
	 * @return The number of tactics defined in the system.
	 *******************************************************/
	public int tacticsSize()
	{
		return mStorage.mTactics.size();
	}

	/*******************************************************
	 * @return The number of the TIM components in the system.
	 *******************************************************/
	public int timComponentsSize()
	{
		return mStorage.mTimComponents.size();
	}

	/*******************************************************
	 * Creates a goal and adds it to the list, provided that the given goal name
	 * is unique (i.e. no other goal has been created with the same name
	 * before).
	 * 
	 * @param goalName
	 *            The unique name of the new goal to create. [Cannot be null or
	 *            empty.]
	 * 
	 * @return true if creation was successful or false otherwise.
	 *******************************************************/
	public boolean addGoal(String goalName)
	{
		// Cannot be null or empty.
		if (goalName == null || goalName.isEmpty())
			throw new IllegalArgumentException();

		// Does it already exist?
		if (mStorage.mGoals.get(goalName) != null)
			return false;

		// Create and store the new goal, and return true.
		mStorage.mGoals.put(goalName, new Goal(goalName));
		return true;
	}

	/*******************************************************
	 * Removes a goal whose name is given (if such a goal exists).
	 * 
	 * @param goalName
	 *            The unique name of the goal to remove.
	 *******************************************************/
	public void removeGoal(String goalName)
	{
		mStorage.mGoals.remove(goalName);
	}

	/*******************************************************
	 * Gets the goal object whose name is provided, or null if it doesn't exist.
	 * 
	 * @param goalName
	 *            The name of the goal.
	 * @return The {@link Goal} object if it exists, or null otherwise.
	 *******************************************************/
	public Goal getGoal(String goalName)
	{
		return mStorage.mGoals.get(goalName);
	}

	/*******************************************************
	 * @return A set that contains the list of goal names that are present in
	 *         the system.
	 *******************************************************/
	public Set<String> getGoalNames()
	{
		return mStorage.mGoals.keySet();
	}

	// -------------------------------

	/*******************************************************
	 * Creates a sub-goal and adds it to the list, provided that the given
	 * sub-goal name is unique (i.e. no other sub-goal has been created with the
	 * same name before).
	 * 
	 * @param subGoalName
	 *            The unique name of the new sub-goal to create. [Cannot be null
	 *            or empty.]
	 * 
	 * @return true if creation was successful or false otherwise.
	 *******************************************************/
	public boolean addSubGoal(String subGoalName)
	{
		// Cannot be null or empty.
		if (subGoalName == null || subGoalName.isEmpty())
			throw new IllegalArgumentException();

		// Does it already exist?
		if (mStorage.mSubGoals.get(subGoalName) != null)
			return false;

		// Create and store the new sub-goal, and return true.
		mStorage.mSubGoals.put(subGoalName, new SubGoal(subGoalName));
		return true;
	}

	/*******************************************************
	 * Removes a sub-goal whose name is given (if such a sub-goal exists).
	 * 
	 * @param subGoalName
	 *            The unique name of the sub-goal to remove.
	 *******************************************************/
	public void removeSubGoal(String subGoalName)
	{
		mStorage.mSubGoals.remove(subGoalName);
	}

	/*******************************************************
	 * Gets the sub-goal object whose name is provided, or null if it doesn't
	 * exist.
	 * 
	 * @param subGoalName
	 *            The name of the sub-goal.
	 * @return The {@link SubGoal} object if it exists, or null otherwise.
	 *******************************************************/
	public SubGoal getSubGoal(String subGoalName)
	{
		return mStorage.mSubGoals.get(subGoalName);
	}

	/*******************************************************
	 * @return A set that contains the list of sub-goal names that are present
	 *         in the system.
	 *******************************************************/
	public Set<String> getSubGoalNames()
	{
		return mStorage.mSubGoals.keySet();
	}

	// -------------------------------

	/*******************************************************
	 * Creates a tactic and adds it to the list, provided that the given tactic
	 * name is unique (i.e. no other tactic has been created with the same name
	 * before).
	 * 
	 * @param tacticName
	 *            The unique name of the new tactic to create. [Cannot be null
	 *            or empty.]
	 * 
	 * @return true if creation was successful or false otherwise.
	 *******************************************************/
	public boolean addTactic(String tacticName)
	{
		// Cannot be null or empty.
		if (tacticName == null || tacticName.isEmpty())
			throw new IllegalArgumentException();

		// Does it already exist?
		if (mStorage.mTactics.get(tacticName) != null)
			return false;

		// Create and store the new sub-goal, and return true.
		mStorage.mTactics.put(tacticName, new Tactic(tacticName));
		return true;
	}

	/*******************************************************
	 * Removes a tactic whose name is given (if such a tactic exists).
	 * 
	 * @param tacticName
	 *            The unique name of the tactic to remove.
	 *******************************************************/
	public void removeTactic(String tacticName)
	{
		mStorage.mTactics.remove(tacticName);
	}

	/*******************************************************
	 * Gets the tactic object whose name is provided, or null if it doesn't
	 * exist.
	 * 
	 * @param tacticName
	 *            The name of the tactic.
	 * @return The {@link Tactic} object if it exists, or null otherwise.
	 *******************************************************/
	public Tactic getTactic(String tacticName)
	{
		return mStorage.mTactics.get(tacticName);
	}

	/*******************************************************
	 * @return A set that contains the list of tactic names that are present in
	 *         the system.
	 *******************************************************/
	public Set<String> getTacticNames()
	{
		return mStorage.mTactics.keySet();
	}

	// -------------------------------

	/*******************************************************
	 * Creates a TIM Component and adds it to the list, provided that the given
	 * TIM Component name is unique (i.e. no other TIM Component has been
	 * created with the same name before).
	 * 
	 * @param timFilePath
	 *            The absolute path of the TIM file that will be represented by
	 *            the new TIM component. [Cannot be null or empty.]
	 * 
	 * @return true if creation was successful or false otherwise.
	 *******************************************************/
	public boolean addTimComponent(String timFilePath)
	{
		// Cannot be null or empty.
		if (timFilePath == null || timFilePath.isEmpty())
			throw new IllegalArgumentException();

		// Does it already exist?
		if (mStorage.mTimComponents.get(timFilePath) != null)
			return false;

		// Create and store the new sub-goal, and return true.
		mStorage.mTimComponents.put(timFilePath, new TimComponent(timFilePath));
		return true;
	}

	/*******************************************************
	 * Removes a TIM Component whose name is given (if such a TIM Component
	 * exists).
	 * 
	 * @param timFilePath
	 *            The absolute path of the TIM file that will be removed.
	 *******************************************************/
	public void removeTimComponent(String timFilePath)
	{
		mStorage.mTimComponents.remove(timFilePath);
	}

	/*******************************************************
	 * Gets the TIM Component object whose name is provided, or null if it
	 * doesn't exist.
	 * 
	 * @param timFilePath
	 *            The name of the TIM Component.
	 * @return The {@link TimComponent} object if it exists, or null otherwise.
	 *******************************************************/
	public TimComponent getTimComponent(String timFilePath)
	{
		return mStorage.mTimComponents.get(timFilePath);
	}

	/*******************************************************
	 * @return A set that contains the list of TIM Component names (of ONLY the
	 *         opened ones) that are present in the system.
	 *******************************************************/
	public Set<String> getTimComponentNames()
	{
		Collection<TimComponent> fullSet = mStorage.mTimComponents.values();
		Set<String> result = new TreeSet<String>();

		// Only return those that are marked open.
		for (TimComponent tim : fullSet)
		{
			if (tim.isOpen())
			{
				result.add(tim.getName());
			}
		}

		return result;
	}

	// --------------------------------

	/*******************************************************
	 * Serializes and saves the stored system architecture archie.hierarchy
	 * components to the database.
	 *******************************************************/
	public void saveToDB()
	{
		try
		{
			// Before saving, we need to make sure that the "mIsBuilt" flag is
			// accurate.
			mStorage.validate();

			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File(ArchieSettings.getInstance()
					.getHierarchyDBFile())));

			// Serialize the components storage.
			os.writeObject(mStorage);

			os.close();
		}
		catch (IOException e)
		{
			System.err.println("Failed to save system architecture archie.hierarchy components to the database file!");
			e.printStackTrace();
		}
	}

	/*******************************************************
	 * Package-private: Will only be called by the UI. Sets the status of the
	 * archie.hierarchy to be built.
	 *******************************************************/
	void setHierarchyBuilt()
	{
		mStorage.mIsBuilt = true;
	}

	/*******************************************************
	 * Private constructor for singleton.
	 * 
	 * Tries to de-serialize the storage from the database file.
	 *******************************************************/
	private ArchitectureComponentsManager()
	{
		// De-serialize from the DB file.
		try
		{
			ObjectInputStream is = new ObjectInputStream(new FileInputStream(new File(ArchieSettings.getInstance()
					.getHierarchyDBFile())));

			mStorage = (ArchCompStorage) is.readObject();

			is.close();
		}
		catch (IOException | ClassNotFoundException | ClassCastException e)
		{
			System.err.println("Failed to read the Hierarchy from database file!");
			e.printStackTrace();

			// Create a new storage.
			mStorage = new ArchCompStorage();
		}

		mStorage.validate();
		
		// Must add yourself as an observer of the TIMs manager.
		TimsManager.getInstance().registerTimsObserver(this);
		
		// Get initial list.
		notifyMeWithTimsChange();
	}

	/*******************************************************
	 * 
	 * @see archie.views.autodetect.internals.IArchieObserver#notifyMeWithTimsChange()
	 *******************************************************/
	@Override
	public void notifyMeWithTimsChange()
	{
		// We will do a "Mark, Add, and Sweep" algorithm!!
		// 1- We'll get the current list of TIMs from the manager.
		// 2- Anything we already have, will be marked.
		// 3- Anything we don't have will be added.
		// 4- Anything that remains unmarked at the end will be left to the
		// sweep phase.

		// 5- The Sweep Phase:
		// 5.1 - Unmarked TIM files that are not present in the file system will
		// be removed completely.
		// 5.2 - Unmarked TIM files that still exist in the file system are most
		// probably in closed projects, mark them as closed.

		ArrayList<String> tims = TimsManager.getInstance().getAllNames();
		HashSet<TimComponent> visited = new HashSet<TimComponent>();
		
		// Mark & Add phase:
		for(String tim : tims)
		{
			// Do we already have it?
			TimComponent timComp = mStorage.mTimComponents.get(tim);
			if(timComp == null)
			{
				// We don't, create it.
				timComp = new TimComponent(tim);
				// Add it.
				mStorage.mTimComponents.put(tim, timComp);
			}
			
			// We now mark it as visited.
			visited.add(timComp);
		}
		
		// Sweep phase:
		Collection<TimComponent> timComps = mStorage.mTimComponents.values();
		for(TimComponent timComp : timComps)
		{
			// Was it marked as visited?
			if(visited.contains(timComp))
			{
				// Yes, make sure that it's open
				timComp.markOpen();
			}
			else
			{
				// No!
				// Does it exist in the file system?
				if(new File(timComp.getName()).exists())
				{
					// Yes, mark it as closed
					timComp.markClosed();
				}
				else
				{
					// No, then remove it completely
					mStorage.mTimComponents.remove(timComp.getName());
				}
			}
		}
	}

	/*******************************************************
	 * 
	 * @see archie.views.autodetect.internals.IArchieObserver#notifyMeWithAcceptedListChange()
	 *******************************************************/
	@Override
	public void notifyMeWithAcceptedListChange()
	{
		// Doesn't do anything here.
	}

	/*******************************************************
	 * 
	 * @see archie.views.autodetect.internals.IArchieObserver#notifyMeWithJavaProjectsChange()
	 *******************************************************/
	@Override
	public void notifyMeWithJavaProjectsChange()
	{
		// Doesn't do anything here.
	}

}
