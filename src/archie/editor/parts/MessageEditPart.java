package archie.editor.parts;

import org.eclipse.draw2d.IFigure;

import archie.editor.figures.shapes.MessageFigure;
import archie.model.shapes.Message;



public class MessageEditPart extends ShapeEditPart {
	// =========== MODEL ============
	@Override
	public Message getModel() {
		return (Message)super.getModel();
	}
	
	// ========== VISUALS ===========
	@Override
	public MessageFigure getFigure() {
		return (MessageFigure)super.getFigure();
	}
	
	@Override
	protected IFigure createFigure() {
		return new MessageFigure();
	}
}
