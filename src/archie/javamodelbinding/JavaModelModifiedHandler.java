
package archie.javamodelbinding;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;

import archie.globals.ArchieSettings;
import archie.monitoring.MonitoringManager;

/**
 * (Singleton)An element changed listener receives notification of changes to
 * Java elements maintained by the Java model.
 * 
 * This interface may be implemented by clients. Tracks changes to *.java files.
 */
class JavaModelModifiedHandler implements IElementChangedListener
{
	private static volatile JavaModelModifiedHandler singleton;

	private JavaModelModifiedHandler()
	{
	}

	/**
	 * Returns the only instance of the class
	 * 
	 * @return the only instance of the class
	 */
	public static JavaModelModifiedHandler getInstance()
	{
		if (singleton == null)
		{
			synchronized (DebugCodeModifiedHandler.class)
			{
				if (singleton == null)
					singleton = new JavaModelModifiedHandler();
			}
		}
		return singleton;
	}

	/**
	 * Handles change to code in an open code editor
	 */
	@Override
	public void elementChanged(ElementChangedEvent event)
	{
		if (event.getDelta() != null)
		{
			traverseDeltaTree(event.getDelta());
		}
	}

	// final List<IMember> pme = extractPotentiallyModifiedElements(
	// event.getDelta() );
	//
	// Display.getDefault().asyncExec(new Runnable() {
	// public void run() {
	// for (IMember m : pme) {
	// BalloonPopup.showInfo("You have modified a tactical code", "");
	// JavaElementsMarker.mark(m);
	// }
	// }
	// });

	public void traverseDeltaTree(IJavaElementDelta delta)
	{
		int kind = delta.getKind();
		int elementType = delta.getElement().getElementType();

		if (kind == IJavaElementDelta.CHANGED && elementType == IJavaElement.JAVA_MODEL)
		{
			if (delta.getAddedChildren().length == 1 && delta.getRemovedChildren().length == 1)
			{
				IJavaProject oldProject = (IJavaProject) delta.getRemovedChildren()[0].getElement();
				IJavaProject newProject = (IJavaProject) delta.getAddedChildren()[0].getElement();
				JavaModelBinding.getInstance().projectMoved(oldProject, newProject);
			}
			else
			{
				for (IJavaElementDelta removedDelta : delta.getRemovedChildren())
				{
					IJavaProject project = (IJavaProject) removedDelta.getElement();
					JavaModelBinding.getInstance().projectRemoved(project);
				}
			}
		}

		if (kind == IJavaElementDelta.CHANGED && elementType == IJavaElement.PACKAGE_FRAGMENT_ROOT)
		{
			if (delta.getAddedChildren().length == 1 && delta.getRemovedChildren().length == 1)
			{
				IPackageFragment oldPackage = (IPackageFragment) delta.getRemovedChildren()[0].getElement();
				IPackageFragment newPackage = (IPackageFragment) delta.getAddedChildren()[0].getElement();
				JavaModelBinding.getInstance().packageMoved(oldPackage, newPackage);
			}
			else
			{
				for (IJavaElementDelta removedDelta : delta.getRemovedChildren())
				{
					IPackageFragment pckg = (IPackageFragment) removedDelta.getElement();
					JavaModelBinding.getInstance().packageRemoved(pckg);
				}
			}
		}

		if (kind == IJavaElementDelta.CHANGED && elementType == IJavaElement.PACKAGE_FRAGMENT)
		{
			if (delta.getAddedChildren().length == 1 && delta.getRemovedChildren().length == 1)
			{
				ICompilationUnit oldCu = (ICompilationUnit) delta.getRemovedChildren()[0].getElement();
				ICompilationUnit newCu = (ICompilationUnit) delta.getAddedChildren()[0].getElement();
				JavaModelBinding.getInstance().compilationUnitMoved(oldCu, newCu);
			}
			else
			{
				for (IJavaElementDelta removedDelta : delta.getRemovedChildren())
				{
					ICompilationUnit cu = (ICompilationUnit) removedDelta.getElement();
					JavaModelBinding.getInstance().compilationUnitRemoved(cu);
				}
			}
		}

		if (kind == IJavaElementDelta.CHANGED && elementType == IJavaElement.COMPILATION_UNIT && isAstAffected(delta))
		{
			
			ICompilationUnit cu = (ICompilationUnit) delta.getElement();

			if (ArchieSettings.MONITORING && isContentChanged(delta))
			{
				// This java file was modified, generate the TIM warnings if
				// it is
				// Added to any of the managed TIMs as a CodeElement
				this.generateWarnings(cu);
			}

			JavaModelBinding.getInstance().codeChanged(cu);
		}

		for (IJavaElementDelta subdelta : delta.getAffectedChildren())
			traverseDeltaTree(subdelta);
	}

	/*******************************************************
	 * Generate the TIM needed warnings for given resource, which should be a
	 * java file, If this file is added to any of the managed TIMs as a code
	 * element.
	 * 
	 * @param cu
	 *            The modified java file.
	 *******************************************************/
	private void generateWarnings(ICompilationUnit cu)
	{
		try
		{
			IResource resource = cu.getCorrespondingResource();

			// The name of the modified file.
			IPath path = resource.getLocation();
			
			if(path != null)
			{
				String name = path.toOSString();
				
				if(name != null)
				{
					MonitoringManager.getIntance().markAndGenerateWarnings(name);
				}
			}
		}
		catch (JavaModelException e)
		{
			System.err.println("Failed to generate warnings!");
			e.printStackTrace();
		}
	}

	private boolean isContentChanged(IJavaElementDelta delta)
	{
		return delta.getKind() == IJavaElementDelta.CHANGED && (delta.getFlags() & IJavaElementDelta.F_CONTENT) != 0;
	}

	private boolean isAstAffected(IJavaElementDelta delta)
	{
		return (delta.getFlags() & IJavaElementDelta.F_AST_AFFECTED) == IJavaElementDelta.F_AST_AFFECTED;
	}
}
