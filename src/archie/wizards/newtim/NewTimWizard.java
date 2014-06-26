
package archie.wizards.newtim;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;

/**
 * <p>
 * Controls the navigation between the different WizardPages and provides the
 * base user interface, i.e. a progress bar and an area for error and
 * information messages.
 * </p>
 * <p>
 * To open a wizard:
 * </p>
 * <p>
 * <code>
 * 	WizardDialog dialog = new WizardDialog(shell, new YourWizardClass()); <br />
 * 	dialog.open();
 * 	</code>
 * </p>
 * 
 * @author Mateusz Wieloch
 */
public class NewTimWizard extends Wizard implements INewWizard
{
	/*******************************************************
	 * This is the unique id of this wizard, so that it can be used to open and
	 * query this wizard from anywhere.
	 * 
	 * @note Remember to change it if you happen to change the id of this wizard
	 *       in the MANIFEST.MF file.
	 *******************************************************/
	public static final String NEW_TIM_WIZARD_ID = new String("archie.wizards.newTimWizard");
	
	private IStructuredSelection selection;
	private IWorkbench workbench;
	
	public NewTimWizard()
	{
		super();
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		this.workbench = workbench;
		this.selection = selection;
		setWindowTitle("New Traceability Information Model");
	}

	@Override
	public void addPages()
	{
		addPage(new NewTimWizardTemplatesPage());
		addPage(new NewTimWizardFileCreationPage(workbench, selection));
	}

	public NewTimWizardTemplatesPage getTemplatesPage()
	{
		return (NewTimWizardTemplatesPage) getPage(NewTimWizardTemplatesPage.PAGE_NAME);
	}

	public NewTimWizardFileCreationPage getFileCreationPage()
	{
		return (NewTimWizardFileCreationPage) getPage(NewTimWizardFileCreationPage.PAGE_NAME);
	}

	
	
	@Override
	public boolean performFinish()
	{
		IFile newFile = getFileCreationPage().createNewFile();
		
		if (newFile == null)
			return false;

		try
		{
			IWorkbenchPage activeWorkbenchPage = workbench.getActiveWorkbenchWindow()
					.getActivePage();
			if (activeWorkbenchPage != null)
				IDE.openEditor(activeWorkbenchPage, newFile, true);
		}
		catch (PartInitException e)
		{
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
