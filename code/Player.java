import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Player describes a player in the game Murder Madness.
 * The class manages each Player's turn and the possible actions they can make.
 * Each Player has a number, character they control, and hand of cards.
 * Whether the Player has made a solve attempt is also stored to know if they're eliminated from the game.
 *
 * Player is the parent class of Computer.
 */
public class Player {

  /** The Player's number (from 1 to 4). */
  public final int number;
  /** The Player's personal nickname: */
  public final String nickname;
  /** The Player's GameCharacter that they control. */
  public final GameCharacter character;
  /** The Player's private collection of Cards dealt to them. */
  protected final Set<Card> hand = new HashSet<>();
  /** True once the Player has made a solve attempt to signify they're eliminated. */
  private boolean solveAttempted;

  private int movesLeft;

  private boolean endedTurn;

  /** Constructs a new Player with a specified number and character to control: */
  public Player(int number, String nickname, GameCharacter c){
    this.number = number;
    this.nickname = nickname;
    this.character = c;
  }

  public void addToHand(Card c){ hand.add(c); }
  /** Returns true if the Player has made a solve attempt. */
  public boolean solveAttempted(){ return solveAttempted; }



  public void startTurn(Game game, InputPanel input) {
    endedTurn = false;
    movesLeft = -1;
    game.setChanged(nickname + "'s turn (" + character + "):");

    addCheckButton(input);
    if (!character.inEstate()) addRollButton(game, input);
    else {
      JButton guess = addGuessButton(game, input);
      addLeaveButton(game, input, guess);
    }
    if (!solveAttempted) addSolveButton(game, input);
    addEndButton(input);


    while (!endedTurn) {
      Game.wait(10);
    }

    input.clearComponents();
  }

  private void addCheckButton(InputPanel input){
    JButton check = new JButton("Check Hand");
    check.addActionListener((event) -> {
      String handOutput = hand.stream().map(Card::toString).collect(Collectors.joining(", "));
      JOptionPane.showMessageDialog(new JFrame(), handOutput);
    });
    input.addComponent(check);
  }

  private void addRollButton(Game game, InputPanel input){
    JButton roll = new JButton("Roll Dice");
    roll.addActionListener((event) -> {
      movesLeft = rollDice();
      game.setChanged("You rolled: " + movesLeft + "\nMove with arrow keys.");
      input.removeComponent(roll);
    });
    input.addComponent(roll);
  }

  private void addLeaveButton(Game game, InputPanel input, JButton guess){
    JButton leave = new JButton("Leave Estate");
    leave.addActionListener((event) -> {
      leaveEstate(game, input, leave, guess);
    });
    input.addComponent(leave);
  }

  private JButton addGuessButton(Game game, InputPanel input){
    JButton guess = new JButton("Make Guess");
    guess.addActionListener((event) -> {
      guess(game);
    });
    input.addComponent(guess);
    return guess;
  }

  private void addSolveButton(Game game, InputPanel input){
    JButton solve = new JButton("Attempt Solve");
    solve.addActionListener((event) -> {
      solve(game);
    });
    input.addComponent(solve);
  }

  private void addEndButton(InputPanel input){
    JButton end = new JButton("End Turn");
    end.addActionListener((event) -> endedTurn = true);
    input.addComponent(end);
  }


  /**
   * Returns a random number from 2 to 12 (inclusive):
   */
  private static int rollDice(){
    return (int)(Math.random() * 11) + 2;
  }

  public void move(KeyEvent key, Game game, InputPanel input){
    if (movesLeft <= 0 || character.inEstate()) return;

    Square current = character.getSquare();
    int row = current.row;
    int col = current.col;
    switch (key.getKeyCode()) {
      case KeyEvent.VK_UP:
        row--;
        break;
      case KeyEvent.VK_LEFT:
        col--;
        break;
      case KeyEvent.VK_DOWN:
        row++;
        break;
      case KeyEvent.VK_RIGHT:
        col++;
        break;
      default:
        return;
    }

    if (row < 0 || row >= Board.ROWS || col < 0 || col >= Board.COLS) return;
    Square next = game.getBoard().getSquare(row, col);
    if (next.isBlocked()) return;

    // Finally move character to next square:
    character.moveToSquare(next);
    current.removeCharacter();
    next.setCharacter(character);

    movesLeft--;
    if (character.inEstate()){
      movesLeft = 0;
      game.setChanged("You Entered:\n"+character.getEstate());
      if (!solveAttempted) addGuessButton(game, input);
    }
    else {
      game.setChanged(movesLeft > 0 ? "Moves Left: "+movesLeft : "Out of moves!");
    }
  }



  /**
   * Lets the Player leave the Estate by choosing which exit to use.
   * Returns false if all exits are blocked:
   */
  private void leaveEstate(Game game, InputPanel input, JButton leaveButton, JButton guessButton){
    assert(character.inEstate());
    Estate e = character.getEstate();

    // Only add exits that aren't blocked:
    Map<String, EstateSquare> exits = new HashMap<>();
    for (EstateSquare s : e.getEntrances()){
      if (!s.isExitBlocked()){
        String side = s.getSide().toString();
        exits.put(side.charAt(0)+side.substring(1).toLowerCase(), s);
      }
    }

    // If all exits are blocked, just return:
    if (exits.isEmpty()){
      game.setChanged("All exits are blocked!");
      return;
    }
    addRollButton(game, input);
    input.removeComponent(leaveButton);
    input.removeComponent(guessButton);

    String[] buttons = exits.keySet().toArray(new String[0]);
    int index = JOptionPane.showOptionDialog(null, "Which exit would you like to use?", "Leaving Estate",
            JOptionPane.YES_NO_OPTION, 0, null, buttons, buttons[buttons.length-1]);

    // Wait for Player to choose an exit:
    NormalSquare outside = exits.get(buttons[index]).getOuterSquare();

    // Move Player outside:
    e.removeContents(character);
    outside.setCharacter(character);
    character.moveToSquare(outside);
    game.setChanged("You left the Estate.");
  }


  /**
   * Moves a Card to the specified Estate.
   * Used when a guess is made.
   */
  public static void moveToEstate(Card c, Estate e){
    // If already in an Estate, only move it if it's in a different one:
    if (c.inEstate()){
      Estate current = c.getEstate();
      if (current != e){
        current.removeContents(c);
        c.setEstate(e);
        e.addContents(c);
      }
    }
    // Else if not in an Estate, move it (only applies to GameCharacters):
    else {
      ((GameCharacter) c).getSquare().removeCharacter();
      c.setEstate(e);
      e.addContents(c);
    }
  }

  /**
   * Returns false if Player has no guess cards in their hand.
   * Else, they must choose one to reveal and it returns true.
   */
  protected boolean refute(Set<Card> guess, Player original){
    // Add guessed Cards to options if they're in hand:
    List<Card> options = new ArrayList<>();
    for (Card c : guess){
      if (hand.contains(c)) options.add(c);
    }

    // If none of the cards were found:
    if (options.isEmpty()) return false;

    // If they do have options, let them choose:
    String message = nickname+" ("+character+") has refutation card(s)! "+nickname+", when ready to choose, press OK.";
    JOptionPane.showMessageDialog(null, message);

    new RefuteOptionPane(options, this, original);
    return true;
  }

  private void guess(Game game){
    new GuessOptionPane(game, this, character.getEstate());
  }

  /**
   * Player attempts to solve the murder. Prompts player to pick 3 card names.
   * If guess is correct, Player wins!
   * Else Player is eliminated and can no longer guess or make solve attempts.
   */
  private void solve(Game game){
    solveAttempted = true;
    new GuessOptionPane(game, this, null);
  }

  public void confirmGuess(Game game, Set<Card> guess, boolean guessing){
    if (guessing){
      boolean refuted = false;
      for (int delta = 1; delta < 4; delta++){
        int i = (game.getCurrentPlayerIndex() + delta) % 4;
        Player p = game.getPlayers()[i];
        // If a refute was made:
        if(p.refute(guess, this)){
          refuted = true;
          break;
        }
      }
      // If the cards were not found:
      if (!refuted) JOptionPane.showMessageDialog(null, "No other Players have those cards!");
    }
    else { // making solve attempt:
      Set<Card> solution = game.getSolution();
      boolean won = true;
      for (Card c : guess){
        if (!solution.contains(c)){
          won = false;
          break;
        }
      }
      if (won){
        JOptionPane.showMessageDialog(new JFrame(), "Congratulations, you win!");
        game.endGame(true);
      }
      else {
        String solutionOutput = solution.stream().map(Card::toString).collect(Collectors.joining(", "));
        JOptionPane.showMessageDialog(new JFrame(), "You are eliminated. The solution was: "+solutionOutput);
        if (game.allPlayersEliminated()) game.endGame(false);
      }
    }
    endedTurn = true;
  }



  @Override
  public String toString(){
    String out = "Player "+number+" ("+character+"):\nCards: ";
    for (Card c : hand) out += c.toString()+", ";
    return out;
  }
}
