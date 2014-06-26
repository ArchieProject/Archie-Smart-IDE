package archie.editor.figures.shapes;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.*;
import org.eclipse.draw2d.text.*;



public class NoteFigure extends ShapeFigure {
	private static final int CORNER_SIZE = 10;
	
	FlowPage flowPage = new FlowPage();
	TextFlow textFlow;
	
	public NoteFigure() {
		setBackgroundColor(ColorConstants.tooltipBackground);
		setForegroundColor(ColorConstants.tooltipForeground);
		setBorder(new MarginBorder(7));
		
		
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

		// fill the note
		PointList outline = new PointList();

		outline.addPoint(0, 0);
		outline.addPoint(rect.width - CORNER_SIZE, 0);
		outline.addPoint(rect.width - 1, CORNER_SIZE);
		outline.addPoint(rect.width - 1, rect.height - 1);
		outline.addPoint(0, rect.height - 1);

		graphics.fillPolygon(outline);

		// draw the inner outline
		PointList innerLine = new PointList();

		innerLine.addPoint(rect.width - CORNER_SIZE - 1, 0);
		innerLine.addPoint(rect.width - CORNER_SIZE - 1, CORNER_SIZE);
		innerLine.addPoint(rect.width - 1, CORNER_SIZE);
		innerLine.addPoint(rect.width - CORNER_SIZE - 1, 0);
		innerLine.addPoint(0, 0);
		innerLine.addPoint(0, rect.height - 1);
		innerLine.addPoint(rect.width - 1, rect.height - 1);
		innerLine.addPoint(rect.width - 1, CORNER_SIZE);

		graphics.setForegroundColor( ColorConstants.black );
		graphics.drawPolygon(innerLine);

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