package archie.wizards.newtim;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;

import archie.globals.ArchieSettings;
import archie.resourcesbinding.StaticResources;




public class TimTemplate implements Serializable {

	private static final long serialVersionUID = -5501873374888647650L;
	
	private String name;
	private String filename;
	private String imageFilename;
	private String description;
	
	public TimTemplate(String name, String filename, String imageFilename, String description) {
		setName(name);
		setFilename(filename);
		setImageFilename(imageFilename);
		setDescription(description);
	}
	
	public String getName() {
		return name;
	}
	
	private void setName(String name) {
		this.name = name;
	}
	
	public InputStream getInputStream() {
		try {
			return StaticResources.getFileContent(ArchieSettings.TIM_TEMPLATES_FOLDER + "/" + filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private void setFilename(String filename) {
		this.filename = filename;
	}
	
	public Image getImage(Device device) {
		InputStream is;
		try {
			//ImageData imageData = new ImageData(is).scaleTo(x,y);
			is = StaticResources.getFileContent(ArchieSettings.TIM_TEMPLATES_FOLDER + "/" + imageFilename);
			return new Image(device, is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private void setImageFilename(String name) {
		imageFilename = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	private void setDescription(String description) {
		this.description = description;
	}
}
