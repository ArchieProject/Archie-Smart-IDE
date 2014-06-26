package archie.javamodelbinding;

import org.eclipse.jdt.core.ICompilationUnit;


/**
 * 
 * @author Mateusz Wieloch
 */
class DebugCompilationUnitModifiedHandler implements CompilationUnitModifiedListener {
	private static volatile DebugCompilationUnitModifiedHandler singleton;
	
	private DebugCompilationUnitModifiedHandler() {}
	
	/**
	 * 
	 * @return
	 */
	public static DebugCompilationUnitModifiedHandler getInstance() {
		if (singleton == null) {
			synchronized (DebugCompilationUnitModifiedHandler.class) {
				if (singleton == null)
					singleton = new DebugCompilationUnitModifiedHandler();
			}
		}
		return singleton;
	}
	
	/**
	 * 
	 */
	@Override
	public void compilationUnitMoved(ICompilationUnit oldCu, ICompilationUnit newCu) {
		String oldName = oldCu.getElementName();
		String newName = newCu.getElementName();
		System.out.println("CompilationUnitMoved " + oldName + " => " + newName);
	}

	/**
	 * 
	 */
	@Override
	public void compilationUnitRemoved(ICompilationUnit cu) {
		System.out.println("CompilationUnitRemoved " + cu.getElementName());
	}
}
