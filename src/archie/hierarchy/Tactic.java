/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.hierarchy;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/*******************************************************
 * Defines a leaf software architecture component. A tactic is used to achieve a
 * goal or sub-goal.
 * 
 * @author Ahmed Fakhry
 *******************************************************/
final class Tactic extends AbstractParentArchComp implements IChildArchitectureComponent, Comparable<Tactic>
{
	/*******************************************************
	 * Defines the behavior of tactics.
	 *******************************************************/
	public static final IComponentTypeBehavior TACTIC_BEHAVIOR = new IComponentTypeBehavior()
	{
		@Override
		public String getComponentType()
		{
			return "Tactic";
		}

		@Override
		public Set<String> getComponentList()
		{
			return ArchitectureComponentsManager.getInstance().getTacticNames();
		}

		@Override
		public IArchitectureComponent getComponent(String name)
		{
			return ArchitectureComponentsManager.getInstance().getTactic(name);
		}
	};

	/*******************************************************
	 * For Serialization.
	 *******************************************************/
	private static final long serialVersionUID = 3962653391600765109L;

	/*******************************************************
	 * Constructs a tactic.
	 * 
	 * Notice that it's package private, as it can only be created by the
	 * components manager.
	 * 
	 * @param name
	 *            The name of the sub-goal, it must be unique. Its uniqueness
	 *            will be validated by the architecture components manager.
	 *            [Can't be null or empty].
	 *******************************************************/
	Tactic(String name)
	{
		super(name);
	}

	/*******************************************************
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 *******************************************************/
	@Override
	public int compareTo(Tactic o)
	{
		return mName.compareTo(o.mName);
	}

	/*******************************************************
	 * Here we override the iterator method so that it only returns an iterator
	 * over the open TimComponents.
	 * 
	 * @see archie.hierarchy.AbstractParentArchComp#iterator()
	 *******************************************************/
	@Override
	public Iterator<IChildArchitectureComponent> iterator()
	{
		Set<IChildArchitectureComponent> result = new TreeSet<IChildArchitectureComponent>();
		for(IChildArchitectureComponent child : mChildren)
		{
			// Only add a child TIM component if it's marked as open.
			if(child instanceof TimComponent)
			{
				TimComponent timComp = (TimComponent) child;
				if(timComp.isOpen())
				{
					result.add(timComp);
				}
			}
			else
			{
				// Another type of children, just add them
				result.add(child);
			}
		}
		
		return result.iterator();
	}
}
