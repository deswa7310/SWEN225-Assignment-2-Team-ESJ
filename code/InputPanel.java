import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * InputPanel is a custom JPanel.
 * It is used as a container to hold a JButton for each available action of the current Player.
 *
 * @author johnh
 */
public class InputPanel extends JPanel {

    /** The inner JPanel that contains the buttons. */
    private final JPanel inputContainer;

    /**
     * Constructs a new InputPanel for holding JButtons:
     */
    public InputPanel(){
        setFocusable(true);
        setMaximumSize(getSize());
        setPreferredSize(getSize());
        setBackground(new Color(36,36,36));
        inputContainer = new JPanel();
        inputContainer.setMaximumSize(getSize());
        inputContainer.setBackground(new Color(69, 69, 69));

        // Creates a custom stylized border:
        Border b = BorderFactory.createEmptyBorder(15, 15, 15, 15);
        b = BorderFactory.createCompoundBorder(b, BorderFactory.createLoweredBevelBorder());
        b = BorderFactory.createCompoundBorder(b, BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBorder(b);

        b = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        inputContainer.setBorder(b);
        inputContainer.setLayout(new BoxLayout(inputContainer, BoxLayout.Y_AXIS));
        setLayout(new BorderLayout());

        // Adds a JScrollPane to allow scrolling if there are too many buttons in the panel:
        JScrollPane scrollPane = new JScrollPane(inputContainer);
        scrollPane.setMaximumSize(getSize());
        scrollPane.getViewport().setBackground(new Color(69, 69, 69));
        add(scrollPane);
    }

    /**
     * Adds a component to the input panel:
     * @param component component to add
     */
    public void addComponent(JComponent component){
        component.setAlignmentX(CENTER_ALIGNMENT);
        component.setFocusable(false);
        inputContainer.add(component);
        inputContainer.add(Box.createRigidArea(new Dimension(15, 15))); // add an invisible box under for padding
        revalidate();
    }


    /**
     * Removes a component from the input panel:
     * @param c component to remove
     */
    public void removeComponent(Component c){
        assert(c != null);
        // Get component index in input panel:
        int index = -1;
        for (int i = 0; i < inputContainer.getComponentCount(); i++) {
            if (c == inputContainer.getComponent(i)){
                index = i;
                break;
            }
        }
        assert(index != -1);
        inputContainer.remove(index);
        inputContainer.remove(index); // second call removes the invisible box used under it for padding
        revalidate();
    }

    /**
     * Removes all components in the input panel:
     */
    public void clearComponents(){
        inputContainer.removeAll();
        revalidate();
    }
}
