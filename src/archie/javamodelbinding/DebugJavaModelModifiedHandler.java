package archie.javamodelbinding;

import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;


/**
 * 
 * @author Mateusz Wieloch
 */
class DebugJavaModelModifiedHandler implements IElementChangedListener {
	private static volatile DebugJavaModelModifiedHandler singleton;
	
	private DebugJavaModelModifiedHandler() {}
	
	/**
	 * 
	 * @return
	 */
	public static DebugJavaModelModifiedHandler getInstance() {
		if (singleton == null) {
			synchronized (DebugJavaModelModifiedHandler.class) {
				if (singleton == null) {
					singleton = new DebugJavaModelModifiedHandler();
				}
			}
		}
		return singleton;
	}
	
	@Override
	public void elementChanged(ElementChangedEvent event) {
		if (event.getDelta() != null) {
			String eventType = eventTypeToString(event.getType());
			System.out.println("== DeltaTree " + eventType + " ==");
        	printDeltaTree(event.getDelta(), 0);
        	System.out.println();
		}
	}
	
	private void printDeltaTree(IJavaElementDelta delta, int depth) {
		for (int i=0; i<depth; ++i)
			System.out.print("\t");
		
		String kind = deltaKindToString(delta.getKind());
		String elementType = elementTypeToString(delta.getElement().getElementType());
		String elementName = delta.getElement().getElementName();
		
		System.out.print( kind + " " + elementType + " " + elementName );
		System.out.println();
		
		for (IJavaElementDelta subdelta : delta.getAffectedChildren())
			printDeltaTree(subdelta, depth+1);
	}
	
	private String eventTypeToString(int type) {
		if (type == ElementChangedEvent.POST_CHANGE)
			return "POST CHANGE";
		else if (type == ElementChangedEvent.POST_RECONCILE)
			return "POST RECONCILE";
		throw new IllegalArgumentException("Unknown event type");
	}
	
	private String deltaKindToString(int kind) {
		if (kind == IJavaElementDelta.ADDED)
			return "ADDED";
		else if (kind == IJavaElementDelta.CHANGED)
			return "CHANGED";
		else if (kind == IJavaElementDelta.REMOVED)
			return "REMOVED";
		throw new IllegalArgumentException("Unknown delta kind");
	}
	
	private String elementTypeToString(int type) {
		if (type == IJavaElement.ANNOTATION)
			return "ANNOTATION";
		else if (type == IJavaElement.CLASS_FILE)
			return "CLASS_FILE";
		else if (type == IJavaElement.COMPILATION_UNIT)
			return "COMPILATION UNIT";
		else if (type == IJavaElement.FIELD)
			return "FIELD";
		else if (type == IJavaElement.IMPORT_CONTAINER)
			return "IMPORT CONTAINER";
		else if (type == IJavaElement.IMPORT_DECLARATION)
			return "IMPORT DECLARATION";
		else if (type == IJavaElement.INITIALIZER)
			return "INITIALIZER";
		else if (type == IJavaElement.JAVA_MODEL)
			return "JAVA MODEL";
		else if (type == IJavaElement.JAVA_PROJECT)
			return "JAVA PROJECT";
		else if (type == IJavaElement.LOCAL_VARIABLE)
			return "LOCAL VARIABLE";
		else if (type == IJavaElement.METHOD)
			return "METHOD";
		else if (type == IJavaElement.PACKAGE_DECLARATION)
			return "PACKAGE DECLARATION";
		else if (type == IJavaElement.PACKAGE_FRAGMENT)
			return "PACKAGE FRAGMENT";
		else if (type == IJavaElement.PACKAGE_FRAGMENT_ROOT)
			return "PACKAGE FRAGMENT ROOT";
		else if (type == IJavaElement.TYPE)
			return "TYPE";
		else if (type == IJavaElement.TYPE_PARAMETER)
			return "TYPE PARAMETER";
		throw new IllegalArgumentException("Unknown element type");
	}
}
