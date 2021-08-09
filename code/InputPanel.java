import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class InputPanel extends JPanel {

    private final Game game;

    private final JPanel inputContainer;


    public InputPanel(Game game){
        this.game = game;
        setFocusable(true);
        setMaximumSize(getSize());
        setPreferredSize(getSize());
        setBackground(new Color(36,36,36));
        inputContainer = new JPanel();
        inputContainer.setMaximumSize(getSize());
        inputContainer.setBackground(new Color(69, 69, 69));

        Border b = BorderFactory.createEmptyBorder(15, 15, 15, 15);
        b = BorderFactory.createCompoundBorder(b, BorderFactory.createLoweredBevelBorder());
        b = BorderFactory.createCompoundBorder(b, BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBorder(b);

        b = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        inputContainer.setBorder(b);
        inputContainer.setLayout(new BoxLayout(inputContainer, BoxLayout.Y_AXIS));
        setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(inputContainer);
        scrollPane.setMaximumSize(getSize());
        scrollPane.getViewport().setBackground(new Color(69, 69, 69));
        add(scrollPane);
    }

    public void addComponent(JComponent component){
        component.setAlignmentX(CENTER_ALIGNMENT);
        component.setFocusable(false);
        inputContainer.add(component);
        inputContainer.add(Box.createRigidArea(new Dimension(15, 15)));
        revalidate();
    }


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
        inputContainer.remove(index);
        revalidate();
    }

    public void clearComponents(){
        inputContainer.removeAll();
        revalidate();
    }
}
