import java.util.*;

/**
 * Estate is a subclass of Card, describing an estate in Murder Madness.
 * It can contain other Cards (contents) and be moved in and out of by GameCharacters via entrances.
 * Every Estate is represented by a group of EstateSquares, shown in the text display.
 *
 * The Estate's contents changes as Cards move in and out of it, and is displayed via the inner EstateSquares.
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

  /** All Estate names. */
  public enum Name {
    Haunted_House, Manic_Manor, Villa_Celia, Calamity_Castle, Peril_Palace
  }

  /** All sides of an Estate. */
  public enum Side {
    LEFT, TOP, RIGHT, BOTTOM
  }

  /**
   * Constructs a new Estate with specified name and initial (first letter, lower case).
   */
  public Estate(String name){
    super(name, Character.toLowerCase(name.charAt(0)));
  }

  /** Adds an inner tile to the Estate that can hold a card: */
  public void addInnerTile(EstateSquare s){
    s.setIndex(capacity);
    capacity++;
  }

  /** Adds an entrance to the Estate that Players can enter or leave via: */
  public void addEntrance(EstateSquare s){ entrances.add(s); }
  public Set<EstateSquare> getEntrances(){ return Collections.unmodifiableSet(entrances); }

  public void addContents(Card c){ contents.add(c); }
  public void removeContents(Card c){ contents.remove(c); }
  /** Gets the Card within the Estate's contents at the specified index: */
  public Card getContents(int index){ return (index < contents.size() ? contents.get(index) : null); }
}
