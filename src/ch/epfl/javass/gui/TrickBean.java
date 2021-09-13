package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * Trick bean is an observable way to represent a trick for the graphical interface
 * @author Liam Mouzaoui (295797)
 * @author Remi Delacourt (300849)
 */
public final class TrickBean {

    private ObjectProperty<Color> trump;

    private ObservableMap<PlayerId, Card > trick;

    private ObjectProperty<PlayerId> winningPlayer;
    private ObjectProperty<PlayerId> nextPlayer;

    /**
    * Constructor of the class TrickBean
    */
    public TrickBean() {
        trump =  new SimpleObjectProperty<>();
        winningPlayer = new SimpleObjectProperty<>();
        trick = FXCollections.observableHashMap();
        nextPlayer = new SimpleObjectProperty<>();
    }

    /**
    * Getter for the property of the trump color    
    * @return ( ReadOnlyObjectProperty<Color>): the property of the trump color  
    */
    public ReadOnlyObjectProperty<Color> trump() {
        return trump;
    }
    
    /**
    * Setter for the property of the trump color   
    * @param newTrump (Color): The new trump color
    */
    public void setTrumpProperty(Color newTrump) {
        trump.set(newTrump);
    }
    
    /**
     * getter for the trick property which is an observable map
     * @return (ObservableMap<PlayerId, Card>): the trick property
     */
    public ObservableMap<PlayerId, Card> trick() { 
        return FXCollections.unmodifiableObservableMap(trick);
    }
    
    /**
     * Setter for the trick property (Also take care to set the winning player and the next player)
     * @param newTrick (Trick): the new trick
     */
    public void setTrickProperty(Trick newTrick) {
        trick.clear();
        for (int i=0; i<newTrick.size(); ++i) {
            trick.put(newTrick.player(i), newTrick.card(i));
            
        }
        if(newTrick.isFull()) {nextPlayer.set(null);}
        else {
            TurnState turn =TurnState.ofPackedComponents(0, 0, newTrick.packed());
            nextPlayer.set(turn.nextPlayer());
        }
        if (newTrick.isEmpty()) { winningPlayer.set(null); }
        else {
            winningPlayer.set(newTrick.winningPlayer());
        }
    }
    
    /**
     * Getter for the winning player property of the trick 
     * @return (ReadOnlyObjectProperty<PlayerId>): the winning player of the trick 
     */
    public ReadOnlyObjectProperty<PlayerId> winningPlayer() {
        return winningPlayer;
    }
    
    /**
     * Getter for the next player property of the trick 
     * @return (ReadOnlyObjectProperty<PlayerId>): the next player of the trick 
     */
    public ReadOnlyObjectProperty<PlayerId> nextPlayer() {
        return nextPlayer;
    }
}
