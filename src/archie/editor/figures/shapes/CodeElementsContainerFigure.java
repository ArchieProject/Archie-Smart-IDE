package archie.editor.figures.shapes;

import org.eclipse.draw2d.*;

public class CodeElementsContainerFigure extends Figure {
	
	public CodeElementsContainerFigure() {
		ToolbarLayout codeElementsLayout = new ToolbarLayout();
		codeElementsLayout.setMinorAlignment(ToolbarLayout.ALIGN_TOPLEFT);
		codeElementsLayout.setStretchMinorAxis(false);
		setLayoutManager(codeElementsLayout);
		
		setBorder(new MarginBorder(3, 0, 0, 0));
	}
	
	@Override
	protected void paintFigure(Graphics graphics) {
		graphics.drawLine(getBounds().x, getBounds().y + 1, getBounds().x + getBounds().width, getBounds().y + 1);
		super.paintFigure(graphics);
	}
}
