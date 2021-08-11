import java.awt.Color;
import java.awt.Graphics;

/**
 * WallSquare is a subclass of Square, representing a Square that GameCharacters
 * can't be placed on.
 *
 * @author johnh
 */
public class WallSquare extends Square {

  /**
   * Constructs a new WallSquare with specified position.
   * @param row row on board
   * @param col column on board
   */
  public WallSquare(int row, int col){ super(row, col); }

  @Override
  public boolean isBlocked(){ return true; }

  @Override
  public String toString(){ return "X"; }

  @Override
  public String getDescription(){
      return "A wall square. Blocks movement.";
  }
  
  @Override
  public void drawSquare(Graphics g) {
	  g.setColor(new Color(36,36,36));
	  g.fillRect((col*SIZE)+WALL, (row*SIZE)+WALL, SIZE, SIZE);
	  
  }
}
