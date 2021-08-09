import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

/**
 * Weapon is a subclass of Card representing a weapon card.
 */
public class Weapon extends Card {

  private static final Image broom = loadImage("images/broom.png");
  private static final Image ipad = loadImage("images/ipad.png");
  private static final Image knife = loadImage("images/knife.png");
  private static final Image scissors = loadImage("images/scissors.png");
  private static final Image shovel = loadImage("images/shovel.png");

  /** Al Weapon names: */
  public enum Name {
    Broom, Scissors, Knife, Shovel, iPad
  }

  /** Constructs a Weapon card with a specified name and initial (just its index in the Name enum): */
  public Weapon(String name){
    // Second argument converts name to enum, gets its numerical value and makes it a char:
    super(name, String.valueOf(Name.valueOf(name).ordinal()).charAt(0));
  }

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
   * Util method to load image. Taken from SWEN221 assignment "robot war".
   * @param filename
   * @return
   */
  private static Image loadImage(String filename) {
    // Using the URL means the image loads when stored
    // in a jar or expanded into individual files.
    java.net.URL imageURL = Weapon.class.getResource(filename);
    try {
      return ImageIO.read(imageURL);
    } catch (IOException e) {
      // We've encountered an error loading the image. There's not much we
      // can actually do at this point, except to abort the game.
      throw new RuntimeException("Unable to load image: " + filename);
    }
  }
}
