
package archie.wizards.newtim;

import java.io.InputStream;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

public class NewTimWizardFileCreationPage extends WizardNewFileCreationPage
{
	public static final String PAGE_NAME = "NewTimWizardFileCreationPage";

	public NewTimWizardFileCreationPage(IWorkbench workbench, IStructuredSelection selection)
	{
		super(PAGE_NAME, selection);

		setTitle("Traceability Information Model Wizard (Step 2 of 2)");
		setDescription("Step 2: Pick file location and name.");
	}

	@Override
	public void createControl(Composite parent)
	{
		super.createControl(parent);

		setFileName("sample");
		setFileExtension("tim");
		setPageComplete(validatePage());
	}

	@Override
	protected void createAdvancedControls(Composite parent)
	{
		// Nothing, we don't want to create links
	}
	
	@Override
	protected boolean validatePage()
	{
		boolean result = super.validatePage();
		
		IPath container = getContainerFullPath();

		if (container != null)
		{
			String secondSegment = container.segment(1);

			if (secondSegment != null && (secondSegment.equals("src") || secondSegment.equals("bin")))
			{
				setErrorMessage(null);
				setMessage("You can't add the TIM file to the \"src\" or \"bin\" folders", ERROR);
				
				result &= false;
				
				getWizard().getContainer().updateButtons();				
			}
		}
		
		return result;
	}

	@Override
	protected IStatus validateLinkedResource()
	{
		return new IStatus()
		{

			@Override
			public boolean matches(int severityMask)
			{
				return true;
			}

			@Override
			public boolean isOK()
			{
				return true;
			}

			@Override
			public boolean isMultiStatus()
			{
				return false;
			}

			@Override
			public int getSeverity()
			{
				return IStatus.OK;
			}

			@Override
			public String getPlugin()
			{
				return null;
			}

			@Override
			public String getMessage()
			{
				return null;
			}

			@Override
			public Throwable getException()
			{
				return null;
			}

			@Override
			public int getCode()
			{
				return 0;
			}

			@Override
			public IStatus[] getChildren()
			{
				return null;
			}
		};
	}

	@Override
	protected void createLinkTarget()
	{
		// Nothing, We're not creating links
	}

	@Override
	protected InputStream getInitialContents()
	{
		NewTimWizard wizard = (NewTimWizard) getWizard();
		TimTemplate selectedTemplate = wizard.getTemplatesPage().getSelectedTemplate();
		return selectedTemplate.getInputStream();
	}
}
