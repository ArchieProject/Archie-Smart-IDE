/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.hierarchy;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import archie.hierarchy.graph.HierarchyGraph;
import archie.utils.EclipsePlatformUtils;
import archie.views.autodetect.internals.SimpleImageRegistry;
import archie.widgets.AddRemoveList;
import archie.widgets.Prompt;

/*******************************************************
 * Creates the wizard that will be used to define the system's architecture
 * components and their hierarchy relationships.
 * 
 * @author Ahmed Fakhry
 *******************************************************/
public class ArchitectureHierarchyWizard
{
	// ----------------------------------------
	// Fields.
	// ----------------------------------------

	private Shell mShell;
	private SimpleImageRegistry mImageRegistry;
	private AddRemoveList mGoalsList;
	private AddRemoveList mSubGoalsList;
	private AddRemoveList mTactics;
	private Button mNextButton;

	// ----------------------------------------
	// Construction.
	// ----------------------------------------

	public ArchitectureHierarchyWizard()
	{
		// Initialize the shell.
		mShell = new Shell(Display.getDefault(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		mShell.setLayout(new GridLayout(3, true));
		mShell.setText("System Architecture Components Wizard (1 of 3)");

		// Initialize the image registry.
		initImageRegistry();

		mShell.setImage(mImageRegistry.getImage("archIcon"));

		// Build the UI
		buildControls();

		// Fill list with initial contents.
		fillLists();

		// Register lists handlers.
		registerListsHandlers();

		// Register next handler.
		registerNextButtonHandler();

		// Display the shell.
		mShell.open();
	}

	// ----------------------------------------
	// Public Methods.
	// ----------------------------------------

	// ----------------------------------------
	// Static Private Classes.
	// ----------------------------------------

	// ##################################

	/*******************************************************
	 * Enum to define which type of list we're defining handlers to.
	 *******************************************************/
	private static enum EComponentType
	{
		GOAL, SUB_GOAL, TACTIC
	}

	// ##################################

	/*******************************************************
	 * Defines and add handler to {@link AddRemoveList}. This is very flexible
	 * and very reusable.
	 *******************************************************/
	private static class AddHandler implements AddRemoveList.IOnAddHandler
	{
		/*******************************************************
		 * Internal command interface for adding either a goal, a sub-goal, or a
		 * tactic. The implementation will decide which one.
		 *******************************************************/
		static interface IAddCommand
		{
			/*******************************************************
			 * Adds the the given input string to the list of goals, sub-goals,
			 * or tactics. The implementation decides which.
			 * 
			 * @param input
			 *            The name to be added to the list.
			 * 
			 * @param owner
			 *            The owner list to which an item will be added.
			 *******************************************************/
			public void add(String input, AddRemoveList owner);
		}

		/*******************************************************
		 * Constant concrete add command for goals.
		 *******************************************************/
		static final IAddCommand GOAL_COMMAND = new IAddCommand()
		{
			/*******************************************************
			 * 
			 * @see archie.hierarchy.ArchitectureHierarchyWizard.AddHandler.IAddCommand#add(java.lang.String)
			 *******************************************************/
			@Override
			public void add(String input, AddRemoveList owner)
			{
				// Adds to the list of goals.
				if (ArchitectureComponentsManager.getInstance().addGoal(input))
				{
					owner.addItem(input);
				}
			}
		};

		/*******************************************************
		 * Constant concrete add command for sub goals.
		 *******************************************************/
		static final IAddCommand SUB_GOAL_COMMAND = new IAddCommand()
		{
			/*******************************************************
			 * 
			 * @see archie.hierarchy.ArchitectureHierarchyWizard.AddHandler.IAddCommand#add(java.lang.String)
			 *******************************************************/
			public void add(String input, AddRemoveList owner)
			{
				// Adds to the list of sub goals.
				if (ArchitectureComponentsManager.getInstance().addSubGoal(input))
				{
					owner.addItem(input);
				}
			};
		};

		/*******************************************************
		 * Constant concrete add command for tactics.
		 *******************************************************/
		static final IAddCommand TACTIC_COMMAND = new IAddCommand()
		{
			@Override
			public void add(String input, AddRemoveList owner)
			{
				// Add to the list of tactics.
				if (ArchitectureComponentsManager.getInstance().addTactic(input))
				{
					owner.addItem(input);
				}
			}
		};

		// ----------------------------------------
		// Fields
		// ----------------------------------------

		final IAddCommand mAddCommand;
		final String mComponentName;
		final AddRemoveList mOwner;

		// ----------------------------------------
		// Construction
		// ----------------------------------------

		/*******************************************************
		 * Constructs an Add handler, using the given type of the list to add
		 * to.
		 * 
		 * @param type
		 *            Either goal, sub-goal, or tactic.
		 * 
		 * @param owner
		 *            The owner list of this handler.
		 *******************************************************/
		public AddHandler(EComponentType type, AddRemoveList owner)
		{
			switch (type)
			{
				case GOAL:
					mAddCommand = GOAL_COMMAND;
					mComponentName = "Goal";
					break;

				case SUB_GOAL:
					mAddCommand = SUB_GOAL_COMMAND;
					mComponentName = "Sub Goal";
					break;

				case TACTIC:
					mAddCommand = TACTIC_COMMAND;
					mComponentName = "Tactic";
					break;

				default:
					throw new IllegalArgumentException();
			}

			if (owner == null)
				throw new IllegalArgumentException();

			mOwner = owner;
		}

		/*******************************************************
		 * 
		 * @see archie.widgets.AddRemoveList.IOnAddHandler#handle()
		 *******************************************************/
		@Override
		public String handle()
		{
			// Prompt the user to enter a new component name.
			new Prompt("Input ...", "Enter the new " + mComponentName + " name: ", new Prompt.IOnOkHandler()
			{
				/*******************************************************
				 * 
				 * @see archie.widgets.Prompt.IOnOkHandler#handle(java.lang.String)
				 *******************************************************/
				@Override
				public void handle(String userInput)
				{
					// Null or empty means add nothing
					if (userInput == null || userInput.isEmpty())
					{
						EclipsePlatformUtils.showErrorMessage("Error", "You must type a unique name!");
						return;
					}
					
					// Validate that the input is a unique new input name
					mAddCommand.add(userInput, mOwner);
				}
			});

			// Always return null, item will be added later by the add handler.
			return null;
		}

	}

	// ##################################

	/*******************************************************
	 * Defines a flexible handler for the various lists remove buttons.
	 *******************************************************/
	private static class RemoveHandler implements AddRemoveList.IOnRemoveHandler
	{
		/*******************************************************
		 * An interface to remove an item from an {@link AddRemoveList}.
		 *******************************************************/
		private static interface IRemoveCommand
		{
			/*******************************************************
			 * The concrete implementation will decide which the list in the
			 * components manager to remove from.
			 * 
			 * @param item
			 *            The item to be removed.
			 * @return true if removed, false otherwise.
			 *******************************************************/
			public boolean remove(String item);
		}

		/*******************************************************
		 * Constant remove command from the goals list.
		 *******************************************************/
		private static final IRemoveCommand GOAL_REMOVE = new IRemoveCommand()
		{
			/*******************************************************
			 * 
			 * @see archie.hierarchy.ArchitectureHierarchyWizard.RemoveHandler.IRemoveCommand#remove(java.lang.String)
			 *******************************************************/
			@Override
			public boolean remove(String item)
			{
				ArchitectureComponentsManager.getInstance().removeGoal(item);
				return true;
			}
		};

		/*******************************************************
		 * Constant remove command from the sub-goals list.
		 *******************************************************/
		private static final IRemoveCommand SUB_GOAL_REMOVE = new IRemoveCommand()
		{
			/*******************************************************
			 * 
			 * @see archie.hierarchy.ArchitectureHierarchyWizard.RemoveHandler.IRemoveCommand#remove(java.lang.String)
			 *******************************************************/
			@Override
			public boolean remove(String item)
			{
				ArchitectureComponentsManager.getInstance().removeSubGoal(item);
				return true;
			}
		};

		/*******************************************************
		 * Constant remove command from the tactics list.
		 *******************************************************/
		private static final IRemoveCommand TACTICS_REMOVE = new IRemoveCommand()
		{
			/*******************************************************
			 * 
			 * @see archie.hierarchy.ArchitectureHierarchyWizard.RemoveHandler.IRemoveCommand#remove(java.lang.String)
			 *******************************************************/
			@Override
			public boolean remove(String item)
			{
				ArchitectureComponentsManager.getInstance().removeTactic(item);
				return true;
			}
		};

		// ----------------------------------------
		// Fields
		// ----------------------------------------

		private final IRemoveCommand mCommand;

		// ----------------------------------------
		// Construction
		// ----------------------------------------

		/*******************************************************
		 * Creates a concrete handler for a remove button in an
		 * {@link AddRemoveList}.
		 * 
		 * @param type
		 *            Either goal, sub-goal, or a tactic.
		 *******************************************************/
		public RemoveHandler(EComponentType type)
		{
			switch (type)
			{
				case GOAL:
					mCommand = GOAL_REMOVE;
					break;

				case SUB_GOAL:
					mCommand = SUB_GOAL_REMOVE;
					break;

				case TACTIC:
					mCommand = TACTICS_REMOVE;
					break;

				default:
					throw new IllegalArgumentException();
			}
		}

		/*******************************************************
		 * 
		 * @see archie.widgets.AddRemoveList.IOnRemoveHandler#handle(java.lang.String[])
		 *******************************************************/
		@Override
		public void handle(String[] selections)
		{
			for (String item : selections)
			{
				mCommand.remove(item);
			}
		}
	}

	// ##################################

	// ----------------------------------------
	// Private Methods.
	// ----------------------------------------

	/*******************************************************
	 * Initializes the image registry.
	 *******************************************************/
	private void initImageRegistry()
	{
		// The shell must have already been initialized.
		if (mShell == null)
			throw new IllegalStateException();

		mImageRegistry = new SimpleImageRegistry(mShell);

		mImageRegistry.registerImagePath("addIcon", "/resources/icons/addIcon.png");

		mImageRegistry.registerImagePath("removeIcon", "/resources/icons/deleteIcon.png");

		mImageRegistry.registerImagePath("archIcon", "/resources/icons/architecture.png");
	}

	/*******************************************************
	 * Build the UI controls.
	 *******************************************************/
	private void buildControls()
	{
		// The instructions label.
		Label label = new Label(mShell, SWT.NONE);
		label.setText("(Step 1 of 3): Define the software architecture components you want.");
		GridData gData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gData.horizontalSpan = 3;
		label.setLayoutData(gData);

		// The images
		Image addImage = mImageRegistry.getImage("addIcon");
		Image removeImage = mImageRegistry.getImage("removeIcon");

		// The goals list.
		mGoalsList = new AddRemoveList(mShell, SWT.NONE, "Goals", addImage, removeImage);
		gData = new GridData(SWT.FILL, SWT.FILL, true, true);
		mGoalsList.setLayoutData(gData);

		// The sub-goals list.
		mSubGoalsList = new AddRemoveList(mShell, SWT.NONE, "Sub Goals", addImage, removeImage);
		gData = new GridData(SWT.FILL, SWT.FILL, true, true);
		mSubGoalsList.setLayoutData(gData);

		// The tactics list.
		mTactics = new AddRemoveList(mShell, SWT.NONE, "Tactics", addImage, removeImage);
		gData = new GridData(SWT.FILL, SWT.FILL, true, true);
		mTactics.setLayoutData(gData);

		// The next button.
		mNextButton = new Button(mShell, SWT.NONE);
		mNextButton.setText("Next >>");
		gData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gData.horizontalSpan = 3;
		mNextButton.setLayoutData(gData);
	}

	/*******************************************************
	 * Fills the various list of the initial contents from the components
	 * manager.
	 *******************************************************/
	private void fillLists()
	{
		// The goals list.
		for (String goal : ArchitectureComponentsManager.getInstance().getGoalNames())
		{
			mGoalsList.addItem(goal);
		}

		// The sub-goals list.
		for (String subGoal : ArchitectureComponentsManager.getInstance().getSubGoalNames())
		{
			mSubGoalsList.addItem(subGoal);
		}

		// The tactics list.
		for (String tactic : ArchitectureComponentsManager.getInstance().getTacticNames())
		{
			mTactics.addItem(tactic);
		}
	}

	/*******************************************************
	 * Registers the add/remove handlers of all three lists.
	 *******************************************************/
	private void registerListsHandlers()
	{
		registerAddHandlers();

		registerRemoveHandlers();
	}

	/*******************************************************
	 * Registers the add handlers for the various lists.
	 *******************************************************/
	private void registerAddHandlers()
	{
		// The Goals add button handler.
		mGoalsList.setAddButtonClickHandler(new AddHandler(EComponentType.GOAL, mGoalsList));

		// The Sub-Goals add button handler.
		mSubGoalsList.setAddButtonClickHandler(new AddHandler(EComponentType.SUB_GOAL, mSubGoalsList));

		// The Tactics add button handler.
		mTactics.setAddButtonClickHandler(new AddHandler(EComponentType.TACTIC, mTactics));
	}

	/*******************************************************
	 * Registers the remove handlers for the various lists.
	 *******************************************************/
	private void registerRemoveHandlers()
	{
		// The Goals remove button handler.
		mGoalsList.setRemoveButtonClickHandler(new RemoveHandler(EComponentType.GOAL));

		// The Sub-Goals remove button handler.
		mSubGoalsList.setRemoveButtonClickHandler(new RemoveHandler(EComponentType.SUB_GOAL));

		// The Tactics remove button handler.
		mTactics.setRemoveButtonClickHandler(new RemoveHandler(EComponentType.TACTIC));
	}

	/*******************************************************
	 * Register the event handler for clicking on the next button.
	 *******************************************************/
	private void registerNextButtonHandler()
	{
		mNextButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e)
			{
				// User must have added at least one item of each type.
				int goalsSize = ArchitectureComponentsManager.getInstance().goalsSize();
				int subGoalsSize = ArchitectureComponentsManager.getInstance().subGoalsSize();
				int tacticsSize = ArchitectureComponentsManager.getInstance().tacticsSize();

				if (goalsSize < 1 || subGoalsSize < 1 || tacticsSize < 1)
				{
					EclipsePlatformUtils.showErrorMessage("Error", "You must add at least one item of each type!");
				}
				else
				{
					// Prepare the runnables to move from page to page:

					// Finish runnable
					final Runnable finishRunnable = new Runnable()
					{
						@Override
						public void run()
						{
							// Set hierarchy built in the
							// manager.
							ArchitectureComponentsManager.getInstance().setHierarchyBuilt();
							
							// Open the graph view
							new HierarchyGraph();
						}
					};

					// 3 to 4: Link Tactics to TIMs
					final Runnable page3to4 = new Runnable()
					{
						@Override
						public void run()
						{
							new HierarchySelectorPage(4, 4, Tactic.TACTIC_BEHAVIOR,
									TimComponent.TIM_COMPONENT_BEHAVIOR, null, finishRunnable);
						}
					};

					// 2 to 3: Page3 - Link Tactics to Sub Goals
					final Runnable page2to3 = new Runnable()
					{
						@Override
						public void run()
						{
							// Go to page 3 of 4.
							new HierarchySelectorPage(3, 4, SubGoal.SUB_GOAL_BEHAVIOR, Tactic.TACTIC_BEHAVIOR, page3to4, null);
						}
					};
					
					// ----------------------------
					
					// Actual page creation here:
					// Go to page 2 of 3.
					new HierarchySelectorPage(2, 4, Goal.GOAL_BEHAVIOR, SubGoal.SUB_GOAL_BEHAVIOR, page2to3, null);

					// Dispose this window.
					mShell.dispose();
				}
			}
		});
	}
}
