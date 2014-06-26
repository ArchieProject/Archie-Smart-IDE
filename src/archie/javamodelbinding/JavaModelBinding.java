
package archie.javamodelbinding;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;

import archie.views.autodetect.internals.JavaProjectsListener;

/**
 * [Facade] Simplifies access to the functionality of the package.
 * 
 * @author Mateusz Wieloch
 */
public class JavaModelBinding
{
	private static volatile JavaModelBinding singleton;

	private boolean isBound = false;
	private List<ProjectModifiedListener> projectModifiedListeners = new ArrayList<>();
	private List<PackageModifiedListener> packageModifiedListeners = new ArrayList<>();
	private List<CompilationUnitModifiedListener> compilationUnitModifiedListeners = new ArrayList<>();
	private List<CodeModifiedListener> codeModifiedListeners = new ArrayList<>();

	private JavaModelBinding()
	{
	}

	/**
	 * Returns the only instance of the class.
	 * 
	 * @return the only instance of the class.
	 */
	public static JavaModelBinding getInstance()
	{
		if (singleton == null)
		{
			synchronized (JavaModelBinding.class)
			{
				if (singleton == null)
					singleton = new JavaModelBinding();
			}
		}
		return singleton;
	}

	/**
	 * 
	 */
	public void bind()
	{
		if (isBound)
			return;
		isBound = true;

		// The java projects listener
		JavaCore.addElementChangedListener(JavaProjectsListener.getInstance());

		JavaCore.addElementChangedListener(DebugJavaModelModifiedHandler.getInstance());
		addProjectModifiedListener(DebugProjectModifiedHandler.getInstance());
		addPackageModifiedListener(DebugPackageModifiedHandler.getInstance());
		addCompilationUnitModifiedListener(DebugCompilationUnitModifiedHandler.getInstance());
		addCodeModifiedListener(DebugCodeModifiedHandler.getInstance());

		JavaCore.addElementChangedListener(JavaModelModifiedHandler.getInstance());
	}

	/**
	 * @throws IllegalStateException
	 *             if the JavaModelSupport component has not been started yet
	 */
	public void unbind()
	{
		if (!isBound)
			throw new IllegalStateException("The JavaModelBinding component has not been bound yet");

		JavaCore.removeElementChangedListener(JavaModelModifiedHandler.getInstance());

		removeCodeModifiedListener(DebugCodeModifiedHandler.getInstance());
		removeCompilationUnitModifiedListener(DebugCompilationUnitModifiedHandler.getInstance());
		removePackageModifiedListener(DebugPackageModifiedHandler.getInstance());
		removeProjectModifiedListener(DebugProjectModifiedHandler.getInstance());
		JavaCore.removeElementChangedListener(DebugJavaModelModifiedHandler.getInstance());

		// The java projects listener
		JavaCore.removeElementChangedListener(JavaProjectsListener.getInstance());
	}

	void projectMoved(IJavaProject oldProject, IJavaProject newProject)
	{
		for (ProjectModifiedListener listener : projectModifiedListeners)
			listener.projectMoved(oldProject, newProject);
	}

	void projectRemoved(IJavaProject project)
	{
		for (ProjectModifiedListener listener : projectModifiedListeners)
			listener.projectRemoved(project);
	}

	/**
	 * 
	 * @param listener
	 * @return
	 */
	public boolean addProjectModifiedListener(ProjectModifiedListener listener)
	{
		if (listener == null)
			return false;
		return projectModifiedListeners.add(listener);
	}

	/**
	 * 
	 * @param listener
	 * @return
	 */
	public boolean removeProjectModifiedListener(ProjectModifiedListener listener)
	{
		if (listener == null)
			return false;
		return projectModifiedListeners.remove(listener);
	}

	void packageMoved(IPackageFragment oldPackage, IPackageFragment newPackage)
	{
		for (PackageModifiedListener listener : packageModifiedListeners)
			listener.packageMoved(oldPackage, newPackage);
	}

	void packageRemoved(IPackageFragment pckg)
	{
		for (PackageModifiedListener listener : packageModifiedListeners)
			listener.packageRemoved(pckg);
	}

	/**
	 * 
	 * @param listener
	 * @return
	 */
	public boolean addPackageModifiedListener(PackageModifiedListener listener)
	{
		if (listener == null)
			return false;
		return packageModifiedListeners.add(listener);
	}

	/**
	 * 
	 * @param listener
	 * @return
	 */
	public boolean removePackageModifiedListener(PackageModifiedListener listener)
	{
		if (listener == null)
			return false;
		return packageModifiedListeners.remove(listener);
	}

	void compilationUnitMoved(ICompilationUnit oldCu, ICompilationUnit newCu)
	{
		for (CompilationUnitModifiedListener listener : compilationUnitModifiedListeners)
			listener.compilationUnitMoved(oldCu, newCu);
	}

	void compilationUnitRemoved(ICompilationUnit pckg)
	{
		for (CompilationUnitModifiedListener listener : compilationUnitModifiedListeners)
			listener.compilationUnitRemoved(pckg);
	}

	/**
	 * 
	 * @param listener
	 * @return
	 */
	public boolean addCompilationUnitModifiedListener(CompilationUnitModifiedListener listener)
	{
		if (listener == null)
			return false;
		return compilationUnitModifiedListeners.add(listener);
	}

	/**
	 * 
	 * @param listener
	 * @return
	 */
	public boolean removeCompilationUnitModifiedListener(CompilationUnitModifiedListener listener)
	{
		if (listener == null)
			return false;
		return compilationUnitModifiedListeners.remove(listener);
	}

	void codeChanged(ICompilationUnit cu)
	{
		for (CodeModifiedListener listener : codeModifiedListeners)
			listener.codeModified(cu);
	}

	/**
	 * 
	 * @param listener
	 * @return
	 */
	public boolean addCodeModifiedListener(CodeModifiedListener listener)
	{
		if (listener == null)
			return false;
		return codeModifiedListeners.add(listener);
	}

	/**
	 * 
	 * @param listener
	 * @return
	 */
	public boolean removeCodeModifiedListener(CodeModifiedListener listener)
	{
		if (listener == null)
			return false;
		return codeModifiedListeners.remove(listener);
	}
}
