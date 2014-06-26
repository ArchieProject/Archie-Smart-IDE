/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.views.autodetect.internals;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/*******************************************************
 * Defines the content provider that will fill the results
 * tree view.
 *******************************************************/
public class TreeContentProvider implements ITreeContentProvider
{

	public TreeContentProvider()
	{
	}

	@Override
	public void dispose()
	{
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
	}

	@Override
	public Object[] getElements(Object inputElement)
	{
		if(inputElement instanceof Object[])
		{
			return (Object[]) inputElement;
		}
		else
		{
			return new Object[0];
		}
	}

	@Override
	public Object[] getChildren(Object parentElement)
	{
		if(parentElement instanceof TreeQualityItem)
		{
			return ((TreeQualityItem)parentElement).getChildren();
		}
		
		return new Object[0];
	}

	@Override
	public Object getParent(Object element)
	{
		if(element instanceof TreeFileItem)
		{
			return ((TreeFileItem)element).getParent();
		}
		
		return null;
	}

	@Override
	public boolean hasChildren(Object element)
	{
		if(element instanceof TreeQualityItem)
		{
			return ((TreeQualityItem)element).hasChildren();
		}
		
		return false;
	}

}
