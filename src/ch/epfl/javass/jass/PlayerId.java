package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents the four players in the game
 */
public enum PlayerId {
    PLAYER_1,
    PLAYER_2,
    PLAYER_3,
    PLAYER_4;

    //List containing every values of the enumerated type PlayerId (in order of declaration)
    public static final List<PlayerId> ALL = Collections.unmodifiableList(Arrays.asList(values()));

    //The number of values of PlayerId
    public static final int COUNT = 4;

    /**
     * Identifies the team of the player
     * @return (TeamId) : The team of the player
     */
    public TeamId team() {
        if(this == PLAYER_1 || this == PLAYER_3) {
            return TeamId.TEAM_1;
        }
        return TeamId.TEAM_2;
    }

}

