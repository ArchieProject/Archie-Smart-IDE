package archie.model.connections;

import org.jdom2.Element;

import archie.model.Tim;
import archie.model.shapes.Shape;


public class Dependency extends Connection {

	private static final long serialVersionUID = -4963608690619591130L;

	public Dependency(Shape source, Shape target) {
		super(source, target);
	}
	
	public Dependency(Tim parent, Element e) {
		super(parent, e);
	}

	@Override
	public String getXmlElementName() {
		return "dependency";
	}
}
