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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import archie.globals.ArchieSettings;

/*******************************************************
 * Defines the singleton manager that manages the storage of all architecture
 * components defined in the system.
 * 
 * @author Ahmed Fakhry
 *******************************************************/
public final class ArchitectureComponentsManager
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
			mIsBuilt = false;
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

	/*******************************************************
	 * Serializes and saves the stored system architecture archie.hierarchy
	 * components to the database.
	 *******************************************************/
	public void saveToDB()
	{
		try
		{
			// Before saving, we need to make sure that the "mIsBuilt" flag is accurate.
			if(goalsSize() < 1 || subGoalsSize() < 1 || tacticsSize() < 1)
			{
				mStorage.mIsBuilt = false;
			}
			
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
	}

}
