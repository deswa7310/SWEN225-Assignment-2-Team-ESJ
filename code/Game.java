import java.awt.event.KeyEvent;
import java.util.*;

/**
 * Game class is the main class of Murder Madness, containing all game data and representing the current game state.
 */
public class Game extends Observable {

    /** Game board containing Square data. */
    private Board board;

    private final InputPanel input;

    /** Array of all 4 Players in turn order. */
    private Player[] players;
    /** Index of Player whose turn it is. */
    private int currentPlayerIndex;

    /** All Cards, keyed by name. */
    private final Map<String, Card> allCards = new HashMap<>();
    /** GameCharacter Cards keyed by symbol/initial. */
    private final Map<Character, GameCharacter> characters = new HashMap<>();
    /** Estate Cards keyed by symbol/initial. */
    private final Map<Character, Estate> estates = new HashMap<>();
    /** Weapon Cards keyed by symbol/initial. */
    private final Map<Character, Weapon> weapons = new HashMap<>();

    /** Set of Cards that make up the solution. */
    private final Set<Card> solution = new HashSet<>();
    /** Set to true when the game has ended. */
    private boolean gameOver;

    private boolean configuring;


    private Game(){
        input = new InputPanel();
        addObserver(new GUI(this, input));
        newGame();
    }

    public void setChanged(String text){
        setChanged();
        notifyObservers(text);
    }

    /**
     * Initializes all game elements (Cards, Players) for a new game:
     */
    public void newGame() {
        initCards();
        initBoard();
        promptConfigurations();
    }

    /**
     * Creates all Cards and puts them in their collections.
     */
    private void initCards(){
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
     * Initializes the Board holding all the Square data.
     */
    private void initBoard(){
        board = new Board(characters, estates);
    }

    private void promptConfigurations(){
        configuring = true;
        new GameConfigurationDialog(this);
        while (configuring){wait(10);}
        dealCards();
        startGame();
    }

    public void confirmConfigurations(Player[] players){
        this.players = players;
        this.configuring = false;
    }

    /**
     * Randomly pick a solution, and shuffle all remaining Cards together
     * before dealing them to each Player.
     */
    private void dealCards(){
        // Convert each card collection into a list so they're ordered, and add a random one to the solution:
        solution.add(new ArrayList<Card>(characters.values()).get((int)(Math.random() * characters.size())));
        solution.add(new ArrayList<Card>(estates.values()).get((int)(Math.random() * estates.size())));
        solution.add(new ArrayList<Card>(weapons.values()).get((int)(Math.random() * weapons.size())));

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
    }

    /**
     * Starts and controls the main game loop.
     */
    private void startGame(){
        // Randomly pick which player starts:
        currentPlayerIndex = (int)(Math.random() * players.length);

        setChanged();
        notifyObservers("Starting Game...");

        // Gameplay loop:
        while (!gameOver){
            Player next = players[currentPlayerIndex];
            next.startTurn(this, input);
            endTurn();
        }
    }

    public void keyPressed(KeyEvent e){
        if (players != null) players[currentPlayerIndex].move(e, this, input);
    }

    /**
     * Changes player at the end of each turn.
     */
    private void endTurn(){
        changePlayer();
    }

    /**
     * Sets the game over flag to true and outputs the winner (or that everyone was eliminated).
     */
    public void endGame(boolean won){
        gameOver = true;
        if (won){
            Player p = players[currentPlayerIndex];
            setChanged("Congratulations! "+p.nickname+" ("+p.character+") wins!");
        }
        else setChanged("All players eliminated. GAME OVER.");
    }

    /**
     * Iterates to the next player index in the character order: Lucilla, Bert, Malina, Percy.
     */
    public void changePlayer(){
        currentPlayerIndex++;
        if (currentPlayerIndex >= players.length) currentPlayerIndex = 0;
    }


    /**
     * Returns true if all players have made solve attempts.
     */
    public boolean allPlayersEliminated(){
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


    // Getters:
    public Board getBoard(){ return board; }
    public Player[] getPlayers(){ return players; }
    public int getCurrentPlayerIndex(){ return currentPlayerIndex; }
    public Collection<GameCharacter> getCharacters(){ return characters.values(); }
    public Collection<Estate> getEstates(){ return estates.values(); }
    public Collection<Weapon> getWeapons(){ return weapons.values(); }
    public Set<Card> getSolution(){ return Collections.unmodifiableSet(solution); }
    public boolean gameOver(){ return this.gameOver; }
    public boolean configuring(){ return this.configuring; }

    public static void main(String... args){
        new Game();
    }
}
