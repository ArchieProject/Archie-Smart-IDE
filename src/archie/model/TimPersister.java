
package archie.model;

import java.io.*;
import java.util.UUID;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.*;

public class TimPersister
{
	/*******************************************************
	 * Static utility class
	 *******************************************************/
	private TimPersister()
	{
		
	}

	public static UUID getTimIdFromFile(IFile file)
	{
		try (InputStream in = file.getContents())
		{
			SAXBuilder builder = new SAXBuilder();
			Document document = builder.build(in);
			String id = document.getRootElement().getAttributeValue("id");
			return UUID.fromString(id);
		}
		catch (IOException | CoreException | JDOMException ex)
		{
			return null;
		}
	}

	public static void save(Tim tim, IFile file) throws IOException
	{
		try
		{
			try (ByteArrayOutputStream out = new ByteArrayOutputStream())
			{
				Document document = new Document(tim.toXml());
				XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
				xout.output(document, out);
				try (ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray()))
				{
					if (file.exists())
						file.setContents(in, true, true, null);
					else
						file.create(in, true, null);
				}
			}
		}
		catch (IOException | CoreException ex)
		{
			throw new IOException(ex);
		}
	}

	public static Tim loadFrom(IFile file) throws IOException
	{
		if (file == null)
			throw new IllegalArgumentException("Provided IFile is null");
		if (!file.exists())
			throw new IllegalArgumentException("Provided IFile doesn't exist on a harddrive");
		if (!"tim".equalsIgnoreCase(file.getFileExtension()))
			throw new IllegalArgumentException(
					"Provided IFile extension is incorrect. Only \"*.tim\" files are expected");
		try
		{
			file.refreshLocal(IResource.DEPTH_ZERO, null); // prevents
															// "resource out of sync"
															// exception
		}
		catch (CoreException ex)
		{
			throw new IOException("Refreshing an IFile failed for unknown reasons", ex);
		}

		try (InputStream in = file.getContents())
		{
			SAXBuilder builder = new SAXBuilder();
			Document document = builder.build(in);
			Element rootElement = document.getRootElement();
			Tim tim = new Tim(rootElement);
			tim.setAssociatedFile(file);
			return tim;
		}
		catch (CoreException | IOException | JDOMException ex)
		{
			throw new IOException(ex);
		}
	}
}
