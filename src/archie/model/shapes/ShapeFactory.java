package archie.model.shapes;

import org.jdom2.Element;

/**
 * 
 * @author Mateusz Wieloch
 */
public class ShapeFactory {
	private static volatile ShapeFactory instance;
	
	private ShapeFactory() { }
	
	/**
	 * 
	 * @return
	 */
	public static ShapeFactory getInstance() {
		if (instance == null) {
			synchronized (ShapeFactory.class) {
				if (instance == null) {
					instance = new ShapeFactory();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 
	 * @param e
	 * @return
	 */
	public Shape build(Element e) {
		switch (e.getName().toLowerCase()) {
			case "attribute":
				return new Attribute(e);
			case "component":
				return new Component(e);
			case "goal":
				return new Goal(e);
			case "message":
				return new Message(e);
			case "note":
				return new Note(e);
			case "parameter":
				return new Parameter(e);
			case "rationale":
				return new Rationale(e);
			case "requirement":
				return new Requirement(e);
			case "tactic":
				return new Tactic(e);
			case "sourcelink":
				return new SourceLink(e);
			default:
				throw new IllegalArgumentException("<" + e.getName() + "> is not a recognized name of a Shape");
		}
	}
}
