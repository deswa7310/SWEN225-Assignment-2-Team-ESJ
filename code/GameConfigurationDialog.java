import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GameConfigurationDialog extends JDialog {

    private final Game game;
    private int numberOfPlayers = 3;
    private final Player[] players = new Player[4];
    private final List<GameCharacter> availableCharacters;
    private GameCharacter selected;
    private String nickname;

    public GameConfigurationDialog(Game game) {
        this.game = game;
        availableCharacters = new ArrayList<>(game.getCharacters());
        setTitle("Game Configuration");
        setAlwaysOnTop(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                game.setChanged("close");
            }
        });

        JPanel titlePanel = createTitlePanel();
        JPanel optionsPanel = createOptionsPanel();
        JPanel confirmPanel = createConfirmPanel(optionsPanel);
        GridBagConstraints c = new GridBagConstraints();
        setLayout(new GridBagLayout());

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

    private JPanel createTitlePanel() {
        JPanel panel = GUI.createBorderedPanel(true);
        JLabel label = new JLabel("Welcome to Murder Madness!");
        Font font = label.getFont();
        label.setFont(new Font(font.getName(), font.getStyle(), 20));
        panel.add(label);
        return panel;
    }

    private JPanel createOptionsPanel() {
        final JRadioButton[] buttons = new JRadioButton[2];
        ButtonGroup group = new ButtonGroup();


        buttons[0] = new JRadioButton("", true);
        buttons[0].setAction(new AbstractAction("3 Players (Computer plays 4th)") {
            public void actionPerformed(ActionEvent e) {
                numberOfPlayers = 3;
            }
        });
        buttons[1] = new JRadioButton("", false);
        buttons[1].setAction(new AbstractAction("4 Players") {
            public void actionPerformed(ActionEvent e) {
                numberOfPlayers = 4;
            }
        });

        group.add(buttons[0]);
        group.add(buttons[1]);

        JPanel panel = GUI.createBorderedPanel(true);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Select Number of Players:"));
        panel.add(new JLabel(" "));
        for (JRadioButton box : buttons) {
            panel.add(box);
        }
        return panel;
    }


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

    private void proceedDialog(JPanel optionsPanel) {
        if (selected != null) availableCharacters.remove(selected);

        int nextPlayerNumber = 5 - availableCharacters.size();

        if (selected != null) {
            int prevPlayerNumber = nextPlayerNumber - 1;
            int index = GameCharacter.Name.valueOf(selected.toString()).ordinal();
            players[index] = new Player(prevPlayerNumber, nickname, selected);
        }

        if (numberOfPlayers == 3 && nextPlayerNumber == 4) {
            GameCharacter c = availableCharacters.remove(0);
            int index = GameCharacter.Name.valueOf(c.toString()).ordinal();
            players[index] = new Computer(4, c);
            setVisible(false);
            game.confirmConfigurations(players);
        } else if (nextPlayerNumber <= 4) {
            optionsPanel.removeAll();

            selected = availableCharacters.get(0);

            boolean fourthPlayer = (nextPlayerNumber == 4);
            JRadioButton[] buttons = new JRadioButton[availableCharacters.size()];

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

            nickname = "Player " + nextPlayerNumber;
            JTextField textField = new JTextField(nickname, 15);
            textField.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e){ nickname = textField.getText(); }

                @Override
                public void keyPressed(KeyEvent e){ nickname = textField.getText(); }

                @Override
                public void keyReleased(KeyEvent e){}
            });

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
            setVisible(false);
            game.confirmConfigurations(players);
        }
    }
}