package archie.editor.parts;

import org.eclipse.draw2d.IFigure;

import archie.editor.figures.shapes.GoalFigure;
import archie.model.shapes.Goal;



public class GoalEditPart extends ShapeEditPart {
	// =========== MODEL ============
	@Override
	public Goal getModel() {
		return (Goal)super.getModel();
	}
	
	// ========== VISUALS ===========
	@Override
	public GoalFigure getFigure() {
		return (GoalFigure)super.getFigure();
	}
	
	@Override
	protected IFigure createFigure() {
		return new GoalFigure();
	}
}
