import java.awt.*;

/**
 * GameCharacter is a subclass of Card and represents a playable character in Murder Madness.
 * They can move onto certain Squares and into Estates.
 *
 * @author johnh
 */
public class GameCharacter extends Card {

  /** The Square this character is currently on. */
  private Square square;

  /** All GameCharacter names: */
  public enum Name {
    Lucilla, Bert, Malina, Percy
  }

  /**
   * Constructs a new GameCharacter with the specified name.
   * @param name character's name
   */
  public GameCharacter(String name){
    super(name, name.charAt(0));
  }

  /**
   * Moves the GameCharacter to a square.
   * If it is an EstateSquare, make square null and set the estate instead.
   * @param square Square to move to
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

    /**
     * Directly sets the GameCharacter's square. Used during guesses.
     * @param square Square character is moved to
     */
  public void setSquare(Square square){ this.square = square; }

    /**
     * Returns the Square this character is on.
     * @return current square
     */
  public Square getSquare(){ return square; }
  
  /**
   * Draws character on the board by calling drawCharToken.
   * @param g the Graphics object
   */
  public void drawCharacter(Graphics g) {
	  int x = 0;
	  int y = 0;
	  if(!this.inEstate()) {
		  x = square.col;
		  y = square.row;
		  drawCharToken(g,x,y,0);
	  }
  }
  
  /**
   * Draws the character tokens on the board. Used by drawCharacter as well as drawEstateContents.
   * @param g the Graphics object
   * @param x x position
   * @param y y position
   * @param offset position offset
   */
  public void drawCharToken(Graphics g, int x, int y, int offset) {
	  x = (x * Square.SIZE) + Square.WALL + offset;
	  y = (y * Square.SIZE) + Square.WALL;
	  //Graphics2D g = (Graphics2D) g1;
	  Color colour;
	  switch(initial) {
  		case 'L':
  			colour = new Color(42,161,70);
  			break;
  		case 'B':
  			colour = new Color(38,75,204);
  			break;
  		case 'M':
  			colour = new Color(248,208,52);
  			break;
  		default:
  			colour = new Color(228,0,39);
	  }
	  g.setColor(colour);
	  g.fillOval(x, y, Square.SIZE, Square.SIZE);
	  g.setColor(Color.WHITE);
	  g.drawString(String.valueOf(initial),(int)(x+Square.SIZE*2.0/5.0),(int)(y+Square.SIZE*3.0/5.0));
  }
  
  
}
