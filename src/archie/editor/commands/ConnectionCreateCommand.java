package archie.editor.commands;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.gef.commands.Command;

import archie.model.connections.Connection;
import archie.model.shapes.Shape;



public class ConnectionCreateCommand extends Command {
	private Connection connection;
	private Class<?> connectionClass;
	private Shape source;
	private Shape target;
	
	public ConnectionCreateCommand(Shape source, Class<?> connectionClass) {
		if (source == null || connectionClass == null)
			throw new IllegalArgumentException();
		this.source = source;
		this.connectionClass = connectionClass;
		setLabel("Create Connection");
	}
	
	@Override
	public boolean canExecute() {
		if(source.equals(target))
			return false;
		
		// false if source already has connection to target
		for(Connection c : source.getSourceConnections())
			if(c.getTarget().equals(target))
				return false;
		
		return true;
	}
	
	@Override
	public void execute() {
		try {
//			connection = (Connection)connectionClass.newInstance();
//			connection.connect(source, target);
			connection = (Connection)connectionClass.getConstructor(Shape.class, Shape.class).newInstance(source, target);
		} catch (InstantiationException | IllegalAccessException| IllegalArgumentException | SecurityException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void redo() {
		connection.reconnect();
	}
	
	@Override
	public void undo() {
		connection.disconnect();
	}
	
	public void setTarget(Shape target) {
		if (target == null)
			throw new IllegalArgumentException();
		this.target = target;
	}
}
