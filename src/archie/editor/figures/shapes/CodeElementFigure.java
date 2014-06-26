package archie.editor.figures.shapes;

import org.eclipse.draw2d.*;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;

import archie.editor.TimEditor;



public class CodeElementFigure extends Label {
	
	public CodeElementFigure(String name) {
		super(name);
	}
	
	public void setJavaElementType(int type) {
		if (type == IJavaElement.TYPE)
			setIcon(ImageDescriptor.createFromFile(TimEditor.class, "icons/class.png").createImage());
		else if(type == IJavaElement.METHOD)
			setIcon(ImageDescriptor.createFromFile(TimEditor.class, "icons/method.png").createImage());
	}
	
	public void setMarked(boolean marked) {
		if(marked) {
			setForegroundColor(ColorConstants.red);
			FontData current = getFont().getFontData()[0];
			current.setStyle(SWT.BOLD);
			setFont(new Font(null, current));
		} else {
			setForegroundColor(ColorConstants.black);
			FontData current = getFont().getFontData()[0];
			current.setStyle(SWT.NORMAL);
			setFont(new Font(null, current));
		}
	}
}
