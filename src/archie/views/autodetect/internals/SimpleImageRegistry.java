/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.views.autodetect.internals;

import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

/*******************************************************
 * Defines a simplified image registry to wrap and abstracts the internal JFace
 * image registry stuff.
 *******************************************************/
public final class SimpleImageRegistry
{
	private final ImageRegistry mImageRegistery;

	/*******************************************************
	 * This constructor creates a simplified image registry that
	 * deals with all the internal setup and loading and storing images.
	 * 
	 * @param parent
	 *            The parent control
	 *******************************************************/
	public SimpleImageRegistry(Composite parent)
	{
		if(parent == null)
			throw new IllegalArgumentException();
		
		// Get the default JFace resource manager
		ResourceManager rm = JFaceResources.getResources();
		// Create a local resource manager
		LocalResourceManager lrm = new LocalResourceManager(rm, parent);
		// Create the internal image registry
		mImageRegistery = new ImageRegistry(lrm);
	}

	/*******************************************************
	 * Registers an image into the registry by providing its resources path.
	 * 
	 * @param imageID
	 *            The ID you want to give to this image to refer to it later.
	 * 
	 * @param imagePath
	 *            The image path from the plug-in resources. Example:
	 *            "/icons/sample.png".
	 *******************************************************/
	public void registerImagePath(String imageID, String imagePath)
	{
		URL imageURL = this.getClass().getResource(imagePath);
		mImageRegistery.put(imageID, ImageDescriptor.createFromURL(imageURL));
	}

	/*******************************************************
	 * Returns the image associated with the given ID in this registry, or null
	 * if none
	 * 
	 * @param imageID
	 *            The image ID used to refer to the desired image.
	 * @return The Image associated with the given ID or null if not found.
	 *******************************************************/
	public Image getImage(String imageID)
	{
		return mImageRegistery.get(imageID);
	}
}
