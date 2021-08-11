import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * RefuteOptionPane is a custom JOptionPane that uses a JComboBox to allow a refuting
 * Player to choose which Card they would like to refute.
 *
 * @author johnh
 */
public class RefuteOptionPane {

    /**
     * Constructs a new RefuteOptionPane that pops up letting a Player choose their card
     * to refute.
     *
     * @param options the List of Cards that can be refuted
     * @param refuting the refuting Player
     * @param original the Player that made the guess
     */
    public RefuteOptionPane(List<Card> options, Player refuting, Player original){

        // Create main panel:
        final JPanel refutePanel = new JPanel();
        refutePanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // Create title panel:
        final JPanel titlePanel = GUI.createTitlePanel("Choose a Card to Refute:");

        // Create JComboBox with options:
        String[] names = new String[options.size()];
        for (int i = 0; i < options.size(); i++){
            names[i] = options.get(i).toString();
        }
        final JComboBox<String> cb = new JComboBox<>(names);
        cb.setSelectedItem(names[0]);

        // Create option panel with combo box in it:
        final JPanel optionPanel = createOptionPanel(cb);

        // Position and add panels to main panel:
        c.gridwidth = GridBagConstraints.REMAINDER;
        refutePanel.add(titlePanel, c);
        c.gridwidth = GridBagConstraints.REMAINDER;
        refutePanel.add(optionPanel, c);

        // Activate popups using panels and a message displaying their selected Card:
        JOptionPane.showMessageDialog(null, refutePanel);
        String message = "Please hand the device back to "+original.nickname+" ("+original.character+").\n";
        message += refuting.nickname+" ("+refuting.character+") revealed: "+cb.getSelectedItem();
        JOptionPane.showMessageDialog(null, message);
    }

    /**
     * Creates a new JPanel containing the JComboBox with all the Card options:
     * @param cb the combo box containing Card options
     * @return the option panel
     */
    private JPanel createOptionPanel(JComboBox<String> cb){

        final JPanel optionPanel = GUI.createBorderedPanel(true);

        optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.Y_AXIS));
        optionPanel.add(new JLabel("Options:"));
        optionPanel.add(cb);

        return optionPanel;
    }

}
