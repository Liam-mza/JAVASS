package ch.epfl.javass.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;

/**
 * Class which represents the client for a remote player 
 * @author Liam Mouzaoui (295797)
 * @author Remi Delacourt (300849)
 */
public final class RemotePlayerClient implements Player, AutoCloseable {

    private Socket s;
    private BufferedReader r;
    private BufferedWriter w;

    
    
    /**
     * Constructor of RemotePlayerClient
     * @param hostName (String): the name of the host on which the server of the distant player executes itself
     * @throws IOException if I/O operations are interrupted
     */
    public RemotePlayerClient(String hostName) throws IOException {
        s = new Socket(hostName, RemotePlayerServer.SERVER_PORT);
        r = new BufferedReader(new InputStreamReader(s.getInputStream(),StandardCharsets.US_ASCII));
        w = new BufferedWriter( new OutputStreamWriter(s.getOutputStream(),StandardCharsets.US_ASCII));
    }


    /**
     * Close All the different streams
     */
    @Override
    public void close() throws Exception {
        try{
            s.close();
            r.close();
            w.close();
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * override of the method cardToPlay of player
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {

        //Serialization and combination of the different informations we need to send 
        String keyWord = JassCommand.CARD.name();

        String score = StringSerializer.serializeLong(state.packedScore());
        String unplayedCard = StringSerializer.serializeLong(state.packedUnplayedCards());
        String trick = StringSerializer.serializeInt(state.packedTrick());
        String turnState = StringSerializer.combine(",", score, unplayedCard, trick);

        String Hand = StringSerializer.serializeLong(hand.packed());
        String message = StringSerializer.combine(" ", keyWord, turnState, Hand);

        //Writing the information in the in the Output Stream
        try {
            w.write(message);
            w.write('\n');
            w.flush();
            String card = r.readLine();
            int c = StringSerializer.deserializeInt(card);

            return Card.ofPacked(c);
        }
        catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * override of the method setPlayers of player
     */
    @Override
    public  void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames, Map<PlayerId, Boolean> helpMap) {

        String keyWord = JassCommand.PLRS.name();
        String ownIndex = StringSerializer.serializeInt(ownId.ordinal());
        String player1 = StringSerializer.serializeString(playerNames.get(PlayerId.PLAYER_1));
        String player2 = StringSerializer.serializeString(playerNames.get(PlayerId.PLAYER_2));
        String player3 = StringSerializer.serializeString(playerNames.get(PlayerId.PLAYER_3));
        String player4 = StringSerializer.serializeString(playerNames.get(PlayerId.PLAYER_4));
        List<String> helpList = new ArrayList<>();
        
        for (int i=0; i<PlayerId.COUNT;++i) {
            if (helpMap.get(PlayerId.ALL.get(i))) { helpList.add("1");}
            else { helpList.add("0");}
        }

        String names = StringSerializer.combine(",", player1, player2, player3, player4);
        
        String help =StringSerializer.combine(",", helpList.get(0),helpList.get(1),helpList.get(2),helpList.get(3));

        String message = StringSerializer.combine(" ", keyWord, ownIndex, names, help);

        try {
            w.write(message);
            w.write('\n');
            w.flush();
        }
        catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    } 

    /**
     * override of the method updateHand of player
     */
    @Override
    public void updateHand(CardSet newHand) {
        String s1 =JassCommand.HAND.name();
        String s2 = StringSerializer.serializeLong(newHand.packed());
        try {
            w.write(StringSerializer.combine(" ", s1, s2));
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    /**
     * override of the method setTrump of player
     */
    @Override
    public void setTrump(Color trump) {

        String trmp = JassCommand.TRMP.name();
        String pkTrmp = StringSerializer.serializeInt(trump.ordinal());
        try {
            w.write(StringSerializer.combine(" ", trmp, pkTrmp));
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    /**
     * override of the method updateTrick of player
     */
    @Override
    public void updateTrick(Trick newTrick) {

        String trck =JassCommand.TRCK.name();
        String pkTrck = StringSerializer.serializeInt(newTrick.packed());
        try {
            w.write(StringSerializer.combine(" ", trck, pkTrck));
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * override of the method updateScore of player
     */
    @Override
    public void updateScore(Score score) {

        String scor =JassCommand.SCOR.name();
        String pkScor = StringSerializer.serializeLong(score.packed());
        try {
            w.write(StringSerializer.combine(" ", scor, pkScor));
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * override of the method setWinningTeam of player
     */
    @Override
    public void setWinningTeam(TeamId winningTeam) {

        String winr =JassCommand.WINR.name();
        String team = StringSerializer.serializeInt(winningTeam.ordinal());
        try {
            w.write(StringSerializer.combine(" ", winr, team));
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }
    
    /**
     * Override of the method setHelp of player
     */
    public void setHelp(TurnState state, CardSet hand, PlayerId id) {
        String help =JassCommand.HELP.name();

        String score = StringSerializer.serializeLong(state.packedScore());
        String unplayedCard = StringSerializer.serializeLong(state.packedUnplayedCards());
        String trick = StringSerializer.serializeInt(state.packedTrick());
        String turnState = StringSerializer.combine(",", score, unplayedCard, trick);
        
        String Hand = StringSerializer.serializeLong(hand.packed());
        
        String ownId = StringSerializer.serializeInt(id.ordinal());
        
        
        
        String message = StringSerializer.combine(" ", help, ownId, turnState, Hand );
        try {
            w.write(message);
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    /**
     * Override of the method resetHelp of player
     */
    public void resetHelp() {
        String res= JassCommand.RESH.name();
        try {
            w.write(res);
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
