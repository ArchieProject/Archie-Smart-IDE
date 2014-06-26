package archie.editor.figures.connections;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.SWT;


public class MappingFigure extends ConnectionFigure{
	
	public MappingFigure() {
		super();
		
		// '>' arrow
		PolylineDecoration decoration = new PolylineDecoration();
		decoration.setAntialias(SWT.ON);
		decoration.setLineWidthFloat(1.3f);
		PointList points = new PointList();
		points.addPoint(-2, 2);
		points.addPoint(0, 0);
		points.addPoint(-2, -2);
		decoration.setTemplate(points);
		
		setTargetDecoration(decoration);
		setLineStyle(SWT.LINE_CUSTOM);
		setAntialias(SWT.ON);
		setLineWidthFloat(1.3f);
		setLineDash(new float[] { 8f, 6f });
	}
}
