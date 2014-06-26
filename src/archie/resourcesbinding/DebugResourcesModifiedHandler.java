package archie.resourcesbinding;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;


/**
 * 
 * @author Mateusz Wieloch
 */
public class DebugResourcesModifiedHandler implements IResourceChangeListener {
	private static volatile DebugResourcesModifiedHandler singleton;
	
	private DebugResourcesModifiedHandler() {}
	
	/**
	 * 
	 * @return
	 */
	public static DebugResourcesModifiedHandler getInstance() {
		if (singleton == null) {
			synchronized (DebugResourcesModifiedHandler.class) {
				if (singleton == null)
					singleton = new DebugResourcesModifiedHandler();
			}
		}
		return singleton;
	}
	
	/**
	 * 
	 */
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		if (event.getDelta() != null) {
			String eventType = eventTypeToString(event.getType());
			System.out.println("++ Resources Delta Tree ++" + eventType + " ++");
        	printDeltaTree(event.getDelta(), 0);
        	System.out.println();
		}
	}
	
	private void printDeltaTree(IResourceDelta delta, int depth) {
		for (int i=0; i<depth; ++i)
			System.out.print("\t");
		
		String kind = deltaKindToString(delta.getKind());
		String elementType = elementTypeToString(delta.getResource().getType());
		String elementName = delta.getResource().getName();
		String flags = flagsToString(delta.getFlags());
		
		System.out.print( kind + " " + elementType + " " + elementName + " [" + flags + "]");
		System.out.println();
		
		for (IResourceDelta subdelta : delta.getAffectedChildren())
			printDeltaTree(subdelta, depth+1);
	}
	
	private String eventTypeToString(int type) {
		if (type == IResourceChangeEvent.POST_BUILD)
			return "POST BUILD";
		else if (type == IResourceChangeEvent.POST_CHANGE)
			return "POST CHANGE";
		else if (type == IResourceChangeEvent.PRE_BUILD)
			return "PRE BUILD";
		else if (type == IResourceChangeEvent.PRE_CLOSE)
			return "PRE CLOSE";
		else if (type == IResourceChangeEvent.PRE_DELETE)
			return "PRE DELETE";
		else if (type == IResourceChangeEvent.PRE_REFRESH)
			return "PRE REFRESH";
		throw new IllegalArgumentException("Unknown event type");
	}
	
	private String elementTypeToString(int type) {
		if (type == IResource.FILE)
			return "FILE";
		else if (type == IResource.FOLDER)
			return "FOLDER";
		else if (type == IResource.PROJECT)
			return "PROJECT";
		else if (type == IResource.ROOT)
			return "ROOT";
		throw new IllegalArgumentException("Unknown resource type");
	}
	
	private String deltaKindToString(int kind) {
		
		String str = "";
		
		if (kind == IResourceDelta.ADDED)
			str += "ADDED ";
		if (kind == IResourceDelta.CHANGED)
			str += "CHANGED ";
		if (kind == IResourceDelta.REMOVED)
			str += "REMOVED ";
		if (kind == IResourceDelta.ADDED_PHANTOM || kind == IResourceDelta.REMOVED_PHANTOM)
			str += "PHANTOM-RELATED ";
		
		
		return str;
		//throw new IllegalArgumentException("Unknown kind of delta");
	}
	
	private String flagsToString(int flags) {
		String str = "";
		if ( (flags & IResourceDelta.CONTENT) != 0)
			str += "CONTENT ";
		if ( (flags & IResourceDelta.DERIVED_CHANGED) != 0)
			str += "DERIVED_CHANGED ";
		if ( (flags & IResourceDelta.ENCODING) != 0)
			str += "ENCODING ";
		if ( (flags & IResourceDelta.DESCRIPTION) != 0)
			str += "DESCRIPTION ";
		if ( (flags & IResourceDelta.OPEN) != 0)
			str += "OPEN ";
		if ( (flags & IResourceDelta.TYPE) != 0)
			str += "TYPE ";
		if ( (flags & IResourceDelta.SYNC) != 0)
			str += "SYNC ";
		if ( (flags & IResourceDelta.MARKERS) != 0)
			str += "MARKERS ";
		if ( (flags & IResourceDelta.REPLACED) != 0)
			str += "REPLACED ";
		if ( (flags & IResourceDelta.LOCAL_CHANGED) != 0)
			str += "LOCAL_CHANGED ";
		if ( (flags & IResourceDelta.MOVED_TO) != 0)
			str += "MOVED_TO ";
		if ( (flags & IResourceDelta.MOVED_FROM) != 0)
			str += "MOVED_FROM ";
		if ( (flags & IResourceDelta.COPIED_FROM) != 0)
			str += "COPIED_FROM ";
		return str;
	}
}
