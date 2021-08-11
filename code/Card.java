/**
 * Card represents a playing card in the game Murder Madness.
 * It has a name, initial (symbol), and may be in an Estate (if it is a GameCharacter or Weapon).
 *
 * Parent class to GameCharacter, Estate, and Weapon.
 *
 * @author johnh
 */
public class Card {

    /** The Card's official name. */
    public final String name;
    /** The Card's defining character symbol. Shown on the board's text display. */
    public final char initial;
    /** The Estate the Card is in, if it is in one. Only applies to GameCharacters and Weapons. */
    private Estate estate;

    /**
     * Constructs a new Card with the specified name and initial.
     *
     * @param name name of Card
     * @param initial char used to represent Card
     */
    protected Card(String name, char initial){
        this.name = name;
        this.initial = initial;
    }

    /**
     * Sets the Estate this Card is in.
     * @param e estate
     */
    public void setEstate(Estate e){ this.estate = e; }

    /**
     * Returns if this Card is in an Estate.
     * @return true if in Estate
     */
    public boolean inEstate(){ return estate != null; }

    /**
     * Returns the Estate the Card is in.
     * @return null if not in Estate, else the Estate
     */
    public Estate getEstate(){ return estate; }

    /**
     * Sets this Card's estate to null.
     */
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
