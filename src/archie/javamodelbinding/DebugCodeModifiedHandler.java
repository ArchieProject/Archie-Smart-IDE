package archie.javamodelbinding;

import org.eclipse.jdt.core.ICompilationUnit;


/**
 * Receives notification of changes to ICompilationUnits maintained by the JavaModel.
 * @author Mateusz Wieloch
 */
class DebugCodeModifiedHandler implements CodeModifiedListener{
	private static volatile DebugCodeModifiedHandler singleton;
	
	private DebugCodeModifiedHandler() {}
	
	/**
	 * 
	 * @return
	 */
	public static DebugCodeModifiedHandler getInstance() {
		if (singleton == null) {
			synchronized (DebugCodeModifiedHandler.class) {
				if (singleton == null)
					singleton = new DebugCodeModifiedHandler();
			}
		}
		return singleton;
	}
	
	/**
	 * Notifies that there were fine-grained changes in a given ICompilationUnit.
	 * @param compilationUnit the ICompilationUnit that contains the fine-grained changes
	 */
	@Override
	public void codeModified(ICompilationUnit compilationUnit) {
		System.out.println("CodeModified in " + compilationUnit.getElementName());
	}
	
}
