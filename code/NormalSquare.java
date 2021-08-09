import java.awt.Color;
import java.awt.Graphics;

/**
 * NormalSquare is a subclass of Square representing the majority of squares on the board.
 * GameCharacters can be placed on them.
 */
public class NormalSquare extends Square {

  /** The GameCharacter currently on this square. */
  private GameCharacter character;

  public NormalSquare(int row, int col){ super(row, col); }

  @Override
  public void setCharacter(GameCharacter c){ this.character = c; }
  @Override
  public void removeCharacter(){ this.character = null; }

  @Override
  public boolean isBlocked(){ return character != null; }

  @Override
  public String toString(){
    return character == null ? " " : character.toString().substring(0, 1);
  }

  @Override
  public String getDescription(){
    if (character != null) return character.name+" on a normal square.";
    return "A normal square. Can be walked on.";
  }
  
  @Override
  public void drawSquare(Graphics g) {
	  g.setColor(new Color(69,69,69));
	  g.fillRect((col*SIZE)+WALL+2, (row*SIZE)+WALL+2, SIZE-3, SIZE-3);
	  
  }
}
