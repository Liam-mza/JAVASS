package ch.epfl.javass.gui;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Jass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

/**
 * Observable way to represent the hands in a game
 * @author Liam Mouzaoui (295797)
 * @author Remi Delacourt (300849)
 */
public final class HandBean {

    private ObservableList<Card> hand;

    private ObservableSet<Card> playableCards;

    /**
     * Constructor of HandBean
     */
    public HandBean() {
        List<Card> tmpHand = new ArrayList<>();
        for (int i=0; i<Jass.HAND_SIZE;++i) {
            tmpHand.add(null);
        }
        
        hand = FXCollections.observableArrayList(tmpHand);

        playableCards = FXCollections.observableSet();
    }

    /**
     * Gets the hand of cards
     * @return (ObservableList<Card>): the observable list of the hand
     */
    public ObservableList<Card> hand() {
        return FXCollections.unmodifiableObservableList(hand);
    }

    /**
     * Sets the hand of cards 
     * @param newHand (CardSet): the new hand of cards
     */
    public void setHand(CardSet newHand) {

        for (int i=0; i<hand.size(); ++i) {
            if (newHand.size()==Jass.HAND_SIZE) {
                hand.set(i, newHand.get(i));
            }else {
                if (hand.get(i)!=null && !newHand.contains(hand.get(i))) {
                    hand.set(i, null);
                }
            }
        }
    }

    /**
     * Gets the the cards that are playable among the hand of cards
     * @return (ObservableSet<Card>): a set of cards containing the playable cards
     */
    public ObservableSet<Card> playableCards() {
        return FXCollections.unmodifiableObservableSet(playableCards);
    }

    /**
     * Sets the playable cards 
     * @param newPlayableCards (CardSet): the card set which contains the playable cards
     */
    public void setPlayableCards(CardSet newPlayableCards) {
        playableCards.clear();
        for (int i=0; i<newPlayableCards.size();++i) {
            playableCards.add(newPlayableCards.get(i));
        }
    }
}
