package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents the two teams in the game
 */
public enum TeamId {
    TEAM_1,
    TEAM_2;

    //List containing every values of the enumerated type TeamId (in order of declaration)
    public static final List<TeamId> ALL = Collections.unmodifiableList(Arrays.asList(values()));

    //The number of values of TeamId
    public static final int COUNT = 2;

    /**
     * Gets the opposite team to which the method is applied to
     * @return (TeamId): TEAM_2 if applied to TEAM_1 and TEAM_1 if applied to TEAM_2
     */
    public TeamId other() {
        return (this==TEAM_1? TEAM_2:TEAM_1);
    }

}

