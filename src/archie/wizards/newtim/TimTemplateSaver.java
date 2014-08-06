/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.wizards.newtim;

import java.io.File;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

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
		File file = new File(filePath);
		if (!(file.exists() && file.isFile() && filePath.endsWith(".tim")))
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

		Text nameText = new Text(shell, SWT.NONE);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		nameText.setLayoutData(gridData);

		Label description = new Label(shell, SWT.NONE);
		description.setText("Description (Optional):");
		gridData = new GridData(SWT.FILL, SWT.FILL, false, false);
		description.setLayoutData(gridData);

		Text descriptionText = new Text(shell, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
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

		// Start running.
		shell.open();
	}

}
