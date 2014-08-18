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
 * Defines a leaf software architecture component. A tactic is used to achieve a
 * goal or sub-goal.
 * 
 * @author Ahmed Fakhry
 *******************************************************/
final class Tactic implements IChildArchitectureComponent, Comparable<Tactic>
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
	 * The unique name of the tactic.
	 *******************************************************/
	private final String mName;

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
		if (name == null || name.isEmpty())
			throw new IllegalArgumentException();

		mName = name;
	}

	/*******************************************************
	 * 
	 * @see archie.hierarchy.IArchitectureComponent#getName()
	 *******************************************************/
	@Override
	public String getName()
	{
		return mName;
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
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 *******************************************************/
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (this.getClass() != obj.getClass())
			return false;

		Tactic other = (Tactic) obj;

		return mName.equals(other.mName);
	}

	/*******************************************************
	 * 
	 * @see java.lang.Object#hashCode()
	 *******************************************************/
	@Override
	public int hashCode()
	{
		return mName.hashCode();
	}
}
