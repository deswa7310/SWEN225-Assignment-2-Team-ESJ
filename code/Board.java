import java.util.*;

/**
 * Board describes a board in the famous family game Murder Madness.
 * It is the main class of the game and contains all Squares, Players, and Cards,
 * along with managing the game state and turn loop.
 */
public class Board {

  /** Grid constants: */
  public static final int ROWS = 24;
  public static final int COLS = 24;

  /** Decorative lines for the interface: */
  public static final String LINE = "----------------------------------------------------------------------|";
  public static final String MAP_BORDER = "|===============================================|";

  /** Grid storing each of the boards Squares (by row then column): */
  private static final Square[][] grid = new Square[ROWS][COLS];

  /** Player fields: */
  private static final Player[] players = new Player[4];
  private static int currentPlayerIndex; // index of player whose turn it is

  /** Card Collections, with allCards keyed by name, and the other Maps keyed by symbol/initial: */
  private static final Map<String, Card> allCards = new HashMap<>();
  private static final Map<Character, GameCharacter> characters = new HashMap<>();
  private static final Map<Character, Estate> estates = new HashMap<>();
  private static final Map<Character, Weapon> weapons = new HashMap<>();

  /** Game ending fields: */
  private static final Set<Card> solution = new HashSet<>();
  private static boolean gameOver;

  /** Scanner for user input: */
  private static final Scanner input = new Scanner(System.in);


  /**
   * Sets up the Board for a new game:
   */
  private static void initBoard() {
    initCards();
    initSquares();
    initPlayers();
    wait(1000);
    dealCards();
    wait(1000);
    startGame();
  }

  /**
   * Creates all Cards and puts them in their collections:
   */
  private static void initCards(){
    // GameCharacters:
    for (GameCharacter.Name cn : GameCharacter.Name.values()) {
      String name = cn.toString();
      GameCharacter c = new GameCharacter(name);
      characters.put(c.initial, c);
      allCards.put(name.toLowerCase(), c);
    }

    // Estates:
    for (Estate.Name en : Estate.Name.values()) {
      String name = en.toString().replace('_', ' '); // replace underscore with space
      Estate e = new Estate(name);
      estates.put(e.initial, e);
      allCards.put(name.toLowerCase(), e);
    }

    // Weapons, also add each one to a random estate:
    List<Estate> estateList = new ArrayList<>(estates.values());
    Collections.shuffle(estateList); // randomizes order
    for (Weapon.Name wn : Weapon.Name.values()) {
      String name = wn.toString();
      Weapon w = new Weapon(name);
      weapons.put(w.initial, w);
      allCards.put(name.toLowerCase(), w);
      Estate e = estateList.get(wn.ordinal());
      e.addContents(w);
      w.setEstate(e);
    }
  }

  /**
   * Create all Squares that make up the Board and setup Estate
   * and GameCharacter positions:
   */
  private static void initSquares(){
    String startingBoard =
            "   0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3\n" +
            "00. . . . . . . . . . . . . . . . . . . . . . . . .\n" +
            "01. . . . . . . . . . . .L. . . . . . . . . . . . .\n" +
            "02. . .h.h.h.h.h. . . . . . . . . . .m.m.m.m.m. . .\n" +
            "03. . .h.h.h.h.e. . . . . . . . . . .m.m.m.m.m. . .\n" +
            "04. . .h.h.h.h.h. . . . . . . . . . .m.m.m.m.m. . .\n" +
            "05. . .h.h.h.h.h. . . . .x.x. . . . .e.m.m.m.m. . .\n" +
            "06. . .h.h.h.e.h. . . . .x.x. . . . .m.m.m.e.m. . .\n" +
            "07. . . . . . . . . . . . . . . . . . . . . . . . .\n" +
            "08. . . . . . . . . . . . . . . . . . . . . . . . .\n" +
            "09. .B. . . . . . . . . . . . . . . . . . . . . . .\n" +
            "10. . . . . . . . . .v.v.v.e.v.v. . . . . . . . . .\n" +
            "11. . . . . .x.x. . .v.v.v.v.v.e. . .x.x. . . . . .\n" +
            "12. . . . . .x.x. . .e.v.v.v.v.v. . .x.x. . . . . .\n" +
            "13. . . . . . . . . .v.v.e.v.v.v. . . . . . . . . .\n" +
            "14. . . . . . . . . . . . . . . . . . . . . . .P. .\n" +
            "15. . . . . . . . . . . . . . . . . . . . . . . . .\n" +
            "16. . . . . . . . . . . . . . . . . . . . . . . . .\n" +
            "17. . .c.e.c.c.c. . . . .x.x. . . . .p.e.p.p.p. . .\n" +
            "18. . .c.c.c.c.e. . . . .x.x. . . . .p.p.p.p.p. . .\n" +
            "19. . .c.c.c.c.c. . . . . . . . . . .p.p.p.p.p. . .\n" +
            "20. . .c.c.c.c.c. . . . . . . . . . .e.p.p.p.p. . .\n" +
            "21. . .c.c.c.c.c. . . . . . . . . . .p.p.p.p.p. . .\n" +
            "22. . . . . . . . . .M. . . . . . . . . . . . . . .\n" +
            "23. . . . . . . . . . . . . . . . . . . . . . . . .";

    String[] lines = startingBoard.split("\n");

    // Go through each row of the starting board:
    for (int row = 0; row < ROWS; row++) {
      String line = lines[row + 1];
      String[] tokens = line.split("\\.");

      // Go through each column in the row (columns are separated by '.'):
      for (int col = 0; col < COLS; col++) {
        char token = tokens[col + 1].toCharArray()[0];

        if (token == ' ') grid[row][col] = new NormalSquare(row, col); // normal squares are blank
        else if (token == 'x') grid[row][col] = new WallSquare(row, col); // wall squares are 'x'

        // Only Estate squares are lowercase (excluding 'x'):
        else if (Character.isLowerCase(token)){
          Estate e;

          // If the Estate square is an entrance:
          boolean entrance = false;
          if (token == 'e'){
            // Get its Estate by checking the squares to the left and above:
            if (grid[row][col-1] instanceof EstateSquare) e = ((EstateSquare) grid[row][col-1]).estate;
            else e = ((EstateSquare) grid[row-1][col]).estate;
            entrance = true;
          }
          else e = estates.get(token);

          EstateSquare s = new EstateSquare(row, col, e, entrance);
          if (entrance) e.addEntrance(s);
          grid[row][col] = s;
        }

        // Else it must be a GameCharacter:
        else {
          NormalSquare s = new NormalSquare(row, col);
          grid[row][col] = s;
          GameCharacter c = characters.get(token);
          s.setCharacter(c);
          c.moveToSquare(s);
        }
      }
    }

    // Finally, run through all Estate Squares to determine which are on the inside or outside.
    // If outside, also determine its side.
    for (int row = 0; row < ROWS; row++){
      for (int col = 0; col < COLS; col++){
        Square square = grid[row][col];
        if (!(square instanceof EstateSquare)) continue;
        EstateSquare s = (EstateSquare) square;
        Estate e = s.estate;

        int sides = 0; // on how many sides of the estate it is. 2 means it is a corner.
        Estate.Side side =  null; // if its only on one side, this will hold that side.
        Square outer = null; // the square directly outside an entrance.

        // Determine what side(s) the EstateSquare is on by checking if an adjacent one
        // isn't part of the Estate:
        if (!(grid[row][col-1] instanceof EstateSquare)){ // Left side
          sides++;
          side = Estate.Side.LEFT;
          outer = grid[row][col-1];
        }
        else if (!(grid[row][col+1] instanceof EstateSquare)){ // Right side
          sides++;
          side = Estate.Side.RIGHT;
          outer = grid[row][col+1];
        }
        if (!(grid[row-1][col] instanceof EstateSquare)){ // Top side
          sides++;
          side = Estate.Side.TOP;
          outer = grid[row-1][col];
        }
        else if (!(grid[row+1][col] instanceof EstateSquare)){ // Bottom side
          sides++;
          side = Estate.Side.BOTTOM;
          outer = grid[row+1][col];
        }

        // If square is an entrance, store the square directly outside of it.
        // This is useful for knowing if the entrance is blocked.
        if (s.entrance) s.setOuterSquare((NormalSquare)outer);

        switch (sides){
          case 0: // it is an inner square
            e.addInnerTile(s);
            break;
          case 1: // it is an outer square
            s.setSide(side);
            break;
        }
        // If it has 2 sides it is a corner, but this is already handled within the class.
      }
    }
  }

  /**
   * Asks user for number of Players and lets each Player choose their GameCharacter:
   */
  private static void initPlayers(){

    // Intro:
    System.out.println(LINE+"\nWelcome to Murder Madness!\n"+LINE);

    // Choose number of players:
    System.out.println("Please enter the number of players (3 or 4):");
    int number = -1;
    while (input.hasNext()){
      if (input.hasNextInt()) {
        number = input.nextInt();
        if (number == 3 || number == 4) break;
      }
      else input.nextLine();
      System.out.println("Please enter a valid integer (3 or 4):");
    }
    System.out.println("Number of players set to: "+number);
    if (number == 3) System.out.println("Player 4 will be played by the computer.");
    System.out.println();

    // Let players select characters:
    Map<Character, GameCharacter> availableCharacters = new HashMap<>(characters);
    for (int i = 0; i < 3; i++){
      String availableNames = "";
      for (GameCharacter c : availableCharacters.values()) availableNames += c + ", ";
      System.out.println("Available characters: "+availableNames);
      int playerNum = i+1;

      // Player must enter valid initial or name to select character:
      System.out.println("Player "+playerNum+": Please select a character by entering their name or initial...");
      while (input.hasNext()){
        String in = input.nextLine();

        if (!in.isEmpty()){
          char initial = Character.toUpperCase(in.charAt(0));

          if (availableCharacters.containsKey(initial)){
            GameCharacter c = availableCharacters.get(initial);

            if (in.length() == 1 || in.equalsIgnoreCase(c.toString())) {
              availableCharacters.remove(initial);
              int index = GameCharacter.Name.valueOf(c.toString()).ordinal();
              players[index] = new Player(playerNum, c);
              System.out.println(c+" chosen successfully!\n");
              break;
            }
          }
        }
        else continue;

        System.out.println("Please enter a valid name or initial:");
      }
    }

    // Player 4 or Computer must select the remaining character:
    GameCharacter c = availableCharacters.values().iterator().next();
    int index = GameCharacter.Name.valueOf(c.toString()).ordinal();
    Player p;
    if (number == 4) p = new Player(4, c);
    else p = new Computer(4, c);
    players[index] = p;
    System.out.println((number == 4 ? "Player 4" : "Computer") + " will play "+c+".\n");
  }

  /**
   * Randomly pick a solution, and shuffle all remaining Cards together
   * before dealing them to each Player:
   */
  private static void dealCards(){
    // Convert each card collection into a list so they're ordered, and add a random one to the solution:
    solution.add(new ArrayList<Card>(characters.values()).get((int)(Math.random() * characters.size())));
    solution.add(new ArrayList<Card>(estates.values()).get((int)(Math.random() * estates.size())));
    solution.add(new ArrayList<Card>(weapons.values()).get((int)(Math.random() * weapons.size())));

    System.out.println("Murder created. :)");

    // Make a list of all remaining cards:
    List<Card> remainingCards = new ArrayList<>(allCards.values());
    for (Card c : solution) remainingCards.remove(c);

    // Shuffle cards and deal them to players starting with a random player:
    Collections.shuffle(remainingCards);
    currentPlayerIndex = (int)(Math.random() * players.length);

    while (!remainingCards.isEmpty()){
      Card c = remainingCards.remove(remainingCards.size()-1);
      Player p = players[currentPlayerIndex];
      p.addToHand(c);
      changePlayer();
    }

    System.out.println("Cards dealt.");
  }


  /**
   * Starts and controls the main game loop:
   */
  private static void startGame(){
    // Randomly pick which player starts:
    currentPlayerIndex = (int)(Math.random() * players.length);

    System.out.println("Starting Game...");
    wait(1000);

    // Gameplay loop:
    while (!gameOver){
      displayBoard();
      Player next = players[currentPlayerIndex];
      next.startTurn(input, players, currentPlayerIndex, allCards, solution, grid);
      endTurn();
    }
  }

  /**
   * Changes player at the end of each turn.
   */
  private static void endTurn(){
    changePlayer();
  }

  /**
   * Sets the game over flag to true and outputs the winner (or that everyone was eliminated).
   */
  public static void endGame(boolean won){
    gameOver = true;
    System.out.println(LINE);
    if (won){
      Player p = players[currentPlayerIndex];
      System.out.println("Congratulations! Player "+p.number+" ("+p.character+") wins!");
    }
    else System.out.println("All players eliminated. GAME OVER.");
    System.out.println(LINE);
  }

  /**
   * Iterates to the next player index in the character order: Lucilla, Bert, Malina, Percy.
   */
  public static void changePlayer(){
    currentPlayerIndex++;
    if (currentPlayerIndex >= players.length) currentPlayerIndex = 0;
  }


  /**
   * Returns true if all players have made solve attempts.
   */
  public static boolean allPlayersEliminated(){
    for (Player p : players){
      if (!p.solveAttempted()) return false;
    }
    return true;
  }

  /**
   * Pauses the program for a specified amount of time:
   */
  public static void wait(int millis){
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e){
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Halts execution until the desired input is entered:
   * (Case insensitive).
   */
  public static void requireInput(String key){
    while (input.hasNext()){
      String in = input.nextLine();
      if (in.equalsIgnoreCase(key)) return;
    }
  }

  /**
   * Prints the board out to the console in text form:
   */
  public static void displayBoard(){
    StringBuilder output = new StringBuilder(LINE+"\nGame Board:\n"+LINE+"\n"+MAP_BORDER+"\n");

    // Set up key:
    List<String> key = new ArrayList<>();
    key.add("Key:");
    key.add("---------------------|");
    key.add("Characters:");
    for (GameCharacter c : characters.values()) key.add(c.initial+" = "+c);
    key.add("Estates:");
    for (Estate e : estates.values()) key.add(e.initial+" = "+e);
    key.add("Weapons:");
    for (char c : weapons.keySet()) key.add(c+" = "+weapons.get(c));
    key.add("Miscellaneous:");
    key.add("X = Obstacle");
    key.add("e = Estate Entrance");

    // Go through grid, printing each square row by row:
    for (int row = 0; row < ROWS; row++) {

      for (int col = 0; col < COLS; col++) {
        Square s = grid[row][col];
        output.append(s);

        // If estate square, don't put "." between squares:
        if (s instanceof EstateSquare){
          boolean onRight = (grid[row][col+1] instanceof EstateSquare);
          if (onRight) output.append(" ");
          else output.append(".");
        }
        else output.append(".");
      }
      if (row < key.size()) output.append(" ").append(key.get(row));
      output.append("\n");
    }
    System.out.println(output+MAP_BORDER);
  }

  public static void main(String... args){
    Board.initBoard();
  }
}
