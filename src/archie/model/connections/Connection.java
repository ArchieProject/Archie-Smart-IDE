package archie.model.connections;

import java.io.Serializable;

import org.jdom2.Element;

import archie.model.Tim;
import archie.model.shapes.Shape;
import archie.utils.PropertyChangeSupport;




public abstract class Connection extends PropertyChangeSupport implements Serializable {

	private static final long serialVersionUID = 530811606676826915L;

	public static final String TEXT_PROP = "Connection.Text";
	
	private boolean isConnected;
	private Shape source;
	private Shape target;
	private String text = "";
	
	public Connection(Shape source, Shape target) {
		connect(source, target);
	}
	
	public Connection(Tim model, Element e) {
		int from = Integer.valueOf(e.getAttributeValue("from"));
		int to = Integer.valueOf(e.getAttributeValue("to"));
		Shape source = model.getChildById(from);
		Shape target = model.getChildById(to);
		connect(source, target);
		String text = e.getAttributeValue("text");
		setText(text);
	}
	
	public Element toXml() {
		Element xml = new Element(getXmlElementName());
		xml.setAttribute("from", String.valueOf(source.getId()));
		xml.setAttribute("to", String.valueOf(target.getId()));
		xml.setAttribute("text", String.valueOf(getText()));
		return xml;
	}
	
	public abstract String getXmlElementName();
	
	public void connect(Shape source, Shape target) {
		if (source == null || target == null || source == target)
			throw new IllegalArgumentException();
		disconnect();
		this.source = source;
		this.target = target;
		reconnect();
	}
	
	public void reconnect() {
		if(!isConnected) {
			source.addConnection(this);
			target.addConnection(this);
			isConnected = true;
		}
	}
	
	public void disconnect() {
		if(isConnected) {
			source.removeConnection(this);
			target.removeConnection(this);
			isConnected = false;
		}
	}
	
	public Shape getSource() {
		return source;
	}
	
	public Shape getTarget() {
		return target;
	}
	
	public String getText() {
		return text;
	}
	
	public boolean setText(String text) {
		if(text == null)
			text = "";
		if(this.text.equals(text) )
			return false;
		
		this.text = text;
		firePropertyChange(TEXT_PROP, null, text);
		return true;
	}
}
