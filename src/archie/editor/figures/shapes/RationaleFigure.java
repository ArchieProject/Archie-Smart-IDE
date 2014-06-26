package archie.editor.figures.shapes;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.text.*;

public class RationaleFigure extends ShapeFigure {
	TextFlow textFlow;
	private boolean marked = false;
	
	public RationaleFigure() {
		setBorder(new MarginBorder(5));
				
		FlowPage flowPage = new FlowPage();
		textFlow = new TextFlow();
		textFlow.setLayoutManager(new ParagraphTextLayout(textFlow, ParagraphTextLayout.WORD_WRAP_SOFT));
		flowPage.setHorizontalAligment(PositionConstants.CENTER);
		flowPage.add(textFlow);
		
		setLayoutManager(new StackLayout());
		add(flowPage);
	}
	
	@Override
	protected void paintFigure(Graphics graphics) {
		Rectangle rect = getBounds().getCopy();
		graphics.translate(getBounds().x, getBounds().y);
		
		// border
		graphics.setBackgroundColor(ColorConstants.white);
		graphics.fillRectangle(0, 0, rect.width-1, rect.height-1);
		graphics.setForegroundColor( marked ? ColorConstants.red : ColorConstants.black );
		if(marked) {
			graphics.setForegroundColor(ColorConstants.red);
			graphics.setLineWidth(2);
			graphics.drawRectangle(1, 1, rect.width-2, rect.height-2);
		} else {
			graphics.setForegroundColor( ColorConstants.black );
			graphics.setLineWidth(1);
			graphics.drawRectangle(0, 0, rect.width-1, rect.height-1);
		}
		
		graphics.translate(-getBounds().x, -getBounds().y);
	}
	
	@Override
	public ConnectionAnchor getConnectionAnchor() {
		return new ChopboxAnchor(this);
	}
	
	@Override
	public Rectangle getClientAreaOfName() {
		return getClientArea();
	}

	@Override
	public String getName() {
		return textFlow.getText();
	}
	
	@Override
	public void setName(String text) {
		textFlow.setText(text);
	}
}