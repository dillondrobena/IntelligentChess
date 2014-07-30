import java.awt.Component;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;


public class main {
	public static void main(String[] args)
	{
		JPanel panel = new JPanel();
		//Set up radio buttons for resolution
		JRadioButton bt1 = new JRadioButton("800x800");
		bt1.setSelected(true); //Default selection
		JRadioButton bt2 = new JRadioButton("600x600");
		//Group to handle buttons
		ButtonGroup bg = new ButtonGroup();
		//Add buttons to group
		bg.add(bt1);
		bg.add(bt2);
		//Add buttons to panel
		panel.add(bt1);
		panel.add(bt2);
		int selection = JOptionPane.showOptionDialog(null, panel, "Please choose a resolution", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
		if (selection == 0)
		{
			for (Component button : panel.getComponents())
			{
				if (((JRadioButton) button).isSelected())
				{
					try {
						Board board = new Board();
						board.init(800, 800);
						break;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{
					try {
						Board board = new Board();
						board.init(600, 600);
						break;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
}
