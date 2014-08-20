/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import archie.views.autodetect.internals.SimpleImageRegistry;

/*******************************************************
 * This is a small shell that encapsulates displaying of an input prompt to take
 * an input string from the user.
 * 
 * @author Ahmed Fakhry
 *******************************************************/
public final class Prompt
{
	/*******************************************************
	 * Defines the interface for a concrete handler to handle what happens when
	 * the user presses the OK button.
	 *******************************************************/
	public static interface IOnOkHandler
	{
		/*******************************************************
		 * Handle the OK button press.
		 * 
		 * @param userInput
		 *            The string value that the user has input.
		 *******************************************************/
		public void handle(String userInput);
	}

	// ----------------------------------------
	// Fields
	// ----------------------------------------

	private final IOnOkHandler mOkHandler;
	private final Shell mShell;
	private final Text mUserInput;

	/*******************************************************
	 * Constructs a user input prompt dialog window.
	 * 
	 * @param title
	 *            The title of the prompt window. [Cannot be null or empty].
	 * 
	 * @param message
	 *            The prompt message instructing the user. [Cannot be null or
	 *            empty].
	 * 
	 * @param okHandler
	 *            The event handler for the OK button click event. [Cannot be
	 *            null].
	 *******************************************************/
	public Prompt(String title, String message, IOnOkHandler okHandler)
	{
		if (title == null || message == null || okHandler == null)
			throw new IllegalArgumentException();

		if (title.isEmpty() || message.isEmpty())
			throw new IllegalArgumentException();

		mOkHandler = okHandler;

		// Create the shell.
		Display display = Display.getDefault();
		mShell = new Shell(display, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		mShell.setLayout(new GridLayout(2, true));
		SimpleImageRegistry imageRegistry = new SimpleImageRegistry(mShell);
		imageRegistry.registerImagePath("editIcon", "/resources/icons/pick.png");
		mShell.setImage(imageRegistry.getImage("editIcon"));

		// Put the title of the shell.
		mShell.setText(title);

		// The prompt message
		Label label = new Label(mShell, SWT.NONE);
		label.setText(message);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.horizontalSpan = 2;
		label.setLayoutData(gridData);

		// The user input
		mUserInput = new Text(mShell, SWT.NONE);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan = 2;
		gridData.minimumWidth = 400;
		mUserInput.setLayoutData(gridData);

		// The OK & Cancel buttons

		// --- OK ---
		Button okButton = new Button(mShell, SWT.NONE);
		okButton.setText("OK");
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		okButton.setLayoutData(gridData);

		// --- Cancel ---
		Button cancelButton = new Button(mShell, SWT.NONE);
		cancelButton.setText("Cancel");
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		cancelButton.setLayoutData(gridData);

		// Register the buttons handlers
		registerHandlers(okButton, cancelButton);
		
		// Now display the shell.
		mShell.pack();
		mShell.open();
	}

	/*******************************************************
	 * Registers the OK and Cancel buttons handlers.
	 *******************************************************/
	private void registerHandlers(Button okButton, Button cancelButton)
	{
		// --- The OK button click handler ----
		okButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e)
			{
				handleOK();
			}
		});

		// --- The Cancel button click handler ---
		cancelButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e)
			{
				// Just dispose the shell.
				mShell.dispose();
			}
		});
		
		// --- The enter key press handler ---
		mUserInput.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if(e.character == '\r')
				{
					handleOK();
				}
			}
		});
	}
	
	/*******************************************************
	 * Handles the acceptance of the input in the prompt.
	 *******************************************************/
	private void handleOK()
	{
		// Send the result to the handler and dispose.
		String userInput = mUserInput.getText();
		mOkHandler.handle(userInput);
		mShell.dispose();
	}
}
