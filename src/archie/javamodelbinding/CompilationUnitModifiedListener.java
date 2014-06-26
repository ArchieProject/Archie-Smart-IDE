package archie.javamodelbinding;

import org.eclipse.jdt.core.ICompilationUnit;

/**
 * 
 * @author Mateusz Wieloch
 */
public interface CompilationUnitModifiedListener {
	/**
	 * 
	 * @param oldCu
	 * @param newCu
	 */
	void compilationUnitMoved(ICompilationUnit oldCu, ICompilationUnit newCu);
	
	/**
	 * 
	 * @param cu
	 */
	void compilationUnitRemoved(ICompilationUnit cu);
}
