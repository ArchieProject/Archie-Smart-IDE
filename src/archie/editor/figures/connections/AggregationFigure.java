package archie.editor.figures.connections;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.SWT;

public class AggregationFigure extends ConnectionFigure {
	
	public AggregationFigure() {
		super();
		
		PolygonDecoration decoration = new PolygonDecoration();
		decoration.setAntialias(SWT.ON);
		decoration.setLineWidthFloat(1.3f);
		PointList points = new PointList();
		points.addPoint(-2, 2);
		points.addPoint(0, 0);
		points.addPoint(-2, -2);
		points.addPoint(-4, 0);
		points.addPoint(-2, 2);
		decoration.setTemplate(points);
		decoration.setBackgroundColor(ColorConstants.white);
		decoration.setFill(true);
		
		setTargetDecoration(decoration);
		setLineStyle(SWT.LINE_SOLID);
		setAntialias(SWT.ON);
		setLineWidthFloat(1.3f);
	}
}
