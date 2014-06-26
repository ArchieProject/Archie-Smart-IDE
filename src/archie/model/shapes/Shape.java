
package archie.model.shapes;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.*;

import org.eclipse.draw2d.geometry.*;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.jdom2.Element;

import archie.model.Tim;
import archie.model.connections.Connection;
import archie.utils.PropertyChangeSupport;

public abstract class Shape extends PropertyChangeSupport implements PropertyChangeListener, Serializable
{
	private static final long serialVersionUID = 235810232568427290L;

	public static final String BOUNDS_PROP = "Shape.Bounds";
	public static final String TEXT_PROP = "Shape.Text";
	public static final String SOURCE_CONNECTIONS_PROP = "Shape.SourceConnections";
	public static final String TARGET_CONNECTIONS_PROP = "Shape.TargetConnections";
	public static final String CODE_ELEMENTS_PROP = "Shape.CodeElements";
	public static final String MARKED_PROP = "Shape.Marked";

	private int id;
	private Tim parent;

	private Rectangle bounds = new Rectangle(0, 0, -1, -1);
	private String text = "";
	private List<Connection> sourceConnections = new ArrayList<>();
	private List<Connection> targetConnections = new ArrayList<>();
	protected List<CodeElement> codeElements = new ArrayList<>();
	protected boolean marked = false;

	public Shape()
	{
		this("<noname>");
	}

	public Shape(String text)
	{
		setText(text);
	}

	public Shape(int id, Rectangle bounds, String text)
	{
		setId(id);
		setBounds(bounds);
		setText(text);
	}

	public Shape(Element e)
	{
		int id = Integer.valueOf(e.getAttributeValue("id"));
		int x = Integer.valueOf(e.getAttributeValue("x"));
		int y = Integer.valueOf(e.getAttributeValue("y"));
		int width = Integer.valueOf(e.getAttributeValue("width"));
		int height = Integer.valueOf(e.getAttributeValue("height"));
		Rectangle bounds = new Rectangle(x, y, width, height);
		String text = e.getAttributeValue("text");

		setId(id);
		setBounds(bounds);
		setText(text);

		for (Element code : e.getChildren())
		{
			CodeElement ce = new CodeElement(code);
			addCodeElement(ce);
		}
	}

	public Element toXml()
	{
		Element xml = new Element(getXmlElementName());

		int id = getId();
		Rectangle bounds = getBounds();
		String text = getText();

		xml.setAttribute("id", String.valueOf(id));
		xml.setAttribute("x", String.valueOf(bounds.x));
		xml.setAttribute("y", String.valueOf(bounds.y));
		xml.setAttribute("width", String.valueOf(bounds.width));
		xml.setAttribute("height", String.valueOf(bounds.height));
		xml.setAttribute("text", text);

		for (CodeElement e : codeElements)
			xml.addContent(e.toXml());

		return xml;
	}

	protected abstract String getXmlElementName();

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public Tim getParent()
	{
		return parent;
	}

	public void setParent(Tim parent)
	{
		this.parent = parent;
	}

	public Rectangle getBounds()
	{
		return bounds;
	}

	public boolean setBounds(Rectangle bounds)
	{
		if (bounds == null)
			throw new IllegalArgumentException();
		if (this.bounds.equals(bounds))
			return false;

		this.bounds = bounds;
		firePropertyChange(BOUNDS_PROP, null, bounds);
		setDirty();
		return true;
	}

	public String getText()
	{
		return text;
	}

	public boolean setText(String text)
	{
		if (text == null)
			text = "";
		if (this.text == text)
			return false;
		this.text = text;

		firePropertyChange(TEXT_PROP, null, this.text);
		setDirty();
		return true;
	}

	public List<Connection> getSourceConnections()
	{
		return sourceConnections;
	}

	public List<Connection> getTargetConnections()
	{
		return targetConnections;
	}

	public boolean addConnection(Connection connection)
	{
		if (connection == null || connection.getSource() == connection.getTarget())
			throw new IllegalArgumentException();

		if (connection.getSource() == this)
		{
			sourceConnections.add(connection);
			firePropertyChange(SOURCE_CONNECTIONS_PROP, null, connection);
			return true;
		}
		else if (connection.getTarget() == this)
		{
			targetConnections.add(connection);
			firePropertyChange(TARGET_CONNECTIONS_PROP, null, connection);
			return true;
		}
		return false;
	}

	public boolean removeConnection(Connection connection)
	{
		if (connection == null)
			throw new IllegalArgumentException();

		if (connection.getSource() == this)
		{
			sourceConnections.remove(connection);
			firePropertyChange(SOURCE_CONNECTIONS_PROP, connection, null);
			return true;
		}
		else if (connection.getTarget() == this)
		{
			targetConnections.remove(connection);
			firePropertyChange(TARGET_CONNECTIONS_PROP, connection, null);
			return true;
		}
		return false;
	}

	public boolean hasCodeElements()
	{
		return !codeElements.isEmpty();
	}

	public List<CodeElement> getCodeElements()
	{
		return new ArrayList<>(codeElements);
	}

	public List<CodeElement> getCodeElementsAssociatedWith(IType type)
	{
		List<CodeElement> result = new ArrayList<>();
		for (CodeElement ce : codeElements)
			if (ce.isAssociatedWith(type))
				result.add(ce);
		return result;
	}

	public List<CodeElement> getCodeElementsAssociatedWith(IMethod method)
	{
		List<CodeElement> result = new ArrayList<>();
		for (CodeElement ce : codeElements)
			if (ce.isAssociatedWith(method))
				result.add(ce);
		return result;
	}

	public boolean addCodeElement(CodeElement ce)
	{
		return addCodeElement(codeElements.size(), ce);
	}

	public boolean addCodeElement(int index, CodeElement ce)
	{
		if (ce == null || codeElements.contains(ce))
			return false;

		if (ce.isMarked())
			setMarked(true);

		ce.setParentShape(this);
		
		ce.addPropertyChangeListener(this);
		codeElements.add(index, ce);
		firePropertyChange(CODE_ELEMENTS_PROP, index, ce);
		return true;
	}

	public boolean removeCodeElement(CodeElement ce)
	{
		if (ce == null || !codeElements.remove(ce))
			return false;

		setMarked(false);
		for (CodeElement e : codeElements)
			if (e.isMarked())
				setMarked(true);

		ce.setParentShape(null);
		
		ce.removePropertyChangeListener(this);
		firePropertyChange(CODE_ELEMENTS_PROP, ce, null);
		return true;
	}

	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		String property = event.getPropertyName();
		if (property.equals(CodeElement.MARKED_PROP))
		{
			setMarked(false);
			for (CodeElement e : codeElements)
				if (e.isMarked())
					setMarked(true);
		}
	}

	public boolean isMarked()
	{
		return marked;
	}

	private boolean setMarked(boolean newMarked)
	{
		if (marked == newMarked)
			return false;
		marked = newMarked;
		firePropertyChange(MARKED_PROP, null, newMarked);
		return true;
	}

	public void setDirty()
	{
		if (parent != null)
			parent.setDirty(true);
	}
}
