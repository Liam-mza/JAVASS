package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

public final class PackedTrick {

    public static final int INVALID_INDEX_FOR_CARD=6; //just an arbitrary invalid index for a card in a trick
    public static final int MAX_CARD_PER_TRICK= 4;
    public static final int BITS_FOR_INDEX = 4, BITS_START_INDEX = 24;
    public static final int BITS_FOR_CARD = 6, CARD1_START_BITS = 0, CARD2_START_BITS = 6, CARD3_START_BITS = 12, CARD4_START_BITS = 18;
    public static final int BITS_FOR_PLAYER=2, BITS_START_PLAYER=28; 
    public static final int BITS_FOR_TRUMP=2, BITS_START_TRUMP=30;
    public static final int LAST_TRICK_INDEX = Jass.TRICKS_PER_TURN-1;




    public static final int INVALID = Bits32.mask(CARD1_START_BITS, Integer.SIZE);

    private PackedTrick() {}

    /**
     * Checks if the packed representation of the current trick is valid
     * @param pkTrick (int) : the packed representation of the current trick
     * @return (boolean) : true iff pkTrick is a valid packed trick, false otherwise
     */
    public static boolean isValid(int pkTrick) {
        if(Bits32.extract(pkTrick, BITS_START_INDEX, BITS_FOR_INDEX)<=8 && Bits32.extract(pkTrick, BITS_START_INDEX, BITS_FOR_INDEX)>=0) {
            boolean verif = false;
            for(int i=0;i<4;++i) {
                if(card(pkTrick, i)==PackedCard.INVALID) {
                    verif = true;
                }
                if(PackedCard.isValid(card(pkTrick,i)) && verif) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }



    /**
     * Returns an empty trick  with the given trump and first player 
     * @param trump (Color): the trump of the round
     * @param firstPlayer (PlayerId): the player to start
     * @return (int): the packed representation of the trick
     */
    public static int firstEmpty(Color trump, PlayerId firstPlayer) {
        int emptyTrick = Bits32.pack(PackedCard.INVALID, BITS_FOR_CARD , PackedCard.INVALID, BITS_FOR_CARD, PackedCard.INVALID, BITS_FOR_CARD, PackedCard.INVALID, BITS_FOR_CARD, 0, BITS_FOR_INDEX, firstPlayer.ordinal(), BITS_FOR_PLAYER, trump.ordinal(), BITS_FOR_TRUMP);
        return emptyTrick;
    }

    /**
     * Returns the next empty trick with the good trump, index and first player according to the given trick
     * @param pkTrick (int): The packed representation of the past trick
     * @return (int): the new reseted trick
     */
    public static int nextEmpty(int pkTrick) {
        if(index(pkTrick)==LAST_TRICK_INDEX) {
            return INVALID;
        }
        int trump = trump(pkTrick).ordinal();
        int index = index(pkTrick)+1;
        PlayerId winner = winningPlayer(pkTrick);
        int newPkTrick = Bits32.pack(PackedCard.INVALID, BITS_FOR_CARD, PackedCard.INVALID, BITS_FOR_CARD, PackedCard.INVALID, BITS_FOR_CARD, PackedCard.INVALID, BITS_FOR_CARD, index, BITS_FOR_INDEX, winner.ordinal(), BITS_FOR_PLAYER, trump, BITS_FOR_TRUMP);
        return newPkTrick;
    }  


    /**
     * Checks if it is the last trick of the turn
     * @param pkTrick (int): the packed representation of the current trick
     * @return (boolean): true if it is the last trick of the turn, false otherwise
     */
    public static boolean isLast(int pkTrick) {
        if(index(pkTrick)==LAST_TRICK_INDEX) {
            return true;
        }
        return false;
    }


    /**
     * Checks if the trick has no played cards yet
     * @param pkTrick (int): the packed representation of the trick
     * @return (boolean): true if no cards have been played during the trick, false otherwise
     */
    public static boolean isEmpty(int pkTrick) {
        if(card(pkTrick,0)==PackedCard.INVALID && card(pkTrick,1)==PackedCard.INVALID && card(pkTrick,2)==PackedCard.INVALID && card(pkTrick,3)==PackedCard.INVALID) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the trick is full or not
     * @param pkTrick (int): the packed representation of the trick
     * @return (boolean): true if the trick is full, false otherwise
     */
    public static boolean isFull(int pkTrick) {
        if(size(pkTrick)==MAX_CARD_PER_TRICK) {
            return true;
        } 
        return false;
    }

    /**
     * Returns the size of the trick (the number of cards that it contains)
     * @param pkTrick (int): the packed representation of the trick
     * @return (int): the number of cards contained in the trick
     */
    public static int size(int pkTrick) {
        
        int size =0;
        for(int i=0; i<MAX_CARD_PER_TRICK; ++i ) {
            if (card(pkTrick,i)!=PackedCard.INVALID) {++size;}
        }
        return size;
    }

    /**
     * Gives the trump of the trick
     * @param pkTrick (int): the packed representation of the trick
     * @return (Color): the trump of the trick
     */
    public static Color trump(int pkTrick) {
        int col= Bits32.extract(pkTrick, BITS_START_TRUMP, BITS_FOR_TRUMP);
        return Color.ALL.get(col);
    }

    /**
     * Returns the index-th player that played  in the trick, i.e. if player 1 placed the second card then 
     * he is the player of index 1.
     * @param pkTrick (int): The packed representation of the trick
     * @param index (int): the index of the wanted player
     * @return (PlayerId): the player associated to the given index 
     */
    public static PlayerId player(int pkTrick, int index) {
        int player= Bits32.extract(pkTrick, BITS_START_PLAYER, BITS_FOR_PLAYER);
        int playerToGet= ((index+player)%PlayerId.COUNT);
        return PlayerId.ALL.get(playerToGet);
    }

    /**
     * Returns the index of the trick, i.e the "index"'th trick
     * @param pkTrick (int): the packed representation of the trick
     * @return (int): the index of the trick
     */
    public static int index(int pkTrick) {
        return Bits32.extract(pkTrick, BITS_START_INDEX, BITS_FOR_INDEX);
    }

    /**
     * Returns a packed representation of the wanted card determined by its index in the trick
     * @param pkTrick (int): the packed representation of the trick
     * @param index (int): the index of the card
     * @return (int): the packed representation of the wanted card 
     */
    public static int card(int pkTrick, int index) {
        return Bits32.extract(pkTrick, BITS_FOR_CARD*index, BITS_FOR_CARD);
    }

    /**
     * Adds the specified card in the trick, and returns the updated trick
     * @param pkTrick (int): the packed representation of the trick
     * @param pkCard (int): the packed card we want to add to the trick
     * @return (int): the updated packed representation of the trick
     */
    public static int withAddedCard(int pkTrick, int pkCard) {
        int trickSize = size(pkTrick);
        pkTrick = pkTrick & (~ Bits32.mask((BITS_FOR_CARD*trickSize), BITS_FOR_CARD)); //this line works because the trick is not supposed to be full
        pkCard = pkCard << (BITS_FOR_CARD*trickSize);
        pkTrick = pkTrick| pkCard; 
        return pkTrick;
    }


    /**
     * Returns the base color of the trick, which is the color of the first card placed 
     * @param pkTrick (int): the packed representation of the trick
     * @return (Color): the base color of the trick
     */
    public static Color baseColor(int pkTrick) {
        int card = card(pkTrick, 0);
        return PackedCard.color(card);
    }

    /**
     * Gets the value of the trick, taking into account the additional 5 points if it is the last trick of the round
     * @param pkTrick (int): the packed representation of the trick
     * @return (int): the value of the trick
     */ 
    public static int points(int pkTrick) {
        int trickPoints =0;
        for (int i=0; i<size(pkTrick); ++i) {
            trickPoints += PackedCard.points(trump(pkTrick), card(pkTrick,i));
        }
        if(isLast(pkTrick)) {trickPoints +=Jass.LAST_TRICK_ADDITIONAL_POINTS;}
        return trickPoints;
    }

    /**
     * Returns the player that is currently leading the trick
     * @param pkTrick (int): the packed representation of the trick
     * @return (PlayerId): the player leading the trick
     */
    public static PlayerId winningPlayer(int pkTrick) {
        int index =0;
        int bestCard = card(pkTrick,0);
        for (int i=0; i< size(pkTrick); ++i) {
            if (PackedCard.isBetter(trump(pkTrick),card(pkTrick,i) , bestCard)) {
                bestCard = card(pkTrick,i);
                index =i;
            }
        }
        return player(pkTrick, index);  
    }


    /**
     * Returns a packed set of cards which contains all the cards of a hand that can be played depending on the cards
     * in the trick
     * @param pkTrick (int): the packed representation of the trick
     * @param pkHand (long): the packed representation of the hand of cards
     * @return (long): all the cards of the hand that can be played depending on the current cards in the trick
     */
    public static long playableCards(int pkTrick, long pkHand) {

        if(card(pkTrick,0)==PackedCard.INVALID) {
            return pkHand;
        }

        //Dividing the hand in four set of card depending on the "nature" of the card
        Color trump = trump(pkTrick);
        long setOfTrump = PackedCardSet.subsetOfColor(pkHand, trump);
        long setOfHigherTrump = findTrumpAbove( pkTrick, pkHand,trump);
        long setOfLowerTrump = PackedCardSet.difference(setOfTrump, setOfHigherTrump);
        long setOfBaseColor = PackedCardSet.subsetOfColor(pkHand, baseColor(pkTrick));
        long setOfOtherColor = PackedCardSet.difference(pkHand, PackedCardSet.union(setOfTrump, setOfBaseColor));

        //Test to return the good set of card
        if (setOfTrump == PackedCardSet.singleton(PackedCard.pack(trump, Rank.JACK))&& baseColor(pkTrick) == trump){
            return pkHand;
        }
        else if(setOfBaseColor!=PackedCardSet.EMPTY ) {
            return PackedCardSet.union(setOfBaseColor, setOfHigherTrump);
        }
        else if(setOfOtherColor!=PackedCardSet.EMPTY) {
            return PackedCardSet.union(setOfOtherColor,setOfHigherTrump);

        }
        else if(setOfHigherTrump!=PackedCardSet.EMPTY) {
            return setOfHigherTrump;
        }
        else {return setOfLowerTrump; }
    }

    /**
     * Returns a representation of the trick, which is represented as a string with the card contained in it, the index of
     * the trick, the player that won the last trick, and the trump
     * @param pkTrick (int): the packed representation of the trick
     * @return (String): the string that represents the trick
     */
    public static String toString(int pkTrick) {
        StringBuilder s = new StringBuilder();
        for (int i=0; i<size(pkTrick);++i) {
            s.append(PackedCard.toString(card(pkTrick,i))+" ");
        }
        return "{"+s.toString()+","+"Trick: "+index(pkTrick)+","+"First player: "
        +player(pkTrick, 0).toString()+","+"Trump: "+trump(pkTrick).toString()+"}";
    }

    /**
     * Spare method to extract all the trump cards of the hand that are higher than the highest trump card placed in the trick
     * @param pkTrick (int): the packed representation of a trick
     * @param pkHand (long): the packed representation of a hand
     * @return (Long): The card set of all the trump cards from the hand that are higher than the highest trump card in the trick
     */
    private static long findTrumpAbove(int pkTrick, long pkHand, Color trump) {

        int size =size(pkTrick);
        int bestCard= PackedCard.INVALID;

        for (int i=0; i<size; ++i) {
            if ( PackedCard.color(card(pkTrick,i))==trump) {
                if (bestCard == PackedCard.INVALID) { bestCard=card(pkTrick,i); }
                else {
                    if (PackedCard.isBetter(trump, card(pkTrick, i), bestCard)) {bestCard=card(pkTrick,i);}
                }
            }
        }

        if (bestCard == PackedCard.INVALID) {
            return PackedCardSet.subsetOfColor(pkHand, trump);
        }
        else {
            return PackedCardSet.intersection(pkHand, PackedCardSet.trumpAbove(bestCard));
        }
    }

}
