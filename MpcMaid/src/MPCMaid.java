import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import com.mpcmaid.gui.BaseFrame;
import com.mpcmaid.gui.MainFrame;
import com.mpcmaid.gui.Preferences;

/**
 * Dummy main class with short name
 * 
 * @author cyrille martraire
 */
public final class MPCMaid {

	// splash screen
	private static JWindow screen = null;

	public static void showSplash() {
		screen = new JWindow();
		final URL resource = MainFrame.class.getResource("mpcmaidlogo400_400.png");
		final JLabel label = new JLabel(new ImageIcon(resource));
		screen.getContentPane().add(label);
		screen.setLocationRelativeTo(null);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension labelSize = screen.getPreferredSize();
		screen
				.setLocation(screenSize.width / 2 - (labelSize.width / 2), screenSize.height / 2
						- (labelSize.height / 2));
		screen.pack();
		screen.setVisible(true);
		label.repaint();
		screen.repaint();
	}

	public static void hideSplash() {
		if (screen == null) {
			return;
		}
		screen.setVisible(false);
		screen.dispose();
		screen = null;
	}

	public static void main(String[] args) {
		// process the command line arguments
		boolean showSplash = true;
		String fileName = null;
		for (String arg : args) {
			if (arg.equals("-n")) {
				showSplash = false;
			} else if (arg.startsWith("-")) {
				showHelp();
				return;
			} else {
				fileName = arg;
				break;
			}
		}
		final File programFile;
		if (fileName != null) {
			programFile = new File(fileName);
		} else {
			programFile = null;
		}

		makeAsNativeAsPossible();
		if (showSplash)
			showSplash();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final BaseFrame baseFrame;
				if (programFile != null && programFile.exists()) {
					baseFrame = new MainFrame(programFile);
				} else {
					baseFrame = new MainFrame();
				}
				// wait to show the splash
				if (screen != null) {
					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				//baseFrame.setVisible(true);
				baseFrame.show();

				hideSplash();
			}
		});
	}

	public static final boolean isMacOsX() {
		return System.getProperty("os.name").toLowerCase().startsWith("mac");
	}

	/**
	 * @return true if macOS is set to the dark appearance
	 */
	private static boolean isMacDarkMode() {
		try {
			final Process process = new ProcessBuilder("defaults", "read", "-g", "AppleInterfaceStyle").start();
			final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			final String style = reader.readLine();
			process.waitFor();
			return "Dark".equalsIgnoreCase(style);
		} catch (Exception e) {
			return false;
		}
	}

	public static final void makeAsNativeAsPossible() {
		final boolean mac = isMacOsX();
		if (mac) {
			// has to be set before AWT starts
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("apple.awt.application.name", "MPC Maid");
			System.setProperty("apple.awt.application.appearance", "system");
		}
		try {
			if (mac) {
				UIManager.setLookAndFeel(isMacDarkMode() ? new FlatMacDarkLaf() : new FlatMacLightLaf());
			} else {
				UIManager.setLookAndFeel(new FlatLightLaf());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Preferences.getInstance().load();
		} catch (Exception ignore) {
		}
	}

	public static final void showHelp() {
		System.err.println("Usage: mpcmaid [-h] [-n] [<program.pgm>]");
		System.err.println();
		System.err.println(" -n     Do not show splash screen");
		System.err.println(" -h     This text");
	}

	public String toString() {
		return "MPCMaid";
	}

}
