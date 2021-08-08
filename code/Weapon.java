
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;



/**
 * Weapon is a subclass of Card representing a weapon card.
 */
public class Weapon extends Card {

  /** Al Weapon names: */
  public enum Name {
    Broom, Scissors, Knife, Shovel, iPad
  }
  
  private static Image broom = loadImage("broom.png");
  private static Image ipad = loadImage("ipad.png");
  private static Image knife = loadImage("knife.png");
  private static Image scissors = loadImage("scissors.png");
  private static Image shovel = loadImage("shovel.png");

  /** Constructs a Weapon card with a specified name and initial (just its index in the Name enum): */
  public Weapon(String name){
    // Second argument converts name to enum, gets its numerical value and makes it a char:
    super(name, String.valueOf(Name.valueOf(name).ordinal()).charAt(0));
  }
  
  public void drawWeapon(Graphics g, int x, int y, int offset) throws IOException {
	  x = (x * Square.SIZE) + Square.WALL + offset;
	  y = (y * Square.SIZE) + Square.WALL;
	  char in = this.initial;
//	  Graphics2D g = (Graphics2D) g1;
	  switch(in) {
  		case '0':
  			g.drawImage(broom, x,y,Square.SIZE,Square.SIZE,null,null);
  			break;
  		case '1':
  			g.drawImage(scissors, x,y,Square.SIZE,Square.SIZE,null,null);
  			break;
  		case '2':
  			g.drawImage(knife, x,y,Square.SIZE,Square.SIZE,null,null);
  			break;
  		case '3':
  			g.drawImage(shovel, x,y,Square.SIZE,Square.SIZE,null,null);
  			break;
  		default:
  			g.drawImage(ipad, x,y,Square.SIZE,Square.SIZE,null,null);
  			break;
	  }
  }
  
  /**
   * Util method to load image. Taken from SWEN221 assignment "robot war".
   * @param filename
   * @return
   */
  private static Image loadImage(String filename) {
		// using the URL means the image loads when stored
		// in a jar or expanded into individual files.
		java.net.URL imageURL = Weapon.class.getResource(filename);
		try {
			Image img = ImageIO.read(imageURL);
			return img;
		} catch (IOException e) {
			// we've encountered an error loading the image. There's not much we
			// can actually do at this point, except to abort the game.
			throw new RuntimeException("Unable to load image: " + filename);
		}
	}
}
