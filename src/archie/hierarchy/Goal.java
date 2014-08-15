/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.hierarchy;


/*******************************************************
 * Defines a system goal software architecture component. It can be a parent to
 * some other sub-goals in the system architecture archie.hierarchy.
 * 
 * @author Ahmed Fakhry
 *******************************************************/
final class Goal extends AbstractParentArchComp implements IParentArchitectureComponent, Comparable<Goal>
{
	/*******************************************************
	 * For serialization
	 *******************************************************/
	private static final long serialVersionUID = 8243564074986497353L;
	
	// -------------------------------------------------------------------------------------

	/*******************************************************
	 * Constructs a goal.
	 * 
	 * Notice that it's package private, as it can only be created by the
	 * components manager.
	 * 
	 * @param name
	 *            The name of the goal, it must be unique. Its uniqueness will
	 *            be validated by the architecture components manager.
	 *            [Can't be null or empty].
	 *******************************************************/
	Goal(String name)
	{
		super(name);
	}

	/*******************************************************
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 *******************************************************/
	@Override
	public int compareTo(Goal o)
	{
		// The comparison is based on names.
		return mName.compareTo(o.mName);
	}
}
