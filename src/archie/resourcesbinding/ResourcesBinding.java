package archie.resourcesbinding;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;

import archie.timstorage.TimsModifiedHandler;

/**
 * 
 * @author Mateusz Wieloch
 */
public class ResourcesBinding {
	private static volatile ResourcesBinding singleton;
	
	private boolean isBound = false;
	
	private ResourcesBinding() {}
	
	/**
	 * Returns the only instance of the class.
	 * @return the only instance of the class.
	 */
	public static ResourcesBinding getInstance() {
		if (singleton == null) {
			synchronized (ResourcesBinding.class) {
				if (singleton == null)
					singleton = new ResourcesBinding();
			}
		}
		return singleton;
	}
	
	/**
	 * 
	 */
	public void bind() {
		if (isBound)
			return;
		isBound = true;
		
		ResourcesPlugin.getWorkspace().addResourceChangeListener( DebugResourcesModifiedHandler.getInstance() );
		
		ResourcesPlugin.getWorkspace().addResourceChangeListener( TimsModifiedHandler.getInstance(), IResourceChangeEvent.POST_CHANGE );
	}
	
	/**
	 * @throws IllegalStateException if the JavaModelSupport component has not been started yet
	 */
	public void unbind() {
		if (!isBound)
			throw new IllegalStateException("The JavaModelBinding component has not been bound yet");
		
		ResourcesPlugin.getWorkspace().removeResourceChangeListener( TimsModifiedHandler.getInstance() );
		
		ResourcesPlugin.getWorkspace().removeResourceChangeListener( DebugResourcesModifiedHandler.getInstance() );
	}
}
