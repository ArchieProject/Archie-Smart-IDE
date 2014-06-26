
package archie.editor;

import java.util.EventObject;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.ui.*;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.*;
import org.eclipse.gef.*;
import org.eclipse.gef.dnd.*;
import org.eclipse.jface.action.IAction;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.requests.*;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.palette.*;
import org.eclipse.gef.ui.parts.*;

import archie.editor.commands.AddCodeElementAction;
import archie.editor.commands.EditCodeElementAction;
import archie.editor.commands.MarkCodeElementAction;
import archie.editor.commands.UnmarkCodeElementAction;
import archie.editor.commands.UnmarkShapeAction;
import archie.editor.parts.TimEditPartFactory;
import archie.model.Tim;
import archie.model.TimPersister;
import archie.timstorage.TimsManager;

public class TimEditor extends GraphicalEditorWithFlyoutPalette
{
	private Tim tim;

	/**
	 * 
	 */
	public TimEditor()
	{
		setEditDomain(new DefaultEditDomain(this));
	}

	/**
	 * 
	 * @return
	 */
	public Tim getTim()
	{
		return tim;
	}

	@Override
	protected void setInput(IEditorInput input)
	{
		super.setInput(input);
		IFile file = ((IFileEditorInput) input).getFile();
		setPartName(file.getName());

		//TimsManager.getInstance().reload(file);
		tim = TimsManager.getInstance().findTimFor(file);
		
		/**
		 * Added by Ahmed Fakhry to get rid of the null pointer exception that
		 * we used to get upon opening eclipse while the TimEditor was opened
		 * from before
		 */
		if (tim != null)
		{
			tim.setEditor(this);
		}
	}

	/**
	 * 
	 * @param monitor
	 */
	@Override
	public void doSave(IProgressMonitor monitor)
	{
		try
		{
			IFile file = ((IFileEditorInput) getEditorInput()).getFile();
			TimPersister.save(tim, file);
			tim.setAssociatedFile(file);
			getCommandStack().markSaveLocation();
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public boolean isSaveAsAllowed()
	{
		return true; // if false, doSaveAs() is never called
	}

	/**
	 * 
	 */
	@Override
	public void doSaveAs()
	{
		SaveAsDialog dialog = new SaveAsDialog(getSite().getShell());
		dialog.setOriginalFile(((IFileEditorInput) getEditorInput()).getFile());
		dialog.open();

		IPath path = dialog.getResult();
		if (path == null) // user clicked cancel
			return;

		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		super.setInput(new FileEditorInput(file));
		doSave(null);
		setPartName(file.getName());
	}

	@Override
	protected void configureGraphicalViewer()
	{
		super.configureGraphicalViewer();
		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setEditPartFactory(new TimEditPartFactory());
		viewer.setRootEditPart(new ScalableFreeformRootEditPart());
		viewer.setKeyHandler(new GraphicalViewerKeyHandler(viewer));

		ContextMenuProvider cmProvider = new TimEditorContextMenuProvider(viewer,
				getActionRegistry());
		viewer.setContextMenu(cmProvider);
		getSite().registerContextMenu(cmProvider, viewer);
	}

	// called by gef framework after configureGraphicalViewer()
	@Override
	protected void initializeGraphicalViewer()
	{
		super.initializeGraphicalViewer();

		GraphicalViewer viewer = getGraphicalViewer();
		
		viewer.setContents(tim);

		viewer.addDropTargetListener(new TemplateTransferDropTargetListener(viewer)
		{
			protected CreationFactory getFactory(Object template)
			{
				return new SimpleFactory((Class<?>) template);
			}
		});

		// viewer.addDropTargetListener( (TransferDropTargetListener) new
		// TextTransferDropTargetListener( getGraphicalViewer(),
		// TextTransfer.getInstance()));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void createActions()
	{
		super.createActions();

		ActionRegistry registry = getActionRegistry();
		IAction action;

		action = new UnmarkShapeAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new MarkCodeElementAction(this, tim);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new UnmarkCodeElementAction(this, tim);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new AddCodeElementAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new EditCodeElementAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());
	}

	/**
	 * 
	 * @param event
	 */
	@Override
	public void commandStackChanged(EventObject event)
	{
		firePropertyChange(IEditorPart.PROP_DIRTY);
		super.commandStackChanged(event);
	}

	private static PaletteRoot PALETTE_ROOT;

	@Override
	protected PaletteRoot getPaletteRoot()
	{
		if (PALETTE_ROOT == null)
			PALETTE_ROOT = TimEditorPaletteFactory.createPaletteRoot();
		return PALETTE_ROOT;
	}

	@Override
	protected PaletteViewerProvider createPaletteViewerProvider()
	{
		return new PaletteViewerProvider(getEditDomain())
		{
			@Override
			protected void configurePaletteViewer(PaletteViewer viewer)
			{
				super.configurePaletteViewer(viewer);

				viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer));
			}
		};
	}
}
