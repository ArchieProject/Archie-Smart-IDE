package archie.editor.commands;
//package archie.editors.tim.commands;
//
//import org.eclipse.draw2d.geometry.Rectangle;
//import org.eclipse.gef.commands.Command;
//
//
////
////public class MoveAndResizeElementCommand extends Command {
////	
////	private final Element element;
////	private Rectangle oldBox;
////	private final Rectangle box;
////	
////	public MoveAndResizeElementCommand(Element element, Rectangle box) {
////		super("Modify " + element.getClass().getSimpleName());
////		this.element = element;
////		this.box = box;
////	}
////	
////	@Override
////	public void execute() {
////		oldBox = new Rectangle(element.getX(), element.getY(), element.getWidth(), element.getHeight());
////		element.setLocation(box.x, box.y);
////		element.setSize(box.width, box.height);
////	}
////	
////	@Override
////	public void undo() {
////		element.setLocation(oldBox.x, oldBox.y);
////		element.setSize(oldBox.width, oldBox.height);
////	}
////}
