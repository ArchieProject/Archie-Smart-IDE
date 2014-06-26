package archie.editor.parts;

import org.eclipse.draw2d.IFigure;

import archie.editor.figures.shapes.ComponentFigure;
import archie.model.shapes.Component;



public class ComponentEditPart extends ShapeEditPart {
	// =========== MODEL ============
	@Override
	public Component getModel() {
		return (Component)super.getModel();
	}
	
	// ========== VISUALS ===========
	@Override
	public ComponentFigure getFigure() {
		return (ComponentFigure)super.getFigure();
	}
	
	@Override
	protected IFigure createFigure() {
		return new ComponentFigure();
	}
	
	// ======= EDIT POLICIES ========
	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		
		//installEditPolicy(EditPolicy.COMPONENT_ROLE, new ShapeDndEditPolicy());
	}
}
