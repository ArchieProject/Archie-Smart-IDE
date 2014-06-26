package archie.model.shapes;

import org.eclipse.draw2d.geometry.Rectangle;
import org.jdom2.Element;



public class Note extends Shape {
	
	private static final long serialVersionUID = 5412936019363223879L;

	public Note() {
		super("");
	}
	
	public Note(Element e) {
		super(e);
	}
	
	@Override
	protected String getXmlElementName() {
		return "note";
	}
	
	@Override
	public boolean setBounds(Rectangle bounds) {
		if(bounds.width < 60)
			bounds.width = 60;
		bounds.height = -1;
		return super.setBounds(bounds);
	}
}