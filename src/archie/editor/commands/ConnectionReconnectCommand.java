package archie.editor.commands;

import org.eclipse.gef.commands.Command;

import archie.model.connections.Connection;
import archie.model.shapes.Shape;



public class ConnectionReconnectCommand extends Command {
	private Connection connection;
	private Shape oldSource;
	private Shape oldTarget;
	private Shape newSource;
	private Shape newTarget;
	
	public ConnectionReconnectCommand(Connection connection) {
		if (connection == null)
			throw new IllegalArgumentException();
		this.connection = connection;
		this.oldSource = connection.getSource();
		this.oldTarget = connection.getTarget();
	}
	
	@Override
	public boolean canExecute() {
		if (newSource != null)
			return isSourceConnectionValid();
		else if (newTarget != null)
			return isTargetConnectionValid();
		return false;
	}
	
	private boolean isSourceConnectionValid() {
		// newSource and newTarget must point to different endpoints
		if (newSource.equals(oldTarget))
			return false;
		// false if connection already exists
		for (Connection c : newSource.getSourceConnections())
			if(c.getTarget().equals(oldTarget) && !c.equals(connection))
				return false;
		
		return true;
	}
	
	private boolean isTargetConnectionValid() {
		if (newTarget.equals(oldSource))
			return false;
		for (Connection c : newTarget.getTargetConnections())
			if (c.getSource().equals(oldSource) && !c.equals(connection))
				return false;
		
		return true;
	}
	
	@Override
	public void execute() {
		if (newSource != null)
			connection.connect(newSource, oldTarget);
		else if (newTarget != null)
			connection.connect(oldSource, newTarget);
		else
			throw new IllegalStateException();
	}
	
	@Override
	public void undo() {
		connection.connect(oldSource, oldTarget);
	}
	
	public void setNewSource(Shape connectionSource) {
		if (connectionSource == null)
			throw new IllegalArgumentException();
		newSource = connectionSource;
		newTarget = null;
		setLabel("Connection Startpoint Moved");
	}
	
	public void setNewTarget(Shape connectionTarget) {
		if (connectionTarget == null)
			throw new IllegalArgumentException();
		newSource = null;
		newTarget = connectionTarget;
		setLabel("ConnectionEndpoint Moved");
	}
}
