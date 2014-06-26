package archie.javamodelbinding;

import org.eclipse.jdt.core.IJavaProject;

public interface ProjectModifiedListener {
	void projectMoved(IJavaProject oldProject, IJavaProject newProject);
	void projectRemoved(IJavaProject project);
}
