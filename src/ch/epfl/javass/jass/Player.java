package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.jass.Card.Color;

/**
 * Interface that simulate a player
 */
public interface Player {
    
    /**
     * Returns the card that the player wishes to play among his hand of cards, knowing the state of the turn
     * @param state (TurnState): the state of the turn
     * @param hand (CardSet): the hand of cards of the player
     * @return (Card): the card that the player wishes to play
     */
    public abstract Card cardToPlay(TurnState state, CardSet hand);

    /**
     * Method to inform the player of his Player Id along with all the Ids of every other players in the game. 
     * Called only once at each beginning of a game.
     * @param ownId (PlayerId): the Id of the player
     * @param playerNames (Map<PlayerId, String>): the Ids of all the players
     */
    public default void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames, Map<PlayerId, Boolean> helpMap) {
        
    }


    /**
     * Called each time the hand of cards of the player changes.
     * @param newHand (CardSet): the updated hand of cards
     */
    public default void updateHand(CardSet newHand) {
        
    }

    /**
     * Called every time the trump changes to inform the player of the new trump
     * @param trump (Color): the new trump set for the turn
     */
    public default void setTrump(Color trump) {
        
    }

    /**
     * Called each time the state of the trick changes 
     * @param newTrick (Trick) : the updated trick
     */
    public default void updateTrick(Trick newTrick) {
        
    }
    
    /**
     * Called each time the score changes 
     * @param score (Score): the score of the game
     */
    public default void updateScore(Score score) {
        
    }

    /**
     * Called once a team has obtained the required points to win the game 
     * @param winningTeam (TeamId): the team that has won the game
     */
    public default void setWinningTeam(TeamId winningTeam) {
        
    }
    
    /**
     * Called when a player has the help activated and is going to play
     * @param state (TurnState): the state of the turn
     * @param hand (CardSet): the hand of cards of the player
     * @param id (PlayerId): the id of the player
     */
    public default void setHelp(TurnState state, CardSet hand, PlayerId id) {
        
    }
    
    /**
    * Called when a player, who has the help activated, finish to play
    */
    public default void resetHelp() {
        
    }
    
}
