package archie.editor.parts;

import org.eclipse.draw2d.IFigure;

import archie.editor.figures.shapes.RationaleFigure;
import archie.model.shapes.Rationale;


public class RationaleEditPart extends ShapeEditPart {
	// =========== MODEL ============
	@Override
	public Rationale getModel() {
		return (Rationale)super.getModel();
	}
	
	// ========== VISUALS ===========
	@Override
	public RationaleFigure getFigure() {
		return (RationaleFigure)super.getFigure();
	}
	
	@Override
	protected IFigure createFigure() {
		return new RationaleFigure();
	}
}
