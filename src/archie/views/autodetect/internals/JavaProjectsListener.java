/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.views.autodetect.internals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/*******************************************************
 * A listener to the changes in the java projects in the eclipse workspace.
 *******************************************************/
public final class JavaProjectsListener implements IElementChangedListener
{
	/*******************************************************
	 * The singleton instance
	 *******************************************************/
	private static final JavaProjectsListener INSTANCE = new JavaProjectsListener();

	/*******************************************************
	 * The list of the projects.
	 *******************************************************/
	Map<String, IJavaProject> mProjects = new HashMap<String, IJavaProject>();

	/*******************************************************
	 * The list of observers who need to be notified of any changes in the java
	 * projects list.
	 *******************************************************/
	private List<IArchieObserver> mObservers = new ArrayList<IArchieObserver>();

	/*******************************************************
	 * Gets the singleton instance.
	 * 
	 * @return The singleton instance of this class.
	 *******************************************************/
	public static JavaProjectsListener getInstance()
	{
		return INSTANCE;
	}

	/*******************************************************
	 * private constructor for singleton.
	 *******************************************************/
	private JavaProjectsListener()
	{
		// Upon the creation, get the list of current projects.
		// Get the workspace root
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		// Get all the projects
		IProject[] projects = root.getProjects();

		for (IProject proj : projects)
		{
			// Is it an "OPEN" java project?
			try
			{
				if (proj.isAccessible() && proj.isNatureEnabled("org.eclipse.jdt.core.javanature"))
				{
					// Get the project
					IJavaProject javaProj = JavaCore.create(proj);

					addJavaProject(javaProj);
				}
			}
			catch (CoreException e)
			{
				System.err.println("Couldn't get the list of java projects!");
				e.printStackTrace();
			}
		}
	}

	/*******************************************************
	 * 
	 * @see org.eclipse.jdt.core.IElementChangedListener#elementChanged(org.eclipse.jdt.core.ElementChangedEvent)
	 *******************************************************/
	@Override
	public void elementChanged(ElementChangedEvent event)
	{
		IJavaElementDelta delta = event.getDelta();

		// Test what element has changed and the type of change.F
		testDelta(delta);
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
	 * Gets a list of the java projects names managed by this listener.
	 * 
	 * @return The list of currently "open" java projects.
	 *******************************************************/
	public Set<String> getProjectsNames()
	{
		return new TreeSet<String>(mProjects.keySet());
	}

	/*******************************************************
	 * Gets the {@link IJavaProject} whose name is given, if this project exists
	 * on the list of this class.
	 * 
	 * @param projectName
	 *            The name of the java project.
	 * @return The {@link IJavaProject} whose name is given, or null if this
	 *         project is not included in the list.
	 *******************************************************/
	public IJavaProject getJavaProject(String projectName)
	{
		return mProjects.get(projectName);
	}

	/*******************************************************
	 * Tests if the delta and its sub-deltas are JavaProject specific.
	 * 
	 * @param delta
	 *            The current delta.
	 *******************************************************/
	private void testDelta(IJavaElementDelta delta)
	{
		if (delta != null)
		{
			IJavaElement element = delta.getElement();

			if (element != null)
			{
				int type = element.getElementType();

				// Only process java projects
				if (type == IJavaElement.JAVA_PROJECT)
				{
					int kind = delta.getKind();

					assert element instanceof IJavaProject;

					if (kind == IJavaElementDelta.ADDED)
					{
						addJavaProject((IJavaProject) element);
					}
					else if (kind == IJavaElementDelta.REMOVED)
					{
						removeJavaProjects((IJavaProject) element);
					}
					else if (kind == IJavaElementDelta.CHANGED)
					{
						changedProject((IJavaProject) element, delta.getFlags());
					}
				}
			}

			for (IJavaElementDelta subDelta : delta.getAffectedChildren())
			{
				testDelta(subDelta);
			}
		}
	}

	/*******************************************************
	 * A java project has been changed. This tests, based on the given delta
	 * flags, if it was opened or closed.
	 * 
	 * @param project
	 *            The modified project.
	 * @param flags
	 *            The delta flags.
	 *******************************************************/
	private void changedProject(IJavaProject project, int flags)
	{
		if ((flags & IJavaElementDelta.F_OPENED) != 0)
		{
			// A java project has been opened, add it to the list of projects
			addJavaProject(project);
		}
		else if ((flags & IJavaElementDelta.F_CLOSED) != 0)
		{
			// A java project has been closed, remove it from the list of
			// projects
			removeJavaProjects(project);
		}
	}

	/*******************************************************
	 * Adds the specified project to the list.
	 * 
	 * @param project
	 *            The project to be added.
	 *******************************************************/
	private void addJavaProject(IJavaProject project)
	{
		mProjects.put(project.getElementName(), project);

		System.out.println("========================================");
		System.out.println("Project: " + project.getElementName() + " was added to the manager!");
		System.out.println("List length is " + mProjects.size());
		System.out.println("========================================");
		
		notifyObservers();
	}

	/*******************************************************
	 * Removed the specified project from the list.
	 * 
	 * @param project
	 *            The project to be removed.
	 *******************************************************/
	private void removeJavaProjects(IJavaProject project)
	{
		mProjects.remove(project.getElementName());

		System.out.println("========================================");
		System.out.println("Project: " + project.getElementName() + " was removed from the manager!");
		System.out.println("List length is " + mProjects.size());
		System.out.println("========================================");
		
		notifyObservers();
	}

	/*******************************************************
	 * Notifies the observers of the change.
	 *******************************************************/
	public void notifyObservers()
	{
		for (IArchieObserver obs : mObservers)
		{
			obs.notifyMeWithJavaProjectsChange();
		}
	}
}
