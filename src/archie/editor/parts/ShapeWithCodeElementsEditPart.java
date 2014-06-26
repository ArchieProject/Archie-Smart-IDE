package archie.editor.parts;
//package archie.tim.editor.parts;
//
//import java.beans.PropertyChangeEvent;
//import java.util.List;
//
//import org.eclipse.draw2d.IFigure;
//import org.eclipse.draw2d.geometry.Rectangle;
//import org.eclipse.gef.*;
//import org.eclipse.gef.commands.Command;
//import org.eclipse.gef.editpolicies.OrderedLayoutEditPolicy;
//import org.eclipse.gef.requests.*;
//
//import archie.tim.editor.commands.*;
//import archie.tim.editor.figures.*;
//import archie.tim.model.*;
//import archie.tim.model.shapes.CodeElement;
//import archie.tim.model.shapes.ShapeWithCodeElements;
//
//public abstract class ShapeWithCodeElementsEditPart extends ShapeEditPart {
//	
//	// ============= MODEL ===============
//	@Override
//	public ShapeWithCodeElements getModel() {
//		return (ShapeWithCodeElements)super.getModel();
//	}
//	
//	@Override
//	protected List<?> getModelChildren() {
//		return getModel().getCodeElements();
//	}
//	
//	
//	// ============= VISUALS =============
//	@Override
//	public ShapeWithStereotypeAndCodeElementsFigure getFigure() {
//		return (ShapeWithStereotypeAndCodeElementsFigure)super.getFigure();
//	}
//	
//	@Override
//	public IFigure getContentPane() {
//		return getFigure().getCodeElementsContainer();
//	}
//	
//	@Override
//	protected void refreshVisuals() {
//		super.refreshVisuals();
//		getFigure().setMarked( getModel().isMarked() );
//		figure.repaint();
//	}
//	
//	// ============= EVENTS ==============
//	@Override
//	public void propertyChange(PropertyChangeEvent event) {
//		super.propertyChange(event);
//		
//		String property = event.getPropertyName();
//		if (property.equals(Shape.MARKED_PROP))
//			refreshVisuals();
//		else if (property.equals(ShapeWithCodeElements.CODE_ELEMENTS_ADDED_PROP)) {
//			addChild(createChild((CodeElement)event.getNewValue()), (int)event.getOldValue());
//			refreshVisuals();
//		}
//		else if (property.equals(ShapeWithCodeElements.CODE_ELEMENTS_REMOVED_PROP)) {
//			Object part = getViewer().getEditPartRegistry().get(event.getOldValue());
//			if (part instanceof EditPart)
//				removeChild((EditPart)part);
//			refreshVisuals();
//		}
//	}
//	
//	// ========== EDIT POLICIES ===========
//	@Override
//	protected void createEditPolicies() {
//		super.createEditPolicies();
//		
//		// mark & unmark commands
//		installEditPolicy("Mark and Unmark", new ShapeWithCodeElementsMarkEditPolicy());
//		
//		// code elements
//		installEditPolicy(EditPolicy.LAYOUT_ROLE, new OrderedLayoutEditPolicy() {
//			
//			/** Returns CreateCodeElementCommand that creates a CodeElement inside this Shape */
//			@Override
//			protected Command getCreateCommand(CreateRequest request) {
//				if (request.getNewObjectType() == CodeElement.class) {
//					CodeElement ce = (CodeElement)request.getNewObject();
//					return new AddCodeElementCommand(getModel(), ce);
//				}
//				return null;
//			}
//			
//			/**
//			 * Invoked when a CodeElement is dragged from one Shape to another.
//			 * Returns null when no changes are required.
//			 */
//			@Override
//			protected Command createAddCommand(EditPart child, EditPart after) {
//				if (!(child instanceof CodeElementEditPart))
//					return null;
//				ShapeWithCodeElements oldContainer = (ShapeWithCodeElements) child.getParent().getModel();
//				if (getModel() == oldContainer)	// if moving to the same Shape, return null
//					return null;
//				CodeElement ce = (CodeElement) child.getModel();
//				ReparentCodeElementCommand cmd = new ReparentCodeElementCommand(oldContainer, getModel(), ce);
//				if (after != null)
//					cmd.putAfter((CodeElement) after.getModel());
//				return cmd;
//			}
//			
//			/** Invoked when a CodeElements is dragged within the same Shape */
//			@Override
//			protected Command createMoveChildCommand(EditPart child, EditPart after) {
//				if (child == after || getChildren().size() == 1)
//					return null;
//				int index = getChildren().indexOf(child);
//				if (index == 0) {
//					if (after == null)
//						return null;
//				} else {
//					if (after == getChildren().get(index-1))
//						return null;
//				}
//				ReorderCodeElementCommand cmd = new ReorderCodeElementCommand(getModel(), (CodeElement)child.getModel());
//				if (after != null)
//					cmd.putAfter((CodeElement)after.getModel());
//				return cmd;
//			}
//			
//			/**Invoked when CodeElement is reparented or reordered
//			 * Returns the insertion point 
//			 */
//			@Override
//			protected EditPart getInsertionReference(Request request) {
//				int y = ((ChangeBoundsRequest)request).getLocation().y;
//				List<?> codeElements = getChildren();
//				CodeElementEditPart afterPart = null;
//				for(Object o : codeElements) {
//					CodeElementEditPart part = (CodeElementEditPart)o;
//					Rectangle bounds = part.getFigure().getBounds();
//					if (y < bounds.y)
//						return afterPart;
//					afterPart = part;
//				}
//				return afterPart;
//			}
//		});
//	}
//}
