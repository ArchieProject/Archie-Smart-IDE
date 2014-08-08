/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.wizards.newtim;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import archie.globals.ArchieSettings;
import archie.utils.EclipsePlatformUtils;

/*******************************************************
 * Defines a class that will be used to display a dialog Shell to save the
 * provided TIM file as a user template with the values that the user will
 * provide.
 *******************************************************/
public final class TimTemplateSaver
{
	/*******************************************************
	 * Constructs and displays the dialog shell to save a template TIM in a
	 * separate thread.
	 * 
	 * @param filePath
	 *            The absolute path of the TIM file to be used as a template.
	 *******************************************************/
	public TimTemplateSaver(String filePath)
	{
		// Validate argument.
		if (filePath == null)
		{
			throw new IllegalArgumentException("Argument 'filePath' can't be null!");
		}

		// More argument validation.
		final File srcFile = new File(filePath);
		if (!(srcFile.exists() && srcFile.isFile() && filePath.endsWith(".tim")))
		{
			throw new IllegalArgumentException("Invalid TIM file!");
		}

		// ---- On to the shell creation ----
		
		// Create the shell.
		Display display = Display.getDefault();
		final Shell shell = new Shell(display, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText("Save TIM file as a user template ...");
		shell.setSize(600, 200);

		// Design the interface.
		shell.setLayout(new GridLayout(2, false));
		Label name = new Label(shell, SWT.NONE);
		name.setText("Template Name:");
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, false);
		name.setLayoutData(gridData);

		final Text nameText = new Text(shell, SWT.NONE);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		nameText.setLayoutData(gridData);

		Label description = new Label(shell, SWT.NONE);
		description.setText("Description (Optional):");
		gridData = new GridData(SWT.FILL, SWT.FILL, false, false);
		description.setLayoutData(gridData);

		final Text descriptionText = new Text(shell, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		descriptionText.setLayoutData(gridData);

		Button saveButton = new Button(shell, SWT.NONE);
		saveButton.setText("Save");
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.horizontalSpan = 2;
		saveButton.setLayoutData(gridData);
		saveButton.setImage(ImageDescriptor.createFromURL(this.getClass()
				.getResource("/resources/icons/save_edit.gif")).createImage() );
		shell.setDefaultButton(saveButton);
		
		// ------------------------------------------------------------------
		
		// The mouse click event handler.
		saveButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e)
			{
				// Make sure the user entered a name.
				String templateName = nameText.getText();
				if(templateName.isEmpty())
				{
					EclipsePlatformUtils.showErrorMessage("Invalid Input", "You must type a name for the template file");
					return;
				}
				
				// Create the target file.
				File targetFile = new File(ArchieSettings.getInstance().getUserTemplatesFolderPath() + templateName + ".tim");
				
				// Do the copying
				try
				{
					Files.copy(srcFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
					// No exception has been thrown? 
					// Add it to the templates provider list
					TimTemplatesProvider.getInstance().addUserTemplate(templateName, descriptionText.getText());
					// Then we're done.
					// Dispose and return
					shell.dispose();
					return;
				}
				catch (IOException e1)
				{
					EclipsePlatformUtils.showErrorMessage("Failure", "Failed to save the template file.");
					e1.printStackTrace();
					shell.dispose();
					return;
				}
			}
		});

		// Start running.
		shell.open();
	}

}
