import java.awt.Graphics;

/**
 * Square is an abstract class representing each square on the Board of Murder Madness.
 * Each Square has a position.
 *
 * Square is extended by NormalSquare, WallSquare, and EstateSquare.
 *
 * @author johnh
 */
public abstract class Square {
    /** Square size when displaying. */
	public static final int SIZE = 32;
	/** Wall size (used for EstateSquares on edge of Estates). */
	public static final int WALL = 20;

    /** Square's row on board grid. */
    public final int row;
    /** Square's column on board grid. */
    public final int col;

    /**
     * Constructs a new Square with the specified position:
     * @param row row on board
     * @param col column on board
     */
    public Square(int row, int col){
        this.row = row;
        this.col = col;
    }

    /**
     * Returns true if GameCharacters cannot move onto it.
     * @return true if blocked
     */
    public abstract boolean isBlocked();
    
    /**
     * Displays the Square graphically.
     * @param g the Graphics object
     */
    public abstract void drawSquare(Graphics g);

    /**
     * Gets the description of the Square and its contents to show in the popup
     * when the mouse is hovered over it.
     * @return the Square's description
     */
    public abstract String getDescription();

    /**
     * Moves the specified character onto this Square.
     * @param c character to be moved
     */
    public void setCharacter(GameCharacter c){ throw new IllegalAccessError(); }

    /**
     * Removes the character from this Square (if there is one).
     */
    public void removeCharacter(){ throw new IllegalAccessError(); }
}
