import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Computer is a subclass of Player describing an autonomous Player.
 * Upon its turn it always ends turn.
 * When refuting, it always chooses a random Card to reveal (if it has one).
 *
 * @author johnh
 */
public class Computer extends Player {

    /**
     * Constructs a new Computer with a specified player number and character
     * to control.
     *
     * @param number player number
     * @param c character to control
     */
    public Computer(int number, GameCharacter c) {
        super(number, "Computer", c);
    }

    /**
     * Just ends turn upon it starting.
     */
    @Override
    public void startTurn(Game game, InputPanel input){
        game.setChanged("Computer's turn ("+character+"):");
        Game.wait(2000);
        game.setChanged("Computer has ended turn.");
        Game.wait(2000);
    }

    /**
     * Returns false if Computer has no guess cards in their hand.
     * Else, Computer chooses a random one to reveal and it returns true.
     */
    @Override
    protected boolean refute(Set<Card> guess, Player original){
        // Add guessed Cards to options if they're in hand:
        List<String> options = guess.stream().map(Card::toString).collect(Collectors.toList());

        // If none of the cards were found:
        if (options.isEmpty()) return false;

        // If they do have options, let them choose a random one:
        String message = "Computer ("+character+") has refutation card(s)!\nThey revealed: "+options.get((int)(Math.random() % options.size()));
        JOptionPane.showMessageDialog(null, message);
        return true;
    }
}
