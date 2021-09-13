package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;

public final class TurnState {
    
    private final long currentScore;
    private final long unplayedCards;
    private final int currentTrick;
    
    
    /**
     * private constructor of TurnState
     * @param currentScore (long): the packed representation of the score of the current game
     * @param cardsNotYetPlayed (long):the packed representation of the set of unplayed cards in the turn
     * @param currentTrick (int): the current trick of the turn
     */
    private TurnState(long currentScore,long cardsNotYetPlayed, int currentTrick ) {
        this.currentScore=currentScore;
        this.unplayedCards= cardsNotYetPlayed;
        this.currentTrick=currentTrick;
    }
    
    /**
     * Returns the initial state of a turn
     * @param trump (Color): the chosen trump of the turn
     * @param score (Score): the score of the current game
     * @param firstPlayer (PlayerId): the player to start the first trick of the turn
     * @return (TurnState): the initial state of a turn
     */
    public static TurnState initial(Color trump, Score score, PlayerId firstPlayer) {
        int initialTrick = PackedTrick.firstEmpty(trump, firstPlayer);
        return new TurnState(score.packed(), PackedCardSet.ALL_CARDS, initialTrick);
    }
    
    /**
     * Returns the TurnSate with the given score, unplayed cards set and trick
     * @param pkScore (long): the packed representation of the score of the current game
     * @param pkUnplayedCards (long): the packed representation of the set of unplayed cards in the turn
     * @param pkTrick (int): the current trick of the turn
     * @throws IllegalArgumentException: if one of the three given packed representation is invalid
     * @return (TurnState):the TurnSate with the given score, unplayed cards set and trick
     */
    public static TurnState ofPackedComponents(long pkScore, long pkUnplayedCards, int pkTrick){
        
        Preconditions.checkArgument(PackedScore.isValid(pkScore));
        Preconditions.checkArgument(PackedCardSet.isValid(pkUnplayedCards));
        Preconditions.checkArgument(PackedTrick.isValid(pkTrick));
        
        return new TurnState(pkScore,pkUnplayedCards,pkTrick);
    }
    
    /**
     * Getter for the packed representation of the current score 
     * @return (long): the score of the game
     */
    public long packedScore() {
        return currentScore;
    }
    
    /**
     * Getter for the packed representation of the set of cards not yet played in the turn 
     * @return (long): the set of cards not yet played in the turn
     */
    public long packedUnplayedCards() {
        return unplayedCards;
    }
    
    /**
     * Getter for the packed representation of the current trick of the turn
     * @return (int): the current trick of the turn
     */
    public int packedTrick() {
        return currentTrick;
    }

    /**
     * Getter for the the score of the turn
     * @return (Score): the score of the turn
     */
    public Score score() {
        return Score.ofPacked(currentScore);
    }
    
    /**
     * Getter for the set of cards not yet played in the turn 
     * @return (CardSet): the set of cards not yet played in the turn
     */
    public CardSet unplayedCards() {
        return CardSet.ofPacked(unplayedCards);
    }
    
    /**
     * Getter for the current trick of the turn
     * @return (Trick): the current trick of the turn
     */
    public Trick trick() {
        return Trick.ofPacked(currentTrick);
    }
    
    /**
     * Checks if the last trick of the turn has been played
     * @return (boolean) : true if it is the last trick of the turn and the trick is full, false otherwise
     */
    public boolean isTerminal() {
        return currentTrick==PackedTrick.INVALID ;
    }
    
    /**
     * Return the next player that has to play
     * @throws IllegalStateException: if the trick is full
     * @return (PlayerId): the next player that has to play
     */
   public PlayerId nextPlayer() {
       if(PackedTrick.isFull(currentTrick)) {throw new IllegalStateException();}
       return PackedTrick.player(currentTrick,PackedTrick.size(currentTrick) );
   }
   
   

     /**
      * Returns the updated state of the turn, with the given card added to the current trick
      * @param card (Card): the new card to add to the current trick
      * @throws IllegalStateException: if the trick is full
      * @return (TurnState): the updated state of the turn
      */
     public TurnState withNewCardPlayed(Card card) {
         if(PackedTrick.isFull(currentTrick)) {throw new IllegalStateException();}
         
         int newCurrentTrick = PackedTrick.withAddedCard(currentTrick, card.packed());
         long newUnplayedCards = PackedCardSet.remove(unplayedCards, card.packed());
         return new TurnState(currentScore, newUnplayedCards, newCurrentTrick);
     }
     
     /**
      * Gives the turn state after that the trick has been collected
      * @throws IllegalStateException: if the trick is not full
      * @return (TurnState): the turn state after that the trick has been collected
      */
     public TurnState withTrickCollected() {
         if(!PackedTrick.isFull(currentTrick)) {throw new IllegalStateException();}
         
         long score = PackedScore.withAdditionalTrick(currentScore, PackedTrick.winningPlayer(currentTrick).team(), PackedTrick.points(currentTrick));
         return new TurnState(score, unplayedCards,PackedTrick.nextEmpty(currentTrick));
     }

     /**
      * Add  the given card to the trick of the turn and if the trick become full the trick is collected
      * @param card (Card): the card to be added
      * @throws IllegalStateException: if the trick is full
      * @return (TurnState): the updated state of the turn
      */
     public TurnState withNewCardPlayedAndTrickCollected(Card card) {
         if (this.trick().isFull()) {throw new IllegalStateException();}
         
         TurnState updatedState = this.withNewCardPlayed(card);
         
         if (updatedState.trick().isFull()) {
         updatedState = updatedState.withTrickCollected();
         }
         return updatedState;
     }
}
