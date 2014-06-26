
package archie.editor.parts;

import org.eclipse.gef.*;

/*
 * Edit parts are being initialized by convention. For each <modelClass> class, instance of <modelClass>EditPart class will be created.
 */
public class TimEditPartFactory implements EditPartFactory
{

	@Override
	public EditPart createEditPart(EditPart context, Object model)
	{

		/**
		 * Added by Ahmed Fakhry to get rid of the null
		 * pointer exception we used to get upon opening eclipse while
		 * the TimEditor was opened from a previous run.
		 */
		if (model == null)
		{
			return null;
		}

		EditPart editPart = null;

		try
		{
			String modelClass = model.getClass().getSimpleName();
			String editPartClass = TimEditPart.class.getPackage().getName() + "." + modelClass
					+ "EditPart";
			editPart = (EditPart) Class.forName(editPartClass).newInstance();
			editPart.setModel(model);
		}
		catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		return editPart;
	}
}
