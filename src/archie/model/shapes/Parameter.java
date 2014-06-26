package archie.model.shapes;

import org.eclipse.draw2d.geometry.Rectangle;
import org.jdom2.Element;



public class Parameter extends Shape {

	private static final long serialVersionUID = -2030529278166316286L;

	public Parameter() {}
	
	public Parameter(Element e) {
		super(e);
	}
	
	@Override
	protected String getXmlElementName() {
		return "parameter";
	}
	
	@Override
	public boolean setBounds(Rectangle bounds) {
		if(bounds.width < 80)
			bounds.width = 80;
		bounds.height = -1;
		return super.setBounds(bounds);
	}
}
