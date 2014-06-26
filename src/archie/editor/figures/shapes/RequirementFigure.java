package archie.editor.figures.shapes;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.text.*;

public class RequirementFigure extends ShapeFigure {
	TextFlow textFlow;
	
	public RequirementFigure() {
		setBorder(new MarginBorder(6, 17, 6, 5));
		
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
		graphics.setForegroundColor( ColorConstants.black );
		graphics.setLineWidth(1);
		graphics.drawRectangle(0, 0, rect.width-1, rect.height-1);
		
		// lines
		graphics.setLineWidth(2);
		graphics.setForegroundColor(ColorConstants.black);
		graphics.drawRectangle(10, 1, 4, rect.height-3);
		graphics.setBackgroundColor(ColorConstants.red);
		graphics.fillRectangle(11, 1, 2, rect.height-2);
		
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