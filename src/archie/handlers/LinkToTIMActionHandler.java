/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.handlers;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import archie.timstorage.LinkToTim;

/*******************************************************
 * This action handler will handle the popup menu action to add a compilation
 * unit to the selected TIM.
 * 
 * @author Ahmed Fakhry
 *******************************************************/
public final class LinkToTIMActionHandler implements IObjectActionDelegate
{
	/*******************************************************
	 * The list of selected compilation units.
	 *******************************************************/
	private ArrayList<ICompilationUnit> mSelectedCompUnits = new ArrayList<ICompilationUnit>();

	/*******************************************************
	 * Tries to link all the selected compilation units to the selected TIM.
	 * 
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 *******************************************************/
	@Override
	public void run(IAction action)
	{
		// Link the selected items to the TIM
		for (ICompilationUnit cu : mSelectedCompUnits)
		{
			// Get the absolute file path
			String filePath = new File(cu.getResource().getLocationURI()).getAbsolutePath();
			// Link it to the TIM
			LinkToTim.linkToActiveTIM(filePath);
		}

		// Clear the list for future additions
		mSelectedCompUnits.clear();
	}

	/*******************************************************
	 * 
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 *******************************************************/
	@Override
	public void selectionChanged(IAction action, ISelection selection)
	{
		// Make sure that the list is clear.
		mSelectedCompUnits.clear();

		// Get the list of selected items
		if (selection != null && selection instanceof StructuredSelection)
		{
			Object[] sels = ((StructuredSelection) selection).toArray();
			for (Object sel : sels)
			{
				if (sel instanceof ICompilationUnit)
				{
					mSelectedCompUnits.add((ICompilationUnit) sel);
				}
			}
		}

	}

	/*******************************************************
	 * 
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction,
	 *      org.eclipse.ui.IWorkbenchPart)
	 *******************************************************/
	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
		// Does nothing
	}

}
