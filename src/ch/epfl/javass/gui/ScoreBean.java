package ch.epfl.javass.gui;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.javass.jass.TeamId;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Observable way to represent the scores of a game
 * @author Liam Mouzaoui (295797)
 * @author Remi Delacourt (300849)
 */
public final class ScoreBean {

    private  Map<TeamId,IntegerProperty> turnPointsMap;
    private  Map<TeamId,IntegerProperty> gamePointsMap;
    private  Map<TeamId,IntegerProperty> totalPointsMap;

    private ObjectProperty<TeamId> winningTeam;

    /** Constructor of ScoreBean **/
    public ScoreBean() {
        turnPointsMap = new HashMap<>();
        gamePointsMap = new HashMap<>();
        totalPointsMap = new HashMap<>();
        for (TeamId t: TeamId.ALL) {
            turnPointsMap.put(t, new SimpleIntegerProperty());
            gamePointsMap.put(t, new SimpleIntegerProperty());
            totalPointsMap.put(t, new SimpleIntegerProperty());
        }
        winningTeam = new SimpleObjectProperty<>(); 
    }  

    /**
     * Getter for the points of the turn of a given team
     * @param team (TeamId): the given team
     * @return (ReadOnlyIntegerProperty): the integer property which represents the turn points of the team
     */

    public ReadOnlyIntegerProperty turnPointsProperty(TeamId team) {
        return turnPointsMap.get(team);
    }

    /**
     * Sets the turn points of the given team
     * @param team (TeamId): the team to which we want to update the turn points
     * @param newTurnPoints
     */
    public void setTurnPoints (TeamId team, int newTurnPoints) {        
        turnPointsMap.get(team).set(newTurnPoints);
    }    

    /**
     * Getter for the game points of a team
     * @param team (TeamId): the team
     * @return (ReadOnlyIntegerProperty): the integer property associated to the game points of the team 
     */
    public ReadOnlyIntegerProperty gamePointsProperty(TeamId team) {
        return gamePointsMap.get(team);
    }

    /**
     * Sets the game points of the given team
     * @param team (TeamId): the team to which we want to update the game points
     * @param newGamePoints
     */
    public void setGamePoints (TeamId team, int newTurnPoints) {
        gamePointsMap.get(team).set(newTurnPoints);
    }  

    /**
     * Getter for the total points of a team
     * @param team (TeamId): the team
     * @return (ReadOnlyIntegerProperty): the integer property associated to the total points of the team
     */
    public ReadOnlyIntegerProperty totalPointsProperty(TeamId team) {
        return totalPointsMap.get(team);
    }

    /**
     * Sets the total points of the given team
     * @param team (TeamId): the team to which we want to update the total points
     * @param newTotalPoints (int): the new total points of the team
     */
    public void setTotalPoints (TeamId team, int newTurnPoints) {
        totalPointsMap.get(team).set(newTurnPoints);
    }

    /**
     * Method to get the winning team of the game
     * @return (ReadOnlyObjectProperty<TeamId>): the property associated to the team that won
     */
    public ReadOnlyObjectProperty<TeamId> winningTeamProperty() {
        return winningTeam;
    }

    /**
     * Sets the winning team of the game
     * @param winningTeam (TeamId): The winning team
     */
    public void setWinningTeam(TeamId winningTeam) {
        this.winningTeam.set(winningTeam);
    }    
}
