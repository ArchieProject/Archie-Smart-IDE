package archie.editor;

import org.eclipse.gef.ui.actions.*;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.actions.*;

public class TimEditorActionBarContributor extends ActionBarContributor {

	public TimEditorActionBarContributor() { }
	
	@Override
	protected void buildActions() {
		addRetargetAction(new DeleteRetargetAction());
		addRetargetAction(new UndoRetargetAction());
		addRetargetAction(new RedoRetargetAction());
		addRetargetAction(new LabelRetargetAction(ActionFactory.SELECT_ALL.getId(), "Select All"));
	}
	
	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		toolBarManager.add(getAction(ActionFactory.UNDO.getId()));
		toolBarManager.add(getAction(ActionFactory.REDO.getId()));
	}

	@Override
	protected void declareGlobalActionKeys() { }

}
