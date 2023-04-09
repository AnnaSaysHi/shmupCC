package game;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class MainMenu extends JFrame implements ActionListener{
	
	public MainMenu() {
		super("test");
		initComponents();
		setSize(960, 720);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void initComponents() {
		

		JPanel menuPanel = new JPanel();
		menuPanel.setBackground(Color.CYAN);
		
		JPanel gamePanel = new JPanel();
		gamePanel.setBackground(Color.MAGENTA);
		
		
		/*JButton button1 = new JButton("Switch to Game");
		button1.addActionListener(this);
		button1.setActionCommand("switchToGame");
		
		JButton button2 = new JButton("Switch to Menu");
		button2.addActionListener(this);
		button2.setActionCommand("switchToMenu");
		menuPanel.add(button1);
		gamePanel.add(button2);*/

		menuPanel.setVisible(true);
		this.add(menuPanel);

		gamePanel.setVisible(false);
		this.add(gamePanel);
	}
	public void actionPerformed(ActionEvent event) {
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MainMenu menu = new MainMenu();
		menu.setVisible(true);

	}

}
