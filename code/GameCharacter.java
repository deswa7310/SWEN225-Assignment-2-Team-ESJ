/**
 * GameCharacter is a subclass of Card and represents a playable character in Murder Madness.
 * They can move onto certain Squares and into Estates.
 */
public class GameCharacter extends Card {

  /** The Square this character is currently on. */
  private Square square;

  /** All GameCharacter names: */
  public enum Name {
    Lucilla, Bert, Malina, Percy
  }

  /** Constructs a new GameCharacter with the specified name and initial (first character, upper case). */
  public GameCharacter(String name){
    super(name, name.charAt(0));
  }

  /**
   * Moves the GameCharacter to a square.
   * If it is an EstateSquare, make square null and set the estate instead.
   */
  public void moveToSquare(Square square){
    if (square instanceof EstateSquare){
      EstateSquare s = (EstateSquare) square;
      setEstate(s.estate);
      this.square = null;
    }
    else {
      leaveEstate();
      this.square = square;
    }
  }

  /** Directly sets the GameCharacter's square. Used during guesses. */
  public void setSquare(Square square){ this.square = square; }
  public Square getSquare(){ return square; }

}
