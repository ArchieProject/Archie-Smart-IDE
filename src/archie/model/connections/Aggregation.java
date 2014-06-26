package archie.model.connections;

import org.jdom2.Element;

import archie.model.Tim;
import archie.model.shapes.Shape;


public class Aggregation extends Connection {

	private static final long serialVersionUID = -8115140702400063620L;

	public Aggregation(Shape source, Shape target) {
		super(source, target);
	}
	
	public Aggregation(Tim parent, Element e) {
		super(parent, e);
	}

	@Override
	public String getXmlElementName() {
		return "aggregation";
	}

}
