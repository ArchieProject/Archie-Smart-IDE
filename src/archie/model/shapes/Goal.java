package archie.model.shapes;

import org.eclipse.draw2d.geometry.*;
import org.jdom2.Element;


public class Goal extends Shape {
	
	private static final long serialVersionUID = 6717564546594152085L;
	private static final int MIN_WIDTH = 90;
	
	public Goal() {}
	
	public Goal(Element e) {
		super(e);
	}
	
	@Override
	protected String getXmlElementName() {
		return "goal";
	}
	
	@Override
	public boolean setBounds(Rectangle bounds) {
		if(bounds.width < MIN_WIDTH)
			bounds.width = MIN_WIDTH;
		bounds.height = -1;
		return super.setBounds(bounds);
	}
}
