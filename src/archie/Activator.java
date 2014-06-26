
package archie;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import archie.globals.ArchieSettings;
import archie.javamodelbinding.JavaModelBinding;
import archie.monitoring.MonitoringManager;
import archie.resourcesbinding.ResourcesBinding;
import archie.timstorage.TimsManager;

/**
 * [Singleton] Controls the plug-in life cycle; provides the Shell object that's
 * being used by the Eclipse IDE.
 */
public class Activator extends AbstractUIPlugin implements IStartup
{
	private static Activator instance;

	/**
	 * (Singleton) Constructs the Activator class. It's supposed to be called
	 * exactly once - by the Eclipse Runtime.
	 */
	public Activator()
	{
		if (instance != null)
			throw new IllegalStateException(
					"Activator class is supposed to have only one instance, which should be created by the Eclipse Runtime");
		instance = this;
	}
	
	@Override
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		ArchieSettings.getInstance().initialize();
		TimsManager.getInstance().scanSolution();
		JavaModelBinding.getInstance().bind();
		ResourcesBinding.getInstance().bind();
		MonitoringManager.getIntance().initialize();
	}

	/**
	 * 
	 */
	@Override
	public void stop(BundleContext context) throws Exception
	{
		ResourcesBinding.getInstance().unbind();
		JavaModelBinding.getInstance().unbind();
		MonitoringManager.getIntance().saveToDatabase();
		super.stop(context);
	}

	/**
	 * Returns Shell object used by the current Eclipse instance.
	 * 
	 * @return Shell object used by the current Eclipse instance.
	 */
	public static Shell getShell()
	{
		return instance.getWorkbench().getActiveWorkbenchWindow().getShell();
	}

	public static Activator getInstance()
	{
		return instance;
	}

	@Override
	public void earlyStartup()
	{
		// TODO Auto-generated method stub
		
	}
}
