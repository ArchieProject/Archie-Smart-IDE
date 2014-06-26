package archie.editor.figures.shapes;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.Rectangle;




public abstract class ShapeFigure extends Figure {
//	Label stereotypeLabel;
//	Label nameLabel;
//	IFigure codeElementsContainer;
	
	public ShapeFigure() {
//		//look
//		setBackgroundColor(color);
//		setOpaque(true);
//		
//		//layout
//		ToolbarLayout layout = new ToolbarLayout();
//		layout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
//		layout.setStretchMinorAxis(true);
//		layout.setSpacing(2);
//		setLayoutManager(layout);
//		
//		//stereotype
//		if(stereotype != null && !stereotype.isEmpty()) {
//			stereotypeLabel = new Label(stereotype);
//			add(stereotypeLabel);
//		}
//		
//		//name
//		nameLabel = new Label("");
//		add(nameLabel);
//		
//		// code
//		codeElementsContainer = new CodeElementsContainerFigure();
//		add(codeElementsContainer);
	}
	
	public IFigure getCodeElementsContainer() { return null; }
//	{
//		return codeElementsContainer;
//	}
	
	public void setMarked(boolean marked) { }
//	{
//		if (marked)
//			setBorder(new LineBorder(ColorConstants.red, 2));
//		else
//			setBorder(new LineBorder(ColorConstants.black, 1));
//	}
	
	public abstract ConnectionAnchor getConnectionAnchor();
//	{
//		return new ChopboxAnchor(this);
//	}
	
	public abstract Rectangle getClientAreaOfName();
//	{
//		return nameLabel.getClientArea();
//	}

	public abstract String getName();
//	{
//		return nameLabel.getText();
//	}

	public abstract void setName(String text);
//	{
//		nameLabel.setText(text);
//	}
	

}

