package archie.editor.parts;

import org.eclipse.draw2d.IFigure;

import archie.editor.figures.shapes.ParameterFigure;
import archie.model.shapes.Parameter;



public class ParameterEditPart extends ShapeEditPart {
	// =========== MODEL ============
	@Override
	public Parameter getModel() {
		return (Parameter)super.getModel();
	}
	
	// ========== VISUALS ===========
	@Override
	public ParameterFigure getFigure() {
		return (ParameterFigure)super.getFigure();
	}
	
	@Override
	protected IFigure createFigure() {
		return new ParameterFigure();
	}
}
