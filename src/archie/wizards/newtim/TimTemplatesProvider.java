
package archie.wizards.newtim;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import archie.globals.ArchieSettings;

public class TimTemplatesProvider
{
	private static volatile TimTemplatesProvider instance;

	private Vector<TimTemplate> mSystemTemplates = new Vector<TimTemplate>();
	private Vector<TimTemplate> mUserTemplates;

	private TimTemplatesProvider()
	{
		initialize();
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

	public void addUserTemplate(String name, String description)
	{
		mUserTemplates.add(new TimTemplate(name, "/user/" + name + ".tim", "userTemp.png", description));
	}

	public Vector<TimTemplate> getSystemTemplates()
	{
		return mSystemTemplates;
	}
	
	public Vector<TimTemplate> getUserTemplates()
	{
		return mUserTemplates;
	}

	public void saveToDB()
	{
		try
		{
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File(ArchieSettings.getInstance()
					.getUserTemplatesDBFile())));

			// Serialize the user templates list
			os.writeObject(mUserTemplates);

			os.close();
		}
		catch (IOException e)
		{
			System.err.println("Failed to save the user templates list to the database file!");
			e.printStackTrace();
		}
	}

	private void initialize()
	{
		loadSystemTemplates();
		loadUserTemplates();
	}

	private void loadSystemTemplates()
	{
		// The empty template.
		mSystemTemplates.add(new TimTemplate("Empty", "empty.tim", "empty.png",
				"An empty Traceability Information Model. Suitable when you want to do everything yourself."));

		// The heartbeat template.
		mSystemTemplates
				.add(new TimTemplate(
						"Heartbeat",
						"heartbeat.tim",
						"heartbeat.png",
						"A heartbeat Traceability Information Model. This tactic describes a subsystem with periodic message exchange between the system monitor and certain process."));

		// The SEDA (Stage Event-Driven Architecture) template
		mSystemTemplates
				.add(new TimTemplate("SEDA", "SEDA.tim", "SEDA.png", "Stage Event-Driven Architecture pattern."));

		// The DDR (Data Distribution And Replication) template.
		mSystemTemplates
				.add(new TimTemplate("DDR", "DDR.tim", "DDR.png", "Data Distribution And Replication pattern."));

		// The EDA (Event-Driven Architecture Pattern) template.
		mSystemTemplates.add(new TimTemplate("EDA", "EDA.tim", "EDA.png", "Event-Driven Architecture pattern."));
	}

	@SuppressWarnings("unchecked")
	private void loadUserTemplates()
	{
		// Deserialize from the DB file.
		try
		{
			ObjectInputStream is = new ObjectInputStream(new FileInputStream(new File(ArchieSettings.getInstance()
					.getUserTemplatesDBFile())));

			Object userTemps = is.readObject();
			boolean failed = false;

			if (userTemps instanceof Vector<?>)
			{
				mUserTemplates = (Vector<TimTemplate>) userTemps;
			}
			else
			{
				failed = true;
			}

			is.close();

			if (failed)
			{
				throw new IOException();
			}
		}
		catch (IOException | ClassNotFoundException e)
		{
			System.err.println("Failed to read the user templates list from database file!");
			e.printStackTrace();

			// Create a new user templates list
			mUserTemplates = new Vector<TimTemplate>();
		}
	}
}
