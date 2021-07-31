/**
 * Square is an abstract class representing each square on the Board of Murder Madness.
 * Each Square has a position.
 *
 * Square is extended by NormalSquare, WallSquare, and EstateSquare.
 */
public abstract class Square {

    /** Position. */
    public final int row, col;

    /** Constructs a new Square with specified position on the grid: */
    public Square(int row, int col){
        this.row = row;
        this.col = col;
    }

    /** Returns true if a GameCharacter cannot move onto it: */
    public abstract boolean isBlocked();

    /** Should not be accessible: */
    public void setCharacter(GameCharacter c){ throw new IllegalAccessError(); }
    /** Should not be accessible: */
    public void removeCharacter(){ throw new IllegalAccessError(); }
}
