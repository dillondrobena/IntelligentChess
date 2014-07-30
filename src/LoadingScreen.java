import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;


public class LoadingScreen {
	JProgressBar progressBar = new JProgressBar();
	JFrame screen = new JFrame();
	int counter = 0;

	public LoadingScreen()
	{
		screen.setUndecorated(true);
		screen.setSize(300, 35);

		JLabel lblLoading = new JLabel("Loading...");
		screen.getContentPane().add(lblLoading, BorderLayout.SOUTH);

		screen.getContentPane().add(progressBar, BorderLayout.CENTER);
		progressBar.setMaximum(96);
		screen.setLocationRelativeTo(null);
		screen.setVisible(true);
	}

	public void setMax()
	{
		progressBar.setValue(96);
	}

	public void update()
	{
		counter++;
		progressBar.setValue(counter);
	}

	public void close()
	{
		screen.setVisible(false);
		screen.dispose();
	}
}
