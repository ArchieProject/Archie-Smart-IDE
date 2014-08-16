/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;

import archie.utils.EclipsePlatformUtils;

/*******************************************************
 * A custom-made widget to encapsulate the creation of a group that has a list
 * with add and remove buttons.
 * 
 * @author Ahmed Fakhry
 *******************************************************/
public final class AddRemoveList extends Composite
{
	/*******************************************************
	 * An interface for a concrete handler that will handle the add button click
	 * event.
	 *******************************************************/
	public static interface IOnAddHandler
	{
		/*******************************************************
		 * Handles the add button click event.
		 * 
		 * @return The String representation of the item to add to the list.
		 *         [null or empty means don't add anything.]
		 *******************************************************/
		String handle();
	}

	/*******************************************************
	 * An interface for a concrete handler that will handle the remove button
	 * click event.
	 *******************************************************/
	public static interface IOnRemoveHandler
	{
		/*******************************************************
		 * Handle the remove button click event.
		 * 
		 * @param selections
		 *            The selected items from the list.
		 *******************************************************/
		void handle(String[] selections);
	}

	// ----------------------------------------
	// Fields
	// ----------------------------------------

	Group mGroup;
	Button mAddButton;
	Button mRemoveButton;
	List mList;
	IOnAddHandler mAddHandler;
	IOnRemoveHandler mRemoveHandler;

	// ----------------------------------------
	// Construction.
	// ----------------------------------------

	/*******************************************************
	 * Constructs an {@link AddRemoveList} widget.
	 * 
	 * @param parent
	 *            The parent widget of this one.
	 * 
	 * @param style
	 *            The style of the widget.
	 * 
	 * @param title
	 *            The title of this list. [Cannot be null or empty].
	 * 
	 * @param addImage
	 *            The image to be used for the add button. [Cannot be null or
	 *            empty].
	 * 
	 * @param removeImage
	 *            The image to be used for the remove button. [Cannot be null or
	 *            empty].
	 *******************************************************/
	public AddRemoveList(Composite parent, int style, String title, Image addImage, Image removeImage)
	{
		super(parent, style);

		// Validate.
		if (title == null || addImage == null || removeImage == null)
			throw new IllegalArgumentException();

		if (title.isEmpty())
			throw new IllegalArgumentException();

		// The layout of this widget.
		this.setLayout(new FillLayout());

		// The other UI controls.
		createControls(title, addImage, removeImage);

		// The internal handlers
		registerHandlers();
	}

	// ----------------------------------------
	// Public methods.
	// ----------------------------------------

	/*******************************************************
	 * Adds an item to the list.
	 * 
	 * @param item
	 *            The item to be added to the list. [Cannot be null or empty].
	 *******************************************************/
	public void addItem(String item)
	{
		if (item == null || item.isEmpty())
			throw new IllegalArgumentException();

		mList.add(item);
	}

	/*******************************************************
	 * Removes an item from the list.
	 * 
	 * @param item
	 *            The item to be removed from the list. [Cannot be null or
	 *            empty].
	 *******************************************************/
	public void removeItem(String item)
	{
		if (item == null || item.isEmpty())
			throw new IllegalArgumentException();

		mList.remove(item);
	}

	/*******************************************************
	 * Sets the external handler of the add button click event.
	 * 
	 * @param handler
	 *            The external handler. [Cannot be null].
	 *******************************************************/
	public void setAddButtonClickHandler(IOnAddHandler handler)
	{
		if (handler == null)
			throw new IllegalArgumentException();

		mAddHandler = handler;
	}

	/*******************************************************
	 * Sets the external handler of the remove button click event.
	 * 
	 * @param handler
	 *            The external handler. [Cannot be null].
	 *******************************************************/
	public void setRemoveButtonClickHandler(IOnRemoveHandler handler)
	{
		if (handler == null)
			throw new IllegalArgumentException();

		mRemoveHandler = handler;
	}

	// ----------------------------------------

	/*******************************************************
	 * Creates the UI elements of this widget.
	 * 
	 * @param title
	 *            The title of the group.
	 * 
	 * @param addImage
	 *            The image for the add button.
	 * 
	 * @param removeImage
	 *            The image for the remove button.F
	 *******************************************************/
	private void createControls(String title, Image addImage, Image removeImage)
	{
		// The list group.
		mGroup = new Group(this, SWT.NONE);
		mGroup.setText(title);
		mGroup.setLayout(new GridLayout(4, true));

		// The add button.
		mAddButton = new Button(mGroup, SWT.NONE);
		mAddButton.setImage(addImage);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, false);
		mAddButton.setLayoutData(gridData);

		// The remove button.
		mRemoveButton = new Button(mGroup, SWT.NONE);
		mRemoveButton.setImage(removeImage);
		gridData = new GridData(SWT.FILL, SWT.FILL, false, false);
		mRemoveButton.setLayoutData(gridData);

		// The list.
		mList = new List(mGroup, SWT.V_SCROLL | SWT.H_SCROLL);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan = 4;
		mList.setLayoutData(gridData);
	}

	/*******************************************************
	 * Registers the internal handlers for the buttons which will in turn call
	 * the external handlers if available.
	 *******************************************************/
	private void registerHandlers()
	{
		// --- Add Button ---
		// The internal on click handler for the add button.
		mAddButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e)
			{
				if (mAddHandler != null)
				{
					String result = mAddHandler.handle();
					if (result != null && !result.isEmpty())
					{
						mList.add(result);
						AddRemoveList.this.redraw();
					}
				}
			}
		});

		// --- Remove Button ---
		// The internal on click handler for the remove button.
		mRemoveButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e)
			{
				// Validate there is at least one item selected.
				String[] selections = mList.getSelection();
				if (selections.length < 1)
				{
					EclipsePlatformUtils.showErrorMessage("Error", "You must select at least one item from the list");
					return;
				}

				if (mRemoveHandler != null)
				{
					// Inform the handler that the selected items will be
					// removed.
					mRemoveHandler.handle(selections);
				}

				// Now remove the selected elements.
				mList.remove(mList.getSelectionIndices());

				AddRemoveList.this.redraw();
			}
		});
	}
}
