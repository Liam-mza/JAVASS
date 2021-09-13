package ch.epfl.javass.jass;

import java.util.StringJoiner;

import ch.epfl.javass.bits.Bits64;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

/**
 * This class represent a set of card in a packed way with a long
 */
public final class PackedCardSet {

    public static final long EMPTY = 0L;

    public static final int COLOR_LENGTH = 16;
    public static final int BITS_COLOR_LENGTH = 9;

    //Constants useful to get the indexes of the packed sets of cards
    public static final int EMPTY_BITS_SIZE = 7;
    public static final int FIRST_UNUSED_BIT_SPADE = 9;
    public static final int FIRST_UNUSED_BIT_HEART = 25;
    public static final int FIRST_UNUSED_BIT_DIAMOND = 41;
    public static final int FIRST_UNUSED_BIT_CLUB = 57;

    //The array that contains every card of each color
    private static final Long[] SUBSET_TAB = subsetArray();

    //The set that contains every card of the game in their packed representation
    public static final long ALL_CARDS = SUBSET_TAB[0] | SUBSET_TAB[1] | SUBSET_TAB[2] | SUBSET_TAB[3];

    // The array that is used by trumpAbove
    public static final long [][] TAB_FOR_TRUMPABOVE =  tabForTrumpAbove();

    /**
     * Constructor of the class PackedCardSet
     */
    private PackedCardSet() {};

    /**
     * Checks if the packed representation of the set of cards is valid
     * @param pkScore (long): the packed representation of the score
     * @return (boolean)true if it's valid, false otherwise
     */
    public static boolean isValid(long pkCardSet) {
        if(Bits64.extract(pkCardSet, FIRST_UNUSED_BIT_SPADE, EMPTY_BITS_SIZE)==0 && 
                Bits64.extract(pkCardSet, FIRST_UNUSED_BIT_HEART, EMPTY_BITS_SIZE)==0 &&
                Bits64.extract(pkCardSet, FIRST_UNUSED_BIT_DIAMOND, EMPTY_BITS_SIZE)==0 &&
                Bits64.extract(pkCardSet, FIRST_UNUSED_BIT_CLUB, EMPTY_BITS_SIZE)==0) {
            return true;
        }
        return false;
    }

    /**
     * Return a set of all the cards that are better than the given card, given that it's a card of the trump color
     * @param pkCard (int): the packed representation of the card
     * @return (long): the set of all the that are better than the given card
     */
    public static long trumpAbove(int pkCard) {
        return (TAB_FOR_TRUMPABOVE[PackedCard.color(pkCard).ordinal()][PackedCard.rank(pkCard).ordinal()]);
    }

    /**
     * Return a packed representation of the Card set containing only the card corresponding to the given packed representation
     * @param pkCard (int): the packed representation of the card
     * @return (long): the packed representation of the Card set containing only the card wanted
     */
    public static long singleton(int pkCard) {
        long set= 1L<<pkCard;
        return set;
    }

    /**
     * Say if the given card set is empty or not 
     * @param pkCardSet (long): the packed representation of the card set
     * @return (boolean): true if the given card set is empty, false otherwise
     */
    public static boolean isEmpty(long pkCardSet) {
        return (pkCardSet==EMPTY);
    }

    /**
     * Returns the number of cards in the set 
     * @param pkCardSet (long): the packed representation of the set of cards
     * @return (int): the number of cards contained in the set
     */
    public static int size(long pkCardSet) {
        return Long.bitCount(pkCardSet);
    }

    /**
     * Give the the index-th card from the set (ordered in an increasing order)  
     * @param pkCardSet (long): the packed representation of the card set
     * @param index (int): the index of the wanted card in the card set 
     * @return (int): the packed representation of the wanted Card
     */
    public static int get(long pkCardSet, int index) {
        for (int i=0; i<index; ++i) {
            long lowestOneBits= Long.lowestOneBit(pkCardSet);
            pkCardSet = pkCardSet ^ lowestOneBits;
        }
        return Long.numberOfTrailingZeros(pkCardSet);
    }

    /**
     * Add in the given card set the card corresponding to the given packed representation 
     * @param pkCardSet (long): the packed representation of the card set
     * @param pkCard (int): the packed representation of the card we want to add
     * @return (long): the packed representation of the Card set with the wanted card added
     */
    public static long add(long pkCardSet, int pkCard) {
        long single = singleton(pkCard);
        pkCardSet = union(pkCardSet, single);
        return pkCardSet;
    }

    /**
     * Remove from the given card set the card corresponding to the given packed representation
     * @param pkCardSet(long): the packed representation of the card set
     * @param pkCard (int): the packed representation of the card we want to remove
     * @return (long): the packed representation of the Card set with the wanted card removed
     */
    public static long remove(long pkCardSet, int pkCard) {
        long single = singleton(pkCard);
        pkCardSet = intersection(pkCardSet, (~single));
        return pkCardSet;
    }

    /**
     * Checks if the given set of packed cards contains the given card
     * @param pkCardSet (long): the set of packed cards
     * @param pkCard (int): the packed card 
     * @return (boolean): true if the Card Set contains the given Card
     */
    public static boolean contains(long pkCardSet, int pkCard) {
        long newCard = singleton(pkCard);
        if(union(pkCardSet, newCard)==pkCardSet) {
            return true;
        }
        return false;
    }

    /**
     * Returns the complement of the given packed set of cards
     * @param pkCardSet (long): the set of packed cards
     * @return (long): the complement of the packed cards of the set
     */
    public static long complement(long pkCardSet) {
        return (ALL_CARDS ^ pkCardSet);
    }

    /**
     * Returns the union of the two given sets: the set containing all the cards of the first set and all the cards the second set
     * @param pkCardSet1 (long): the first set
     * @param pkCardSet2 (long): the second set
     * @return (long) the union of all the cards contained in pkCardSet1 and pkCardSet2
     */
    public static long union(long pkCardSet1, long pkCardSet2) {
        return (pkCardSet1 | pkCardSet2);
    }

    /**
     * Returns the intersection of the two given sets: the set containing all the cards which are in both set
     * @param pkCardSet1 (long): first set of packed cards
     * @param pkCardSet2 (long): second set of packed cards
     * @return (long): the intersection of the two sets of packed cards
     */
    public static long intersection(long pkCardSet1, long pkCardSet2) {
        return (pkCardSet1 & pkCardSet2);
    }

    /**
     * Returns the packed cards that are contained in the first set but not in the second
     * @param pkCardSet1 (long): the first set
     * @param pkCardSet2 (long): the second set
     * @return (long): the packed cards contained in pkCardSet1 but not in pkCardSet2
     */
    public static long difference(long pkCardSet1, long pkCardSet2) {
        return intersection(pkCardSet1, (~pkCardSet2));
    }

    /**
     * Returns all the card of the given color contained in the given set
     * @param pkCardSet (long): the packed representation of the set cards
     * @param color (Card.Color): the specified color
     * @return (long): the subset which contain all the cards of color "color"
     */
    public static long subsetOfColor(long pkCardSet, Card.Color color) {
        return intersection(pkCardSet, SUBSET_TAB[color.ordinal()]);
    }

    /**
     * Returns a clear representation of all the cards contained in the specified set
     * @param pkCardSet (long): the set of the packed cards
     * @return (String): the set with all the cards contained in it with its color and its rank
     */
    public static String toString(long pkCardSet) {
        StringJoiner cardString = new StringJoiner(",", "{", "}");
        int sizeOfCardSet = size(pkCardSet);
        for(int i=0;i<sizeOfCardSet;++i) {
            cardString.add(PackedCard.toString(get(pkCardSet, i)));
        }
        return cardString.toString();
    }

    /**
     * A spare method to fill in the array SUBSET_TAB with four subsets of packed cards which contain every cards of a 
     * specified color (this array is useful for the method subsetOfColor)
     * @return (long[]): the array with all the subsets associated to each color respectively
     */
    private static Long[] subsetArray() {
        Long[] tab = new Long[Color.COUNT];
        for(int i=0;i<Card.Color.COUNT;++i) {
            tab[i] = Bits64.mask(COLOR_LENGTH*i, BITS_COLOR_LENGTH);
        }
        return tab;
    }

    /**
     * Create the table for trump above which contains for each possible card the set
     * containing all the card that are better given that it's a card of the trump color 
     * @return: (long [][]): the table for trump above
     */
    private static long[][] tabForTrumpAbove(){

        long [][] tab = new long[Color.COUNT][Rank.COUNT];
        for (int i=0; i< Color.COUNT; ++i) {
            for (int j=0; j< Rank.COUNT; ++j) {
                tab [i][j]= betterCard(Color.ALL.get(i), Rank.ALL.get(j));
            }
        }
        return tab;
    }

    /**
     * Give the set containing all the card that are better than the card of the given color and rank given that 
     * the given color is the trump color
     * @param c (Color): the color of the card 
     * @param r (Rank): the rank of the card
     * @return (long): the set containing all the card that are better than the card of the given color and rank
     */
    private static long betterCard (Color c, Rank r) {
        Card card = Card.of(c, r);
        long set = EMPTY;

        for (Rank rank: Rank.ALL) {
            Card testedCard = Card.of(c, rank);
            if ( testedCard.isBetter(c, card)) {
                set =add(set, testedCard.packed()) ;
            }
        }
        return set;
    }
}
