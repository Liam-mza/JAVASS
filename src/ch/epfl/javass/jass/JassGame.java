package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;
import javafx.collections.ObservableMap;

public final class JassGame {

    private final Random shuffleRng;
    private final Random trumpRng;
    private final Map<PlayerId, Player>players;
    private final Map<PlayerId, String> playerNames;
    
    //The deck of card well-ordered in an increasing order
    private final List<Card> deck= createDeck(); 
    
    //The hand of the different players
    private Map<PlayerId, CardSet> handsOfCards;
    
    //The first player of the turn
    private PlayerId firstPlayer;
    
    //Say if it's the first turn of the game 
    private boolean isFirstTurn;
    
    //The TurnState of the game 
    private TurnState state;
    
    /**
     * Constructor of JassGame
     * @param rngSeed (long): the random seed
     * @param players (Map<PlayerId, Player>): the type of the players
     * @param playerNames (Map<PlayerId, String>): the names of the players
     */
    public JassGame(long rngSeed, Map<PlayerId, Player> players, Map<PlayerId, String> playerNames,Map<PlayerId, Boolean> helpMap){
        
        Random rng = new Random(rngSeed);
        
        this.shuffleRng = new Random(rng.nextLong());
        this.trumpRng = new Random(rng.nextLong());
        this.players = Collections.unmodifiableMap(new EnumMap<>(players));
        this.playerNames = Collections.unmodifiableMap(new EnumMap<>(playerNames));
        this.handsOfCards = new HashMap<PlayerId, CardSet>();
        this.isFirstTurn=true;

        for (Map.Entry<PlayerId, Player> pId: this.players.entrySet()) {
            pId.getValue().setPlayers(pId.getKey(), this.playerNames,helpMap);
        }
    }

    /**
     * Checks if the game is over (if one of the teams has reached the maximum number of points)
     * @return (boolean): true if the game is over, false otherwise
     */
    public boolean isGameOver() {
        if (!isFirstTurn) {
            if (state.score().totalPoints(TeamId.TEAM_1)>=Jass.WINNING_POINTS||state.score().totalPoints(TeamId.TEAM_2)>=Jass.WINNING_POINTS) {
                return true;
            }
            else {
                return false;
            }
        } 
        else {return false;}
    }

    /**
     * Create the deck of card non-shuffled
     * @return (List<Card>) the deck of card
     */
    private List<Card> createDeck(){
        List<Card> newDeck= new ArrayList<Card>();
        for (int i =0;i<CardSet.ALL_CARDS.size(); ++i ) {
            newDeck.add(CardSet.ALL_CARDS.get(i));
        }
        return newDeck;
    }

    /**
     * Shuffle and deals cards to the players
     */
    private void shuffleAndGiveCards() {

        List<Card> tmpDeck= new ArrayList<Card>(deck); 
        Collections.shuffle(tmpDeck, shuffleRng);
        
        for (int i=0; i<players.size(); ++i) {
            int startIndex= Jass.HAND_SIZE*i; //So for player 0 the startIndex will be 0, for player2 9, etc...
            int endIndex = Jass.HAND_SIZE*(i+1); //So for player 1 the endIndex will be 9, for player2 18, etc...

            CardSet hand= CardSet.of(tmpDeck.subList(startIndex, endIndex));
            PlayerId player = PlayerId.ALL.get(i);

            players.get(player).updateHand(hand);
            handsOfCards.put(player, hand); 
        }
    }

    /**
     * Spare method to find and set the first player to play at the beginning of each turn
     */
    private void  findFirstPlayer() {  
        if(isFirstTurn) {
            for(PlayerId p: PlayerId.ALL) {
                if(handsOfCards.get(p).contains(Card.of(Color.DIAMOND, Rank.SEVEN))) {
                    firstPlayer = p;
                }
            }
        }
        else {
            firstPlayer = PlayerId.ALL.get((firstPlayer.ordinal()+1)%PlayerId.COUNT);
        }
    }

    /**
     * Start a turn and with a randomly chosen Trump
     */
    private void startTurn() {
        
        handsOfCards.clear();
        shuffleAndGiveCards();
        
        Color trump= Color.ALL.get(trumpRng.nextInt(Color.COUNT));
        findFirstPlayer();

        for (Map.Entry<PlayerId, Player> pId: players.entrySet()) {
            pId.getValue().setTrump(trump);
        }
        
        if(isFirstTurn) {
            state = TurnState.initial(trump, Score.INITIAL, firstPlayer);
        }
        else {
            state = TurnState.initial(trump, state.score().nextTurn(), firstPlayer);
        }
    }

    /**
     * Makes the next player play
     */
    private void playNextPlayer() {
        
        PlayerId nextPlayer = state.nextPlayer();
        players.get(nextPlayer).setHelp(state, handsOfCards.get(nextPlayer), nextPlayer);
        Card c = players.get(nextPlayer).cardToPlay(state, handsOfCards.get(nextPlayer));
        state = state.withNewCardPlayed(c);
        
        handsOfCards.put(nextPlayer, handsOfCards.get(nextPlayer).remove(c));
        players.get(nextPlayer).updateHand(handsOfCards.get(nextPlayer));
        
        for (Map.Entry<PlayerId, Player> pId: players.entrySet()) {
            pId.getValue().updateTrick(state.trick());
        }
        players.get(nextPlayer).resetHelp();
    }

    /**
     * Starts a turn or collect the previous trick if there is a turn already started and makes the 
     * game advance to the end of the next trick by making the players play
     */
    public void advanceToEndOfNextTrick() {

        if (isGameOver()) {
            return;
        }
            
        if (isFirstTurn) {
            startTurn();
            isFirstTurn=false;
        }
        else {
            state=state.withTrickCollected();
            for (Map.Entry<PlayerId, Player> pId: players.entrySet()) {
                pId.getValue().updateScore(state.score());
            }
        }

        if (isGameOver()) {
            for (Map.Entry<PlayerId, Player> pId: players.entrySet()) {
                pId.getValue().setWinningTeam(findWinningTeam());
            }
            return;
        }

        if (!isFirstTurn && state.isTerminal()) {
            startTurn();
        }

        for (Map.Entry<PlayerId, Player> pId: players.entrySet()) {
            pId.getValue().updateScore(state.score());
            pId.getValue().updateTrick(state.trick());
        }

        while (!(state.trick().isFull())) {
            playNextPlayer();
        }
    }

    /**
     * When the game is over returns the winning team
     * @return (TeamId): the winning team
     */
    private TeamId findWinningTeam(){
        if (state.score().totalPoints(TeamId.TEAM_1)>=Jass.WINNING_POINTS) {
            return TeamId.TEAM_1;
        }
        else {
            return TeamId.TEAM_2;
        }
    }
}
