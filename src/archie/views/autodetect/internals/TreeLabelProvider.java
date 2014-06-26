/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.views.autodetect.internals;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import archie.views.autodetect.internals.SimpleImageRegistry;

/*******************************************************
 * Defines a class that will provide labels for the results tree view
 *******************************************************/
public final class TreeLabelProvider extends LabelProvider implements IStyledLabelProvider, IFontProvider,
		IColorProvider
{
	private final SimpleImageRegistry mIr;
	private final FontRegistry mFr;

	/*******************************************************
	 * Constructs a label provider for the results tree view in UI
	 * 
	 * @param ir
	 *            The {@link SimpleImageRegistry} to be used for locating images
	 * @param fr
	 *            The {@link FontRegistry} to be used by this label provider
	 *******************************************************/
	public TreeLabelProvider(SimpleImageRegistry ir, FontRegistry fr)
	{
		mIr = ir;
		mFr = fr;
	}

	/*******************************************************
	 * 
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 *******************************************************/
	@Override
	public String getText(Object element)
	{
		return element.toString();
	}

	/*******************************************************
	 * 
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 *******************************************************/
	@Override
	public Image getImage(Object element)
	{
		if (element instanceof TreeQualityItem)
		{
			return mIr.getImage("timIcon");
		}
		else if (element instanceof TreeFileItem)
		{
			TreeFileItem fItem = (TreeFileItem) element;

			if (fItem.isMarked())
			{
				return mIr.getImage("fileWarningIcon");
			}
			else
			{
				return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
			}
		}
		else
		{
			return super.getImage(element);
		}
	}

	/*******************************************************
	 * 
	 * @see org.eclipse.jface.viewers.IFontProvider#getFont(java.lang.Object)
	 *******************************************************/
	@Override
	public Font getFont(Object element)
	{
		Font result;

		if (element instanceof TreeQualityItem)
		{
			// Bold font for the quality types
			result = mFr.getBold(JFaceResources.DEFAULT_FONT);
		}
		else
		{
			// Normal font for the files
			result = mFr.get(JFaceResources.DEFAULT_FONT);

			if (((TreeFileItem) element).isMarked())
			{

			}
		}

		return result;
	}

	/*******************************************************
	 * 
	 * @see org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider#getStyledText(java.lang.Object)
	 *******************************************************/
	@Override
	public StyledString getStyledText(Object element)
	{
		StyledString ss = new StyledString(this.getText(element));

		if (element instanceof TreeFileItem)
		{
			// Add the quality name to the file name as a styled text
			ss.append(" (" + ((TreeFileItem) element).getQualityName() + ")", StyledString.DECORATIONS_STYLER);
		}

		return ss;
	}

	/*******************************************************
	 * 
	 * @see org.eclipse.jface.viewers.IColorProvider#getForeground(java.lang.Object)
	 *******************************************************/
	@Override
	public Color getForeground(Object element)
	{
		// Default color is black
		Color result = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);

		if (element instanceof TreeFileItem)
		{
			if (((TreeFileItem) element).isMarked())
			{
				result = Display.getDefault().getSystemColor(SWT.COLOR_RED);
			}
		}

		return result;
	}

	/*******************************************************
	 * 
	 * @see org.eclipse.jface.viewers.IColorProvider#getBackground(java.lang.Object)
	 *******************************************************/
	@Override
	public Color getBackground(Object element)
	{
		return Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
	}

}
