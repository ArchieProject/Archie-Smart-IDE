package archie.editor;

import org.eclipse.gef.*;
import org.eclipse.gef.ui.actions.*;
import org.eclipse.jface.action.*;
import org.eclipse.ui.actions.ActionFactory;

import archie.editor.commands.*;


public class TimEditorContextMenuProvider extends ContextMenuProvider {
	private ActionRegistry actionRegistry;
	
	public TimEditorContextMenuProvider(EditPartViewer viewer, ActionRegistry registry) {
		super(viewer);
		actionRegistry = registry;
	}

	@Override
	public void buildContextMenu(IMenuManager manager) {
		GEFActionConstants.addStandardActionGroups(manager);
		
		IAction action;
		
		action = actionRegistry.getAction(ActionFactory.UNDO.getId());
		manager.appendToGroup(GEFActionConstants.GROUP_UNDO, action);
		
		action = actionRegistry.getAction(ActionFactory.REDO.getId());
		manager.appendToGroup(GEFActionConstants.GROUP_UNDO, action);
		
		action = actionRegistry.getAction(AddCodeElementAction.ADD_CODE_ELEMENT);
		if (action.isEnabled())
			manager.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
		
		action = actionRegistry.getAction(EditCodeElementAction.EDIT_CODE_ELEMENT);
		if (action.isEnabled())
			manager.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
		
		action = actionRegistry.getAction(UnmarkShapeAction.ACCEPT_CHANGES);
		if (action.isEnabled())
			manager.appendToGroup(GEFActionConstants.GROUP_REST, action);
		
		action = actionRegistry.getAction(MarkCodeElementAction.MARK);
		if (action.isEnabled())
			manager.appendToGroup(GEFActionConstants.GROUP_REST, action);
		
		action = actionRegistry.getAction(UnmarkCodeElementAction.UNMARK);
		if (action.isEnabled())
			manager.appendToGroup(GEFActionConstants.GROUP_REST, action);
		
		action = actionRegistry.getAction(ActionFactory.DELETE.getId());
		if (action.isEnabled())
			manager.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
	}

}
