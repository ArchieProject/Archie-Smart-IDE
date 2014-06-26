package archie.gui;

import java.util.Collection;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import archie.editor.commands.MarkCodeElementCommand;
import archie.model.*;
import archie.model.shapes.CodeElement;
import archie.model.shapes.Shape;
import archie.timstorage.TimsManager;



/**
 * 
 */
public class JavaElementsMarker {
	
	/**
	 * 
	 * @param member
	 */
	public static void mark(IMember member) {
		try {
			switch(member.getElementType()) {
				case IJavaElement.TYPE:
					mark((IType)member);
					break;
				case IJavaElement.METHOD:
					mark((IMethod)member);
					break;
			}
		} catch (JavaModelException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
	private static void mark(IType type) throws JavaModelException {
		Collection<Tim> allTimsInSolution = TimsManager.getInstance().getAll();
		
		for (Tim tim : allTimsInSolution)
			for (Shape shape : tim.getChildren())
				for(CodeElement ce : shape.getCodeElementsAssociatedWith(type)) {
					int hash = type.getSource().hashCode();
					if (ce.getHash() != hash) {
						ce.setHash(hash);
						markCodeElement(ce, tim);
					}
				}
	}
	
	private static void mark(IMethod method) throws JavaModelException {
		Collection<Tim> allTimsInSolution = TimsManager.getInstance().getAll();
		for (Tim tim : allTimsInSolution)
			for (Shape shape : tim.getChildren())
				for(CodeElement ce : shape.getCodeElementsAssociatedWith(method)) {
					int hash = method.getSource().hashCode();
					if (ce.getHash() != hash) {
						ce.setHash(hash);
						markCodeElement(ce, tim);
					}
				}
	}
	
	private static void markCodeElement(CodeElement ce, Tim owner) {
		if (owner.isOpenInEditor()) {
			MarkCodeElementCommand cmd = new MarkCodeElementCommand();
			cmd.setShape(ce);
			CommandStack commandStack = (CommandStack) owner.getEditor().getAdapter(CommandStack.class);
			commandStack.execute(cmd);
		}
		else {
			ce.setMarked(true);
		}
	}
}
