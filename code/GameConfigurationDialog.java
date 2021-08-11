import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * GameConfigurationDialog is a customized JDialog that uses JRadioButtons and JTextFields
 * to let Players choose their character and type their nickname.
 * It pops up at the start of the game.
 *
 * @author johnh
 */
public class GameConfigurationDialog extends JDialog {

    /** The current Game. */
    private final Game game;
    /** The selected number of Players this game. */
    private int numberOfPlayers = 3;
    /** An array of all Players this game. */
    private final Player[] players = new Player[4];
    /** Contains all GameCharacters that haven't been picked yet. */
    private final List<GameCharacter> availableCharacters;
    /** The GameCharacter selected by the current Player being configured. */
    private GameCharacter selected;
    /** The nickname made by the current Player being configured. */
    private String nickname;

    /**
     * Creates a name GameConfigurationDialog that pops up to allow players to
     * configure their Player objects.
     * @param game the current Game
     */
    public GameConfigurationDialog(Game game) {
        this.game = game;
        availableCharacters = new ArrayList<>(game.getCharacters());
        setTitle("Game Configuration");
        setAlwaysOnTop(true);

        // If dialog window is closed, tell GUI to close the program.
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                game.setChanged("close");
            }
        });

        // Construct each panel:
        JPanel titlePanel = createTitlePanel();
        JPanel optionsPanel = createOptionsPanel();
        JPanel confirmPanel = createConfirmPanel(optionsPanel);
        GridBagConstraints c = new GridBagConstraints();
        setLayout(new GridBagLayout());

        // Position each panel:
        c.gridwidth = GridBagConstraints.REMAINDER;
        add(titlePanel, c);
        c.gridwidth = GridBagConstraints.REMAINDER;
        add(optionsPanel, c);
        c.gridwidth = GridBagConstraints.REMAINDER;
        add(confirmPanel, c);
        pack();

        // Center window:
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screensize = toolkit.getScreenSize();
        setBounds((screensize.width - getWidth())/2, (screensize.height - getHeight())/2, getWidth(), getHeight());

        // Display window:
        setVisible(true);
    }

    /**
     * Creates a new JPanel with a JLabel with big text "Welcome to Murder Madness".
     * @return title JPanel
     */
    private JPanel createTitlePanel() {
        JPanel panel = GUI.createBorderedPanel(true);
        JLabel label = new JLabel("Welcome to Murder Madness!");
        Font font = label.getFont();
        label.setFont(new Font(font.getName(), font.getStyle(), 20));
        panel.add(label);
        return panel;
    }

    /**
     * Creates a new JPanel with a JRadioButton for each option.
     * @return options JPanel
     */
    private JPanel createOptionsPanel() {
        final JRadioButton[] buttons = new JRadioButton[2];
        ButtonGroup group = new ButtonGroup();

        // Creates a radio button to select 3 Players this game.
        buttons[0] = new JRadioButton("", true);
        buttons[0].setAction(new AbstractAction("3 Players (Computer plays 4th)") {
            public void actionPerformed(ActionEvent e) {
                numberOfPlayers = 3;
            }
        });
        // Creates a radio button to select 4 players this game.
        buttons[1] = new JRadioButton("", false);
        buttons[1].setAction(new AbstractAction("4 Players") {
            public void actionPerformed(ActionEvent e) {
                numberOfPlayers = 4;
            }
        });

        group.add(buttons[0]);
        group.add(buttons[1]);

        // Adds radio buttons along with a JLabel to panel:
        JPanel panel = GUI.createBorderedPanel(true);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Select Number of Players:"));
        panel.add(new JLabel(" "));
        for (JRadioButton box : buttons) {
            panel.add(box);
        }
        return panel;
    }


    /**
     * Creates a JPanel with a JButton letting players confirm their choice.
     * The button proceeds the dialog to the next set of options.
     * @param optionsPanel the options panel with options to change when dialog is proceeded
     * @return confirm panel
     */
    private JPanel createConfirmPanel(JPanel optionsPanel) {
        JPanel panel = GUI.createBorderedPanel(false);
        JButton button = new JButton();
        button.setAction(new AbstractAction("Confirm") {
            public void actionPerformed(ActionEvent e) {
                proceedDialog(optionsPanel);
            }
        });
        panel.add(button);
        return panel;
    }

    /**
     * When called, the optionsPanel is changed to hold a new set of JRadioButtons
     * with the next set of options to choose from.
     * @param optionsPanel the panel of options
     */
    private void proceedDialog(JPanel optionsPanel) {
        // Removes the last selected character from the list of available characters.
        if (selected != null) availableCharacters.remove(selected);

        // Calculates next player number to choose their character
        int nextPlayerNumber = 5 - availableCharacters.size();

        // If a character was selected, make a new Player object:
        if (selected != null) {
            int prevPlayerNumber = nextPlayerNumber - 1;
            int index = GameCharacter.Name.valueOf(selected.toString()).ordinal();
            players[index] = new Player(prevPlayerNumber, nickname, selected);
        }

        // If all Players have selected and there are only 3 Players, make a Computer player
        // with the remaining character.
        if (numberOfPlayers == 3 && nextPlayerNumber == 4) {
            GameCharacter c = availableCharacters.remove(0);
            int index = GameCharacter.Name.valueOf(c.toString()).ordinal();
            players[index] = new Computer(4, c);
            setVisible(false);
            game.confirmConfigurations(players);

        } else if (nextPlayerNumber <= 4) {
            // Else if a Player made a selection, recreate the options
            // on the optionsPanel to remove the unavailable character.

            optionsPanel.removeAll();

            selected = availableCharacters.get(0); // default selection

            boolean fourthPlayer = (nextPlayerNumber == 4);
            JRadioButton[] buttons = new JRadioButton[availableCharacters.size()];

            // If this is not the 4th player selecting, present a radio button for each
            // character available:
            if (!fourthPlayer) {
                ButtonGroup group = new ButtonGroup();

                for (int i = 0; i < availableCharacters.size(); i++) {
                    GameCharacter c = availableCharacters.get(i);

                    buttons[i] = new JRadioButton("", i == 0);
                    buttons[i].setAction(new AbstractAction(c.toString()) {
                        public void actionPerformed(ActionEvent e) {
                            selected = c;
                        }
                    });
                    group.add(buttons[i]);
                }
            }

            nickname = "Player " + nextPlayerNumber; // default nickname

            // Add a JTextField so players can enter their nickname:
            JTextField textField = new JTextField(nickname, 15);
            textField.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e){ nickname = textField.getText(); }

                @Override
                public void keyPressed(KeyEvent e){ nickname = textField.getText(); }

                @Override
                public void keyReleased(KeyEvent e){}
            });

            // Add JLabels for text and spacing, along with radio buttons and text field:
            optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
            optionsPanel.add(new JLabel("Player " + nextPlayerNumber + ":"));
            optionsPanel.add(new JLabel(" "));
            optionsPanel.add(new JLabel("Enter your name:"));
            optionsPanel.add(new JLabel(" "));
            optionsPanel.add(textField);
            if (!fourthPlayer) {
                optionsPanel.add(new JLabel(" "));
                optionsPanel.add(new JLabel("Choose your character:"));
                optionsPanel.add(new JLabel(" "));
                for (JRadioButton box : buttons) {
                    optionsPanel.add(box);
                }
            }
            pack();
            optionsPanel.revalidate();
        } else {
            // If configuration is done, hide the dialog and start the game.
            setVisible(false);
            game.confirmConfigurations(players);
        }
    }
}