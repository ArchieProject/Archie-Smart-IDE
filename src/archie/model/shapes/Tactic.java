package archie.model.shapes;

import org.eclipse.draw2d.geometry.Rectangle;
import org.jdom2.Element;



public class Tactic extends Shape {
	
	private static final long serialVersionUID = 7119471148729019136L;
	private static final int MIN_WIDTH = 80;
	
	public Tactic() {}
	
	public Tactic(Element e) {
		super(e);
	}
	
	@Override
	protected String getXmlElementName() {
		return "tactic";
	}
	
	@Override
	public boolean setBounds(Rectangle bounds) {
		if(bounds.width < MIN_WIDTH)
			bounds.width = MIN_WIDTH;
		bounds.height = -1;
		return super.setBounds(bounds);
	}
}
