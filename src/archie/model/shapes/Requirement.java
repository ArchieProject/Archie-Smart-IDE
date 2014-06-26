package archie.model.shapes;

import org.eclipse.draw2d.geometry.Rectangle;
import org.jdom2.Element;


public class Requirement extends Shape {

	private static final long serialVersionUID = 3190364420602264863L;
	private static final int MIN_WIDTH = 90;

	public Requirement() {}
	
	public Requirement(Element e) {
		super(e);
	}
	
	@Override
	public String getXmlElementName() {
		return "requirement";
	}
	
	@Override
	public boolean setBounds(Rectangle bounds) {
		if(bounds.width < MIN_WIDTH)
			bounds.width = MIN_WIDTH;
		bounds.height = -1;
		return super.setBounds(bounds);
	}
}
