import java.awt.Color;
import java.awt.Graphics;

/**
 * EstateSquare is a subclass of Square, representing a square within an Estate.
 * They have different properties based on which part of the Estate they are:
 * (inside, outside, corner, entrance).
 *
 * @author johnh
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
     *
     * @param row row on board
     * @param col column on board
     * @param e Estate it is part of
     * @param entrance true if it is an entrance to its Estate
     */
  public EstateSquare(int row, int col, Estate e, boolean entrance){
    super(row, col);
    this.estate = e;
    this.entrance = entrance;
  }

    /**
     * Sets the index of this square in the Estate's contents.
     * Only used for inner squares, when displaying contents on board.
     * @param i index
     */
  public void setIndex(int i){ this.index = i; }

    /**
     * Sets the side of the Estate this square is on.
     * Only applies to outer squares.
     * @param s side of Estate
     */
  public void setSide(Estate.Side s){ this.side = s; }

    /**
     * Returns the side of the Estate this square is on.
     * Only applies to outer squares.
     * @return side of Estate
     */
  public Estate.Side getSide(){ return side; }

    /**
     * Sets a NormalSquare as the square directly outside this square.
     * Only applies if this square is an entrance.
     * @param s square directly outside this entrance
     */
  public void setOuterSquare(NormalSquare s){ this.outerSquare = s; }

    /**
     * Gets the square directly outside this square.
     * Only applies if this square is an entrance.
     * @return square directly outside this entrance.
     */
  public NormalSquare getOuterSquare(){ return outerSquare; }

    /**
     * Returns true if this entrance/exit is blocked by a character.
     * Only applies to entrances.
     * @return true if blocked by character.
     */
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

    @Override
    public String getDescription(){
        return estate.getDescription();
    }
  
  @Override
  public void drawSquare(Graphics g) { //TO DO: draw entrance and walls
	  char in = estate.initial;
	  //Color c = null;
	  switch (in) {
	  	case 'h':
	  		g.setColor(new Color(130,209,172));
	  		//c = new Color(130,209,172);
	  		break;
	  	case 'm':
	  		g.setColor(new Color(224,228,173));
	  		//c = new Color(224,228,173);
	  		break;
	  	case 'v':
	  		g.setColor(new Color(228,166,40));
	  		//c = new Color(228,166,40);
	  		break;
	  	case 'c':
	  		g.setColor(new Color(211,120,41));
	  		//c = new Color(211,120,41);
	  		break;
	  	default:
	  		g.setColor(new Color(123,91,83));
	  		//c = new Color(123,91,83);
	  }
	  g.fillRect((col*SIZE)+WALL, (row*SIZE)+WALL, SIZE, SIZE);
  }

    /**
     * Draws a dark rectangle on the board showing the wall of the Estate.
     * Is drawn on top of this EstateSquare.
     * @param g the Graphics object.
     */
    public void drawEstateSide(Graphics g) {
        if(side == null) return;
        g.setColor(new Color(36,36,36));
        int thickness = SIZE/4;
        switch(side) {
            case TOP:
                g.fillRect(((col*SIZE)+WALL) - SIZE, (row*SIZE)+WALL, SIZE*3, thickness);
                return;
            case BOTTOM:
                g.fillRect(((col*SIZE)+WALL) - SIZE, ((row*SIZE)+WALL) + (thickness*3), SIZE*3, thickness);
                return;
            case RIGHT:
                g.fillRect(((col*SIZE)+WALL) + (thickness*3), (row*SIZE)+WALL - SIZE, thickness, SIZE*3);
                return;
            default:
                g.fillRect(((col*SIZE)+WALL), (row*SIZE)+WALL - SIZE, thickness, SIZE*3);
        }
    }

    /**
     * Draws a brown rectangle on the board showing this is an entrance to the Estate.
     * It is drawn on top of this square.
     * @param g the Graphics object
     */
    public void drawEntrance(Graphics g) {
        if(side == null || !entrance) return;
        g.setColor(new Color(85,60,42));
        int thickness = SIZE/4;
        switch(side) {
            case TOP:
                g.fillRect(((col*SIZE)+WALL), (row*SIZE)+WALL, SIZE, thickness);
                return;
            case BOTTOM:
                g.fillRect(((col*SIZE)+WALL), ((row*SIZE)+WALL) + (thickness*3), SIZE, thickness);
                return;
            case RIGHT:
                g.fillRect(((col*SIZE)+WALL) + (thickness*3), (row*SIZE)+WALL, thickness, SIZE);
                return;
            default:
                g.fillRect(((col*SIZE)+WALL), (row*SIZE)+WALL, thickness, SIZE);
        }
    }


}
