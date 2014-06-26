package archie.editor.figures.shapes;

import org.eclipse.draw2d.*;

public class NameFigure extends Label {
	
	public NameFigure(String name) {
		super(name);
		//setBorder(new MarginBorder(0, 0, 3, 0));
	}
	
	@Override
	protected void paintFigure(Graphics graphics) {
		//graphics.drawLine(getBounds().x, getBounds().y + getBounds().height -1, getBounds().x + getBounds().width, getBounds().y + getBounds().height-1);
		super.paintFigure(graphics);
	}
}
