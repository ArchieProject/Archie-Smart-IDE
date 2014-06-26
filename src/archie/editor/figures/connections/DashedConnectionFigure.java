package archie.editor.figures.connections;

import org.eclipse.swt.SWT;


public class DashedConnectionFigure extends ConnectionFigure {
	
	public DashedConnectionFigure() {
		super();
		
		setLineStyle(SWT.LINE_CUSTOM);
		setAntialias(SWT.ON);
		setLineWidthFloat(1.3f);
		setLineDash(new float[] { 3f, 6f });
	}
}
