package archie.javamodelbinding;

import org.eclipse.jdt.core.IPackageFragment;

public interface PackageModifiedListener {
	void packageMoved(IPackageFragment oldPackage, IPackageFragment newPackage);
	void packageRemoved(IPackageFragment packag);
}
