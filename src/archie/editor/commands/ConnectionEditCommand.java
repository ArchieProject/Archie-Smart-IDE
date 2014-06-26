package archie.editor.commands;

import org.eclipse.gef.commands.Command;

import archie.model.connections.Connection;



public class ConnectionEditCommand extends Command {
	private String newText, oldText;
	private Connection connection;
	
	public ConnectionEditCommand(Connection connection, String newText) {
		this.connection = connection;
		if(newText == null)
			this.newText = "";
		else
			this.newText = newText;
	}
	
	@Override
	public void execute() {
		oldText = connection.getText();
		connection.setText(newText);
	}
	
	@Override
	public void undo() {
		connection.setText(oldText);
	}
}
