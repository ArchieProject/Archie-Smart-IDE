package archie.model.shapes;

import org.eclipse.draw2d.geometry.Rectangle;
import org.jdom2.Element;




public class Message extends Shape {
	
	private static final long serialVersionUID = 6774052095514881099L;

	public Message() {}
	
	public Message(Element e) {
		super(e);
	}
	
	@Override
	protected String getXmlElementName() {
		return "message";
	}
	
	@Override
	public boolean setBounds(Rectangle bounds) {
		if(bounds.width < 80)
			bounds.width = 80;
		bounds.height = -1;
		return super.setBounds(bounds);
	}
}
