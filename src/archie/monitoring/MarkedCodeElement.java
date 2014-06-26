/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.monitoring;

import java.util.List;

import archie.model.Tim;
import archie.model.shapes.CodeElement;
import archie.model.shapes.Shape;
import archie.timstorage.TimsManager;

/*******************************************************
 * Defines a marked code element in a TIM file for which warnings have been
 * generated.
 *******************************************************/
final class MarkedCodeElement implements IMarkedItem
{
	/*******************************************************
	 * Serializabale ID
	 *******************************************************/
	private static final long serialVersionUID = 322715877181700093L;

	/*******************************************************
	 * I had to revert to a string file path after I used to store the TIM and
	 * the CodeElement themselves. They're not serializable unfortunately as
	 * they contain fields that are of types implemented by eclipse like the
	 * editor part and that I have no control over to make it serializable it
	 * would have been much cleaner and faster that way.
	 *******************************************************/
	private final String mFullFilePath;

	/*******************************************************
	 * Constructs a marked element for a TIM code element.
	 * 
	 * @param fullFilePath
	 *            The full file path associated with this code element.
	 *******************************************************/
	public MarkedCodeElement(String fullFilePath)
	{
		if (fullFilePath == null)
		{
			throw new IllegalArgumentException();
		}

		mFullFilePath = fullFilePath;
	}

	/*******************************************************
	 * 
	 * @see archie.monitoring.IMarkedItem#unmark()
	 *******************************************************/
	@Override
	public void unmark()
	{
		// Test if the file is added to one of the TIMs
		List<Tim> Tims = TimsManager.getInstance().getAll();
		for (final Tim tim : Tims)
		{
			List<Shape> shapes = tim.getChildren();
			for (Shape shape : shapes)
			{
				List<CodeElement> codeElements = shape.getCodeElements();
				for (final CodeElement ce : codeElements)
				{
					String cePath = ce.getAssociatedPath();

					if (cePath.equals(mFullFilePath))
					{
						// This code element is linked to one of the
						// Shapes in this TIM

						// --- unmark and clear warning
						if (ce.isMarked())
						{
							ce.setMarked(false);
							ce.getParentShape().setDirty();

							WarningsUtils.deleteWarningFromCodeElement(ce, tim);
						}
					}
				}
			}
		}
	}

	/*******************************************************
	 * 
	 * @see archie.monitoring.IMarkedItem#mark()
	 *******************************************************/
	@Override
	public void mark()
	{
		// Test if the file is added to one of the TIMs
		List<Tim> Tims = TimsManager.getInstance().getAll();
		for (final Tim tim : Tims)
		{
			List<Shape> shapes = tim.getChildren();
			for (Shape shape : shapes)
			{
				List<CodeElement> codeElements = shape.getCodeElements();
				for (final CodeElement ce : codeElements)
				{
					String cePath = ce.getAssociatedPath();

					if (cePath.equals(mFullFilePath))
					{
						// This code element is linked to one of the
						// Shapes in this TIM

						// --- Mark and add to the list
						if (!ce.isMarked())
						{
							ce.setMarked(true);
							ce.getParentShape().setDirty();

							WarningsUtils.addWarningToCodeElement(ce, tim);
						}

					}
				}
			}
		}
	}

}
