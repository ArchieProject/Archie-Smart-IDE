/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.hierarchy;

import java.util.Set;

/*******************************************************
 * This is the only leaf component in the software architecture hierarchy, they
 * represent a TIM file linked as a child of a particular tactic that it
 * implements.
 * 
 * @author Ahmed Fakhry
 *******************************************************/
public final class TimComponent extends AbstractArchitectureComponent implements IChildArchitectureComponent,
		Comparable<TimComponent>
{
	/*******************************************************
	 * Defines the behavior of the TIM components.
	 *******************************************************/
	public static final IComponentTypeBehavior TIM_COMPONENT_BEHAVIOR = new IComponentTypeBehavior()
	{
		@Override
		public String getComponentType()
		{
			return "TIM";
		}

		@Override
		public Set<String> getComponentList()
		{
			return ArchitectureComponentsManager.getInstance().getTimComponentNames();
		}

		@Override
		public IArchitectureComponent getComponent(String name)
		{
			return ArchitectureComponentsManager.getInstance().getTimComponent(name);
		}
	};

	/*******************************************************
	 * For Serialization.
	 *******************************************************/
	private static final long serialVersionUID = -8969045555939798548L;

	/*******************************************************
	 * A TIM file might reside in a project that can be either opened or closed.
	 * We have to track that, so that we can only diplay those that are open,
	 * but still keep those that are closed present, as they might be opened
	 * later.
	 *******************************************************/
	private boolean mIsOpen = true;
	
	/*******************************************************
	 * This will be true when it's needed to be deleted.
	 *******************************************************/
	private boolean mIsInvalid = false;

	/*******************************************************
	 * Constructs a TIM component in the architecture hierarchy.
	 * 
	 * @param name
	 *            This is the absolute path of the TIM file that this component
	 *            represents.
	 *******************************************************/
	public TimComponent(String name)
	{
		super(name);
	}

	/*******************************************************
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 *******************************************************/
	@Override
	public int compareTo(TimComponent o)
	{
		return mName.compareTo(o.mName);
	}

	/*******************************************************
	 * Marks the TIM file as present in an opened project.
	 *******************************************************/
	public void markOpen()
	{
		mIsOpen = true;
	}

	/*******************************************************
	 * Marks the TIM file as present in a closed project.
	 *******************************************************/
	public void markClosed()
	{
		mIsOpen = false;
	}

	/*******************************************************
	 * @return true if marked opened, false otherwise.
	 *******************************************************/
	public boolean isOpen()
	{
		return (mIsOpen == true);
	}

	/*******************************************************
	 * Sets it to invalid so that it can be removed by parent tactics.
	 *******************************************************/
	public void markInvalid()
	{
		mIsInvalid = true;
	}
	
	/*******************************************************
	 * @return true if this TIM is invalid (deleted from the file system).
	 *******************************************************/
	public boolean isInvalid()
	{
		return (mIsInvalid == true);
	}
}
