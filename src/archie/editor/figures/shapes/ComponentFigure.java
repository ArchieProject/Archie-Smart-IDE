package archie.editor.figures.shapes;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.Rectangle;



public class ComponentFigure extends ShapeFigure {
	Label stereotypeLabel;
	Label nameLabel;
	IFigure codeElementsContainer;
	
	public ComponentFigure() {
		//look
		setBackgroundColor(ColorConstants.orange);
		setOpaque(true);
		
		//layout
		ToolbarLayout layout = new ToolbarLayout();
		layout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
		layout.setStretchMinorAxis(true);
		layout.setSpacing(2);
		setLayoutManager(layout);
		
		//stereotype
		add(new Label("�component�"));
		
		//name
		nameLabel = new Label("");
		add(nameLabel);
		
		// code
		codeElementsContainer = new CodeElementsContainerFigure();
		add(codeElementsContainer);
	}
	
	@Override
	public IFigure getCodeElementsContainer() {
		return codeElementsContainer;
	}
	
	@Override
	public void setMarked(boolean marked) {
		if (marked)
			setBorder(new LineBorder(ColorConstants.red, 2));
		else
			setBorder(new LineBorder(ColorConstants.black, 1));
	}
	
	@Override
	public ConnectionAnchor getConnectionAnchor() {
		return new ChopboxAnchor(this);
	}
	
	@Override
	public Rectangle getClientAreaOfName() {
		return nameLabel.getClientArea();
	}

	@Override
	public String getName() {
		return nameLabel.getText();
	}

	@Override
	public void setName(String text) {
		nameLabel.setText(text);
	}
}
