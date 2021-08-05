import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

/**
 * 
 * GUI class initiates the user interface
 * @author stefanjenkins
 *
 */
public class GUI extends JFrame{
	public static final int SIZE = 808; //size of square Jframe
	
	public GUI() {
		initUI();
	}
	
	
	
	private void initUI() {
		DrawPanel drawPanel = new DrawPanel();
		add(drawPanel);
		
		initMenuBar();
		
		setSize(SIZE,SIZE+50);
		setTitle("MurderMadness");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	
	/*
	 * Sets up menu bar *needs work*
	 */
	private void initMenuBar() {
		JMenuBar menu = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		
		JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.setToolTipText("Exit game");
		exitMenuItem.addActionListener((event) -> System.exit(0));
		
		fileMenu.add(exitMenuItem);
		menu.add(fileMenu);
		
		setJMenuBar(menu);
	}
}


/*
 * Displays the game board. Paint component of the GUI
 */
class DrawPanel extends JPanel {
	public static final int WIDTH = GUI.SIZE -500; //not used
	public static final int HEIGHT = GUI.SIZE -500; //not used
	
	public DrawPanel() {
		//this.setPreferredSize(new Dimension(200,200));
		this.setBackground(new Color(36,36,36));
	}
	
	/*
	 * Calls draw on each game aspect
	 */
	private void drawGame(Graphics g) {
		Graphics g2 = (Graphics2D) g;
		
		Board.drawBoard(g2); //draws the grid and squares
		
		for(GameCharacter c : Board.characterList()) { //draws characters onto the grid
			c.drawCharacter(g2);
		}
		
		for(Estate e : Board.estateList()) { //draws estates contents like weapons and characters
			e.drawEstateContents(g2);
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawGame(g);
	}
}
