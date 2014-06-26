package archie.javamodelbinding;

import org.eclipse.jdt.core.IPackageFragment;

/**
 * 
 * @author Mateusz Wieloch
 */
class DebugPackageModifiedHandler implements PackageModifiedListener {
	private static volatile DebugPackageModifiedHandler singleton;
	
	private DebugPackageModifiedHandler() {}
	
	/**
	 * 
	 * @return
	 */
	public static DebugPackageModifiedHandler getInstance() {
		if (singleton == null) {
			synchronized (DebugPackageModifiedHandler.class) {
				if (singleton == null)
					singleton = new DebugPackageModifiedHandler();
			}
		}
		return singleton;
	}
	
	/**
	 * 
	 */
	@Override
	public void packageMoved(IPackageFragment oldPackage, IPackageFragment newPackage) {
		String oldName = oldPackage.getElementName();
		String newName = newPackage.getElementName();
		System.out.println("PackageMoved " + oldName + " => " + newName);
	}

	/**
	 * 
	 */
	@Override
	public void packageRemoved(IPackageFragment packag) {
		System.out.println("PackageRemoved " + packag.getElementName());
	}
}
