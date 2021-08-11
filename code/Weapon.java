import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

/**
 * Weapon is a subclass of Card representing a weapon card.
 *
 * @author johnh
 */
public class Weapon extends Card {

  /** Broom image representation. */
  private static final Image broom = loadImage("images/broom.png");
  /** Ipad image representation. */
  private static final Image ipad = loadImage("images/ipad.png");
  /** Knife image representation. */
  private static final Image knife = loadImage("images/knife.png");
  /** Scissors image representation. */
  private static final Image scissors = loadImage("images/scissors.png");
  /** Shovel image representation. */
  private static final Image shovel = loadImage("images/shovel.png");

  /** Al Weapon names: */
  public enum Name {
    Broom, Scissors, Knife, Shovel, iPad
  }

  /**
   * Constructs a new Weapon with the given name:
   * @param name name of Weapon
   */
  public Weapon(String name){
    // Second argument converts name to enum, gets its numerical value and makes it a char:
    super(name, String.valueOf(Name.valueOf(name).ordinal()).charAt(0));
  }

  /**
   * Draws the Weapon on the board display using a custom Image.
   * @param g the Graphics object
   * @param x x position
   * @param y y position
   * @param offset position offset
   */
  public void drawWeapon(Graphics g, int x, int y, int offset){
    x = (x * Square.SIZE) + Square.WALL + offset;
    y = (y * Square.SIZE) + Square.WALL;
//	  Graphics2D g = (Graphics2D) g1;
    Image i;
    switch(initial) {
      case '0':
        i = broom;
        break;
      case '1':
        i = scissors;
        break;
      case '2':
        i = knife;
        break;
      case '3':
        i = shovel;
        break;
      default:
        i = ipad;
        break;
    }
    g.drawImage(i, x,y,Square.SIZE,Square.SIZE,null,null);
  }

  /**
   * Utility method to load weapon images.
   * @param filename name of image file
   * @return the Image object of the weapon image file.
   */
  private static Image loadImage(String filename) {
    java.net.URL imageURL = Weapon.class.getResource(filename);
    // Attempt loading image:
    try {
      return ImageIO.read(imageURL);
    } catch (IOException e) {
      // Throw exception if failed:
      throw new RuntimeException("Could not load image: " + filename);
    }
  }
}
