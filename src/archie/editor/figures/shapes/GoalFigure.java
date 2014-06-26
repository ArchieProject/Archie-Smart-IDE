package archie.editor.figures.shapes;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.*;
import org.eclipse.draw2d.text.*;




public class GoalFigure extends ShapeFigure {
	TextFlow textFlow;
	
	public GoalFigure() {
		setBackgroundColor(ColorConstants.lightGray);
		setForegroundColor(ColorConstants.black);
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
		graphics.fillRectangle(0, 0, rect.width-1, rect.height-1);
		graphics.setForegroundColor( ColorConstants.black );
		graphics.setLineWidth(1);
		graphics.drawRectangle(0, 0, rect.width-1, rect.height-1);
		
		// lines
		graphics.setForegroundColor(ColorConstants.black);
		graphics.drawLine(11, 0, 11, rect.height-1);
		graphics.drawLine(14, 0, 14, rect.height-1);
		
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
