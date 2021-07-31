/**
 * WallSquare is a subclass of Square, representing a Square that GameCharacters
 * can't be placed on.
 */
public class WallSquare extends Square {

  public WallSquare(int row, int col){ super(row, col); }

  @Override
  public boolean isBlocked(){ return true; }

  @Override
  public String toString(){ return "X"; }
}