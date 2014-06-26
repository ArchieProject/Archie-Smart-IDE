package archie.editor.parts;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Text;

import archie.editor.figures.shapes.ShapeFigure;


public class ShapeCellEditorLocator implements CellEditorLocator {
	private ShapeFigure figure;
	
	public ShapeCellEditorLocator(ShapeFigure figure) {
		this.figure = figure;
	}
	
	@Override
	public void relocate(CellEditor celleditor) {
		Text text = (Text)celleditor.getControl();
		
		Rectangle clientArea = figure.getClientAreaOfName();
		figure.translateToAbsolute(clientArea);
		org.eclipse.swt.graphics.Rectangle trim = text.computeTrim(0, 0, 0, 0);
		clientArea.translate(trim.x, trim.y);
		clientArea.width += trim.width;
		clientArea.height += trim.height;
		text.setBounds(clientArea.x, clientArea.y, clientArea.width, clientArea.height);
	}
}
