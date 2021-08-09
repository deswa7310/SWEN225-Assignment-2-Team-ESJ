import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class GuessOptionPane {

    private GameCharacter cSelected;
    private Estate eSelected;
    private Weapon wSelected;

    public GuessOptionPane(Game game, Player player, Estate estate){
        boolean guessing = (estate != null);
        eSelected = estate;

        final JPanel guessPanel = new JPanel();
        guessPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        final JPanel titlePanel = GUI.createTitlePanel("Make your "+(guessing ? "Guess: ("+eSelected+")" : "Solve Attempt"));
        final JPanel characterPanel = createCharacterPanel(game);
        final JPanel estatePanel = (guessing ? null : createEstatePanel(game));
        final JPanel weaponPanel = createWeaponPanel(game);


        c.gridwidth = GridBagConstraints.REMAINDER;
        guessPanel.add(titlePanel, c);
        guessPanel.add(characterPanel);
        if (!guessing) guessPanel.add(estatePanel);
        guessPanel.add(weaponPanel);


        JOptionPane.showMessageDialog(null, guessPanel);
        // Move GameCharacter and Weapon into Estate:
        if (guessing) {
            Player.moveToEstate(cSelected, eSelected);
            Player.moveToEstate(wSelected, eSelected);
            game.setChanged("Character and Weapon moved.");
        }
        player.confirmGuess(game, new HashSet<>(Arrays.asList(cSelected, eSelected, wSelected)), guessing);
    }



    private JPanel createCharacterPanel(Game game){
        final JPanel characterPanel = GUI.createBorderedPanel(true);
        characterPanel.setLayout(new BoxLayout(characterPanel, BoxLayout.Y_AXIS));
        characterPanel.add(new JLabel("Characters:"));
        List<GameCharacter> characters = new ArrayList<>(game.getCharacters());
        cSelected = characters.get(0);
        ButtonGroup group = new ButtonGroup();

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

    private JPanel createEstatePanel(Game game){
        final JPanel estatePanel = GUI.createBorderedPanel(true);
        estatePanel.setLayout(new BoxLayout(estatePanel, BoxLayout.Y_AXIS));
        estatePanel.add(new JLabel("Estates:"));
        List<Estate> estates = new ArrayList<>(game.getEstates());
        eSelected = estates.get(0);
        ButtonGroup group = new ButtonGroup();

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

    private JPanel createWeaponPanel(Game game){
        final JPanel weaponPanel = GUI.createBorderedPanel(true);
        weaponPanel.setLayout(new BoxLayout(weaponPanel, BoxLayout.Y_AXIS));
        weaponPanel.add(new JLabel("Weapons:"));
        List<Weapon> weapons = new ArrayList<>(game.getWeapons());
        wSelected = weapons.get(0);
        ButtonGroup group = new ButtonGroup();
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
