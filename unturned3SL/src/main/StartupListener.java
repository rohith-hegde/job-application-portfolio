package main;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.swing.SwingUtilities;

public class StartupListener implements ServletContextListener
{

	@Override
	public void contextDestroyed(ServletContextEvent arg0) 
	{
		Main.stop();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) 
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run() 
			{
				try {
					Main.main(null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
	}
}