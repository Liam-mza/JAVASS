package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.jass.Card.Color;

public final class PrintingPlayer implements Player {
    private final Player underlyingPlayer;

    public PrintingPlayer(Player underlyingPlayer) {
      this.underlyingPlayer = underlyingPlayer;
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
      System.out.print("C'est à moi de jouer... Je joue : ");
      Card c = underlyingPlayer.cardToPlay(state, hand);
      System.out.println(c);
     
      return c;
    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames, Map<PlayerId, Boolean> helpMap) {
        
        System.out.println("LES JOUEURS SONT:");
        for (Map.Entry<PlayerId, String> n: playerNames.entrySet() ) {
            if (n.getKey()== ownId) {
                System.out.println(n.getValue()+ " (moi)");
            }
            else {
                System.out.println(n.getValue());
            }
        } 
    }

    @Override
    public void updateHand(CardSet newHand) {
        System.out.println("Ma main nouvelle main est: "+newHand.toString());
    }

    @Override
    public void setTrump(Color trump) {
        System.out.println("TRUMP: "+trump);
        
    }

    @Override
    public void updateTrick(Trick newTrick) {
        System.out.println("Plis "+ newTrick.index()+", commencer par "+ newTrick.player(0)+": " +newTrick);
        
    }

    @Override
    public void updateScore(Score score) {
        System.out.println(score);
        
    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        System.out.println("FIN DE LA PARTIE");
        System.out.println("VICTOIRE DE L'EQUIPE: "+winningTeam);
    }
}
    
