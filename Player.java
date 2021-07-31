import java.util.*;

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
  /** The Player's GameCharacter that they control. */
  public final GameCharacter character;
  /** The Player's private collection of Cards dealt to them. */
  protected final Set<Card> hand = new HashSet<>();
  /** True once the Player has made a solve attempt to signify they're eliminated. */
  private boolean solveAttempted;

  /** Constructs a new Player with a specified number and character to control: */
  public Player(int number, GameCharacter c){
    this.number = number;
    this.character = c;
  }

  public void addToHand(Card c){ hand.add(c); }
  /** Sets the solveAttempted flag once a Player has made a solve attempt. */
  public boolean solveAttempted(){ return solveAttempted; }

  /**
   * Starts and manages a Player's turn:
   * (Works similar to a state machine).
   */
  public void startTurn(Scanner input, Player[] players, int currentPlayerIndex,
                        Map<String, Card> allCards, Set<Card> solution, Square[][] grid){
    System.out.println("Player "+number+"'s turn ("+character+"):");
    System.out.println("Please enter \"start\" when you are ready.");
    Board.requireInput("start");

    // Add all possible starting commands:
    Map<String, String> commands = new HashMap<>(); // holds commands and descriptions

    commands.put("check", "check the cards in your hand."); // basic ones
    commands.put("end", "end turn.");
    if (!solveAttempted) commands.put("solve", "attempt solving the case!");

    if (character.inEstate()) { // If starting turn in an Estate:
      Estate e = character.getEstate();
      commands.put("leave", "leave "+e+".");
      if (!solveAttempted) commands.put("guess", "make a guess within "+e+".");
    }
    else commands.put("roll", "roll the dice.");


    // Handle turn operations within loop:
    int movesLeft = 0;
    String message = "Welcome, Player " + number + "! (" + character + ")";

    while (true) {
      Board.displayBoard();
      System.out.println(message);
      System.out.println("Available actions:");
      for (String command : commands.keySet())
        System.out.println(" - Enter \"" + command + "\" to " + commands.get(command));

      while (input.hasNext()) {
        String command = input.nextLine().toLowerCase(); // make commands case insensitive

        // If a valid command is entered:
        if (commands.containsKey(command)){

          // If it's a move:
          if (command.length() == 1){
            if (move(command, grid)){
              if (character.inEstate()) { // if just entered estate during move
                movesLeft = 0;
                Estate e = character.getEstate();
                message = "You ("+character+") entered "+e+".";
                if (!solveAttempted) commands.put("guess", "make a guess within "+e+".");
              }
              else { // if didn't enter estate
                movesLeft--;
                message = "Moved " + character + ". "+ (movesLeft == 0 ? "Out of moves!" : "Moves remaining: " + movesLeft);
              }
              // If out of moves, remove move commands:
              if (movesLeft == 0){
                commands.remove("w");
                commands.remove("a");
                commands.remove("s");
                commands.remove("d");
              }
              break;
            }
            // If unable to move:
            System.out.println("Movement blocked.");
            continue; // skip to next input
          }

          // If command is not a move:
          switch (command){
            case "check":
              checkHand();
              break;
            case "roll":
              commands.remove("roll");
              movesLeft = rollDice();
              message = "You ("+character+") rolled: "+movesLeft;
              commands.put("w", "move up.");
              commands.put("a", "move left.");
              commands.put("s", "move down.");
              commands.put("d", "move right.");
              break;
            case "leave":
              if (leaveEstate(input)) {
                commands.remove("leave");
                commands.remove("guess");
                commands.put("roll", "roll the dice.");
              }
              break;
            case "guess":
              guess(input, players, currentPlayerIndex, allCards);
              return;
            case "solve":
              attemptSolve(input, allCards, solution);
              return;
            case "end":
              return;
          }
          break;
        }
        System.out.println("Unrecognized action, please try again!");
      }
    }
  }


  /**
   * Displays the Cards in the Player's hand:
   */
  private void checkHand(){
    String out = "Your cards: ";
    for (Card c : hand) out += c+", ";
    System.out.println(out+"\nEnter \"done\" when finished looking...");
    Board.requireInput("done");
  }

  /**
   * Returns a random number from 2 to 12 (inclusive):
   */
  private static int rollDice(){
    return (int)(Math.random() * 11) + 2;
  }

  /**
   * Attempts to move the current Player in the specified key direction.
   * Returns true if successful.
   */
  private boolean move(String directionKey, Square[][] grid){
    Square current = character.getSquare();
    int row = current.row;
    int col = current.col;
    switch (directionKey){
      case "w":
        row--;
        break;
      case "a":
        col--;
        break;
      case "s":
        row++;
        break;
      case "d":
        col++;
        break;
      default:
        throw new IllegalArgumentException("Invalid direction key: "+directionKey);
    }
    if (row < 0 || row >= Board.ROWS || col < 0 || col >= Board.COLS) return false;
    Square next = grid[row][col];
    if (next.isBlocked()) return false;

    // Finally move character to next square:
    character.moveToSquare(next);
    current.removeCharacter();
    next.setCharacter(character);
    return true;
  }

  /**
   * Lets the Player leave the Estate by choosing which exit to use.
   * Returns false if all exits are blocked:
   */
  private boolean leaveEstate(Scanner input){
    assert(character.inEstate());
    Estate e = character.getEstate();

    // Only add exits that aren't blocked:
    Map<String, EstateSquare> exits = new HashMap<>();
    for (EstateSquare s : e.getEntrances()){
      if (!s.isExitBlocked()){
        exits.put(s.getSide().toString().toLowerCase(), s);
      }
    }

    // If all exits are blocked, just return false:
    if (exits.isEmpty()){
      System.out.println("All exits are blocked!");
      Board.wait(2000);
      return false;
    }

    // Print out all possible exits:
    System.out.println("Which exit would you like to use?");
    String out = "Enter a side: ";
    for (String side : exits.keySet()){
      out += "\""+side+"\", ";
    }
    System.out.println(out);

    // Wait for Player to choose an exit:
    NormalSquare outside = null;
    while (input.hasNext()){
      String in = input.nextLine().toLowerCase();
      if (exits.containsKey(in)){
        outside = exits.get(in).getOuterSquare();
        break;
      }
      System.out.println("Unrecognized input. Please try again:");
    }

    // Move Player outside:
    assert(outside != null);
    e.removeContents(character);
    outside.setCharacter(character);
    character.moveToSquare(outside);
    return true;
  }


  /**
   * Lets the Player guess the cards that might be in the solution.
   * A guess involves 3 cards; a GameCharacter, Estate, and Weapon.
   * Following Players must refute the guess if they can.
   */
  private void guess(Scanner input, Player[] players, int currentPlayerIndex, Map<String, Card> allCards){
    assert(character.inEstate());
    Estate e = character.getEstate();
    System.out.println();

    // Set up collections for this guess:
    Set<Card> guess = new HashSet<>(Collections.singletonList(e));
    Map<String, GameCharacter> characterMap = new HashMap<>();
    Map<String, Weapon> weaponMap = new HashMap<>();
    for (Card c : allCards.values()){
      if (c instanceof GameCharacter) characterMap.put(c.name.toLowerCase(), (GameCharacter) c);
      else if (c instanceof Weapon) weaponMap.put(c.name.toLowerCase(), (Weapon) c);
    }

    // Let Player pick a GameCharacter:
    System.out.println("Enter a Character name for your guess:");
    while (input.hasNext()){
      String in = input.nextLine().toLowerCase();
      if (characterMap.containsKey(in)){
        GameCharacter c = characterMap.get(in);
        guess.add(c);

        // Move GameCharacter to Estate:
        moveToEstate(c, c.getSquare(), e);
        c.setSquare(null);
        break;
      }
      System.out.println("Invalid name. Please try again:");
    }

    // Now pick a Weapon:
    System.out.println("Now enter a Weapon name for your guess:");
    while (input.hasNext()){
      String in = input.nextLine().toLowerCase();
      if (weaponMap.containsKey(in)){
        Weapon w = weaponMap.get(in);
        guess.add(w);

        // Move Weapon to Estate:
        moveToEstate(w, null, e);
        break;
      }
      System.out.println("Invalid name. Please try again:");
    }

    Board.wait(1000);

    // Output guess:
    String out = "Your guess: ";
    for (Card c : guess) out += c+", ";
    System.out.println(out+"\n");

    Board.wait(1000);

    // Cycle through other players:
    for (int delta = 1; delta < 4; delta++){
      int i = (currentPlayerIndex + delta) % 4;
      Player p = players[i];
      // If a refute was made:
      if(p.refute(input, guess, this)){
        System.out.println("Please enter \"done\" when finished looking:");
        Board.requireInput("done");
        return;
      }
      Board.wait(1000);
    }

    // If cards were not found:
    System.out.println("\nNo other players have those cards!");
    System.out.println("Please enter \"end\" to end turn:");
    Board.requireInput("end");
  }

  /**
   * Moves a Card to the specified Estate.
   * Used when a guess is made.
   */
  private void moveToEstate(Card c, Square s, Estate e){
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
      s.removeCharacter();
      c.setEstate(e);
      e.addContents(c);
    }
  }

  /**
   * Returns false if Player has no guess cards in their hand.
   * Else, they must choose one to reveal and it returns true.
   */
  protected boolean refute(Scanner input, Set<Card> guess, Player original){
    // Add guessed Cards to options if they're in hand:
    Map<String, Card> options = new HashMap<>();
    for (Card c : guess){
      if (hand.contains(c)) options.put(c.name.toLowerCase(), c);
    }

    // If none of the cards were found:
    if (options.isEmpty()){
      System.out.println("Player "+number+" ("+character+") has no refutation cards.");
      return false;
    }

    // If they do have options, let them choose:
    System.out.println("Player "+number+" ("+character+") has refutation card(s)!\n");
    System.out.println("Player "+number+", when ready to choose, please enter \"ready\":");
    Board.requireInput("ready");

    String out = "Enter the Card you wish to reveal: ";
    for (Card c : options.values()) out += "\""+c.toString().toLowerCase()+"\", ";
    System.out.println(out);
    Card c = null;
    while (input.hasNext()){
      String in = input.nextLine().toLowerCase();
      if (options.containsKey(in)){
        c = options.get(in);
        break;
      }
      System.out.println("Invalid name. Please try again:");
    }

    Board.wait(1000);
    System.out.println("You have chosen "+c+".\n");
    Board.wait(1000);

    // Finally, let original Player see it:
    Board.displayBoard();
    System.out.println("Please hand the device back to Player "+original.number+" ("+original.character+").");
    Board.wait(2000);
    System.out.println("Player "+number+" revealed: "+c);
    return true;
  }


  /**
   * Player attempts to solve the murder. Prompts player to enter 3 card names.
   * If guess is correct, Player wins!
   * Else Player is eliminated and can no longer guess or make solve attempts.
   */
  private void attemptSolve(Scanner input, Map<String, Card> allCards, Set<Card> solution){
    solveAttempted = true;
    System.out.println("Please enter the 3 card names, separated by commas:");
    while (input.hasNext()){
      String in = input.nextLine();
      String[] names = in.split(",");

      // If wrong number of names entered:
      if (names.length != 3){
        System.out.println("Invalid number of names entered ("+names.length+"). Please try again:");
        continue;
      }

      // Check if names are valid card names:
      Set<String> invalidNames = new HashSet<>();
      for (int i = 0; i < names.length; i++){
        String name = names[i].trim();
        names[i] = name.toLowerCase();
        if (!allCards.containsKey(names[i])) invalidNames.add(name);
      }

      // If there are invalid names:
      if (!invalidNames.isEmpty()){
        String out = "Invalid card names: ";
        for (String name : invalidNames) out += name+", ";
        System.out.println(out+"\nPlease try again:");
        continue;
      }

      // Check for duplicates:
      Set<String> uniqueNames = new HashSet<>(Arrays.asList(names));
      if (uniqueNames.size() != 3){
        System.out.println("Duplicate names entered. Please try again:");
        continue;
      }

      // Check if prediction matches solution:
      String predictionOut = "\nYou entered: ";
      boolean failed = false;
      for (String name : names){
        Card c = allCards.get(name.toLowerCase());
        predictionOut += c.toString() + ", ";
        if (!solution.contains(c)) failed = true;
      }
      System.out.println(predictionOut);

      String solutionOut = "The solution: ";
      for (Card c : solution) solutionOut += c.toString() + ", ";
      System.out.println(solutionOut+"\n");

      Board.wait(2000);

      // Determine output based on whether they solved it or failed:
      if (failed){
        System.out.println("Your prediction was wrong. You have been eliminated.");
        Board.wait(2000);
        if (Board.allPlayersEliminated()) Board.endGame(false);
        else {
          System.out.println("Please enter \"end\" to end turn:");
          Board.requireInput("end");
        }
      }
      else {
        System.out.println("Your prediction was right! You win.");
        Board.wait(2000);
        Board.endGame(true);
      }
      return;
    }
    throw new IllegalAccessError();
  }


  @Override
  public String toString(){
    String out = "Player "+number+" ("+character+"):\nCards: ";
    for (Card c : hand) out += c.toString()+", ";
    return out;
  }
}