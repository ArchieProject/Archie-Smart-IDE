package archie.editor.dnd;

import org.eclipse.gef.*;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.swt.dnd.*;



public class TextTransferDropTargetListener extends AbstractTransferDropTargetListener {

	public TextTransferDropTargetListener(EditPartViewer viewer, Transfer xfer) {
		super(viewer, xfer);
	}

	protected Request createTargetRequest() {
		return new NativeDropRequest();
	}

	protected NativeDropRequest getNativeDropRequest() {
		return (NativeDropRequest) getTargetRequest();
	}

	protected void updateTargetRequest() {
		getNativeDropRequest().setData(getCurrentEvent().data);
	}
}