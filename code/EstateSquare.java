/**
 * EstateSquare is a subclass of Square, representing a square within an Estate.
 * They have different properties based on which part of the Estate they are:
 * (inside, outside, corner, entrance).
 */
public class EstateSquare extends Square {

  /** The Estate that this square is a part of. */
  public final Estate estate;
  /** True if this square is an entrance to its Estate. */
  public final boolean entrance;
  /**
   * The index of this square within the Estate.
   * Used when displaying an Estate's contents (only applies to inner squares).
   */
  private int index = -1;
  /**
   * The side of the Estate this square is on.
   * Only used by outer squares (including entrances).
   */
  private Estate.Side side;
  /** The square directly outside this entrance (only applies to entrances). */
  private NormalSquare outerSquare;


  /**
   * Constructs a new EstateSquare with the specified position, Estate to be a part of,
   * and whether it is an entrance to the Estate.
   */
  public EstateSquare(int row, int col, Estate e, boolean entrance){
    super(row, col);
    this.estate = e;
    this.entrance = entrance;
  }

  public void setIndex(int i){ this.index = i; }
  public void setSide(Estate.Side s){ this.side = s; }
  public Estate.Side getSide(){ return side; }
  public void setOuterSquare(NormalSquare s){ this.outerSquare = s; }
  public NormalSquare getOuterSquare(){ return outerSquare; }
  public boolean isExitBlocked(){
    assert(entrance);
    return outerSquare.isBlocked();
  }

  @Override
  public boolean isBlocked(){ return !entrance; }

  @Override
  public void setCharacter(GameCharacter c){
    if (!entrance) throw new IllegalAccessError();
    estate.addContents(c);
  }

  @Override
  public String toString(){
    // If entrance, return e:
    if (entrance) return "e";
    // If side of estate, return line:
    if (side != null){
      switch (side){
        case LEFT:
        case RIGHT:
          return "|";
        default:
          return "-";
      }
    }
    // If corner of estate, return initial:
    if (index == -1) return String.valueOf(estate.initial);
    // Else if inside, return estate contents at that index, or blank if no contents:
    Card c = estate.getContents(index);
    if (c == null) return " ";
    return String.valueOf(c.initial);
  }
}
