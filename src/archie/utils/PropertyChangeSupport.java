package archie.utils;


import java.beans.PropertyChangeListener;

public class PropertyChangeSupport
{
	private transient java.beans.PropertyChangeSupport changeSupport = new java.beans.PropertyChangeSupport(this);
	
	public synchronized void addPropertyChangeListener(PropertyChangeListener l) {
		if(l==null)
			throw new IllegalArgumentException();
		changeSupport.addPropertyChangeListener(l);
	}
	
	public synchronized void removePropertyChangeListener(PropertyChangeListener l) {
		if(l!=null)
			changeSupport.removePropertyChangeListener(l);
	}
	
	protected void firePropertyChange(String property, Object oldValue, Object newValue) {
		if(changeSupport.hasListeners(property))
			changeSupport.firePropertyChange(property, oldValue, newValue);
	}
}
