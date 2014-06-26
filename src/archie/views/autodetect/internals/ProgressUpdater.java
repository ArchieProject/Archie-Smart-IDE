/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.views.autodetect.internals;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

/*******************************************************
 * A class for easily updating any progress bar component
 *******************************************************/
public final class ProgressUpdater
{
	private final ProgressBar mProgressBar;
	private final Label mProgressLabel;
	private final Display mDisplay;
	private boolean mVisible;
	private int mProgressValue;
	private String mProgressText;

	/*******************************************************
	 * Creates a {@link ProgressUpdater} to encapsulate the updating of
	 * graph view progress bar and progress label, doing this operation on the
	 * UI thread.
	 * 
	 * @param progBar
	 *            The graph view's progress bar
	 * @param label
	 *            The graph view's progress label
	 *******************************************************/
	public ProgressUpdater(final ProgressBar progBar, final Label label)
	{
		if (progBar == null || label == null)
		{
			throw new IllegalArgumentException();
		}

		mProgressBar = progBar;
		mProgressLabel = label;
		mDisplay = mProgressBar.getDisplay();

		mVisible = false;
		mProgressValue = 0;
		mProgressText = "";
	}

	/*******************************************************
	 * Sets whether the progress bar & label are visible or not.
	 * Note that it won't be updated until you call the run method.
	 * 
	 * @param visible
	 *            True or false.
	 * 
	 * @return A reference to itself, so that it can be used as:
	 * 
	 *         <code>updater.setVisible(true).setProgressValue(80).run();</code>
	 * 
	 *******************************************************/
	public ProgressUpdater setVisible(boolean visible)
	{
		mVisible = visible;

		return this;
	}

	/*******************************************************
	 * Sets the value of the progress bar.
	 * Note that it won't be updated until you call the run method.
	 * 
	 * @param value
	 *            The progress value.
	 * 
	 * @return A reference to itself, so that it can be used as:
	 * 
	 *         <code>updater.setVisible(true).setProgressValue(80).run();</code>
	 * 
	 *******************************************************/
	public ProgressUpdater setProgressValue(int value)
	{
		mProgressValue = value;

		return this;
	}

	/*******************************************************
	 * Sets the value of the progress label's text.
	 * Note that it won't be updated until you call the run method.
	 * 
	 * @param text
	 *            The text of progress label.
	 * 
	 * @return A reference to itself, so that it can be used as:
	 * 
	 *         <code>updater.setVisible(true).setProgressValue(80).run();</code>
	 * 
	 *******************************************************/
	public ProgressUpdater setProgressText(String text)
	{
		mProgressText = text;

		return this;
	}

	/*******************************************************
	 * Updates the progress bar & label according to the values currently stored
	 * in this {@link ProgressUpdater} object. It makes sure to run this
	 * update on the UI thread.
	 *******************************************************/
	public void run()
	{
		mDisplay.asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				if (!mProgressBar.isDisposed() && !mProgressLabel.isDisposed())
				{
					// Set the visible value
					mProgressBar.setVisible(mVisible);
					mProgressLabel.setVisible(mVisible);

					// Set the text of the label
					mProgressLabel.setText(mProgressText);

					// Set the value of the bar
					mProgressBar.setSelection(mProgressValue);

					// Perform the draw and update
					mProgressBar.redraw();
					mProgressLabel.redraw();
					mProgressBar.update();
					mProgressLabel.update();
				}
			}
		});
	}
}
