package ch.epfl.javass.gui;


import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Helpers;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * This class represent a player using a graphical interface
 * @author Liam Mouzaoui (295797)
 * @author Remi Delacourt (300849)
 */
public class GraphicalPlayerAdapter implements Player {

    private TrickBean trickBean;
    private HandBean handBean;
    private ScoreBean scoreBean;
    private GraphicalPlayer graphicalPlayer;
    private ArrayBlockingQueue<Card> comQueue;
    private ObservableMap <Card,String> helpHandMap;
    private boolean help;
    

    /** The capacity of the queue that communicate the card to play between the two thread **/
    private final static int ARRAY_CAPACITY = 1;

    /**
     * Constructor of the class GraphicalPlayerAdapter 
     */
    public GraphicalPlayerAdapter() {
        trickBean = new TrickBean();
        scoreBean = new ScoreBean();
        handBean = new HandBean();
        comQueue = new ArrayBlockingQueue<>(ARRAY_CAPACITY);
        help=false;
        
        helpHandMap= FXCollections.observableHashMap();
        
    }

    /**
     * Override of the method cardToPlay of player
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        CardSet playableCards = state.trick().playableCards(hand);
        Platform.runLater(()->handBean.setPlayableCards(playableCards));


        try {
            Card c = comQueue.take();
            Platform.runLater(()->handBean.setPlayableCards(CardSet.EMPTY));
            return c;
        } catch (InterruptedException e) {
            throw new Error();

        }
    }

    /**
     * Override of the method setPlayers of player
     */
    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames, Map<PlayerId, Boolean> helpMap) {
        boolean help =helpMap.get(ownId);
        this.help=help;
        
        graphicalPlayer = new GraphicalPlayer(ownId, playerNames, scoreBean, trickBean, handBean, comQueue,help, helpHandMap);
        Platform.runLater(() -> { graphicalPlayer.createStage().show(); });
    }

    /**
     * Override of the method updateHand of player
     */
    @Override
    public void updateHand(CardSet newHand) {
        Platform.runLater(() -> { handBean.setHand(newHand); });
    }

    /**
     * Override of the method  setTrump of player
     */
    @Override
    public void setTrump(Color trump) {
        Platform.runLater(() -> { trickBean.setTrumpProperty(trump); });
    }

    /**
     * Override of the method updateTrick of player
     */
    @Override
    public void updateTrick(Trick newTrick) {
        Platform.runLater(() -> { trickBean.setTrickProperty(newTrick); });
    }

    /**
     * Override of the method setWinningTeam of player
     */
    @Override
    public void setWinningTeam(TeamId winningTeam) {
        Platform.runLater(() -> { scoreBean.setWinningTeam(winningTeam); });
    }

    /**
     * Override of the method updateScore of player
     */
    @Override
    public void updateScore(Score score) {
        Platform.runLater(() -> {  
            for(TeamId t : TeamId.ALL) {
                scoreBean.setTurnPoints(t, score.turnPoints(t));
                scoreBean.setGamePoints(t, score.gamePoints(t));
                scoreBean.setTotalPoints(t, score.totalPoints(t));
            }
        });
    }

    /**
     * Override of the method setHelp of player
     */
    @Override
    public void setHelp(TurnState state, CardSet hand, PlayerId id) {
        
        Platform.runLater(() -> {
        helpHandMap.clear();
        for(int i=0; i<hand.size();++i) {
            helpHandMap.put(hand.get(i), null);
        }

        if(help) {
            Map<Card,String> results = Helpers.needHelp(state, hand, id);
            for (Map.Entry<Card, String> e: results.entrySet()) {
                helpHandMap.put(e.getKey(), String.format("%.5s", e.getValue()));
            }
        }
    });
        
    }
    
    /**
     * Override of the method resetHelp of player
     */
    @Override
    public void resetHelp() {
        helpHandMap.clear();  
    }
}

