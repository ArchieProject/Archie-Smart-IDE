package archie.editor.parts;

import org.eclipse.draw2d.*;

import archie.editor.figures.connections.AggregationFigure;



public class AggregationEditPart extends ConnectionEditPart {
	@Override
	protected IFigure createFigure() {
		return new AggregationFigure();
	}
}
