/**
 * Card represents a playing card in the game Murder Madness.
 * It has a name, initial (symbol), and may be in an Estate (if it is a GameCharacter or Weapon).
 *
 * Parent class to GameCharacter, Estate, and Weapon.
 */
public class Card {

    /** The Card's official name. */
    public final String name;
    /** The Card's defining character symbol. Shown on the board's text display. */
    public final char initial;
    /** The Estate the Card is in, if it is in one. Only applies to GameCharacters and Weapons. */
    private Estate estate;

    /** Creates a new Card with the specified name and initial: */
    protected Card(String name, char initial){
        this.name = name;
        this.initial = initial;
    }

    public void setEstate(Estate e){ this.estate = e; }
    public boolean inEstate(){ return estate != null; }
    public Estate getEstate(){ return estate; }
    public void leaveEstate(){ estate = null; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return name.equals(card.name);
    }

    @Override
    public int hashCode() { return name.hashCode(); }

    @Override
    public String toString(){ return name; }
}
