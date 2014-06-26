package archie.editor.parts;

import org.eclipse.draw2d.*;

import archie.editor.figures.shapes.NoteFigure;
import archie.model.shapes.Note;



public class NoteEditPart extends ShapeEditPart {
	
	// =========== MODEL ============
	@Override
	public Note getModel() {
		return (Note)super.getModel();
	}
	
	// ========== VISUALS ===========
	@Override
	public NoteFigure getFigure() {
		return (NoteFigure)super.getFigure();
	}
	
	@Override
	protected IFigure createFigure() {
		return new NoteFigure();
	}
}