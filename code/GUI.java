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
 * GUI initiates and controls the user interface.
 * @author stefanjenkins
 *
 */
public class GUI extends JFrame implements KeyListener, Observer {

	public static final int SIZE = 808; // size of square JFrame

	private final Game game;
	private TextPanel textPanel;
	private final InputPanel inputPanel;

	public GUI(Game game, InputPanel input) {
		this.game = game;
		this.inputPanel = input;
		initUI();
		initCloseDialog();
	}

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

		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.gridheight = 10;
		c.weightx = 0.75;
		c.weighty = 1;
		add(drawPanel, c);

		c.fill = GridBagConstraints.BOTH;
		c.gridx = 3;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 0.25;
		c.weighty = 0.2;
		add(textPanel, c);

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

	/*
	 * Sets up menu bar.
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
		repaint();
		if (arg instanceof String) {
			String text = (String) arg;
			if (text.equals("close")) System.exit(-1);
			textPanel.setText(text);
		}
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

	public static JPanel createBorderedPanel(boolean lines) {
		JPanel panel = new JPanel();
		Border b = BorderFactory.createEmptyBorder(15, 15, 15, 15);
		if (lines) b = BorderFactory.createCompoundBorder(b, BorderFactory.createLineBorder(Color.GRAY, 1));
		b = BorderFactory.createCompoundBorder(b, BorderFactory.createEmptyBorder(15, 15, 15, 15));
		panel.setBorder(b);
		return panel;
	}

	public static JPanel createTitlePanel(String title) {
		final JPanel titlePanel = createBorderedPanel(true);
		JLabel label = new JLabel(title);
		Font font = label.getFont();
		label.setFont(new Font(font.getName(), font.getStyle(), 20));
		titlePanel.add(label);
		return titlePanel;
	}

	/*
	 * Displays the game board. Paint component of the GUI
	 */
	private static class DrawPanel extends JPanel implements MouseListener, MouseMotionListener {

		private final Game game;
		private static final Image gameOverPNG = loadImage("images/gameover.png");
		private final JPopupMenu popup = new JPopupMenu();
		private final JMenuItem popupItem = new JMenuItem("Hi");

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
			if(game.gameOver()) {
				drawGameOver(g);
			}
		}

		/*
		 * Calls draw on each game aspect.
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

		private void drawGameOver(Graphics g) {
			g.drawImage(gameOverPNG, 20,20,GUI.SIZE-40,GUI.SIZE-40,null,null);
		}

		private static Image loadImage(String filename) {
			// Using the URL means the image loads when stored
			// in a jar or expanded into individual files.
			java.net.URL imageURL = Weapon.class.getResource(filename);
			try {
				return ImageIO.read(imageURL);
			} catch (IOException e) {
				// We've encountered an error loading the image. There's not much we
				// can actually do at this point, except to abort the game.
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
			if (game.configuring()) return;

			double row = (e.getY() - Square.WALL) * 1.0 / Square.SIZE;
			double col = (e.getX() - Square.WALL) * 1.0 / Square.SIZE;

			if (row < 0 || row >= Board.ROWS || col < 0 || col >= Board.COLS){
				popup.setVisible(false);
				return;
			}

			popup.setVisible(true);
			popupItem.setText(game.getBoard().getSquare((int)row, (int)col).getDescription());
			popup.show(e.getComponent(), e.getX(), e.getY()-40);
		}

	}

	private static class TextPanel extends JPanel {

		private final JTextArea textArea;

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

			Border b = BorderFactory.createEmptyBorder(15, 15, 15, 15);
			b = BorderFactory.createCompoundBorder(b, BorderFactory.createLoweredBevelBorder());
			b = BorderFactory.createCompoundBorder(b, BorderFactory.createEmptyBorder(10, 10, 10, 10));
			setBorder(b);

			b = BorderFactory.createEmptyBorder(10, 10, 10, 10);
			textArea.setBorder(b);
			setLayout(new BorderLayout());

			JScrollPane scrollPane = new JScrollPane(textArea);
			scrollPane.setPreferredSize(new Dimension(80, 80));
			scrollPane.getViewport().setBackground(new Color(69, 69, 69));
			add(scrollPane);

			textArea.setText("Welcome!");
		}

		protected void setText(String text){
			textArea.setText(text);
		}
	}
}