package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;

/**
 * Class which contains methods to manipulate the cards of the game
 */
public final class PackedCard {

    // Represent an invalid packed card
    public static final int INVALID= 0b111111;
    
    //Constant of the structure of a packed card representation
    private static final int START_RANK=0 , SIZE_RANK=4;
    private static final int START_COLOR=4 , SIZE_COLOR=2;  
    private static final int START_UNSUED=6, SIZE_UNSUED=26;
    /**
     * Constructor of PackedCard
     */
    private PackedCard(){};


    /**
     * Checks if the packed representation of a card is valid
     * @param pkCard (int): the packed representation of the card
     * @return (boolean): true if it is valid, and false otherwise
     */
    public static boolean isValid(int pkCard) {

        if ((Bits32.extract(pkCard, START_RANK, SIZE_RANK))<Card.Rank.COUNT && (Bits32.extract(pkCard, START_UNSUED, SIZE_UNSUED))==0) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Gives the packed representation of the card to the given color and rank 
     * @param c (Color): the color of the card
     * @param r (Rank): the rank of the card
     * @return (Card): the card of color c and rank r
     */
    public static int pack(Card.Color c, Card.Rank r) {

        int color= c.ordinal();
        int rank= r.ordinal();

        int packedCard = Bits32.pack(rank, SIZE_RANK, color , SIZE_COLOR);

        return packedCard;
    }


    /**
     * Gives the color of the given card
     * @param pkCard (int): the packed representation of the card
     * @return (Color): the color which corresponds to the given card
     */
    public static Card.Color color(int pkCard) {
        //assert isValid(pkCard);
        int color = Bits32.extract(pkCard, START_COLOR, SIZE_COLOR);
        return Card.Color.ALL.get(color);
    }

    /**
     * Gives the rank of the given card
     * @param pkCard (int): the packed representation of the card
     * @return (Rank): the rank which corresponds to the given card
     */
    public static Card.Rank rank(int pkCard) {
        //assert isValid(pkCard);
        int rank = Bits32.extract(pkCard, START_RANK, SIZE_RANK);      
        return Card.Rank.ALL.get(rank);
    }


    /**
     * Gives the point given by the card
     * @param trump (Color): The color which is the trump
     * @param pkCard (int): the specified card with its packed representation
     * @return (int): the point given by the card
     */

    public static int points(Card.Color trump, int pkCard) {

        int rank=Bits32.extract(pkCard, START_RANK, SIZE_RANK);

        if (trump.ordinal()== Bits32.extract(pkCard, START_COLOR, SIZE_COLOR)) {
            if (rank==3) {return 14;} 
            if (rank==5) {return 20;} 
        }

        switch (Card.Rank.ALL.get(rank)) {
        case TEN:
            return 10;
        case JACK:
            return 2;
        case QUEEN:
            return 3;
        case KING:
            return 4;
        case ACE:
            return 11;
        default:   
            return 0;
        }
    }

    /**
     * Compares the rank of the card pkCardL with the rank of the card pkCardR
     * @param trump (Color): The color which is the trump
     * @param pkCardL (int): The packed representation of the first card involved in the comparison
     * @param pkCardR (int): The packed representation of the second card involved in the comparison
     * @return (boolean): True if the first card is better, false otherwise
     */
    public static boolean isBetter(Card.Color trump, int pkCardL, int pkCardR) {

        if(color(pkCardL).equals(color(pkCardR))) {
            if(color(pkCardL).equals(trump)) {
                if(rank(pkCardL).trumpOrdinal() > rank(pkCardR).trumpOrdinal()) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                if(rank(pkCardL).ordinal() > rank(pkCardR).ordinal()) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        else {
            if(color(pkCardL).equals(trump)) {
                return true;
            }
            else {
                return false;
            }
        }
    }


    /**
     * Returns a representation of the card given as a string with first the symbol of the color followed by the
     * initial of the rank of the card
     * @param pkCard (int): the packed representation of the card
     * @return (String): a representation of the the card as explained above
     */
    public static String toString(int pkCard) {
        //assert isValid(pkCard);
        return color(pkCard).toString()+rank(pkCard).toString();
    }






}
