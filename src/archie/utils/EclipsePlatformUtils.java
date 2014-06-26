/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.SimpleMarkerAnnotation;
import org.eclipse.ui.wizards.IWizardDescriptor;

/*******************************************************
 * Defines a static class that groups some utility static methods to deal with
 * the eclipse platform.
 * 
 * @author Ahmed Fakhry
 *******************************************************/
public final class EclipsePlatformUtils
{
	/*******************************************************
	 * Private constructor, not to be instantiable
	 *******************************************************/
	private EclipsePlatformUtils()
	{
	}

	/*******************************************************
	 * Opens an Eclipse View that extends the ViewPart abstract class.
	 * 
	 * @param viewID
	 *            The ID of the view desired to be opened
	 * @return The opened view
	 *******************************************************/
	public static Object openView(String viewID)
	{
		try
		{
			// Try opening the view
			return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(viewID);
		}
		catch (PartInitException e)
		{
			// Couldn't open the view for here.
			e.printStackTrace();
			return null;
		}
	}

	/*******************************************************
	 * Opens the Eclipse's New Wizard for the specified wizard ID.
	 * 
	 * @param wizardID
	 *            The ID of the desired wizard.
	 *******************************************************/
	public static void openNewWizard(String wizardID)
	{
		// First see if this is a "new wizard".
		IWizardDescriptor descriptor = PlatformUI.getWorkbench().getNewWizardRegistry().findWizard(wizardID);
		// If not check if it is an "import wizard".
		if (descriptor == null)
		{
			descriptor = PlatformUI.getWorkbench().getImportWizardRegistry().findWizard(wizardID);
		}
		// Or maybe an export wizard
		if (descriptor == null)
		{
			descriptor = PlatformUI.getWorkbench().getExportWizardRegistry().findWizard(wizardID);
		}
		try
		{
			// Then if we have a wizard, open it.
			if (descriptor != null)
			{
				IWorkbenchWizard wizard = descriptor.createWizard();

				IWorkbench wb = PlatformUI.getWorkbench();

				wizard.init(wb, new StructuredSelection());

				Display display = Display.getCurrent();
				// may be null if outside the UI thread
				if (display == null)
					display = Display.getDefault();

				WizardDialog wd = new WizardDialog(display.getActiveShell(), wizard);
				wd.setTitle(wizard.getWindowTitle());
				wd.open();
			}
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
	}

	/*******************************************************
	 * Initializes and opens an already created wizard object
	 * 
	 * @param wizard
	 *            The wizard desired to be opened
	 * @throws IllegalArgumentException
	 *             If wizard is null
	 *******************************************************/
	public static void openAlreadyCreatedWizard(IWorkbenchWizard wizard)
	{
		if (wizard == null)
		{
			throw new IllegalArgumentException();
		}

		// Initialize the wizard, wrap it in a wizard dialog, and open
		// it
		wizard.init(PlatformUI.getWorkbench(), new StructuredSelection());
		WizardDialog wd = new WizardDialog(Display.getDefault().getActiveShell(), wizard);
		wd.setTitle(wizard.getWindowTitle());
		wd.open();
	}

	/*******************************************************
	 * @return The absolute path of the eclipse's workspace
	 *******************************************************/
	public static String getWorkspacePath()
	{
		return ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile().getAbsolutePath();
	}

	/*******************************************************
	 * Refreshes all the projects in the current eclipse workspace
	 *******************************************************/
	public static void refreshAllProjectsInWorkspace()
	{
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject project : projects)
		{
			try
			{
				project.refreshLocal(IResource.DEPTH_INFINITE, null);
			}
			catch (CoreException e1)
			{
				e1.printStackTrace();
			}
		}
	}

	/*******************************************************
	 * Opens the given file in its default editor
	 * 
	 * @param filePath
	 *            The file path of the file desired to open
	 * @param confirm
	 *            Determines what happens if the file to be opened is part of a
	 *            project that is closed. If {@code confirm} is true, then the
	 *            user will be presented by a confirm dialog whether to open the
	 *            project or not. If the user selects yes, the project is opened
	 *            and the file is opened. If the user selects no, the file will
	 *            not be opened. Otherwise if {@code confirm} is false, the file
	 *            will be opened in all cases.
	 * 
	 * @return The opened {@link IEditorPart} for the given file, or null if an
	 *         external editor was opened.
	 *******************************************************/
	public static IEditorPart openFileInDefaultEditor(final String filePath, final boolean confirm)
	{
		// Get the default file editor for this file extension
		IEditorDescriptor editorDescriptor = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(filePath);

		// We need to test whether the file is outside the workspace or not
		IPath location = new Path(filePath);
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IAdaptable fileToOpen = workspace.getRoot().getFileForLocation(location);
		IEditorInput editorInput;

		if (confirm)
		{
			if (fileToOpen != null)
			{
				if (((IFile) fileToOpen).isAccessible())
				{
					// File is in a project (in workspace) that is open
					editorInput = new FileEditorInput((IFile) fileToOpen);

					// Open the editor
					return openEditor(editorInput, editorDescriptor);
				}
				else
				{
					// Confirm opening the project
					boolean result = EclipsePlatformUtils.showConfirmMessage("Open Project",
							"The file you want to open is in a project that is closed."
									+ " Do you want to open the project?");
					if (result)
					{
						// Open the project and the file.
						IProject proj = ((IFile) fileToOpen).getProject();
						if (proj != null)
						{
							try
							{
								// Open project
								proj.open(null);

								// Open file
								editorInput = new FileEditorInput((IFile) fileToOpen);

								// Open the editor
								return openEditor(editorInput, editorDescriptor);
							}
							catch (CoreException e)
							{
								System.err.println("Failed to open project: " + proj.getName());
								e.printStackTrace();
								return null;
							}
						}
					}
				}
			}
		}
		else
		{
			if (fileToOpen != null && ((IFile) fileToOpen).isAccessible())
			{
				// File is in workspace
				editorInput = new FileEditorInput((IFile) fileToOpen);
			}
			else
			{
				// File is outside the workspace
				fileToOpen = EFS.getLocalFileSystem().getStore(location);
				editorInput = new FileStoreEditorInput((IFileStore) fileToOpen);
			}

			// Open the editor
			return openEditor(editorInput, editorDescriptor);
		}

		// If it has not returned so far, then:
		// User selected "NO", or proj was null for some reason
		return null;

	}

	/*******************************************************
	 * Private: Opens an editor for the given input and descriptor.
	 * 
	 * @param editorInput
	 *            The input to the editor to be opened.
	 * @param editorDescriptor
	 *            The descriptor of the editor to be opened.
	 * @return The {@link IEditorPart} for the opened editor, or null if editor
	 *         was not opened, or it is an external editor (outside workspace).
	 *******************************************************/
	private static IEditorPart openEditor(IEditorInput editorInput, IEditorDescriptor editorDescriptor)
	{
		if (editorInput == null || editorDescriptor == null)
		{
			throw new IllegalArgumentException();
		}

		try
		{
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			return page.openEditor(editorInput, editorDescriptor.getId());
		}
		catch (PartInitException e)
		{
			System.err.println("Could not open editor");
			e.printStackTrace();
			return null;
		}
	}

	/*******************************************************
	 * Closes an editor displaying a specific file.
	 * 
	 * @param filePath
	 *            The file that is opened in the editor desired to be closed
	 * @param save
	 *            Flag whether to save or discard changes before closing
	 *******************************************************/
	public static void closeFileEditor(final String filePath, final boolean save)
	{
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkbench workbench = PlatformUI.getWorkbench();

		IPath location = Path.fromOSString(new File(filePath).getAbsolutePath());
		IFile fileToClose = workspace.getRoot().getFileForLocation(location);

		// Search for any editor with this file open
		for (IWorkbenchWindow windows : workbench.getWorkbenchWindows())
		{
			for (final IWorkbenchPage page : windows.getPages())
			{
				final IEditorPart editor = page.findEditor(new FileEditorInput(fileToClose));

				if (editor != null)
				{
					// The file is opened in an editor and it will be closed

					// Must be done on the UI thread
					Display.getDefault().asyncExec(new Runnable()
					{
						@Override
						public void run()
						{
							page.closeEditor(editor, save);
						}
					});

				}
			}
		}
	}

	/*******************************************************
	 * Creates a marker on the specified resource.
	 * 
	 * @param resource
	 *            The resource on which the marker will be added.
	 * @param markerType
	 *            The type of the marker as defined in the plug-in Manifest
	 *            file, in the extensions
	 * @param message
	 *            The marker message
	 * @param severity
	 *            The severity type of the marker. Must be one of
	 *            {@link IMarker#SEVERITY_ERROR}, {@link IMarker#SEVERITY_INFO},
	 *            or {@link IMarker#SEVERITY_WARNING}.
	 * @param lineNum
	 *            The line number at which the marker will be added
	 * @throws CoreException
	 *             If marker creation failed.
	 * @throws IllegalArgumentException
	 *             If resource, markerType, or message is null or if severity is
	 *             none of the above valid values.
	 * 
	 * @return The newly created marker
	 *******************************************************/
	public static IMarker addMarker(IResource resource, String markerType, String message, int severity, int lineNum)
			throws CoreException
	{
		if (resource == null || markerType == null || message == null)
			throw new IllegalArgumentException();

		if (severity != IMarker.SEVERITY_ERROR && severity != IMarker.SEVERITY_INFO
				&& severity != IMarker.SEVERITY_WARNING)
			throw new IllegalArgumentException("invalid severity value.");

		if (lineNum < 1)
			lineNum = 1;

		IMarker marker = resource.createMarker(markerType);
		marker.setAttribute(IMarker.MESSAGE, message);
		marker.setAttribute(IMarker.SEVERITY, severity);
		marker.setAttribute(IMarker.LINE_NUMBER, lineNum);

		return marker;
	}

	/*******************************************************
	 * Creates the specific text marker type on the given resource
	 * 
	 * @param resource
	 *            The resource on which to create the marker. Can't be null.
	 * @param markerType
	 *            The type of the marker as defined in the plug-in manifest.
	 *            This marker must be a sub-type of
	 *            org.eclipse.core.resources.textmarker. Can't be null.
	 * @param message
	 *            The message text of the marker. Can't be null.
	 * @param lineNum
	 *            The line number at which the marker will be added
	 * @param startPos
	 *            The starting position of the marker. Can't be less than 0.
	 * @param endPos
	 *            The end position of the marker. Can't be less than 0. Can't be
	 *            less than or equal to startPos.
	 * @return The created marker.
	 * 
	 * @throws CoreException
	 *             If marker creation failed
	 * @throws IllegalArgumentException
	 *             If any of the parameters are invalid
	 *******************************************************/
	public static IMarker addTextMarker(IResource resource, String markerType, String message, int lineNum,
			int startPos, int endPos) throws CoreException
	{
		if (resource == null || markerType == null || message == null)
			throw new IllegalArgumentException();

		if (startPos < 0 || endPos < 0)
			throw new IllegalArgumentException("Invalid character positions!");

		if (endPos <= startPos)
			throw new IllegalArgumentException("Invalid endPos!");

		if (lineNum < 1)
			lineNum = 1;

		IMarker marker = resource.createMarker(markerType);
		marker.setAttribute(IMarker.MESSAGE, message);
		marker.setAttribute(IMarker.LINE_NUMBER, lineNum);
		marker.setAttribute(IMarker.CHAR_START, startPos);
		marker.setAttribute(IMarker.CHAR_END, endPos);

		return marker;
	}

	/*******************************************************
	 * Adds an annotation of the specified type to the specified marker in the
	 * specified editor.
	 * 
	 * @param editor
	 *            The text editor in which text will be annotated.
	 * @param marker
	 *            The marker that will be used for annotation.
	 * @param annotationType
	 *            The type of the annotation as specified in the Manifest file.
	 * @param startPos
	 *            The starting position of the text to annotate.
	 * @param length
	 *            The length of the text to annotate
	 *******************************************************/
	public static void addAnnotation(ITextEditor editor, IMarker marker, String annotationType, int startPos, int length)
	{
		if (editor == null || marker == null || annotationType == null)
			throw new IllegalArgumentException();

		if (startPos < 0 || length <= 0)
			throw new IllegalArgumentException("Invalid marker positions!");

		// The DocumentProvider enables to get the document currently loaded in
		// the editor
		IDocumentProvider docProvider = editor.getDocumentProvider();

		// This is the document we want to connect to. This is taken from
		// the current editor input.
		IDocument document = docProvider.getDocument(editor.getEditorInput());

		// The IannotationModel enables to add/remove/change annotation to a
		// Document loaded in an Editor
		IAnnotationModel annotationModel = docProvider.getAnnotationModel(editor.getEditorInput());

		// Note: The annotation type id specify that you want to create one of
		// your annotations
		SimpleMarkerAnnotation markerAnnotation = new SimpleMarkerAnnotation(annotationType, marker);

		// Finally add the new annotation to the model
		annotationModel.connect(document);
		annotationModel.addAnnotation(markerAnnotation, new Position(startPos, length));
		annotationModel.disconnect(document);
	}

	/*******************************************************
	 * Finds the markers that have the specified attribute-value pairs
	 * 
	 * @param resource
	 *            The resource on which the markers will be searched
	 * @param markerType
	 *            The type of the markers desired to be found
	 * @param markersAttributes
	 *            Markers attribute-value pairs to be matched with the found
	 *            markers
	 * 
	 * @throws IllegalArgumentException
	 *             If any of the parameters is null
	 * 
	 * @return An array of {@link IMarker}s matching the search criteria
	 *******************************************************/
	public static IMarker[] findSpecificMarkers(IResource resource, String markerType,
			Map<String, Object> markersAttributes)
	{
		if (resource == null || markerType == null || markersAttributes == null)
		{
			throw new IllegalArgumentException();
		}

		ArrayList<IMarker> markers = new ArrayList<IMarker>();

		try
		{
			// Get all the markers in the resource
			IMarker[] allMarkers = resource.findMarkers(markerType, true, IResource.DEPTH_INFINITE);
			for (IMarker marker : allMarkers)
			{
				// Iterate over the given the attributes
				for (String attrib : markersAttributes.keySet())
				{
					// Does this marker have this attribute
					Object attribVal = marker.getAttribute(attrib);
					if (attribVal != null)
					{
						// Is the given attribute value equal to the marker's
						// attribute value
						Object givenVal = markersAttributes.get(attrib);
						if (attribVal.equals(givenVal))
						{
							// Add the marker to the found markers
							markers.add(marker);
							// The marker is already added, break out of the
							// inner loops
							break;
						}
					}
				}
			}

		}
		catch (CoreException e)
		{
			System.err.println("Failed to find the markers");
			e.printStackTrace();
		}

		return markers.toArray(new IMarker[markers.size()]);
	}

	/*******************************************************
	 * Deletes the given array of markers
	 * 
	 * @param markers
	 *            The markers desired to be deleted
	 *******************************************************/
	public static void deleteMarkers(IMarker[] markers)
	{
		for (IMarker marker : markers)
		{
			try
			{
				marker.delete();
			}
			catch (CoreException e)
			{
				System.err.println("Unable to delete a marker! " + e);
			}
		}
	}

	/*******************************************************
	 * Shows a message dialog box with the provided message and title.
	 * 
	 * @param title
	 *            The title of the dialog box
	 * @param message
	 *            The message to be displayed
	 *******************************************************/
	public static void showMessage(String title, String message)
	{
		MessageDialog.openInformation(Display.getDefault().getActiveShell(), title, message);
	}

	/*******************************************************
	 * Shows a confirm dialog box to the user with OK and CANCEL buttons.
	 * 
	 * @param title
	 *            The title of the dialog box
	 * @param message
	 *            The message to be displayed
	 * @return true if the user presses the OK button, false otherwise.
	 *******************************************************/
	public static boolean showConfirmMessage(String title, String message)
	{
		return MessageDialog.openConfirm(Display.getDefault().getActiveShell(), title, message);
	}

	/*******************************************************
	 * Shows an error message to the user.
	 * 
	 * @param title
	 *            The title of the error message.
	 * @param message
	 *            The message to be displayed.
	 *******************************************************/
	public static void showErrorMessage(String title, String message)
	{
		MessageDialog.openError(Display.getDefault().getActiveShell(), title, message);
	}
}
