package archie.editor.dialogs;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;



public class MyJavaElementFilter extends ViewerFilter {

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof IJavaProject)
			return true;
		else if (element instanceof IPackageFragmentRoot) {
			IPackageFragmentRoot pckg = (IPackageFragmentRoot) element;
			if (!pckg.getElementName().endsWith(".jar"))
				return true;
		}
		else if (element instanceof IPackageFragment) {
			IPackageFragment pckg = (IPackageFragment) element;
			try {
				for(IJavaElement child : pckg.getChildren())
					if (child.getElementType() == IJavaElement.COMPILATION_UNIT )
						return true;
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (element instanceof ICompilationUnit)
			return true;
		else if (element instanceof IType)
			return true;
		else if (element instanceof IMethod)
			return true;
		return false;
	}

}
