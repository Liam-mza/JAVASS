package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.bits.Bits64;

public final class PackedScore {

    //Initial score when the game starts
    public static final long INITIAL = 0L;

    //Constants relative to the stucture of the packed representation of a score
    public static final int BITS_FOR_TRICKS = 4, BITS_FOR_TURNPOINTS = 9, BITS_FOR_GAMEPOINTS = 11;
    public static final int STARTING_BIT_TRICKS = 0;
    public static final int STARTING_BIT_TURNPOINTS = STARTING_BIT_TRICKS+BITS_FOR_TRICKS;
    public static final int STARTING_BIT_GAMEPOINTS = STARTING_BIT_TURNPOINTS+BITS_FOR_TURNPOINTS;
    public static final int FULL_SCORE_BITLENGTH = 32;

    //Same constants as given above, but associated to TEAM_1
    public static final int T1_STARTING_BIT_TRICKS = STARTING_BIT_TRICKS;
    public static final int T1_STARTING_BIT_TURNPOINTS = T1_STARTING_BIT_TRICKS+BITS_FOR_TRICKS;
    public static final int T1_STARTING_BIT_GAMEPOINTS = T1_STARTING_BIT_TURNPOINTS+BITS_FOR_TURNPOINTS;

    //Same constants as given above, but associated to TEAM_2
    public static final int T2_STARTING_BIT_TRICKS = 32;
    public static final int T2_STARTING_BIT_TURNPOINTS = T2_STARTING_BIT_TRICKS+BITS_FOR_TRICKS;
    public static final int T2_STARTING_BIT_GAMEPOINTS = T2_STARTING_BIT_TURNPOINTS+BITS_FOR_TURNPOINTS;

  //Constants relative to the unused bits
    public static final int ZERO_STARTING_BIT_T1 = 24, ZERO_STARTING_BIT_T2 = ZERO_STARTING_BIT_T1+T2_STARTING_BIT_TRICKS;
    public static final int ZERO_BITS_LENGTH = 8;

    public static final int MAX_POINTS_PER_TURN = 257, MAX_POINTS_PER_GAME = 2000;



    /**
     * Construtor of PackedScore
     */
    private PackedScore() {
    }

    /**
     * Checks if the packed representation of the points is valid
     * @param pkScore (long): the packed representation of the score
     * @return true if it is valid, false otherwise
     */
    public static boolean isValid(long pkScore) {
        if(Bits64.extract(pkScore, T1_STARTING_BIT_TRICKS, BITS_FOR_TRICKS)<=Jass.TRICKS_PER_TURN && 
                Bits64.extract(pkScore, T1_STARTING_BIT_TURNPOINTS, BITS_FOR_TURNPOINTS)<=MAX_POINTS_PER_TURN &&
                Bits64.extract(pkScore, T1_STARTING_BIT_GAMEPOINTS, 11)<=MAX_POINTS_PER_GAME &&
                Bits64.extract(pkScore, ZERO_STARTING_BIT_T1, ZERO_BITS_LENGTH)==INITIAL &&
                Bits64.extract(pkScore, T2_STARTING_BIT_TRICKS, BITS_FOR_TRICKS)<=Jass.TRICKS_PER_TURN &&
                Bits64.extract(pkScore, T2_STARTING_BIT_TURNPOINTS, BITS_FOR_TURNPOINTS)<=MAX_POINTS_PER_TURN &&
                Bits64.extract(pkScore, T2_STARTING_BIT_GAMEPOINTS, BITS_FOR_GAMEPOINTS)<=MAX_POINTS_PER_GAME &&
                Bits64.extract(pkScore, ZERO_STARTING_BIT_T2, ZERO_BITS_LENGTH)==INITIAL) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Takes 6 integers and packs them into one integer by putting them side by side in their binary representation to represent a score
     * @param turnTricks1 (int): the first integer we want to pack (represents the number of tricks of team 1)
     * @param turnPoints1 (int): the second integer we want to pack (represents the number of points that team 1 scored during the current turn)
     * @param gamePoints1 (int): the third integer we want to pack (represents the number of points that team 1 scored during the full game)
     * @param turnTricks2 (int): the fourth integer we want to pack (represents the number of tricks of team 2)
     * @param turnPoints2 (int): the fith integer we want to pack (represents the number of points that team 2 scored during the current turn)
     * @param gamePoints2 (int): the sixth integer we want to pack (represents the number of points that team 2 scored during the full game)
     * @return (long): the 64 bit integer that results from packing all six initial integers
     */
    public static long pack(int turnTricks1, int turnPoints1, int gamePoints1, int turnTricks2, int turnPoints2, int gamePoints2) {
        long packedScore1 = Bits32.pack(turnTricks1, BITS_FOR_TRICKS, turnPoints1, BITS_FOR_TURNPOINTS, gamePoints1, BITS_FOR_GAMEPOINTS);
        long packedScore2 = Bits32.pack(turnTricks2, BITS_FOR_TRICKS, turnPoints2, BITS_FOR_TURNPOINTS, gamePoints2, BITS_FOR_GAMEPOINTS);
        long packedTotScore = Bits64.pack(packedScore1, FULL_SCORE_BITLENGTH, packedScore2, FULL_SCORE_BITLENGTH);
        return packedTotScore;
    }

    /**
     * Gives the number of tricks obtained by the given team
     * @param pkScore (long): the packed representation of the score
     * @param t (TeamId): the team we want to know the points of
     * @return (int): The number of tricks obtained by the given team
     */
    public static int turnTricks(long pkScore, TeamId t) {
       
        return extractScore(pkScore, STARTING_BIT_TRICKS, BITS_FOR_TRICKS, t);
    }

    /**
     * Gives the number of points scored by the given team during the current turn
     * @param pkScore (long): the packed representation of the score
     * @param t (TeamId): the team we want to know the points of
     * @return (int): The number of points scored by the given team 
     */
    public static int turnPoints(long pkScore, TeamId t) {
       
        return extractScore(pkScore, STARTING_BIT_TURNPOINTS, BITS_FOR_TURNPOINTS, t);
    }

    /**
     * Gives the number of points scored by the given team during the full game
     * @param pkScore (long):the packed representation of the score
     * @param t (TeamId): the team we want to know the points of
     * @return (int):The number of points scored by the given team during the game
     */
    public static int gamePoints(long pkScore, TeamId t) {
        
        return extractScore(pkScore, STARTING_BIT_GAMEPOINTS, BITS_FOR_GAMEPOINTS, t);
    }

    /**
     * Sums up the points scored by the given team during the game with the points scored by this team during the current 
     * turn, to get a total of the points scored by this team
     * @param pkScore (long): the packed representation of the score
     * @param t (TeamId): the  given team
     * @return  (int):The total points scored by the given team
     */
    public static int totalPoints(long pkScore, TeamId t) {
       
        return turnPoints(pkScore, t)+gamePoints(pkScore, t);
    }

    /**
     * A spare method that allows us to extract the scores we need in the above methods for the given team
     * @param pkScore (long): the packed representation of the score
     * @param start (int): the index of the first bit we want to extract
     * @param size (int): the number of bits we want to extract
     * @param team (TeamId): the specified team
     * @return (int): the extracted score
     */
    private static int extractScore(long pkScore, int start, int size, TeamId team) {
        if(team==TeamId.TEAM_2) {
            start+=T2_STARTING_BIT_TRICKS;
        }
        return (int)Bits64.extract(pkScore, start, size);
    }


    /**
     * Adds up the points of the trick to the turnpoints of the team that won the trick and add one to its turntrick
     * @param pkScore (long): the packed representation of all the scores
     * @param winningTeam (TeamId): the team that won the trick
     * @param trickPoints (int): the points won by the given team during the round
     * @return (long): the updated packed representation the scores with the updated scores
     */
    public static long withAdditionalTrick(long pkScore, TeamId winningTeam, int trickPoints) {
        
        int start= STARTING_BIT_TURNPOINTS;
        if(winningTeam ==TeamId.TEAM_2) {
            start=T2_STARTING_BIT_TURNPOINTS;  
        }

        pkScore += (1L<< start-BITS_FOR_TRICKS);

        long turnPoint= (turnPoints(pkScore, winningTeam)+trickPoints);

        if(turnTricks(pkScore,winningTeam)== Jass.TRICKS_PER_TURN) {
            turnPoint+= Jass.MATCH_ADDITIONAL_POINTS;
        }

        turnPoint=turnPoint<<start;

        long mask=(~(Bits64.mask(start, BITS_FOR_TURNPOINTS)));

        pkScore = pkScore & mask;

        pkScore = pkScore |turnPoint;

        return pkScore;
    }

    /**
     * add the turn points to the game points of each team and reset their turn points and turn tricks
     * @param pkScore (long): the packed representation of all the scores
     * @return (long): the updated scores for the next turn
     */
    public static long nextTurn(long pkScore) {

        long score1=reinitScore(Bits64.extract(pkScore, STARTING_BIT_TRICKS, FULL_SCORE_BITLENGTH));
        long score2=reinitScore(Bits64.extract(pkScore, T2_STARTING_BIT_TRICKS, FULL_SCORE_BITLENGTH));

        return Bits64.pack(score1, FULL_SCORE_BITLENGTH, score2, FULL_SCORE_BITLENGTH);
    }

    /**
     * A spare method to reset the scores of a team as explained in the method nextTurn
     * @param score (long): the packed representation of the scores of a team
     * @return (long): the updated scores of the team
     */
    private static long reinitScore(long score) {

        long gamePoints = totalPoints(score, TeamId.TEAM_1);

        score =INITIAL;

        gamePoints = gamePoints<<STARTING_BIT_GAMEPOINTS;

        score = score | gamePoints;

        return score;
    }

    /**
     * Give a representation of the score 
     * @param pkScore (long) the packed representation of all the scores
     * @return (String): the representation of the scores
     */
    public static String toString(long pkScore) {
        String s1= "("+ turnTricks(pkScore, TeamId.TEAM_1)+","+turnPoints(pkScore, TeamId.TEAM_1)+","+gamePoints(pkScore, TeamId.TEAM_1)+")";
        String s2= "("+ turnTricks(pkScore, TeamId.TEAM_2)+","+turnPoints(pkScore, TeamId.TEAM_2)+","+gamePoints(pkScore, TeamId.TEAM_2)+")";

        return s1+"/"+s2;
    }



}
