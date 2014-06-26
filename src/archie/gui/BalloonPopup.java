package archie.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;

import archie.Activator;


public class BalloonPopup {
	
	public static void showInfo(String title, String message) {
        Shell shell = Activator.getShell();
        int x = shell.getBounds().x + shell.getBounds().width;
        int y = shell.getBounds().y + shell.getBounds().height; 
        
        final ToolTip tip = new ToolTip(shell, SWT.BALLOON | SWT.ICON_INFORMATION);
        tip.setText(title);
        tip.setMessage(message);
        tip.setLocation(x, y);
        tip.setVisible(true);
        tip.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("CLICKED!");
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
        });
	}
}
