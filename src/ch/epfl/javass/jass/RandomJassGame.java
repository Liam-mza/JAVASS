package ch.epfl.javass.jass;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ch.epfl.javass.net.RemotePlayerClient;

public final class RandomJassGame {
    public static void main(String[] args) {
        double t1 =System.currentTimeMillis()/1000.00;
        Map<PlayerId, Player> players = new HashMap<>();
        Map<PlayerId, String> playerNames = new HashMap<>();

        for(PlayerId pId: PlayerId.ALL) {
            if(pId== PlayerId.PLAYER_1||pId== PlayerId.PLAYER_3 ) {
                Random rng = new Random(4683473);
                Player mctsPlayer = new MctsPlayer(pId, 1000+rng.nextInt(5000), 10);
                
                players.put(pId, mctsPlayer);
                playerNames.put(pId, pId.name());
            }
            if(pId== PlayerId.PLAYER_2 /* PLAYER_2 */) {
               
                try {
                Player randomPlayer = new RemotePlayerClient("128.179.140.225");
                players.put(pId, randomPlayer);
                playerNames.put(pId, pId.name());
                } catch(Exception e) {}
                
            }
            
        }


     
        Random rng = new Random(9989667325657L);
        for (int i=0; i<1; ++i) {
            
            JassGame g = new JassGame(1000+rng.nextInt(2000), players, playerNames,null);
            while (! g.isGameOver()) {
                g.advanceToEndOfNextTrick();
                
            }
            
        }
        double t2 =System.currentTimeMillis()/1000.00;
               System.out.println(t2-t1);
    }
}
