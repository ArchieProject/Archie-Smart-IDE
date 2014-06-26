package archie.editor.figures.shapes;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.*;
import org.eclipse.draw2d.text.*;
import org.eclipse.swt.SWT;



public class TacticFigure extends ShapeFigure {
	TextFlow textFlow;
	
	public TacticFigure() {
		setForegroundColor(ColorConstants.white);
		setBorder(new MarginBorder(7));
		
		FlowPage flowPage = new FlowPage();
		textFlow = new TextFlow();
		textFlow.setLayoutManager(new ParagraphTextLayout(textFlow, ParagraphTextLayout.WORD_WRAP_SOFT));
		flowPage.setHorizontalAligment(PositionConstants.CENTER);
		flowPage.add(textFlow);
		
		setLayoutManager(new StackLayout());
		add(flowPage);
	}
	
	@Override
	public void paintFigure(Graphics graphics) {
		Rectangle rect = getBounds().getCopy();
		graphics.translate(getBounds().x, getBounds().y);

		// oval
		graphics.setBackgroundColor(ColorConstants.gray);
		graphics.fillOval(1, 1, rect.width-2, rect.height-2);
		
		graphics.setLineWidth(2);
		graphics.setAntialias(SWT.ON);
		graphics.setForegroundColor( ColorConstants.black );
		graphics.drawOval(1, 1, rect.width-2, rect.height-2);
		
		graphics.translate(-getBounds().x, -getBounds().y);
	}
	
	@Override
	public ConnectionAnchor getConnectionAnchor() {
		return new EllipseAnchor(this);
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
