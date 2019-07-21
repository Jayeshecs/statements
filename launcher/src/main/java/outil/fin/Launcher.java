/**
 * 
 */
package outil.fin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Prajapati
 *
 */
public class Launcher {

	public static void main(String[] args) {
		final Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Statements Manager");
		shell.setLayout(new FillLayout());
		Browser browser = null;
		try {
			browser = new Browser(shell, SWT.NONE);
		} catch (SWTError e) {
			/* The Browser widget throws an SWTError if it fails to
			 * instantiate properly. Application code should catch
			 * this SWTError and disable any feature requiring the
			 * Browser widget.
			 * Platform requirements for the SWT Browser widget are available
			 * from the SWT FAQ website.
			 */
			e.printStackTrace();
		}
		if (browser != null) {
			/* The Browser widget can be used */
			browser.setUrl("http://localhost:8080/wicket");
		}
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
