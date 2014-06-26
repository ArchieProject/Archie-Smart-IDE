package archie.model.shapes;

import org.eclipse.draw2d.geometry.Rectangle;
import org.jdom2.Element;


public class Component extends Shape {

	private static final long serialVersionUID = -315389488101302847L;
	private static int MIN_WIDTH = 100;
	
	public Component() {}
	
	public Component(Element e) {
		super(e);
	}
	
	@Override
	public String getXmlElementName() {
		return "component";
	}
	
	@Override
	public boolean setBounds(Rectangle bounds) {
		if(bounds.width < MIN_WIDTH)
			bounds.width = MIN_WIDTH;
		bounds.height = -1;
		return super.setBounds(bounds);
	}
}
