package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;

public final class Trick {
    
    private final int pkTrick;

    public final static Trick INVALID = new Trick(PackedTrick.INVALID);


    /**
     * Constructor of Trick
     * @param pkTrick (int): the packed representation of the trick
     */
    private Trick(int pkTrick) {
        this.pkTrick = pkTrick;
    }

    /**
     * Returns an empty trick with the given trump and first player 
     * @param trump (Color): the trump of the round
     * @param firstPlayer (PlayerId): the player to start
     * @return (Trick): the empty trick
     */
    public static Trick firstEmpty(Color trump, PlayerId firstPlayer) {
        return new Trick(PackedTrick.firstEmpty(trump, firstPlayer));
    }

    /**
     * Returns the trick corresponding to the packed trick given in argument
     * @param packed (int): the packed representation of the wanted trick
     * @throws IllegalArgumentException: if the given packed representation of the trick is invalid 
     * @return (Trick): the trick corresponding to the given PackedTrick
     */
    public static Trick ofPacked(int packed) {
        Preconditions.checkArgument(PackedTrick.isValid(packed));
        return new Trick(packed);
    }

    /**
     * Getter for the packed representation of the trick
     * @return (int): the packed trick
     */
    public int packed() {
        return pkTrick;
    }

    /**
     * Returns the next empty trick with the good trump, index and first player according to this trick
     * @throws IllegalStateException: if the trick is not full
     * @return (Trick) : the new reseted trick
     */
    public Trick nextEmpty() {
        if(!this.isFull())  {throw new IllegalStateException();}
        return new Trick(PackedTrick.nextEmpty(pkTrick));
    }

    /**
     * Checks if the trick has no played cards yet
     * @return (boolean): true if there are no played cards in the trick, false otherwise
     */
    public boolean isEmpty() {
        return PackedTrick.isEmpty(pkTrick);
    }

    /**
     * Checks if the trick is full or not 
     * @return (boolean): true if the trick is full, false otherwise
     */
    public boolean isFull() {
        return PackedTrick.isFull(pkTrick);
    }

    /**
     * Checks if it's the last trick of the turn
     * @return (boolean): true if it's the last trick of the turn, false otherwise
     */
    public boolean isLast() {
        return PackedTrick.isLast(pkTrick);
    }

    /**
     * Returns the size of the trick (the number of cards that it contains)
     * @return (int): the number of cards contained in the trick
     */
    public int size() {
        return PackedTrick.size(pkTrick);
    }

    /**
     * Gets the trump of the trick
     * @return (Color): the trump of the trick
     */
    public Color trump() {
        return PackedTrick.trump(pkTrick);
    }

    /**
     * Returns the index of the trick in the turn
     * @return (int): the index of the trick
     */
    public int index() {
        return PackedTrick.index(pkTrick);
    }

    /**
     * Returns the "index"-th player that played in the trick (starting at 0)
     * @param index (int): the index of the wanted player
     * @throws IndexOutOfBoundsException: if the given index is negative or bigger than the number of player
     * @return (PlayerId): the player associated to the index of the card
     */
    public PlayerId player(int index) {
        Preconditions.checkIndex(index, PlayerId.COUNT);
        return PackedTrick.player(pkTrick, index);
    }

    /**
     * Returns the "index"-th card that have been played
     * @param index (int): the index of the card
     * @throws IndexOutOfBoundsException: if the given index is negative or bigger than the size of the trick
     * @return (Card): the wanted card 
     */
    public Card card(int index) {
        Preconditions.checkIndex(index, this.size());
        return Card.ofPacked(PackedTrick.card(pkTrick, index));
    }

    /**
     * Adds the given card in the trick, and returns the updated trick
     * @param c (Card): the card to add
     * @throws IllegalStateException: if the trick is full
     * @return (Trick): the updated trick
     */
    public Trick withAddedCard(Card c) {
        if(this.isFull())  {throw new IllegalStateException();}
        return new Trick(PackedTrick.withAddedCard(pkTrick, c.packed()));
    }

    /**
     * Returns the base color of the trick, which is the color of the first card placed 
     * @throws IllegalStateException: if the trick is empty
     * @return (Color): the base color of the trick
     */
    public Color baseColor() {
        if(this.isEmpty())  {throw new IllegalStateException();}
        return PackedTrick.baseColor(pkTrick);
    }
    
    /**
     * Returns the set of cards which contains all the cards of a hand that can be played depending on the cards
     * in the trick
     * @param hand (CardSet): the set of cards representing the hand
     * @throws IllegalStateException: if the trick is full
     * @return (CardSet): all the cards of the hand that can be played depending on the current cards in the trick
     */
    public CardSet playableCards(CardSet hand) {
        if (this.isFull()) { throw new IllegalStateException(); }
        return CardSet.ofPacked(PackedTrick.playableCards(pkTrick, hand.packed()));
    }

    /**
     * Give the points that given by this trick 
     * @return (int): the value of the trick
     */ 
    public int points() {
        return PackedTrick.points(pkTrick);
    }

    /**
     * Returns the player that is currently leading the trick
     * @throws IllegalStateException: if the trick is empty
     * @return (PlayerId): the player leading the trick
     */
    public PlayerId winningPlayer() {
        if(this.isEmpty())  {throw new IllegalStateException();}
        return PackedTrick.winningPlayer(pkTrick);
    }

    
    /**
     * Override of the method equals
     */
    @Override
    public boolean equals(Object thatO) {
        if (thatO==null) {
            return false;
        }
        else {
            if (thatO instanceof Trick) {
                return (this.pkTrick== ((Trick)thatO).pkTrick);
            }
            else {return false;}
        }  
    }
    
    /**
     * Override of the method hashCode
     */
    @Override
    public int hashCode() {
        return pkTrick;
    }
    
    /**
     * Override of the method toString
     */
    @Override
    public String toString() {
        return PackedTrick.toString(pkTrick);
    }
}
