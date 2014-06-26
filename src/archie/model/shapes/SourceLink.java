package archie.model.shapes;

import org.eclipse.draw2d.geometry.Rectangle;
import org.jdom2.Element;



public class SourceLink extends Shape
{

	private static final long serialVersionUID = -666920746185862085L;

	public SourceLink() {}

	public SourceLink(Element e) {
		super(e);
	}

	@Override
	protected String getXmlElementName() {
		return "sourcelink";
	}

	@Override
	public boolean setBounds(Rectangle bounds) {
		if(bounds.width < 80)
			bounds.width = 80;
		bounds.height = -1;
		return super.setBounds(bounds);
	}

}
