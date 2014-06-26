package archie.resourcesbinding;

import java.io.IOException;
import java.io.InputStream;

import archie.Activator;



/**
 * 
 * @author Mateusz Wieloch
 */
public class StaticResources {
	
	/**
	 * Don't forget to close the stream!
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static InputStream getFileContent(String path) throws IOException {
		return Activator.getInstance().getBundle().getEntry(path).openStream();
	}
}
