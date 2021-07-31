/**
 * Weapon is a subclass of Card representing a weapon card.
 */
public class Weapon extends Card {

  /** Al Weapon names: */
  public enum Name {
    Broom, Scissors, Knife, Shovel, iPad
  }

  /** Constructs a Weapon card with a specified name and initial (just its index in the Name enum): */
  public Weapon(String name){
    // Second argument converts name to enum, gets its numerical value and makes it a char:
    super(name, String.valueOf(Name.valueOf(name).ordinal()).charAt(0));
  }
}
