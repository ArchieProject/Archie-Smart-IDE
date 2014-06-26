package archie.editor.parts;

import org.eclipse.draw2d.IFigure;

import archie.editor.figures.shapes.AttributeFigure;
import archie.model.shapes.Attribute;


public class AttributeEditPart extends ShapeEditPart {
	// =========== MODEL ============
	@Override
	public Attribute getModel() {
		return (Attribute)super.getModel();
	}
	
	// ========== VISUALS ===========
	@Override
	public AttributeFigure getFigure() {
		return (AttributeFigure)super.getFigure();
	}
	
	@Override
	protected IFigure createFigure() {
		return new AttributeFigure();
	}
}
