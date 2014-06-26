package archie.editor.figures.connections;

import org.eclipse.draw2d.*;

public abstract class ConnectionFigure extends PolylineConnection {
	private Label label;
	
	public ConnectionFigure() {
		label = new Label();
		label.setOpaque(true);
		label.setBorder(new MarginBorder(0,4,0,4));
		ConnectionLocator locator = new ConnectionLocator(this, ConnectionLocator.MIDDLE);
		add(label, locator);
	}
	
	public String getText() {
		return label.getText();
	}
	
	public void setText(String text) {
		if(text == null)
			text = "";
		if(text.equals(""))
			label.setVisible(false);
		else
			label.setVisible(true);
		label.setText(text);
	}
	
	public Label getLabel() {
		return label;
	}
}
