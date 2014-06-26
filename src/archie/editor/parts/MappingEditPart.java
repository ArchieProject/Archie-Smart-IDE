package archie.editor.parts;

import org.eclipse.draw2d.*;

import archie.editor.figures.connections.MappingFigure;



public class MappingEditPart extends ConnectionEditPart {
	@Override
	protected IFigure createFigure() {
		return new MappingFigure();
	}
}
