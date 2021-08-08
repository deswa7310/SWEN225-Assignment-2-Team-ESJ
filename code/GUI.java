import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;
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
	private static Image gameOverPNG = loadImage("gameover.png");
	
	public DrawPanel() {
		//this.setPreferredSize(new Dimension(200,200));
		this.setBackground(new Color(36,36,36));
	}
	
	/*
	 * Calls draw on each game aspect
	 */
	private void drawGame(Graphics g) throws IOException {
		Graphics g2 = (Graphics2D) g;
		
		Board.drawBoard(g2); //draws the grid and squares
		
		for(GameCharacter c : Board.characterList()) { //draws characters onto the grid
			c.drawCharacter(g2);
		}
		
		for(Estate e : Board.estateList()) { //draws estates contents like weapons and characters
			e.drawEstateContents(g2);
			e.drawEstateName(g2);
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		try {
			drawGame(g);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(Board.gameOver()) {
			drawGameOver(g);
		}
	}
	
	private void drawGameOver(Graphics g) {
		  g.drawImage(gameOverPNG, 20,20,GUI.SIZE-40,GUI.SIZE-40,null,null);
	}
	
	private static Image loadImage(String filename) {
		// using the URL means the image loads when stored
		// in a jar or expanded into individual files.
		java.net.URL imageURL = Weapon.class.getResource(filename);
		try {
			Image img = ImageIO.read(imageURL);
			return img;
		} catch (IOException e) {
			// we've encountered an error loading the image. There's not much we
			// can actually do at this point, except to abort the game.
			throw new RuntimeException("Unable to load image: " + filename);
		}
  }
}
