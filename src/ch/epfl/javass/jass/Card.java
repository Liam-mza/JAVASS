package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.epfl.javass.Preconditions;

/**
 * Represents a card of the game 
 */
public final class Card {

    //Packed representation of the card
    private final int pcknum;


    /**
     * Constructor of Card
     * @param pcknum (int): the packed representation of the card
     */
    private Card(int pcknum) {

        this.pcknum=pcknum;
    }


    /**
     * Returns a card of the given color and rank
     * @param c (Color): the color of the card
     * @param r (Rank): the rank of the card
     * @return (Card): the card of given color and rank
     */
    public static Card of(Color c, Rank r) {

        return new Card(PackedCard.pack(c, r));
    }


    /**
     * Returns the card which its packed representation is the parameter packed
     * @param  packed (int): the packed representation of the card 
     * @throws IllegalArgumentException: if the given packed representation of the card is invalid 
     * @return (Card): the card which corresponds to the integer packed
     */
    public static Card ofPacked(int packed) {
        Preconditions.checkArgument(PackedCard.isValid(packed));

        return new Card(packed); 
    }


    /**
     * Returns the packed representation of the card
     * @return (int): the packed representation of the card
     */
    public int packed() {
        return pcknum;
    }

    /**
     * Returns the color of the card from the packed representation of the card
     * @return (Color): the color of the card
     */
    public Color color() {
        return PackedCard.color(pcknum);
    }

    /**
     * Returns the rank of the card from the packed representation of the card
     * @return (Rank): the rank of the card
     */
    public Rank rank() {
        return PackedCard.rank(pcknum);
    }

    /**
     * Compares the rank of the card to which we apply the method with the rank of the given card 
     * @param trump (Color): The color which is the trump
     * @param that (Card): the card to compare to
     * @return (Boolean): true if the card to which we apply the method is superior than the other one, and false otherwise
     */
    public boolean isBetter(Color trump, Card that) {
        return PackedCard.isBetter(trump, this.packed(), that.packed());
    }

    /**
     * Give the number of point the card give
     * @param trump (Color) : The color which is the trump
     * @return (int): the number of point
     */
    public int points(Color trump) {
        return PackedCard.points(trump, pcknum);
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
            if (thatO instanceof Card) {
                return (this.pcknum== ((Card)thatO).pcknum);
            }
            else {return false;}
        }  
    }

    /**
     * Override of the method hashCode
     */
    @Override
    public int hashCode() {
        return this.packed();
    }

    /**
     * Override of the method toString
     */
    @Override
    public String toString() {
        return PackedCard.toString(pcknum);
    }



    /**
     *Represents the color of the card
     */
    public enum Color {
        SPADE("\u2660"), //♠
        HEART("\u2665"), //♥
        DIAMOND("\u2666"), //♦
        CLUB("\u2663"); //♣


        //List containing every values of the enumerated type Color (in order of declaration)
        public static final List<Color> ALL = Collections.unmodifiableList(Arrays.asList(values()));

        //The number of values of Color
        public static final int COUNT = 4;

        //The symbol of the color
        private final String symbol;

        /**
         * Constructor of Color
         * @param symbol (String): the string representing the symbol of the color
         */
        private Color(String symbol) {
            this.symbol=symbol;
        }

        /**
         * Override of the method toString of the class Object 
         * @return (String): the symbol associated to the color
         */
        @Override
        public String toString() {
            return symbol;
        }
    }

    /**
     *Represents the rank of the card
     */
    public enum Rank {
        SIX("6",0),
        SEVEN("7",1),
        EIGHT("8",2),
        NINE("9",7),
        TEN("10",3),
        JACK("J",8),
        QUEEN("Q",4),
        KING("K",5),
        ACE("A",6);

        //The name of the rank of the card
        private final String rankName;

        //The ordinal of the when it's trump 
        private final int trumpRank;

        //List containing every values of the enumerated type Rank (in order of declaration)
        public static final List<Rank> ALL = Collections.unmodifiableList(Arrays.asList(values()));

        //The number of values of Rank
        public static final int COUNT = 9;

        /**
         * Constructor of Rank
         * @param rankName (String): the string representing the symbol of the rank
         * @param trumpRank (int): the Trumprank of the rank
         */
        private Rank(String rankName, int trumpRank) {
            this.rankName = rankName;
            this.trumpRank=trumpRank;
        }


        /**
         * Get the position associated to the rank of the trump card (between 0 and 8)
         * @return (int): this position
         */
        public int trumpOrdinal() {
            return trumpRank;
        }

        /**
         * Override of the method toString of the class Object 
         * @return (String): a compact representation of every card name of a certain color
         */
        @Override
        public String toString() {
            return rankName;
        }
    }

}

