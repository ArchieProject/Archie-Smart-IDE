package archie.editor.parts;

import org.eclipse.draw2d.*;

import archie.editor.figures.connections.DependencyFigure;



public class DependencyEditPart extends ConnectionEditPart {
	@Override
	protected IFigure createFigure() {
		return new DependencyFigure();
	}
}
