package archie.javamodelbinding;

import org.eclipse.jdt.core.IJavaProject;

/**
 * 
 * @author Mateusz Wieloch
 */
class DebugProjectModifiedHandler implements ProjectModifiedListener {
	private static volatile DebugProjectModifiedHandler singleton;
	
	private DebugProjectModifiedHandler() {}
	
	/**
	 * 
	 * @return
	 */
	public static DebugProjectModifiedHandler getInstance() {
		if (singleton == null) {
			synchronized (DebugProjectModifiedHandler.class) {
				if (singleton == null)
					singleton = new DebugProjectModifiedHandler();
			}
		}
		return singleton;
	}
	
	/**
	 * 
	 */
	@Override
	public void projectMoved(IJavaProject oldProject, IJavaProject newProject) {
		String oldName = oldProject.getElementName();
		String newName = newProject.getElementName();
		System.out.println("ProjectMoved " + oldName + " => " + newName);
	}
	
	/**
	 * 
	 */
	@Override
	public void projectRemoved(IJavaProject project) {
		System.out.println("ProjectRemoved " + project.getElementName());
	}
}
