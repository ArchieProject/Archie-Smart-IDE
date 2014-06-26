package archie.javamodelbinding;

import org.eclipse.jdt.core.ICompilationUnit;


/**
 * Receives notification of changes to ICompilationUnits maintained by the JavaModel.
 * @author Mateusz Wieloch
 */
public interface CodeModifiedListener {
	/**
	 * Notifies that there were fine-grained changes in a given ICompilationUnit.
	 * @param compilationUnit the ICompilationUnit that contains the fine-grained changes
	 */
	void codeModified(ICompilationUnit compilationUnit);
}
