package archie.editor.parts;

import org.eclipse.draw2d.IFigure;

import archie.editor.figures.shapes.RequirementFigure;
import archie.model.shapes.Requirement;




public class RequirementEditPart extends ShapeEditPart {
	
	// =========== MODEL ============
	@Override
	public Requirement getModel() {
		return (Requirement)super.getModel();
	}
	
	// ========== VISUALS ===========
	@Override
	public RequirementFigure getFigure() {
		return (RequirementFigure)super.getFigure();
	}
	
	@Override
	protected IFigure createFigure() {
		return new RequirementFigure();
	}
}
