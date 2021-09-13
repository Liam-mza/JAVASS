package ch.epfl.javass.jass;

public interface Jass {
    
    //The number of cards in a hand at the beginning of a round
    public static final int HAND_SIZE = 9;
    
    //The number of tricks in a round
    public static final int TRICKS_PER_TURN = 9;
    
    //The number of points needed to win
    public static final int WINNING_POINTS = 1000;
    
    //The number of additional points gained by a team taking every tricks of a round
    public static final int MATCH_ADDITIONAL_POINTS = 100;
    
    //The number of additional points gained by a team taking the last trick of a round
    public static final int LAST_TRICK_ADDITIONAL_POINTS = 5;

}
