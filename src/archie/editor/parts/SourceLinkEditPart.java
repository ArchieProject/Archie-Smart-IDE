package archie.editor.parts;

import org.eclipse.draw2d.IFigure;

import archie.editor.figures.shapes.SourceLinkFigure;
import archie.model.shapes.SourceLink;


public class SourceLinkEditPart extends ShapeEditPart {
	// =========== MODEL ============
	@Override
	public SourceLink getModel() {
		return (SourceLink)super.getModel();
	}
	
	// ========== VISUALS ===========
	@Override
	public SourceLinkFigure getFigure() {
		return (SourceLinkFigure)super.getFigure();
	}
	
	@Override
	protected IFigure createFigure() {
		return new SourceLinkFigure();
	}
}
