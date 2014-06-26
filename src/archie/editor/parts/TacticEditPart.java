package archie.editor.parts;

import org.eclipse.draw2d.IFigure;

import archie.editor.figures.shapes.TacticFigure;
import archie.model.shapes.Tactic;


public class TacticEditPart extends ShapeEditPart {
	// =========== MODEL ============
	@Override
	public Tactic getModel() {
		return (Tactic)super.getModel();
	}
	
	// ========== VISUALS ===========
	@Override
	public TacticFigure getFigure() {
		return (TacticFigure)super.getFigure();
	}
	
	@Override
	protected IFigure createFigure() {
		return new TacticFigure();
	}
}
