import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;

/**
 * 
 * GUI initiates and controls the display of the user interface.
 * It is an Observer of the Game class, updating the display whenever Game's
 * data has been changed.
 *
 * @author stefanjenkins
 *
 */
public class GUI extends JFrame implements KeyListener, Observer {

	/** The base size of the original square JFrame. */
	public static final int SIZE = 808;
	/** The current Game being observed. */
	private final Game game;
	/** The custom JPanel on the right of the GUI used to display important text. */
	private TextPanel textPanel;
	/** The custom JPanel under the textPanel, used to contain buttons for available actions. */
	private final InputPanel inputPanel;
	/** Flag for whether or not the Player's hand should be displayed. */
	private boolean drawHand = false;

	/**
	 * Constructs a new GUI object to display the given Game.
	 * @param game Game to be displayed
	 * @param input InputPanel with buttons for input
	 */
	public GUI(Game game, InputPanel input) {
		this.game = game;
		this.inputPanel = input;
		initUI();
		initCloseDialog();
	}

	/**
	 * Initializes the GUI, adding its components and positioning them.
	 */
	private void initUI() {
		setSize((int) (SIZE * 1.5), SIZE + 100);
		setFocusable(true);
		setTitle("Murder Madness");
		setLocationRelativeTo(null);
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		DrawPanel drawPanel = new DrawPanel(game);
		textPanel = new TextPanel();
		addKeyListener(this);

		// Position and add drawPanel:
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.gridheight = 10;
		c.weightx = 0.75;
		c.weighty = 1;
		add(drawPanel, c);

		// Position and add textPanel:
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 3;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 0.25;
		c.weighty = 0.2;
		add(textPanel, c);

		// Position and add inputPanel:
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 3;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 9;
		c.weightx = 0.25;
		c.weighty = 0.8;
		add(inputPanel, c);

		initMenuBar();

		setVisible(true);
	}

	/**
	 * Initializes the JOptionPane that pops up to confirm your choice
	 * when attempting to close the GUI window.
	 */
	private void initCloseDialog(){
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int index = JOptionPane.showConfirmDialog(null,
						"Are you sure you want to exit?", "Exit Confirmation",
						JOptionPane.YES_NO_OPTION);
				switch (index){
					case JOptionPane.YES_OPTION:
						setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
						break;
					case JOptionPane.NO_OPTION:
						setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
						break;
				}
			}
		});
	}

	/**
	 * Initializes the menu bar and adds menu items to it.
	 */
	private void initMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Menu");

		// About menu item:
		JMenuItem aboutMenuItem = new JMenuItem("About");
		aboutMenuItem.setToolTipText("Shows game description");
		aboutMenuItem.addActionListener((event) -> {
			String desc = "Murder Madness is a fun family game where players race to solve a murder in a battle of wits!";
			JOptionPane.showMessageDialog(null, desc);
		});

		// Exit Game menu item:
		JMenuItem exitMenuItem = new JMenuItem("Exit Game");
		exitMenuItem.setToolTipText("Closes game");
		exitMenuItem.addActionListener((event) -> System.exit(0));

		menu.add(aboutMenuItem);
		menu.add(exitMenuItem);
		menuBar.add(menu);

		setJMenuBar(menuBar);
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof String) {
			String text = (String) arg;
			if (text.equals("close")) System.exit(-1);
			else if (text.equals("Toggled hand display...")) drawHand = !drawHand;
			else if (text.endsWith("):")) drawHand = false;
			textPanel.setText(text);
		}
		repaint();
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
	@Override
	public void keyReleased(KeyEvent e) {
	}
	@Override
	public void keyPressed(KeyEvent e) {
		game.keyPressed(e);
	}

	/**
	 * Creates and returns a JPanel with a stylised border.
	 * @param lines true if lines should be added to border
	 * @return a stylized JPanel
	 */
	public static JPanel createBorderedPanel(boolean lines) {
		JPanel panel = new JPanel();
		Border b = BorderFactory.createEmptyBorder(15, 15, 15, 15);
		if (lines) b = BorderFactory.createCompoundBorder(b, BorderFactory.createLineBorder(Color.GRAY, 1));
		b = BorderFactory.createCompoundBorder(b, BorderFactory.createEmptyBorder(15, 15, 15, 15));
		panel.setBorder(b);
		return panel;
	}

	/**
	 * Creates and returns a JPanel with a JLabel using a big size font to display a specified String.
	 * @param title String to be displayed
	 * @return the title panel
	 */
	public static JPanel createTitlePanel(String title) {
		final JPanel titlePanel = createBorderedPanel(true);
		JLabel label = new JLabel(title);
		Font font = label.getFont();
		label.setFont(new Font(font.getName(), font.getStyle(), 20));
		titlePanel.add(label);
		return titlePanel;
	}

	/**
	 * DrawPanel is a custom JPanel displaying the graphics of the GUI.
	 * It displays the game board and all game elements on it.
	 */
	private class DrawPanel extends JPanel implements MouseListener, MouseMotionListener {

		/** The current game. */
		private final Game game;
		/** Game over image. */
		private final Image gameOverPNG = loadImage("images/gameover.png");
		/** Popup displayed when hovering mouse over DrawPanel. */
		private final JPopupMenu popup = new JPopupMenu();
		/** Item displayed in popup, containing Square description. */
		private final JMenuItem popupItem = new JMenuItem("Hi");

		/**
		 * Constructs a new DrawPanel to display the game board.
		 * @param game current Game
		 */
		public DrawPanel(Game game) {
			this.game = game;
			//this.setPreferredSize(new Dimension(200,200));
			setFocusable(true);
			setBackground(new Color(36,36,36));

			addMouseListener(this);
			addMouseMotionListener(this);

			popup.setFocusable(false);
			popup.add(popupItem);
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			drawGame(g);
			if(drawHand && game.getPlayers() != null) {
				Player p = game.getPlayers()[game.getCurrentPlayerIndex()];
				p.drawHand(g);
			}
			if(game.gameOver()) {
				drawGameOver(g);
			}
		}

		/**
		 * Calls the board's draw method, along with draw methods for
		 * each GameCharacter and Estate.
		 * @param g the Graphics object
		 */
		private void drawGame(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			Board b = game.getBoard();
			if (b == null) return; // Board not initialized

			b.drawBoard(g2); //draws the grid and squares

			for(GameCharacter c : game.getCharacters()) { //draws characters onto the grid
				c.drawCharacter(g2);
			}

			for(Estate e : game.getEstates()) { //draws estates contents like weapons and characters
				e.drawEstateContents(g2);
				e.drawEstateName(g2);
			}
		}

		/**
		 * Draws the game over image at the end of the game.
		 * @param g the Graphics object
		 */
		private void drawGameOver(Graphics g) {
			g.drawImage(gameOverPNG, 20,20,GUI.SIZE-40,GUI.SIZE-40,null,null);
		}

		/**
		 * Loads an weapon image and returns it.
		 * @param filename image file name.
		 * @return Image object of the image file.
		 */
		private Image loadImage(String filename) {
			java.net.URL imageURL = Weapon.class.getResource(filename);
			// Attempt to load Image:
			try {
				return ImageIO.read(imageURL);
			} catch (IOException e) {
				// Throw exception if failed
				throw new RuntimeException("Unable to load image: " + filename);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) { }
		@Override
		public void mousePressed(MouseEvent e) { }
		@Override
		public void mouseReleased(MouseEvent e) { }
		@Override
		public void mouseEntered(MouseEvent e) { }
		@Override
		public void mouseExited(MouseEvent e) {
			popup.setVisible(false);
		}
		@Override
		public void mouseDragged(MouseEvent e) {}
		@Override
		public void mouseMoved(MouseEvent e) {
			if (game.configuring()) return; // don't allow mouse hovering popups during player configuration at start

			// Calculate row and col of mouse position on board:
			double row = (e.getY() - Square.WALL) * 1.0 / Square.SIZE;
			double col = (e.getX() - Square.WALL) * 1.0 / Square.SIZE;

			// If out of bounds, don't show popup.
			if (row < 0 || row >= Board.ROWS || col < 0 || col >= Board.COLS){
				popup.setVisible(false);
				return;
			}

			// Else, show popup with description of the Square being hovered over:
			popup.setVisible(true);
			popupItem.setText(game.getBoard().getSquare((int)row, (int)col).getDescription());
			popup.show(e.getComponent(), e.getX(), e.getY()-40);
		}

	}

	/**
	 * TextPanel is a custom JPanel on that displays important text during the game.
	 */
	private static class TextPanel extends JPanel {

		/** The JTextArea used to display text. */
		private final JTextArea textArea;

		/**
		 * Constructs a new TextPanel:
		 */
		public TextPanel() {
			setFocusable(true);
			setBackground(new Color(36,36,36));
			textArea = new JTextArea();
			textArea.setBackground(new Color(69, 69, 69));
			textArea.setEditable(false);
			textArea.setLineWrap(true);
			Font f = textArea.getFont();
			textArea.setFont(new Font(f.getName(), f.getStyle(), 30));
			textArea.setForeground(new Color(255, 255, 255));

			// Customize the border to make it fancy:
			Border b = BorderFactory.createEmptyBorder(15, 15, 15, 15);
			b = BorderFactory.createCompoundBorder(b, BorderFactory.createLoweredBevelBorder());
			b = BorderFactory.createCompoundBorder(b, BorderFactory.createEmptyBorder(10, 10, 10, 10));
			setBorder(b);

			b = BorderFactory.createEmptyBorder(10, 10, 10, 10);
			textArea.setBorder(b);
			setLayout(new BorderLayout());

			// Add a JScrollPane to allow scrolling if too much text:
			JScrollPane scrollPane = new JScrollPane(textArea);
			scrollPane.setPreferredSize(new Dimension(80, 80));
			scrollPane.getViewport().setBackground(new Color(69, 69, 69));
			add(scrollPane);

			textArea.setText("Welcome!");
		}

		/**
		 * Sets the text to be displayed:
		 * @param text custom text
		 */
		private void setText(String text){
			textArea.setText(text);
		}
	}
}