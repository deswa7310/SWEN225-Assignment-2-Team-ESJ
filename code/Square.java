import java.awt.Graphics;

/**
 * Square is an abstract class representing each square on the Board of Murder Madness.
 * Each Square has a position.
 *
 * Square is extended by NormalSquare, WallSquare, and EstateSquare.
 */
public abstract class Square {
	public static final int SIZE = 32;
	// public static final int GAP = 5; unused 
	public static final int WALL = 20;

    /** Position. */
    public final int row, col;

    /** Constructs a new Square with specified position on the grid: */
    public Square(int row, int col){
        this.row = row;
        this.col = col;
    }

    /** Returns true if a GameCharacter cannot move onto it: */
    public abstract boolean isBlocked();
    
    /**
     * displays square graphically
     * @param g
     */
    public abstract void drawSquare(Graphics g);

    public abstract String getDescription();

    /** Should not be accessible: */
    public void setCharacter(GameCharacter c){ throw new IllegalAccessError(); }
    /** Should not be accessible: */
    public void removeCharacter(){ throw new IllegalAccessError(); }
}
