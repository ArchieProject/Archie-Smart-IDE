package archie.editor.commands;

import org.eclipse.gef.commands.*;

import archie.model.connections.Connection;



public class ConnectionDeleteCommand extends Command {
	private final Connection connection;
	
	public ConnectionDeleteCommand(Connection connection) {
		if(connection == null)
			throw new IllegalArgumentException();
		this.connection = connection;
		setLabel("Delete Connection");
	}
	
	@Override
	public void execute() {
		connection.disconnect();
	}
	
	@Override
	public void undo() {
		connection.reconnect();
	}
}
