import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * GuessOptionPane is a JOptionPane with JRadioButtons that allows Players to
 * make a guess/solve attempt by selecting a Card of each applicable type.
 *
 * @author johnh
 */
public class GuessOptionPane {

    /** The selected GameCharacter Card. */
    private GameCharacter cSelected;
    /** The selected Estate Card. */
    private Estate eSelected;
    /** The selected Weapon Card. */
    private Weapon wSelected;

    /**
     * Creates a new GuessOptionPane that pops up and allows the Player to select Cards
     * for their guess/solve attempt.
     *
     * @param game the current Game
     * @param player the Player making the guess/solve attempt
     * @param estate the Estate the Player's character is in (null if solve attempt)
     */
    public GuessOptionPane(Game game, Player player, Estate estate){
        boolean guessing = (estate != null);
        eSelected = estate;

        // Main panel:
        final JPanel guessPanel = new JPanel();
        guessPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // Construct sub panels:
        final JPanel titlePanel = GUI.createTitlePanel("Make your "+(guessing ? "Guess: ("+eSelected+")" : "Solve Attempt"));
        final JPanel characterPanel = createCharacterPanel(game);
        final JPanel estatePanel = (guessing ? null : createEstatePanel(game));
        final JPanel weaponPanel = createWeaponPanel(game);


        c.gridwidth = GridBagConstraints.REMAINDER;
        guessPanel.add(titlePanel, c);
        guessPanel.add(characterPanel);
        if (!guessing) guessPanel.add(estatePanel);
        guessPanel.add(weaponPanel);

        // Show the panel in a JOptionPane:
        JOptionPane.showMessageDialog(null, guessPanel);
        // Move GameCharacter and Weapon into Estate:
        if (guessing) {
            Player.moveToEstate(cSelected, eSelected);
            Player.moveToEstate(wSelected, eSelected);
            game.setChanged("Character and Weapon moved.");
        }
        player.confirmGuess(game, new HashSet<>(Arrays.asList(cSelected, eSelected, wSelected)), guessing);
    }


    /**
     * Constructs the panel that allows the Player to choose a GameCharacter Card using radio buttons.
     * @param game current game
     * @return the character panel
     */
    private JPanel createCharacterPanel(Game game){
        final JPanel characterPanel = GUI.createBorderedPanel(true);
        characterPanel.setLayout(new BoxLayout(characterPanel, BoxLayout.Y_AXIS));
        characterPanel.add(new JLabel("Characters:"));
        List<GameCharacter> characters = new ArrayList<>(game.getCharacters());
        cSelected = characters.get(0);
        ButtonGroup group = new ButtonGroup();

        // Construct a JRadioButton for each GameCharacter:
        for (int i = 0; i < characters.size(); i++){
            GameCharacter c = characters.get(i);

            JRadioButton button = new JRadioButton("", i == 0);
            button.setAction(new AbstractAction(c.toString()) {
                public void actionPerformed(ActionEvent e) {
                    cSelected = c;
                }
            });
            group.add(button);
            characterPanel.add(button);
        }
        return characterPanel;
    }

    /**
     * Constructs the panel that allows the Player to choose an Estate Card using radio buttons.
     * @param game current game
     * @return the estate panel
     */
    private JPanel createEstatePanel(Game game){
        final JPanel estatePanel = GUI.createBorderedPanel(true);
        estatePanel.setLayout(new BoxLayout(estatePanel, BoxLayout.Y_AXIS));
        estatePanel.add(new JLabel("Estates:"));
        List<Estate> estates = new ArrayList<>(game.getEstates());
        eSelected = estates.get(0);
        ButtonGroup group = new ButtonGroup();

        // Constructs a JRadioButton for each Estate:
        for (int i = 0; i < estates.size(); i++) {
            Estate e = estates.get(i);

            JRadioButton button = new JRadioButton("", i == 0);
            button.setAction(new AbstractAction(e.toString()) {
                public void actionPerformed(ActionEvent event) {
                    eSelected = e;
                }
            });
            group.add(button);
            estatePanel.add(button);
        }
        return estatePanel;
    }

    /**
     * Constructs the panel that allows the Player to choose a Weapon Card using radio buttons.
     * @param game current game
     * @return the weapon panel
     */
    private JPanel createWeaponPanel(Game game){
        final JPanel weaponPanel = GUI.createBorderedPanel(true);
        weaponPanel.setLayout(new BoxLayout(weaponPanel, BoxLayout.Y_AXIS));
        weaponPanel.add(new JLabel("Weapons:"));
        List<Weapon> weapons = new ArrayList<>(game.getWeapons());
        wSelected = weapons.get(0);
        ButtonGroup group = new ButtonGroup();

        // Constructs a new JRadioButton for each Weapon:
        for (int i = 0; i < weapons.size(); i++){
            Weapon w = weapons.get(i);

            JRadioButton button = new JRadioButton("", i == 0);
            button.setAction(new AbstractAction(w.toString()) {
                public void actionPerformed(ActionEvent e) {
                    wSelected = w;
                }
            });
            group.add(button);
            weaponPanel.add(button);
        }
        return weaponPanel;
    }
}
