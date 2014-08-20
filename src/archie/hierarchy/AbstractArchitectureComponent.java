/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.hierarchy;

/*******************************************************
 * Defines an abstract architecture component.
 * 
 * @author Ahmed Fakhry
 *******************************************************/
abstract class AbstractArchitectureComponent implements IArchitectureComponent
{
	/*******************************************************
	 * For serialization
	 *******************************************************/
	private static final long serialVersionUID = -4210871295868931839L;
	
	/*******************************************************
	 * The unique name of the goal.
	 *******************************************************/
	protected final String mName;

	/*******************************************************
	 * Constructs an abstract architecture component with the given name.
	 * 
	 * @param name
	 * 			The unique name of the component. [Cannot be null or empty].
	 *******************************************************/
	public AbstractArchitectureComponent(String name)
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
	 * @see java.lang.Object#equals(java.lang.Object)
	 *******************************************************/
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		// Must the same class.
		if (obj.getClass() != this.getClass())
			return false;
		
		AbstractParentArchComp other = (AbstractParentArchComp) obj;
		
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
