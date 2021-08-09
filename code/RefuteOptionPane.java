import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RefuteOptionPane {


    public RefuteOptionPane(List<Card> options, Player refuting, Player original){

        final JPanel refutePanel = new JPanel();
        refutePanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        final JPanel titlePanel = GUI.createTitlePanel("Choose a Card to Refute:");

        String[] names = new String[options.size()];
        for (int i = 0; i < options.size(); i++){
            names[i] = options.get(i).toString();
        }
        final JComboBox<String> cb = new JComboBox<>(names);
        cb.setSelectedItem(names[0]);

        final JPanel optionPanel = createOptionPanel(cb);

        c.gridwidth = GridBagConstraints.REMAINDER;
        refutePanel.add(titlePanel, c);
        c.gridwidth = GridBagConstraints.REMAINDER;
        refutePanel.add(optionPanel, c);

        JOptionPane.showMessageDialog(null, refutePanel);
        String message = "Please hand the device back to "+original.nickname+" ("+original.character+").\n";
        message += refuting.nickname+" ("+refuting.character+") revealed: "+cb.getSelectedItem();
        JOptionPane.showMessageDialog(null, message);
    }

    private JPanel createOptionPanel(JComboBox<String> cb){

        final JPanel optionPanel = GUI.createBorderedPanel(true);

        optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.Y_AXIS));
        optionPanel.add(new JLabel("Options:"));
        optionPanel.add(cb);

        return optionPanel;
    }

}
