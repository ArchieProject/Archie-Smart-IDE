
package archie.model;

import java.util.*;

import org.eclipse.core.resources.IFile;
import org.jdom2.Element;

import archie.editor.TimEditor;
import archie.model.connections.Connection;
import archie.model.connections.ConnectionFactory;
import archie.model.shapes.Shape;
import archie.model.shapes.ShapeFactory;
import archie.utils.PropertyChangeSupport;

/**
 * A Traceability Information Model. Each model has an unique identifier, is
 * optionally opened in an editor and is associated with an IFile. The model is
 * composed of Shapes and Connections and exposes several events in a form of a
 * PropertyChangeSupport.
 * 
 * @author Mateusz Wieloch
 */
public class Tim extends PropertyChangeSupport
{
	public static final String CHILDREN_PROP = "Tim.Children";
	public static final String DIRTY_PROP = "Tim.Dirty";

	private UUID id;
	private boolean dirty = false;
	private TimEditor editor;
	private IFile file;
	private List<Shape> children = new ArrayList<>();
	private int biggestIdAmongChildren = 1;

	/**
	 * Constructs an instance of the class with a randomly generated id.
	 */
	public Tim()
	{
		setId(UUID.randomUUID());
	}

	public Tim(Element e)
	{
		if (e.getName() != "diagram")
			System.out.println("no root <diagram> element found");

		String id = e.getAttributeValue("id");
		if (id == null || id.isEmpty())
			setId(UUID.randomUUID());
		else
			setId(UUID.fromString(id));

		Element shapes = e.getChild("shapes");
		if (shapes != null)
		{
			for (Element xmlElement : shapes.getChildren())
			{
				Shape s = ShapeFactory.getInstance().build(xmlElement);
				addChild(s);
				s.setParent(this);
			}
		}

		Element connections = e.getChild("connections");
		for (Element xmlElement : connections.getChildren())
			ConnectionFactory.getInstance().build(this, xmlElement);
	}

	public Element toXml()
	{
		Element diagram = new Element("diagram");
		diagram.setAttribute("id", getId().toString());

		Element elements = new Element("shapes");
		for (Shape s : getChildren())
			elements.addContent(s.toXml());
		diagram.addContent(elements);

		org.jdom2.Element connections = new org.jdom2.Element("connections");
		for (Shape s : getChildren())
			for (Connection c : s.getSourceConnections())
				connections.addContent(c.toXml());
		diagram.addContent(connections);
		return diagram;
	}

	/**
	 * Returns true if the provided Object is Tim and has the same unique id.
	 * 
	 * @param other
	 *            the reference object with which to compare.
	 * @return true if the provided Object is Time and has the same unique id.
	 */
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof Tim))
			return false;
		Tim otherTim = (Tim) other;
		UUID otherTimId = otherTim.getId();
		return id.equals(otherTimId);
	}

	/**
	 * Returns a String representation of the object
	 * 
	 * @return a String representation of the object
	 */
	@Override
	public String toString()
	{
		String idString = getId().toString();
		String dirtyString = isDirty() ? "Dirty" : "Not Dirty";
		String editorString = isOpenInEditor() ? "Opened In Editor" : "Not Opened In Any Editor";
		String fileString = "Associated with " + getAssociatedFile().getName();
		String childrenString = "contains " + children.size() + " children";
		return "[Id: " + idString + "; " + dirtyString + "; " + editorString + "; " + fileString + "; "
				+ childrenString + "]";
	}

	/**
	 * Returns an unique id of the Tim
	 * 
	 * @return an unique id of the Tim
	 */
	public UUID getId()
	{
		return id; // can safely return, UUID is immutable
	}

	private void setId(UUID id)
	{
		this.id = id;
	}

	/**
	 * Returns true if the Tim object has been modified and contains unpersisted
	 * changes.
	 * 
	 * @return true if the Tim object has been modified and contains unpersisted
	 *         changes; false otherwise.
	 */
	public boolean isDirty()
	{
		return dirty;
	}

	/**
	 * Sets a value of the Dirty property. If the value is different from the
	 * old one, property change is fired.
	 * 
	 * @param newValue
	 *            a new value for the Dirty property.
	 */
	public void setDirty(boolean newValue)
	{
		if (dirty != newValue)
		{
			dirty = newValue;
			firePropertyChange(DIRTY_PROP, !newValue, newValue);
		}
	}

	/**
	 * Returns true if the Tim is open in any editor.
	 * 
	 * @return true if the Tim is open in any editor.
	 */
	public boolean isOpenInEditor()
	{
		return getEditor() != null;
	}

	/**
	 * 
	 * @return
	 */
	public TimEditor getEditor()
	{
		return editor;
	}

	/**
	 * 
	 * @param editor
	 */
	public void setEditor(TimEditor editor)
	{
		this.editor = editor;
	}

	/**
	 * 
	 * @return
	 */
	public IFile getAssociatedFile()
	{
		return file;
	}

	/**
	 * 
	 * @param file
	 */
	public void setAssociatedFile(IFile file)
	{
		this.file = file;
	}

	/**
	 * Returns a List containing all direct children of the Tim
	 * 
	 * @return a List containing all direct children of the Tim
	 */
	public List<Shape> getChildren()
	{
		return new ArrayList<>(children);
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Shape getChildById(int id)
	{
		for (Shape s : children)
			if (s.getId() == id)
				return s;
		throw new IllegalStateException("No Shape for a given id exists in the Tim");
	}

	/**
	 * 
	 * @param s
	 * @return
	 */
	public boolean addChild(Shape s)
	{
		if (s != null && children.add(s))
		{
			int id = s.getId();

			if (id <= 0)
			{
				s.setId(++biggestIdAmongChildren);
			}
			else
			{
				biggestIdAmongChildren = Math.max(biggestIdAmongChildren, id);
			}
			
			firePropertyChange(CHILDREN_PROP, null, s);
			s.setParent(this);
			setDirty(true);
			
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * 
	 * @param s
	 * @return
	 */
	public boolean removeChild(Shape s)
	{
		if (s != null && children.remove(s))
		{
			firePropertyChange(CHILDREN_PROP, s, null);
			setDirty(true);
			return true;
		}
		return false;
	}
}
