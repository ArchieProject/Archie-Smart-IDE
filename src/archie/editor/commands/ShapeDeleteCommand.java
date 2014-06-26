package archie.editor.commands;

import java.util.List;

import org.eclipse.gef.commands.Command;

import archie.model.*;
import archie.model.connections.Connection;
import archie.model.shapes.Shape;



public class ShapeDeleteCommand extends Command {
	private final Shape shape;
	private final Tim parent;
	private List<Connection> sourceConnections;
	private List<Connection> targetConnections;
	private boolean wasRemoved;
	
	public ShapeDeleteCommand(Tim parent, Shape shape) {
		if(parent == null || shape == null)
			throw new IllegalArgumentException();
		this.parent = parent;
		this.shape = shape;
		setLabel("Delete Shape");
	}
	
	@Override
	public void execute() {
		sourceConnections = shape.getSourceConnections();
		targetConnections = shape.getTargetConnections();
		redo();
	}
	
	@Override
	public void redo() {
		wasRemoved = parent.removeChild(shape);
		if(wasRemoved) {
			// remove connections
			while (!sourceConnections.isEmpty())
				sourceConnections.get(0).disconnect();
			while (!targetConnections.isEmpty())
				targetConnections.get(0).disconnect();
			//for(Connection c : sourceConnections)
			//	c.disconnect();
//			for(Connection c : targetConnections)
//				c.disconnect();
		}
	}
	
	@Override
	public boolean canUndo() {
		return wasRemoved;
	}
	
	@Override
	public void undo() {
		if(parent.addChild(shape)) {
			for(Connection c : sourceConnections)
				c.reconnect();
			for(Connection c : targetConnections)
				c.reconnect();
			}
	}
}
