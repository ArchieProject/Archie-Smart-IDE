package archie.model;

import org.jdom2.Element;


public class TimFactory {
	private static volatile TimFactory instance;
	
	private TimFactory() { }
	
	/**
	 * 
	 * @return
	 */
	public static TimFactory getInstance() {
		if (instance == null) {
			synchronized (TimFactory.class) {
				if (instance == null) {
					instance = new TimFactory();
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
	public Tim build(Element e) {
		return new Tim(e);
	}
}
