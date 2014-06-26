package archie.utils;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.graphics.Image;

import archie.Activator;


public class ImageHelper {
	protected static Image createImage(String name) {
		InputStream stream = Activator.class.getResourceAsStream(name);
		Image image = new Image(null, stream);
		try {
			stream.close();
		} catch (IOException ioe) {
		}
		return image;
	}
}
