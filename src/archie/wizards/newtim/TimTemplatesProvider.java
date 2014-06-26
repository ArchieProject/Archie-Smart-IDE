package archie.wizards.newtim;

import java.util.Vector;

public class TimTemplatesProvider {
	private static volatile TimTemplatesProvider instance;
	
	private TimTemplatesProvider() {}
	
	public static TimTemplatesProvider getInstance() {
		if (instance == null) {
			synchronized (TimTemplatesProvider.class) {
				if (instance == null)
					instance = new TimTemplatesProvider();
			}
		}
		return instance;
	}
	
	public Vector<TimTemplate> getTemplates() {
		Vector<TimTemplate> result = new Vector<>();
		result.add( new TimTemplate("Empty", "empty.tim", "empty.png", "An empty Traceability Information Model. Suitable when you want to do everything yourself.") );
		result.add( new TimTemplate("Heartbeat", "heartbeat.tim", "heartbeat.png", "A heartbeat Traceability Information Model. This tactic describes a subsystem with periodic message exchange between the system monitor and certain process.") );
		return result;
	}
}
