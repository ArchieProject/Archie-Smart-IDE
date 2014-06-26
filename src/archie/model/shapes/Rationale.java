package archie.model.shapes;

import org.eclipse.draw2d.geometry.Rectangle;
import org.jdom2.Element;


public class Rationale extends Shape {

	private static final long serialVersionUID = 2466791562205316464L;
	private static final int MIN_WIDTH = 80;
	
	public Rationale() {}
	
	public Rationale(Element e) {
		super(e);
	}
	
	@Override
	protected String getXmlElementName() {
		return "rationale";
	}

	@Override
	public boolean setBounds(Rectangle bounds) {
		if(bounds.width < MIN_WIDTH)
			bounds.width = MIN_WIDTH;
		bounds.height = -1;
		return super.setBounds(bounds);
	}
}
