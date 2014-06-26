/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.monitoring;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import archie.globals.ArchieSettings;
import archie.model.Tim;
import archie.model.shapes.CodeElement;
import archie.utils.EclipsePlatformUtils;
import archie.views.autodetect.internals.TreeFileItem;

/*******************************************************
 * Utility static methods to handle TIM-related operations
 *******************************************************/
final class WarningsUtils
{
	/*******************************************************
	 * private - non-instantiable
	 *******************************************************/
	private WarningsUtils()
	{
	}

	/*******************************************************
	 * Adds a warning to the specified code element in the specified TIM.
	 * 
	 * @param ce
	 *            The {@link CodeElement} for which a warning will be generated.
	 * @param tim
	 *            The {@link Tim} file on which the the given code element
	 *            reside.
	 * @return true if succeeded in adding the warning, false otherwise.
	 *******************************************************/
	public static boolean addWarningToCodeElement(CodeElement ce, Tim tim)
	{
		if (ce == null || tim == null)
		{
			throw new IllegalArgumentException();
		}

		try
		{
			String filePath = ce.getAssociatedPath();

			// Add the marker
			IMarker marker = EclipsePlatformUtils.addMarker(tim.getAssociatedFile(), ArchieSettings.TIM_MARKER_TYPE,
					"The file " + filePath + " is linked to this TIM, Please review!", IMarker.SEVERITY_WARNING, 1);

			// Add the code element attribute
			marker.setAttribute(ArchieSettings.CODE_ELEMENT_MARKER_ATTRIB, ce.getQualifiedName() + ce.getParentShape());

			return true;
		}
		catch (CoreException e)
		{
			System.err.println("Unable to create marker!" + e);

			return false;
		}
	}

	/*******************************************************
	 * Marks the given TreeFileItem and adds a warning to its given associated
	 * resource.
	 * 
	 * @note Warnings will only be added if the file item is not marked already.
	 *       This is to prevent multiple warnings for the same file.
	 * 
	 * @param fileItem
	 *            The file item to mark.
	 * @param associatedResource
	 *            The associated resource with the file item.
	 * @return true if succeeded in adding the warning, false otherwise.
	 *******************************************************/
	public static boolean addWarningToAcceptedFile(TreeFileItem fileItem, IResource associatedResource)
	{
		if (fileItem == null || fileItem == null)
		{
			throw new IllegalArgumentException();
		}

		try
		{
			if (!fileItem.isMarked())
			{
				fileItem.mark();

				String filePath = fileItem.getAbsolutePath();

				// Add the marker
				IMarker marker = EclipsePlatformUtils.addMarker(associatedResource, ArchieSettings.TIM_MARKER_TYPE,
						"The file " + filePath + " belongs to an accepted tactic, Please review!",
						IMarker.SEVERITY_WARNING, 1);

				// Add the code element attribute
				marker.setAttribute(ArchieSettings.FILE_ITEM_MARKER_ATTRIB, filePath);
			}

			return true;
		}
		catch (CoreException e)
		{
			System.err.println("Unable to create marker!" + e);

			return false;
		}
	}
	
	/*******************************************************
	 * 
	 * @param fItem
	 * @return
	 *******************************************************/
	public static boolean deleteWarningFromFileItem(TreeFileItem fItem)
	{
		if (fItem == null)
		{
			throw new IllegalArgumentException();
		}

		boolean result = false;

		// Delete the warning markers upon the acceptance of the code
		// element changes
		Map<String, Object> attribs = new TreeMap<String, Object>();

		attribs.put(ArchieSettings.FILE_ITEM_MARKER_ATTRIB, fItem.getAbsolutePath());

		IResource resource = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(fItem.getAbsolutePath()));
		
		if(resource == null)
		{
			resource = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fItem.getAbsolutePath()));
		}
		
		// Find the markers that matches these attributes
		IMarker[] markers = EclipsePlatformUtils.findSpecificMarkers(resource,
				ArchieSettings.TIM_MARKER_TYPE, attribs);

		if (markers != null && markers.length != 0)
		{
			result = true;
		}

		// Unmark
		fItem.unMark();
		
		// Delete them
		EclipsePlatformUtils.deleteMarkers(markers);

		return result;
	}

	/*******************************************************
	 * Deletes the warning associated with the given code element in the given
	 * TIM file.
	 * 
	 * @param ce
	 *            The {@link CodeElement} for which the warning will be deleted.
	 * @param tim
	 *            The {@link Tim} file on which the code element resides.
	 * 
	 * @return true if markers were actually found and deleted, false otherwise.
	 *******************************************************/
	public static boolean deleteWarningFromCodeElement(CodeElement ce, Tim tim)
	{
		if (ce == null || tim == null)
		{
			throw new IllegalArgumentException();
		}

		boolean result = false;

		// Delete the warning markers upon the acceptance of the code
		// element changes
		Map<String, Object> attribs = new TreeMap<String, Object>();

		attribs.put(ArchieSettings.CODE_ELEMENT_MARKER_ATTRIB, ce.getQualifiedName() + ce.getParentShape());

		// Find the markers that matches these attributes
		IMarker[] markers = EclipsePlatformUtils.findSpecificMarkers(tim.getAssociatedFile(),
				ArchieSettings.TIM_MARKER_TYPE, attribs);

		if (markers != null && markers.length != 0)
		{
			result = true;
		}

		// Delete them
		EclipsePlatformUtils.deleteMarkers(markers);

		return result;
	}
}
