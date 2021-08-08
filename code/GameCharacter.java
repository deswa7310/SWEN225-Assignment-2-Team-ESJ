import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

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
  
  /**
   * draws character on grid by calling drawCharToken.
   * @param g
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
   * Draws the character tokens. Used by drawCharacter as well as drawEstateContents.
   * @param g
   * @param x
   * @param y
   */
  public void drawCharToken(Graphics g, int x, int y, int offset) {
	  x = (x * Square.SIZE) + Square.WALL + offset;
	  y = (y * Square.SIZE) + Square.WALL;
	  offset = Square.SIZE/2;
	  char in = this.initial;
//	  Graphics2D g = (Graphics2D) g1;
	  switch(in) {
  		case 'L':
  			g.setColor(new Color(42,161,70));
  			g.fillOval(x, y, Square.SIZE, Square.SIZE);
//  			g.setColor(new Color(36,36,36));
//  			g.setStroke(new BasicStroke(2));
//  			g.drawOval(x, y, Square.SIZE, Square.SIZE);
  			g.setColor(Color.WHITE);
  			g.drawString("L",x+offset,y+offset);
  			break;
  		case 'B':
  			g.setColor(new Color(38,75,204));
  			g.fillOval(x, y, Square.SIZE, Square.SIZE);
//  			g.setColor(new Color(36,36,36));
//  			g.setStroke(new BasicStroke(3));
//  			g.drawOval(x, y, Square.SIZE, Square.SIZE);
  			g.setColor(Color.WHITE);
  			g.drawString("B",x+offset,y+offset);
  			break;
  		case 'M':
  			g.setColor(new Color(248,208,52));
  			g.fillOval(x, y, Square.SIZE, Square.SIZE);
//  			g.setColor(new Color(36,36,36));
//  			g.setStroke(new BasicStroke(2));
//  			g.drawOval(x, y, Square.SIZE, Square.SIZE);
  			g.setColor(Color.WHITE);
  			g.drawString("M",x+offset,y+offset);
  			break;
  		default:
  			g.setColor(new Color(228,0,39));
  			g.fillOval(x, y, Square.SIZE, Square.SIZE);
//  			g.setColor(new Color(36,36,36));
//  			g.setStroke(new BasicStroke(2));
//  			g.drawOval(x, y, Square.SIZE, Square.SIZE);
  			g.setColor(Color.WHITE);
  			g.drawString("P",x+offset,y+offset);
  			break;
	  }
  }
  
  
}
