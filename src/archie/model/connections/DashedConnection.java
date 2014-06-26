package archie.model.connections;

import org.jdom2.Element;

import archie.model.Tim;
import archie.model.shapes.Shape;


public class DashedConnection extends Connection {

	private static final long serialVersionUID = 8952135943209249957L;

	public DashedConnection(Shape source, Shape target) {
		super(source, target);
	}
	
	public DashedConnection(Tim parent, Element e) {
		super(parent, e);
	}

	@Override
	public String getXmlElementName() {
		return "dashedconnection";
	}

}
