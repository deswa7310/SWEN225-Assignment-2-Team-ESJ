import java.awt.Color;
import java.awt.Graphics;
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
 *
 * @author johnh
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
  /** The number of moves the Player has left. */
  private int movesLeft;
  /** True once the Player has made a guess or solve attempt, or chosen to end their turn. */
  private boolean endedTurn;

  /**
   * Constructs a new Player with specified number, nickname, and GameCharacter to control.
   *
   * @param number player number
   * @param nickname player's nickname they are referred to as
   * @param c character to be controlled
   */
  public Player(int number, String nickname, GameCharacter c){
    this.number = number;
    this.nickname = nickname;
    this.character = c;
  }

  /**
   * Adds a Card to the Player's hand.
   * @param c Card to add
   */
  public void addToHand(Card c){ hand.add(c); }
  
  /**
   * Returns the Player's hand of Cards.
   * @return unmodifiable Set of Cards in Player's hand
   */
  public Set<Card> getHand(){ return Collections.unmodifiableSet(hand); }

  /**
   * Eliminates the Player.
   */
  public void setSolveAttempted() { solveAttempted = true; }
  
  /**
   * Returns true if the Player has made a solve attempt.
   * @return if solve attempted
   */
  public boolean solveAttempted(){ return solveAttempted; }

  /**
   * Starts the Player's turn by presenting them with their available actions.
   * Adds a JButton for each available action to the input panel.
   *
   * @param game the current Game
   * @param input the InputPanel
   */
  public void startTurn(Game game, InputPanel input) {
    endedTurn = false;
    movesLeft = -1;
    game.setChanged(nickname + "'s turn (" + character + "):");

    // Add a JButton to the InputPanel for each available action:
    addCheckButton(game, input);
    if (!character.inEstate()) addRollButton(game, input);
    else {
      JButton guess = addGuessButton(game, input);
      addLeaveButton(game, input, guess);
    }
    if (!solveAttempted) addSolveButton(game, input);
    addEndButton(input);

    // Wait until the Player has ended their turn:
    while (!endedTurn) {
      Game.wait(10);
    }

    // Reset available actions for the next Player's turn.
    input.clearComponents();
  }

  /**
   * Adds a JButton that displays the Player's hand when pressed.
   * @param game the current Game
   * @param input the InputPanel
   */
  private void addCheckButton(Game game, InputPanel input){
    JButton check = new JButton("Toggle Hand");
    check.addActionListener((event) -> {
      game.setChanged("Toggled hand display...");
    });
    input.addComponent(check);
  }

  /**
   * Adds a JButton that rolls the dice and sets the Player's movesLeft when pressed.
   * @param game the current Game
   * @param input the InputPanel
   */
  private void addRollButton(Game game, InputPanel input){
    JButton roll = new JButton("Roll Dice");
    roll.addActionListener((event) -> {
      rollDice();
      game.setChanged("You rolled: " + movesLeft + "\nMove with arrow keys.");
      input.removeComponent(roll);
    });
    input.addComponent(roll);
  }

  /**
   * Adds a JButton that lets the Player leave the Estate if they're in one.
   * @param game the current Game
   * @param input the InputPanel
   * @param guess the JButton used to make a guess
   */
  private void addLeaveButton(Game game, InputPanel input, JButton guess){
    JButton leave = new JButton("Leave Estate");
    leave.addActionListener((event) -> {
      leaveEstate(game, input, leave, guess);
    });
    input.addComponent(leave);
  }

  /**
   * Adds a JButton that lets the Player make a guess.
   * @param game the current Game
   * @param input the InputPanel
   * @return the guess JButton
   */
  private JButton addGuessButton(Game game, InputPanel input){
    JButton guess = new JButton("Make Guess");
    guess.addActionListener((event) -> {
      guess(game);
    });
    input.addComponent(guess);
    return guess;
  }

  /**
   * Adds a JButton that lets the Player make a solve attempt.
   * @param game the current Game
   * @param input the InputPanel
   */
  private void addSolveButton(Game game, InputPanel input){
    JButton solve = new JButton("Attempt Solve");
    solve.addActionListener((event) -> {
      solve(game);
    });
    input.addComponent(solve);
  }

  /**
   * Adds a JButton that lets the Player end turn without making a guess or solve attempt.
   * @param input the InputPanel
   */
  private void addEndButton(InputPanel input){
    JButton end = new JButton("End Turn");
    end.addActionListener((event) -> endedTurn = true);
    input.addComponent(end);
  }


  /**
   * Sets movesLeft to a random number from 2 to 12 (inclusive):
   */
  public void rollDice(){
    movesLeft = (int)(Math.random() * 11) + 2;
  }


  /**
   * Moves the Player by one Square in the direction specified by the key.
   * @param key the KeyEvent of the key pressed
   * @param game the current Game
   * @param input the InputPanel
   */
  public void move(KeyEvent key, Game game, InputPanel input){
    if (movesLeft <= 0 || character.inEstate()) return;

    Square current = character.getSquare();
    int row = current.row;
    int col = current.col;
    // Get the next Square position using the direction of the movement:
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
    // If they entered an Estate, they can make a guess (if not eliminated):
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
   * Lets the Player leave the Estate they're in by choosing an exit to use.
   *
   * @param game the current Game
   * @param input the InputPanel
   * @param leaveButton the leave action button, removed from InputPanel if leave successful
   * @param guessButton the guess action button, removed from InputPanel if leave successful
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

    // If leaving is possible, update available actions:
    addRollButton(game, input);
    input.removeComponent(leaveButton);
    input.removeComponent(guessButton);

    // Wait for Player to choose an exit:
    String[] buttons = exits.keySet().toArray(new String[0]);
    int index = JOptionPane.showOptionDialog(null, "Which exit would you like to use?", "Leaving Estate",
            JOptionPane.YES_NO_OPTION, 0, null, buttons, buttons[buttons.length-1]);

    // Get square outside exit:
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
   *
   * @param c Card to be moved.
   * @param e Estate the Card is being moved into.
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
   *
   * @param guess the Set of Cards in the guess
   * @param original the Player that made the guess
   * @return true if refute can be made
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

  /**
   * Lets the Player make a guess by popping up a new GuessOptionPane.
   *
   * @param game the current Game
   */
  private void guess(Game game){
    new GuessOptionPane(game, this, character.getEstate());
  }

  /**
   * Player attempts to solve the murder. Pops up a new GuessOptionPane.
   * If guess is correct, Player wins!
   * Else Player is eliminated and can no longer guess or make solve attempts.
   *
   * @param game the current Game
   */
  private void solve(Game game){
    solveAttempted = true;
    new GuessOptionPane(game, this, null);
  }

  /**
   * Called when a the Cards involved in a guess or solve attempt have been confirmed.
   * If it was a guess, lets Players refute.
   * If it was a solve attempt, eliminates Player if wrong and ends game if right or all eliminated.
   * Ends turn either way.
   *
   * @param game the current Game
   * @param guess the Set of Cards selected for the guess
   * @param guessing true if a guess, false if a solve attempt
   */
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
  
  /**
   * Draws the Player's hand of Cards onto the DrawPanel.
   * @param g the Graphics object
   */
  public void drawHand(Graphics g) {
	  int index = 0;
	  int size = Square.SIZE*6;
	  int arcSize = Square.SIZE;
	  int gap = Square.SIZE;
	  int y = (16*Square.SIZE)+Square.WALL;
	  Color detailColor = new Color(36,36,36);

	  for(Card c: hand) {
		  int x = (index*size) + Square.WALL + gap;
		  String name = c.name;
		  String type = "";
		  Color color = null;
		  if(c instanceof Estate) {
			  type = "ESTATE";
			  color = new Color(118,117,170);
		  } else if(c instanceof GameCharacter) {
			  type = "CHARACTER";
			  color = new Color(163,190,156);
		  } else {
			  type = "WEAPON";
			  color = new Color(177,131,128);
		  }

		  g.setColor(detailColor);
		  g.fillRoundRect(x, y, size-gap, size, arcSize, arcSize);
		  g.setColor(color);
		  g.fillRoundRect(x+5, y+5, size-gap-10, size-10, arcSize, arcSize);

		  g.setColor(detailColor);
		  g.drawString(type, x+Square.SIZE, y+Square.SIZE);
		  g.drawString(name, x+Square.SIZE, y+(Square.SIZE*3));
		  index++;
	  }
  }


  @Override
  public String toString(){
    String out = "Player "+number+" ("+character+"):\nCards: ";
    for (Card c : hand) out += c.toString()+", ";
    return out;
  }
}
