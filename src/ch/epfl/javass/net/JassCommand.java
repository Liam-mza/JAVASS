package ch.epfl.javass.net;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Enumeration which contains the seven types of messages exchanged by the client and the server
 * @author Liam Mouzaoui (295797)
 * @author Remi Delacourt (300849)
 */
public enum JassCommand {
    /** setPlayers */
    PLRS, 
    /** setTrump */
    TRMP,
    /** updateHand */
    HAND,
    /** updateTrick */
    TRCK,
    /** cardToPlay */
    CARD, 
    /** updateScore */
    SCOR, 
    /** setWinningTeam */
    WINR,
    /** setHelp*/
    HELP,
    /** resetHelp*/
    RESH;

    
  /**The number of values of JassCommand **/
    public static final int COUNT = 9;
    
    /** List containing every values of the enumerated type PlayerId (in order of declaration) **/
    public static final List<JassCommand> ALL = Collections.unmodifiableList(Arrays.asList(values()));
    
}
