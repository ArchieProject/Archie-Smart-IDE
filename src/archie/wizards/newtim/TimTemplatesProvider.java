
package archie.wizards.newtim;

import java.util.Vector;

public class TimTemplatesProvider
{
	private static volatile TimTemplatesProvider instance;

	private TimTemplatesProvider()
	{
	}

	public static TimTemplatesProvider getInstance()
	{
		if (instance == null)
		{
			synchronized (TimTemplatesProvider.class)
			{
				if (instance == null)
					instance = new TimTemplatesProvider();
			}
		}
		return instance;
	}

	public Vector<TimTemplate> getTemplates()
	{
		Vector<TimTemplate> result = new Vector<>();
		
		// The empty template.
		result.add(new TimTemplate("Empty", "empty.tim", "empty.png",
				"An empty Traceability Information Model. Suitable when you want to do everything yourself."));
		
		// The heartbeat template.
		result.add(new TimTemplate(
				"Heartbeat",
				"heartbeat.tim",
				"heartbeat.png",
				"A heartbeat Traceability Information Model. This tactic describes a subsystem with periodic message exchange between the system monitor and certain process."));
		
		// The SEDA (Stage Event-Driven Architecture) template
		result.add(new TimTemplate("SEDA", "SEDA.tim", "SEDA.png", "Stage Event-Driven Architecture pattern."));
		
		// The DDR (Data Distribution And Replication) template.
		result.add(new TimTemplate("DDR", "DDR.tim", "DDR.png", "Data Distribution And Replication pattern."));
		
		// The EDA (Event-Driven Architecture Pattern) template.
		result.add(new TimTemplate("EDA", "EDA.tim", "EDA.png", "Event-Driven Architecture pattern."));
		
		return result;
	}
}
