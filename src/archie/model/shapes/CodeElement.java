
package archie.model.shapes;

import java.io.File;
import java.io.Serializable;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jdom2.Element;

import archie.utils.PropertyChangeSupport;

/**
 * 
 * @author Mateusz Wieloch
 */
public class CodeElement extends PropertyChangeSupport implements Serializable
{

	private static final long serialVersionUID = 3234257603965728526L;

	public static final String JAVA_ELEMENT_TYPE_PROP = "CodeElement.JavaElementType";
	public static final String QUALIFIED_NAME_PROP = "CodeElement.QualifiedName";
	public static final String HASH_CODE_PROP = "CodeElement.HashCode";
	public static final String MARKED_PROP = "CodeElement.Marked";

	private int javaElementType;
	private String qualifiedName;
	private int hash;
	private boolean marked;
	// private ArrayList<String> sourcePath = new ArrayList<String>();
	private String sourcePath;
	// private ISourceRange sourceRange;

	private Shape mParentShape;

	/**
	 * 
	 * @param element
	 *            IType or IMethod
	 */
	public CodeElement(IMember element)
	{
		setJavaElementType(element.getElementType());
		// sourcePath.add(element.getCompilationUnit().getPath().toOSString());
		try
		{
			IResource res = element.getCompilationUnit().getCorrespondingResource();
			sourcePath = new File(res.getLocationURI()).getAbsolutePath();

			if (element.getElementType() == IJavaElement.TYPE)
			{
				// sourceRange = element.getSourceRange();
				IType type = (IType) element;
				setQualifiedName(type.getFullyQualifiedName());
				setHash(type.getSource().hashCode());
			}
			else if (element.getElementType() == IJavaElement.METHOD)
			{
				// sourceRange = element.getSourceRange();
				IMethod method = (IMethod) element;
				IType parent = method.getDeclaringType();
				setQualifiedName(parent.getFullyQualifiedName() + "." + method.getElementName());
				setHash(method.getSource().hashCode());
			}
		}
		catch (JavaModelException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param e
	 */
	public CodeElement(Element e)
	{
		int type = Integer.valueOf(e.getAttributeValue("type"));
		boolean marked = Boolean.valueOf(e.getAttributeValue("marked"));
		int hash = Integer.valueOf(e.getAttributeValue("hash"));
		String sourcePath = String.valueOf(e.getAttributeValue("Path"));
		String qualifiedName = e.getValue();

		setJavaElementType(type);
		setMarked(marked);
		setHash(hash);
		setQualifiedName(qualifiedName);

		// String temp[] = sourcePath.split(", ");
		// ArrayList<String> sources = new ArrayList<String>();
		// Collections.addAll(sources, temp);
		setSourcePath(sourcePath);
	}

	/**
	 * Allows for ICompilationUnit to be attached to as the code element
	 * 
	 * @param ICompilationUnit
	 */
	public CodeElement(ICompilationUnit unit)
	{
		setJavaElementType(ICompilationUnit.TYPE);
		// sourcePath.add(unit.getPath().toOSString());
		IResource res;
		try {
			res = unit.getCorrespondingResource();
			sourcePath = new File(res.getLocationURI()).getAbsolutePath();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 


		setQualifiedName(unit.getElementName());

	}

	public void setParentShape(Shape shape)
	{
		mParentShape = shape;
	}

	public Shape getParentShape()
	{
		return mParentShape;
	}

	// return the path of the java file associated with this node
	public String getAssociatedPath()
	{
		return sourcePath;
	}

	// public ISourceRange getRange()
	// {
	// return sourceRange;
	// }

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		CodeElement other = (CodeElement) obj;

		if (javaElementType == other.javaElementType && qualifiedName.equals(other.qualifiedName)
				&& sourcePath.equals(other.sourcePath))
			return true;
		else
			return false;

	}

	/**
	 * 
	 * @return
	 */
	public Element toXml()
	{
		Element xml = new Element(getClass().getSimpleName());
		xml.setAttribute("type", String.valueOf(getJavaElementType()));
		xml.setAttribute("marked", String.valueOf(marked));
		xml.setAttribute("hash", String.valueOf(hash));
		xml.setAttribute("Path", sourcePath.toString());
		xml.addContent(getQualifiedName());
		return xml;
	}

	/**
	 * 
	 * @return IJavaElement.TYPE or IJavaElement.METHOD
	 */
	public int getJavaElementType()
	{
		return javaElementType;
	}

	/**
	 * IJavaElement.TYPE or IJavaElement.METHOD
	 * 
	 * @param newElementType
	 *            IJavaElement.TYPE or IJavaElement.METHOD
	 */
	public void setJavaElementType(int newElementType)
	{
		if (newElementType != IJavaElement.TYPE && newElementType != IJavaElement.METHOD) // &&
																							// newElementType
																							// !=
																							// 5)//5
																							// so
																							// it
																							// recognizes
																							// the
																							// ICompilationUnit
																							// unit.getElementType()
																							// unit
																							// type,
																							// and
																							// is
																							// different
																							// from
																							// IJavaElementType.type;
			throw new IllegalArgumentException("Provided IJavaElement is not an instance of an IType or IMethod");

		if (javaElementType != newElementType)
		{
			javaElementType = newElementType;
			firePropertyChange(JAVA_ELEMENT_TYPE_PROP, null, newElementType);
		}
	}

	/**
	 * eg.: edu.depaul.SampleClass.SampleMethod
	 * 
	 * @return
	 */
	public String getQualifiedName()
	{
		return qualifiedName;
	}

	/**
	 * 
	 * @param newQualifiedName
	 *            eg.: edu.depaul.SampleClass.SampleMethod
	 * @return
	 */
	public void setQualifiedName(String newQualifiedName)
	{
		if (newQualifiedName == null)
			throw new IllegalArgumentException();

		if (!newQualifiedName.equals(qualifiedName))
		{
			qualifiedName = newQualifiedName;
			firePropertyChange(QUALIFIED_NAME_PROP, null, newQualifiedName);
		}
	}

	/**
	 * when the code element is read in, the source path will be set
	 * 
	 * @param newSourcepath
	 * @return
	 */
	public void setSourcePath(String newSourcePath)
	{
		if (newSourcePath == null)
			throw new IllegalArgumentException();

		sourcePath = newSourcePath;
		firePropertyChange(QUALIFIED_NAME_PROP, null, newSourcePath);
	}

	/**
	 * 
	 * @return
	 */
	public int getHash()
	{
		return hash;
	}

	/**
	 * 
	 * @param newHash
	 */
	public void setHash(int newHash)
	{
		if (hash != newHash)
		{
			hash = newHash;
			firePropertyChange(HASH_CODE_PROP, null, newHash);
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean isMarked()
	{
		return marked;
	}

	/**
	 * 
	 * @param newMarked
	 */
	public void setMarked(boolean newMarked)
	{
		if (marked != newMarked)
		{
			marked = newMarked;
			firePropertyChange(MARKED_PROP, null, newMarked);
		}
	}

	/**
	 * 
	 * @param element
	 * @return
	 */
	public boolean isAssociatedWith(IMember element)
	{
		if (element.getElementType() == IJavaElement.TYPE)
		{
			IType type = (IType) element;
			return type.getFullyQualifiedName().equals(getQualifiedName());
		}
		else if (element.getElementType() == IJavaElement.METHOD)
		{
			IMethod method = (IMethod) element;
			String fqNameOfMethod = method.getDeclaringType().getFullyQualifiedName() + "." + method.getElementName();
			return fqNameOfMethod.equals(getQualifiedName());
		}
		else
		{
			return false;
		}
	}

	/**
	 * 
	 * @return
	 */
	public String getDisplayName()
	{
		String qualifiedName = getQualifiedName();
		if (getJavaElementType() == IJavaElement.TYPE)
		{
			if (qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1).equals("java"))
				return qualifiedName;
			return qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
		}
		// else if(getJavaElementType() == 5)//5 so it recognizes the
		// ICompilationUnit unit.getElementType() unit type, and takes a
		// different action from IJavaElementType.type;
		// {
		// return qualifiedName;
		// }
		else
		{
			int numOfDots = 0;
			int i = qualifiedName.length();
			while (i >= 0 && numOfDots < 2)
				if (qualifiedName.charAt(--i) == '.')
					++numOfDots;
			return qualifiedName.substring(i + 1);
		}
	}
}
