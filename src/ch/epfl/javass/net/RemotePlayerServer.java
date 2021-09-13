package ch.epfl.javass.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;

/**
 * Class which represents a server of a player 
 * @author Liam Mouzaoui (295797)
 * @author Remi Delacourt (300849)
 */
public final class RemotePlayerServer {

    private Player localPlayer ;
    
  /**Port number **/
    protected static final int SERVER_PORT = 5108;
    
    
    /**
     * Constructor of RemotePlayerServer
     * @param localPlayer (Player) : the player type of the local player
     */
    public RemotePlayerServer(Player localPlayer) {
        this.localPlayer=localPlayer;
    }
    
    /**
     * Method that runs the server which allows to communicate between the host and the distant player
     * Waits for the connection of the distant player, then sends information to it. Finally, if the distant player
     * sent some information, reads it and interprets it.
     */
    public void run()  {
        try (ServerSocket sev0 = new ServerSocket(SERVER_PORT);
                Socket s = sev0.accept();
                BufferedReader r =
                  new BufferedReader(
                    new InputStreamReader(s.getInputStream(),
                            StandardCharsets.US_ASCII));
                BufferedWriter w =
                  new BufferedWriter(
                    new OutputStreamWriter(s.getOutputStream(),
                            StandardCharsets.US_ASCII))) {
            String str = new String();
            
            while ( (str= r.readLine())!=null) {
                String[] baseTab= StringSerializer.split(str, " ");
                String code = baseTab[0]; 
                switch(JassCommand.valueOf(code)) {
                
                case CARD:
                    String [] turnInfo = StringSerializer.split(baseTab[1], ",");
                    
                    TurnState turnState = TurnState.ofPackedComponents(StringSerializer.deserializeLong(turnInfo[0]), 
                                                                       StringSerializer.deserializeLong(turnInfo[1]), 
                                                                       StringSerializer.deserializeInt(turnInfo[2]));
                    
                    long packedHand = StringSerializer.deserializeLong(baseTab[2]);
                    int pkCard = localPlayer.cardToPlay(turnState, CardSet.ofPacked(packedHand)).packed();
                    String card = StringSerializer.serializeInt(pkCard);
                   
                    w.write(card);
                    w.write('\n');
                    w.flush();
                    break;
                    
                case HAND:
                    long pckHand = StringSerializer.deserializeLong(baseTab[1]);
                    CardSet hand = CardSet.ofPacked(pckHand);
                    localPlayer.updateHand(hand);
                    break;
                    
                case PLRS:
                    int ownIndex = StringSerializer.deserializeInt(baseTab[1]);
                    String[] nameTab = StringSerializer.split(baseTab[2], ",");
                    String[] helpTab = StringSerializer.split(baseTab[3], ",");
                    Map<PlayerId, String> playerNames = new TreeMap<>();
                    Map<PlayerId, Boolean> helpMap = new HashMap<>();
                    
                    for (int i=0; i<PlayerId.COUNT; ++i) {
                        playerNames.put(PlayerId.ALL.get(i),StringSerializer.deserializeString(nameTab[i]));
                        int needHelp= StringSerializer.deserializeInt(helpTab[i]);
                        helpMap.put(PlayerId.ALL.get(i), needHelp==1);
                    }
                    localPlayer.setPlayers(PlayerId.ALL.get(ownIndex), playerNames, helpMap);
                    break;
                    
                case SCOR:
                    long score = StringSerializer.deserializeLong(baseTab[1]);
                    localPlayer.updateScore(Score.ofPacked(score));
                    break;
                    
                case TRCK:
                    int trick = StringSerializer.deserializeInt(baseTab[1]);
                    localPlayer.updateTrick(Trick.ofPacked(trick));
                    break;
                    
                case TRMP:
                    int trump = StringSerializer.deserializeInt(baseTab[1]);
                    localPlayer.setTrump(Color.ALL.get(trump));
                    break;
                    
                case WINR:
                    int winner = StringSerializer.deserializeInt(baseTab[1]);
                    localPlayer.setWinningTeam(TeamId.ALL.get(winner));
                    break;
                case HELP:
                    int Id = StringSerializer.deserializeInt(baseTab[1]);
                    
                    String [] turn = StringSerializer.split(baseTab[2], ",");
                    
                    TurnState sate = TurnState.ofPackedComponents(StringSerializer.deserializeLong(turn[0]), 
                                                                       StringSerializer.deserializeLong(turn[1]), 
                                                                       StringSerializer.deserializeInt(turn[2]));
                    long packHand = StringSerializer.deserializeLong(baseTab[3]);
                    
                    localPlayer.setHelp(sate, CardSet.ofPacked(packHand), PlayerId.ALL.get(Id));
                    
                    break;
                    
                case RESH:
                    localPlayer.resetHelp();
                    break;
                    
                default:
                    System.err.println("Unknow Jass Command: "+code );
                    break;
                }
            }
            
            
        }
        catch(IOException e){
            throw new UncheckedIOException(e);
        }
    }
}
