/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.hierarchy;

/*******************************************************
 * Defines a sub-goal which can be a parent to a tactic and a child of a goal.
 * 
 * @author Ahmed Fakhry
 *******************************************************/
final class SubGoal extends AbstractParentArchComp implements IChildArchitectureComponent, Comparable<SubGoal>
{
	/*******************************************************
	 * For serialization.
	 *******************************************************/
	private static final long serialVersionUID = 4923168170125573473L;

	/*******************************************************
	 * Constructs a sub-goal.
	 * 
	 * Notice that it's package private, as it can only be created by the
	 * components manager.
	 * 
	 * @param name
	 *            The name of the sub-goal, it must be unique. Its uniqueness will
	 *            be validated by the architecture components manager.
	 *            [Can't be null or empty].
	 *******************************************************/
	SubGoal(String name)
	{
		super(name);
	}

	/*******************************************************
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 *******************************************************/
	@Override
	public int compareTo(SubGoal o)
	{
		return mName.compareTo(o.mName);
	}
}
