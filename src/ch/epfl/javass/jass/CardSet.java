package ch.epfl.javass.jass;

import java.util.List;

import ch.epfl.javass.Preconditions;

/**
 * Class which represents a set of cards in the game
 */
public final class CardSet {

    //The packed representation of the set of card
    private final long pkCardSet;

    //Empty set of cards
    public static final CardSet EMPTY = new CardSet(PackedCardSet.EMPTY);

    //Full set of every cards
    public static final CardSet ALL_CARDS = new CardSet(PackedCardSet.ALL_CARDS);

    /**
     * Constructor of CardSet
     * @param pkCardSet (long): packed representation of the set of cards
     */
    private CardSet(long pkCardSet) {
        this.pkCardSet = pkCardSet;
    }

    /**
     * Create a set containing all the cards in the list 
     * @param cards (List<Card>): the list that contains the cards
     * @return (CardSet): the set of all the cards contained in the list
     */
    public static CardSet of(List<Card> cards) {
        long set = PackedCardSet.EMPTY;
        for(Card theseCards : cards) {
            set = PackedCardSet.add(set, theseCards.packed());
        }
        return new CardSet(set);
    }

    /**
     * Creates the set corresponding to the given packed representation
     * @param packed (long): the packed representation of the set of cards
     * @throws IllegalArgumentException: if the given packed representation of the set is invalid 
     * @return (CardSet): the set corresponding to the given packed representation
     */
    public static CardSet ofPacked(long packed) {
        Preconditions.checkArgument(PackedCardSet.isValid(packed));
        return new CardSet(packed);
    }

    /**
     * Gives the packed representation of the CardSet
     * @return (long): the packed representation of the set of cards
     */
    public long packed() {
        return pkCardSet;
    }

    /**
     * Says if the given card set is empty or not 
     * @return (boolean): true if the given card set is empty, false otherwise
     */
    public boolean isEmpty() {
        return PackedCardSet.isEmpty(pkCardSet);
    }

    /**
     * Returns the number of cards in the set of cards
     * @return (int): the number of cards contained in the set
     */
    public int size() {
        return PackedCardSet.size(pkCardSet);
    }

    /**
     * Gives the the "index"-th card from the set (ordered in an increasing order)  
     * @param index (int): the index of the wanted card in the card set 
     * @return (Card): the wanted Card
     */
    public Card get(int index) {
        return Card.ofPacked(PackedCardSet.get(pkCardSet, index));
    }

    /**
     * Adds, in the card set, the specified card
     * @param card (Card): the card we want to add
     * @return (CardSet): the set of cards with the wanted card added in
     */
    public CardSet add(Card card) {
        return new CardSet(PackedCardSet.add(pkCardSet, card.packed()));
    }

    /**
     * Removes from the set of cards the specified card
     * @param card (Card): the card we want to remove
     * @return (CardSet): the set of cards with the wanted card removed
     */
    public CardSet remove(Card card) {
        return new CardSet(PackedCardSet.remove(pkCardSet, card.packed()));
    }

    /**
     * Checks if the set of cards contains a specified card
     * @param card (Card): the card
     * @return (boolean): true if the set of cards contains the specified card
     */
    public boolean contains(Card card) {
        return PackedCardSet.contains(pkCardSet, card.packed());
    }

    /**
     * Returns all the cards that the set of cards doesn't have
     * @return (CardSet): all the cards not contained in the set of cards
     */
    public CardSet complement() {
        return new CardSet(PackedCardSet.complement(pkCardSet));
    }

    /**
     * Creates a set that contains all the cards of this card set and all the cards of the given "that" set
     * @param that (CardSet): the specified set 
     * @return (CardSet): a set that contains all the cards of the card set and all the cards of "that" set
     */
    public CardSet union(CardSet that) {
        return new CardSet(PackedCardSet.union(pkCardSet, that.packed()));
    }

    /**
     * Returns a set of cards that a the current set and a second set of cards have in common 
     * @param that (CardSet) the second set of cards
     * @return (CardSet) the intersection of the two sets of cards
     */
    public CardSet intersection(CardSet that) {
        return new CardSet(PackedCardSet.intersection(pkCardSet, that.packed()));
    }

    /**
     * Returns the set of cards that are contained in the current set of cards but not in the other
     * @param that (CardSet): the other set
     * @return (CardSet): the set of cards contained in the current set but not in the other one
     */
    public CardSet difference(CardSet that) {
        return new CardSet(PackedCardSet.difference(pkCardSet,that.packed()));
    }

    /**
     * Returns the subset of the set of cards which only contain cards that have the specified color
     * @param color (Card.Color): the specified color
     * @return (CardSet): the subset which contains all the cards of color "color"
     */
    public CardSet subsetOfColor(Card.Color color) {
        return new CardSet(PackedCardSet.subsetOfColor(pkCardSet, color));
    }

    /**
     * Override of the method equals of Object
     */
    @Override
    public boolean equals(Object thatO) {
        if (thatO==null) {
            return false;
        }
        else {
            if (thatO instanceof CardSet) {
                return (this.pkCardSet== ((CardSet)thatO).pkCardSet);
            }
            else {return false;}
        }  
    }

    /**
     * Override of the method hashCode of Object
     */
    @Override
    public int hashCode() {
        return Long.hashCode(pkCardSet);
    }

    /**
     * Override of the method toString of Object
     */
    @Override
    public String toString() {
        return PackedCardSet.toString(pkCardSet);  
    }

}
