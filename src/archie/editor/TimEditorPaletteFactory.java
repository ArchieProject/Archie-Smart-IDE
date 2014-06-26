package archie.editor;

import org.eclipse.gef.palette.*;
import org.eclipse.gef.requests.*;
import org.eclipse.jface.resource.ImageDescriptor;

import archie.model.connections.*;
import archie.model.shapes.*;



public class TimEditorPaletteFactory {
	
	private TimEditorPaletteFactory() { /* utility class */ }
	
	public static PaletteRoot createPaletteRoot() {
		PaletteRoot palette = new PaletteRoot();
		palette.add(createToolsGroup(palette));
		palette.add(createShapesDrawer());
		//palette.add(createList());
		return palette;
	}
	
	private static PaletteContainer createToolsGroup(PaletteRoot palette) {
		PaletteToolbar toolbar = new PaletteToolbar("Tools");
		
		// add selection tool
		ToolEntry tool = new PanningSelectionToolEntry();
		toolbar.add(tool);
		palette.setDefaultEntry(tool);
		
		// Add mapping connection tool
		tool = new ConnectionCreationToolEntry(
				"Mapping", "", 
				new CreationFactory() {
					public Object getNewObject() {
						return null;
					}
					public Object getObjectType() {
						return Mapping.class;
					}
				}, 
				getImage("icons/Mapping16.png"),
				getImage("icons/Mapping24.png"));
		toolbar.add(tool);
		
		// Add dependency connection tool
		tool = new ConnectionCreationToolEntry(
				"Dependency",	"", 
				new CreationFactory() {
					public Object getNewObject() {
						return null;
					}
					@Override
					public Object getObjectType() {
						return Dependency.class;
					}
				}, 
				getImage("icons/Dependency16.png"),
				getImage("icons/Dependency24.png")
			);
		toolbar.add(tool);

		// Add aggregation connection tool
		tool = new ConnectionCreationToolEntry(
				"Aggregation",	"", 
				new CreationFactory() {
					public Object getNewObject() {
						return null;
					}
					@Override
					public Object getObjectType() {
						return Aggregation.class;
					}
				},
				getImage("icons/Aggregation16.png"),
				getImage("icons/Aggregation24.png")
			);
		toolbar.add(tool);

		// Add dashed connection tool
		tool = new ConnectionCreationToolEntry(
				"Dashed Connection",	"", 
				new CreationFactory() {
					public Object getNewObject() {
						return null;
					}
					@Override
					public Object getObjectType() {
						return DashedConnection.class;
					}
				}, 
				getImage("icons/DashedConnection16.png"),
				getImage("icons/DashedConnection24.png")
			);
		toolbar.add(tool);
		
		return toolbar;
	}
	
	private static ImageDescriptor getImage(String path) {
		return ImageDescriptor.createFromFile(TimEditor.class, path);
	}
	
	private static PaletteContainer createShapesDrawer() {
		PaletteDrawer componentsDrawer = new PaletteDrawer("Shapes");
		
		// attribute
		CombinedTemplateCreationEntry attribute = new CombinedTemplateCreationEntry("Attribute", "", Attribute.class, new SimpleFactory(Attribute.class), 
															getImage("icons/rectangle16.gif"), getImage("icons/Attribute24.png"));
		componentsDrawer.add(attribute);
		
		// component
		CombinedTemplateCreationEntry component = new CombinedTemplateCreationEntry("Component", "", Component.class, new SimpleFactory(Component.class), 
															getImage("icons/rectangle16.gif"), getImage("icons/Component24.png"));
		componentsDrawer.add(component);

		// goal
		CombinedTemplateCreationEntry goal = new CombinedTemplateCreationEntry("Goal", "", Goal.class, new SimpleFactory(Goal.class), 
															getImage("icons/rectangle16.gif"), getImage("icons/Goal24.png"));
		componentsDrawer.add(goal);
		
		// message
		CombinedTemplateCreationEntry message = new CombinedTemplateCreationEntry("Message", "", Message.class, new SimpleFactory(Message.class), 
															getImage("icons/rectangle16.gif"), getImage("icons/Message24.png"));
		componentsDrawer.add(message);
		
		// note
		CombinedTemplateCreationEntry note = new CombinedTemplateCreationEntry("Note", "", Note.class, new SimpleFactory(Note.class), 
															getImage("icons/rectangle16.gif"), getImage("icons/Note24.png"));
		componentsDrawer.add(note);
		
		// parameter
		CombinedTemplateCreationEntry parameter = new CombinedTemplateCreationEntry("Parameter", "", Parameter.class, new SimpleFactory(Parameter.class), 
															getImage("icons/rectangle16.gif"), getImage("icons/Parameter24.png"));
		componentsDrawer.add(parameter);
		
		// rationale
		CombinedTemplateCreationEntry rationale = new CombinedTemplateCreationEntry("Rationale", "", Rationale.class, new SimpleFactory(Rationale.class), 
															getImage("icons/rectangle16.gif"), getImage("icons/Rationale24.png"));
		componentsDrawer.add(rationale);
		
		// requirement
		CombinedTemplateCreationEntry requirement = new CombinedTemplateCreationEntry("Requirement", "", Requirement.class, new SimpleFactory(Requirement.class), 
															getImage("icons/rectangle16.gif"), getImage("icons/Requirement24.png"));
		componentsDrawer.add(requirement);
		
		// tactic
		CombinedTemplateCreationEntry tactic = new CombinedTemplateCreationEntry("Tactic", "", Tactic.class, new SimpleFactory(Tactic.class), 
															getImage("icons/rectangle16.gif"), getImage("icons/Tactic24.png"));
		componentsDrawer.add(tactic);
		
		return componentsDrawer;
	}
//	private static PaletteContainer createList() {
//		PaletteDrawer componentsDrawer = new PaletteDrawer("Source List");
//		
//		CombinedTemplateCreationEntry att = new CombinedTemplateCreationEntry("my Attribute", "", myAttribute.class, new SimpleFactory(myAttribute.class), getImage("icons/rectangle16.gif"), getImage("icons/Tactic24.png"));
//		componentsDrawer.add(att);
//		
//		return componentsDrawer;
//	}
}
