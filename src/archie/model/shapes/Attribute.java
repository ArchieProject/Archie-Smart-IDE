package archie.model.shapes;

import org.eclipse.draw2d.geometry.Rectangle;
import org.jdom2.Element;


public class Attribute extends Shape {
	
	private static final long serialVersionUID = -1622127229115624790L;

	public Attribute() {}
	
	public Attribute(Element e) {
		super(e);
	}
	
	@Override
	protected String getXmlElementName() {
		return "attribute";
	}
	
	@Override
	public boolean setBounds(Rectangle bounds) {
		if(bounds.width < 80)
			bounds.width = 80;
		bounds.height = -1;
		return super.setBounds(bounds);
	}
}
