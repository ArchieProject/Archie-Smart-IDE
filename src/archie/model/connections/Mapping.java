package archie.model.connections;

import org.jdom2.Element;

import archie.model.Tim;
import archie.model.shapes.Shape;


public class Mapping extends Connection {

	private static final long serialVersionUID = -8308193838580534964L;

	public Mapping(Shape source, Shape target) {
		super(source, target);
	}
	
	public Mapping(Tim parent, Element e) {
		super(parent, e);
	}

	@Override
	public String getXmlElementName() {
		return "mapping";
	}

}
