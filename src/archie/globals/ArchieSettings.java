/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.globals;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;

import archie.views.autodetect.internals.IndicatorTermsReader;

/*******************************************************
 * Defines a class that contains most of the constant settings for this plug-in
 *******************************************************/
public final class ArchieSettings
{
	/*******************************************************
	 * Static Fields
	 *******************************************************/

	public static final String TEXT_MARKER_TYPE = "archie.TermsTextMarker";
	public static final String TERM_ANNOTATION_TYPE = "archie.indicatorTermAnnotation";
	public static final String TIM_MARKER_TYPE = "archie.LinkedToTimWarning";
	public static final String CODE_ELEMENT_MARKER_ATTRIB = "CodeElement";
	public static final String FILE_ITEM_MARKER_ATTRIB = "TreeFileItem";
	public static final String RESOURCES_FOLDER = "resources";
	public static final String TIM_TEMPLATES_FOLDER = RESOURCES_FOLDER + "/tim_templates";
	public static boolean MONITORING = false;

	/**
	 * The singleton instance.
	 */
	private static final ArchieSettings INSTANCE = new ArchieSettings();

	/*******************************************************
	 * Member Fields
	 *******************************************************/
	private String mStopWordsPath;
	private String mDatabaseFile;
	private String mMonitoringFile;
	private String mUserTemplatesFolder;

	/*******************************************************
	 * Gets the singleton instance.
	 * 
	 * @return The singleton instance.
	 *******************************************************/
	public static ArchieSettings getInstance()
	{
		return INSTANCE;
	}

	/*******************************************************
	 * Non-instantiable singleton class
	 *******************************************************/
	private ArchieSettings()
	{
	}

	/*******************************************************
	 * Initializes the plug-in settings, by reading the indicator terms of the
	 * engine, and getting the stop words path.
	 *******************************************************/
	public void initialize()
	{
		// Read the indicator terms file
		try
		{
			// Locate the file
			URL path = FileLocator.resolve(this.getClass().getResource("/resources/engine/IndicatorTerms.csv"));
			String indicatorTermsPath = path.getFile();
			IndicatorTermsReader.readIndicatorTerms(indicatorTermsPath);
		}
		catch (IOException e)
		{
			System.err.println("Indicator terms file read error.");
			e.printStackTrace();
		}

		// Get the stop words path
		try
		{
			URL path = FileLocator.resolve(this.getClass().getResource(
					"/resources/engine/stopwords_w_java_cpp_keywords.txt"));
			mStopWordsPath = path.getFile();
		}
		catch (IOException e)
		{
			System.err.println("Stop words file read error.");
			e.printStackTrace();
		}

		// Get the database file path
		try
		{
			URL path = FileLocator.resolve(this.getClass().getResource("/resources/engine/AcceptedList.dat"));
			mDatabaseFile = path.getFile();
		}
		catch (IOException e)
		{
			System.err.println("Database file read error.");
			e.printStackTrace();
		}

		// Get the monitoring file path
		try
		{
			URL path = FileLocator.resolve(this.getClass().getResource("/resources/engine/MonitoringList.dat"));
			mMonitoringFile = path.getFile();
		}
		catch (IOException e)
		{
			System.err.println("Monitoring file read error.");
			e.printStackTrace();
		}

		// Get the user templates path
		try
		{
			URL path = FileLocator.resolve(this.getClass().getResource("/" + TIM_TEMPLATES_FOLDER + "/user/"));
			mUserTemplatesFolder = path.getFile();
		}
		catch (IOException e)
		{
			System.err.println("User templates path error.");
			e.printStackTrace();
		}
	}

	/*******************************************************
	 * Gets the path of the stop words file.
	 * 
	 * @return The path of the stop words file as a string.
	 *******************************************************/
	public String getStopWordsFilePath()
	{
		return mStopWordsPath;
	}

	/*******************************************************
	 * Gets the path of the engine's database file.
	 * 
	 * @return The path of the engine's database file.
	 *******************************************************/
	public String getDatabaseFilePath()
	{
		return mDatabaseFile;
	}

	/*******************************************************
	 * Gets the path of the engine's monitoring list file.
	 * 
	 * @return The path of the engine's monitoring list file.
	 *******************************************************/
	public String getMonitoringListFilePath()
	{
		return mMonitoringFile;
	}

	/*******************************************************
	 * Gets the path of the folder where the user-saved TIMs templates are
	 * saved.
	 * 
	 * @return The path of the folder where the user templates are saved.
	 *******************************************************/
	public String getUserTemplatesFolderPath()
	{
		return mUserTemplatesFolder;
	}
}
