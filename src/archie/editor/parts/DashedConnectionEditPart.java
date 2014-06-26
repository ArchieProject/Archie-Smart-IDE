package archie.editor.parts;

import org.eclipse.draw2d.*;

import archie.editor.figures.connections.DashedConnectionFigure;



public class DashedConnectionEditPart extends ConnectionEditPart {
	@Override
	protected IFigure createFigure() {
		return new DashedConnectionFigure();
	}
}
