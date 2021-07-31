import java.util.*;

/**
 * Computer is a subclass of Player describing an autonomous Player.
 * Upon its turn it always ends turn.
 * When refuting, it always chooses a random Card to reveal (if it has one).
 */
public class Computer extends Player {

    /**
     * Constructs a new Computer with a specified number and character to control.
     */
    public Computer(int number, GameCharacter c) {
        super(number, c);
    }

    /**
     * Just ends turn upon it starting.
     */
    @Override
    public void startTurn(Scanner input, Player[] players, int currentPlayerIndex,
                          Map<String, Card> allCards, Set<Card> solution, Square[][] grid){
        System.out.println("Computer's turn ("+character+"):");
        Board.wait(2000);
        System.out.println("Computer has ended turn.");
        Board.wait(2000);
    }

    /**
     * Returns false if Computer has no guess cards in their hand.
     * Else, Computer chooses a random one to reveal and it returns true.
     */
    @Override
    protected boolean refute(Scanner input, Set<Card> guess, Player original){
        // Add guessed Cards to options if they're in hand:
        List<Card> options = new ArrayList<>();
        for (Card c : guess){
            if (hand.contains(c)) options.add(c);
        }

        // If none of the cards were found:
        if (options.isEmpty()){
            System.out.println("Computer ("+character+") has no refutation cards.");
            return false;
        }

        // If they do have options, let them choose a random one:
        System.out.println("Computer ("+character+") has refutation card(s)!\n");


        Board.wait(1000);
        System.out.println("Computer revealed: "+options.get((int)(Math.random() % options.size())));
        Board.wait(1000);
        return true;
    }
}
