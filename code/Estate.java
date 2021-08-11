import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Estate is a subclass of Card, describing an estate in Murder Madness.
 * It can contain other Cards (contents) and be moved in and out of by GameCharacters via entrances.
 * Every Estate is represented by a group of EstateSquares.
 *
 * The Estate's contents changes as Cards move in and out of it, and is displayed via the inner EstateSquares.
 *
 * @author johnh
 */
public class Estate extends Card {

  /** A Set of EstateSquares that can be used by GameCharacters to enter or exit the Estate. */
  private final Set<EstateSquare> entrances = new HashSet<>();
  /** The cards currently stored within this Estate. Changes as GameCharacters move in/out and guesses are made. */
  private final List<Card> contents = new ArrayList<>();
  /**
   * The number of inner EstateSquares in the Estate and hence how the maximum
   * number of Cards from its contents that can be displayed.
   */
  private int capacity = 0;

  /** The offset for the left/top of the board display. */
  public static final int SIDE_OFFSET = (Square.SIZE/4) + 4;

  /** All Estate names. */
  public enum Name {
	Haunted_House, Manic_Manor, Villa_Celia, Calamity_Castle, Peril_Palace
  }

  /** All sides of an Estate. */
  public enum Side {
	LEFT, TOP, RIGHT, BOTTOM
  }

	/**
	 * Creates a new Estate with the specified name.
	 *
	 * @param name estate's name
	 */
	public Estate(String name){
	super(name, Character.toLowerCase(name.charAt(0)));
	}

	/**
	 * Adds an inner square to the Estate that can hold a card.
	 *
	 * @param s inner square that can hold contents
	 */
  	public void addInnerTile(EstateSquare s){
		s.setIndex(capacity);
		capacity++;
	  }

	/**
	 * Adds an entrance to the Estate that characters can enter/exit.
	 * @param s entrance square
	 */
  	public void addEntrance(EstateSquare s){ entrances.add(s); }

	/**
	 * Returns a set of all entrance squares.
	 * @return an unmodifiable set of entrance squares for this Estate.
	 */
	public Set<EstateSquare> getEntrances(){ return Collections.unmodifiableSet(entrances); }

	/**
	 * Adds a Card to the contents of this estate.
	 * @param c Card to add
	 */
	public void addContents(Card c){ contents.add(c); }

	/**
	 * Removes a Card from the contents of this estate.
	 * @param c Card to remove
	 */
  public void removeContents(Card c){ contents.remove(c); }

	/**
	 * Gets the Card at the specified index within this estate's contents.
	 * @param index index of Card
	 * @return Card at index in contents, else null
	 */
	public Card getContents(int index){ return (index < contents.size() ? contents.get(index) : null); }

	/**
	 * Returns a description of this estate.
	 * @return description.
	 */
  public String getDescription(){
  	return name + " estate. Contents: " + contents.stream().map(Card::toString).collect(Collectors.joining(", "));
  }
  
  /**
   * Gets column positioning of contents. Used when drawn.
   * @return column on board grid
   */
  public int leftCol() {
	  switch(initial) {
	  	case 'h':	
	  	case 'c':
	  		return 2;
	  	case 'p':
	  	case 'm':
	  		return 17;
	  	default:
	  		return 9;
	  }
  }
  
  /**
   * Gets row positioning of characters. Used when drawn.
   * @return row on board grid
   */
  public int charRow() {
	  switch(initial) {
	  	case 'h':	
	  	case 'm':
	  		return 4;
	  	case 'p':
	  	case 'c':
	  		return 19;
	  	default:
	  		return 11;
	  }
  }
  
  /**
   * Gets row positioning of weapons. Used when drawn.
   * @return row on board grid
   */
  public int weaponRow() {
	  switch(initial) {
	  	case 'h':	
	  	case 'm':
	  		return 5;
	  	case 'p':
	  	case 'c':
	  		return 20;
	  	default:
	  		return 12;
	  }
  }
  
  /**
   * Displays the contents of the estate.
   * @param g the Graphics object.
   */
  public void drawEstateContents(Graphics g) {
	  if(contents.size() == 0) return;
	  Set<GameCharacter> characters = new HashSet<>();
	  Set<Weapon> weapons = new HashSet<>();

	  // Separate characters and weapons in contents:
	  for(Card card : contents) {
		  if(card instanceof GameCharacter) {
			  characters.add((GameCharacter) card);
		  } else if(card instanceof Weapon){
			  weapons.add((Weapon) card);
		  }
	  }
	  // Draw characters:
	  if(!characters.isEmpty()) {
		  int index = 0;
		  for(GameCharacter c : characters) {
			  int x = leftCol()+index;
			  c.drawCharToken(g, x, charRow(),SIDE_OFFSET);
			  index++;
		  }
	  }
	  // Draw weapons:
	  if(!weapons.isEmpty()) {
		  int index = 0;
		  for(Weapon w : weapons) {
			  int x = leftCol()+index;
			  w.drawWeapon(g, x, weaponRow(),SIDE_OFFSET);
			  index++;
		  }
	  }
  }

	/**
	 * Displays the name of the estate on the board.
	 * @param g the Graphics object
	 */
	public void drawEstateName(Graphics g) {
		float fSize = 15.0f;
		g.setFont(g.getFont().deriveFont(fSize));
		g.setColor(new Color(36,36,36));
		int y = (charRow()*Square.SIZE)-Square.SIZE+Square.WALL;
		if (initial == 'v') y += Square.SIZE;
		g.drawString(name, (leftCol()*Square.SIZE)+Square.WALL+SIDE_OFFSET, y);
	}
}
