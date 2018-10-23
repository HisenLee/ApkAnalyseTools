package libchecker.app.lhx.view;

import javax.swing.SwingUtilities;

/**
 * Launch Application
 */
public class MainActivity {

	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainView view = new MainView();
				view.LaunchMainFrame();
			}
		});
		
		
	}

}
