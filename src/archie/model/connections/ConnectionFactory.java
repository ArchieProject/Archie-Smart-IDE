package archie.model.connections;

import org.jdom2.Element;

import archie.model.Tim;



public class ConnectionFactory {
	private static volatile ConnectionFactory instance;
	
	private ConnectionFactory() { }
	
	public static ConnectionFactory getInstance() {
		if (instance == null) {
			synchronized (ConnectionFactory.class) {
				if (instance == null) {
					instance = new ConnectionFactory();
				}
			}
		}
		return instance;
	}
	
	public Connection build(Tim model, Element e) {
		switch (e.getName().toLowerCase()) {
			case "aggregation":
				return new Aggregation(model, e);
			case "dashedconnection":
				return new DashedConnection(model, e);
			case "dependency":
				return new Dependency(model, e);
			case "mapping":
				return new Mapping(model, e);
			default:
				throw new IllegalArgumentException("<" + e.getName() + "> is not a recognized name of a Connection");
		}
	}
}
