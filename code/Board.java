import java.awt.Color;
import java.awt.Graphics;
import java.util.*;

/**
 * Board represents the game's board, containing all Square data in a grid.
 */
public class Board {

  /** Grid constants: */
  public static final int ROWS = 24;
  public static final int COLS = 24;

  /** Grid 2d array storing each Square (by row then column). */
  private final Square[][] grid = new Square[ROWS][COLS];


  /**
   * Constructs a new Board from a template String representing the starting board setup.
   */
  public Board(Map<Character, GameCharacter> characters, Map<Character, Estate> estates) {
    String template =
            "   0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3\n" +
            "00. . . . . . . . . . . . . . . . . . . . . . . . .\n" +
            "01. . . . . . . . . . . .L. . . . . . . . . . . . .\n" +
            "02. . .h.h.h.h.h. . . . . . . . . . .m.m.m.m.m. . .\n" +
            "03. . .h.h.h.h.e. . . . . . . . . . .m.m.m.m.m. . .\n" +
            "04. . .h.h.h.h.h. . . . . . . . . . .m.m.m.m.m. . .\n" +
            "05. . .h.h.h.h.h. . . . .x.x. . . . .e.m.m.m.m. . .\n" +
            "06. . .h.h.h.e.h. . . . .x.x. . . . .m.m.m.e.m. . .\n" +
            "07. . . . . . . . . . . . . . . . . . . . . . . . .\n" +
            "08. . . . . . . . . . . . . . . . . . . . . . . . .\n" +
            "09. .B. . . . . . . . . . . . . . . . . . . . . . .\n" +
            "10. . . . . . . . . .v.v.v.e.v.v. . . . . . . . . .\n" +
            "11. . . . . .x.x. . .v.v.v.v.v.e. . .x.x. . . . . .\n" +
            "12. . . . . .x.x. . .e.v.v.v.v.v. . .x.x. . . . . .\n" +
            "13. . . . . . . . . .v.v.e.v.v.v. . . . . . . . . .\n" +
            "14. . . . . . . . . . . . . . . . . . . . . . .P. .\n" +
            "15. . . . . . . . . . . . . . . . . . . . . . . . .\n" +
            "16. . . . . . . . . . . . . . . . . . . . . . . . .\n" +
            "17. . .c.e.c.c.c. . . . .x.x. . . . .p.e.p.p.p. . .\n" +
            "18. . .c.c.c.c.e. . . . .x.x. . . . .p.p.p.p.p. . .\n" +
            "19. . .c.c.c.c.c. . . . . . . . . . .p.p.p.p.p. . .\n" +
            "20. . .c.c.c.c.c. . . . . . . . . . .e.p.p.p.p. . .\n" +
            "21. . .c.c.c.c.c. . . . . . . . . . .p.p.p.p.p. . .\n" +
            "22. . . . . . . . . .M. . . . . . . . . . . . . . .\n" +
            "23. . . . . . . . . . . . . . . . . . . . . . . . .";

    initSquares(template, characters, estates);
    configureEstateSquares();
  }


  /**
   * Iterates through the template String, initiating all Squares in the representation.
   */
  private void initSquares(String template, Map<Character, GameCharacter> characters, Map<Character, Estate> estates) {
    // Split template into rows:
    String[] lines = template.split("\n");

    // Go through each row of the starting board:
    for (int row = 0; row < ROWS; row++) {
      String line = lines[row + 1];
      String[] tokens = line.split("\\.");

      // Go through each column in the row (columns are separated by '.'):
      for (int col = 0; col < COLS; col++) {
        char token = tokens[col + 1].toCharArray()[0];

        if (token == ' ') grid[row][col] = new NormalSquare(row, col); // normal squares are blank
        else if (token == 'x') grid[row][col] = new WallSquare(row, col); // wall squares are 'x'

          // Only Estate squares are lowercase (excluding 'x'):
        else if (Character.isLowerCase(token)) {
          Estate e;

          // If the Estate square is an entrance:
          boolean entrance = false;
          if (token == 'e') {
            // Get its Estate by checking the squares to the left and above:
            if (grid[row][col - 1] instanceof EstateSquare) e = ((EstateSquare) grid[row][col - 1]).estate;
            else e = ((EstateSquare) grid[row - 1][col]).estate;
            entrance = true;
          } else e = estates.get(token);

          EstateSquare s = new EstateSquare(row, col, e, entrance);
          if (entrance) e.addEntrance(s);
          grid[row][col] = s;
        }

        // Else it must be a GameCharacter:
        else {
          NormalSquare s = new NormalSquare(row, col);
          grid[row][col] = s;
          GameCharacter c = characters.get(token);
          s.setCharacter(c);
          c.moveToSquare(s);
        }
      }
    }
  }

  /**
   * Configures all EstateSquares by determining if they are on the inside or outside,
   * and which Square is directly outside if it is an entrance.
   */
  private void configureEstateSquares(){
    // Finally, run through all Estate Squares to determine which are on the inside or outside.
    // If outside, also determine its side.
    for (int row = 0; row < ROWS; row++){
      for (int col = 0; col < COLS; col++){
        Square square = grid[row][col];
        if (!(square instanceof EstateSquare)) continue;
        EstateSquare s = (EstateSquare) square;
        Estate e = s.estate;

        int sides = 0; // on how many sides of the estate it is. 2 means it is a corner.
        Estate.Side side =  null; // if its only on one side, this will hold that side.
        Square outer = null; // the square directly outside an entrance.

        // Determine what side(s) the EstateSquare is on by checking if an adjacent one
        // isn't part of the Estate:
        if (!(grid[row][col-1] instanceof EstateSquare)){ // Left side
          sides++;
          side = Estate.Side.LEFT;
          outer = grid[row][col-1];
        }
        else if (!(grid[row][col+1] instanceof EstateSquare)){ // Right side
          sides++;
          side = Estate.Side.RIGHT;
          outer = grid[row][col+1];
        }
        if (!(grid[row-1][col] instanceof EstateSquare)){ // Top side
          sides++;
          side = Estate.Side.TOP;
          outer = grid[row-1][col];
        }
        else if (!(grid[row+1][col] instanceof EstateSquare)){ // Bottom side
          sides++;
          side = Estate.Side.BOTTOM;
          outer = grid[row+1][col];
        }

        // If square is an entrance, store the square directly outside of it.
        // This is useful for knowing if the entrance is blocked.
        if (s.entrance) s.setOuterSquare((NormalSquare)outer);

        switch (sides){
          case 0: // it is an inner square
            e.addInnerTile(s);
            break;
          case 1: // it is an outer square
            s.setSide(side);
            break;
        }
        // If it has 2 sides it is a corner, but this is already handled within the class.
      }
    }
  }


  public Square getSquare(int row, int col){
    if (row < 0 || row >= Board.ROWS || col < 0 || col >= Board.COLS) return null;
    return grid[row][col];
  }

  /*
   * Draws the grid by calling draw on each square.
   */
  public void drawBoard(Graphics g) {
    g.setColor(new Color(56,56,56));
    g.fillRect(20, 20, GUI.SIZE-40, GUI.SIZE-40);

    // Draws each Square.
    for(int row = 0; row < ROWS; row++) {
      for(int col = 0; col < COLS; col++) {
        Square s = grid[row][col];
        s.drawSquare(g);
      }
    }

    // Draws the walls of the estates. Needs to be done after previous loop to stop overlapping of squares and sides.
    for(int row = 0; row < ROWS; row++) {
      for(int col = 0; col < COLS; col++) {
        Square s = grid[row][col];
        if(s instanceof EstateSquare) {
          EstateSquare es = (EstateSquare) s;
          es.drawEstateSide(g);
        }
      }
    }

    // Draws each entrance.
    for(int row = 0; row < ROWS; row++) {
      for(int col = 0; col < COLS; col++) {
        Square s = grid[row][col];
        if(s instanceof EstateSquare) {
          EstateSquare es = (EstateSquare) s;
          es.drawEntrance(g);
        }
      }
    }
  }
}
