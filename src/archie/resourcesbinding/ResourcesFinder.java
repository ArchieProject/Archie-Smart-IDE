
package archie.resourcesbinding;

import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;

/**
 * Utility class. Scans solution in search of specified resources.
 */
public class ResourcesFinder
{

	/**
	 * Returns list of all files with the specified extension from the entire
	 * solution (from all projects)
	 * 
	 * @param extension
	 *            extension of files that will be returned
	 * @return list of all files with the specified extension from the entire
	 *         solution; empty list in case no files are found
	 */
	public static List<IFile> findAllFilesInSolutionWithProvidedExtension(String extension)
	{
		List<IFile> filesList = new ArrayList<>();

		// Get all projects in the workspace
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();

		// load all models from all projects
		try
		{
			for (IProject project : projects)
				filesList.addAll(findInProject(project, extension));
		}
		catch (CoreException ex)
		{
			// search of files shouldn't really end up with an exception
			ex.printStackTrace();
			System.exit(1);
		}

		return filesList;
	}

	/**
	 * 
	 * @param project
	 * @param extension
	 * @return
	 * @throws CoreException
	 */
	public static List<IFile> findInProject(IProject project, String extension) throws CoreException
	{
		List<IFile> timFiles = new ArrayList<>();

		if (project.isAccessible())
		{
			for (IResource resource : project.members())
				if (resource instanceof IFile)
				{
					IFile file = (IFile) resource;
					String ext = file.getFileExtension();
					if (ext != null && ext.equals(extension))
						timFiles.add(file);
				}
			// else if (resource instanceof IFolder) timFiles.addAll(
			// findInFolder((IFolder)resource, extension) );
		}
		return timFiles;
	}

	/**
	 * 
	 * @param folder
	 * @param extension
	 * @return
	 * @throws CoreException
	 */
	public static List<IFile> findInFolder(IFolder folder, String extension) throws CoreException
	{
		List<IFile> timFiles = new ArrayList<>();

		for (IResource resource : folder.members())
		{
			if (resource instanceof IFile)
			{
				IFile file = (IFile) resource;
				if (file.getFileExtension().equals(extension))
					timFiles.add(file);
			}
			else if (resource instanceof IFolder)
				timFiles.addAll(findInFolder((IFolder) resource, extension));
		}

		return timFiles;
	}
}
