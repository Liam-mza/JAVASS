package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;

/**
 * This Class allow players to receive help base on the mcts algotihm
 * @author Liam Mouzaoui (295797)
 * @author Remi Delacourt (300849)
 *
 */
public final class Helpers {
    private final static int VALUE_C = 40;
    
    private final static int ITERATIONS = 150000;
    

    /**
     * Return the percentage of recommendation for each card of the given hand
     *@param turnState (TurnState): the turnstate when the player need to play
     * @param Hand (CardSet): the hand of the Player 
     * @param Id (PlayerId): the Id of the Player
     * @return (Map<Card, String>): the map associating each card with its  percentage of recommendation
     */
    public static Map<Card, String> needHelp(TurnState state, CardSet hand, PlayerId id){
        Map<Card, String> results= new HashMap<>(); 
        Node root = new Node(state, hand,id);
        SplittableRandom rng = new SplittableRandom();
        for (int i=0; i<ITERATIONS;++i) {
            explorer(root, id, rng);
        }
        double total=0;
        for (int k=0; k<state.trick().playableCards(hand).size();++k) {
            total += root.childsOfNode[k].calculateV(root, 0);
           
        }
        if (total==0) {total=1;}
        
        for (int j=0; j<state.trick().playableCards(hand).size();++j) {
            Float percentage = (float)( root.childsOfNode[j].calculateV(root, 0)/total)*100;
            Card card= state.trick().playableCards(hand).get(j);
            results.put(card,percentage.toString());
        }
        return results;
    }
    
    /**
     * Creates a new child to the given parent node  and updates the score of all the node contained 
     * in the given list
     * @param parents (Node): the node that we want to add a child to
     * @param path (List<Node>): the path of nodes to go from the root of the tree to the child that is going to be 
     * created
     */
    private static void addChild(Node parents, List<Node> path, PlayerId plyId, SplittableRandom rng) {

        TurnState turn = parents.nodeTurnState;
        CardSet hand =parents.playerHand;

        //If the node is terminal we just simulate a random turn and update the score of the nodes in the path 
        if(turn.isTerminal()) {
            Score score = turn.score();
            updateScore (path,score, plyId);
            return;
        }

        //Creation of the TurnState of the child by choosing the card to add 
        if (turn.nextPlayer()== plyId) {
            Card cardToPlay = turn.trick().playableCards(parents.cardSetofInexistantNodes).get(0);
            turn = turn.withNewCardPlayedAndTrickCollected(cardToPlay);
            hand = hand.remove(cardToPlay);
            parents.cardSetofInexistantNodes= parents.cardSetofInexistantNodes.remove(cardToPlay);
        }
        else {
            Card cardToPlay = turn.trick().playableCards(parents.cardSetofInexistantNodes).get(0);
            turn = turn.withNewCardPlayedAndTrickCollected(cardToPlay);
            parents.cardSetofInexistantNodes= parents.cardSetofInexistantNodes.remove(cardToPlay);
        }

        Node child= new Node(turn, hand, plyId );

        //Simulation of the random turn and update of the score and finished turns count for the child
        //and all the nodes in the path
        Score score= randomTurn(hand, child.nodeTurnState, plyId, rng);
        child.scoreForNode = score.turnPoints(parents.nodeTurnState.nextPlayer().team());
        child.finishedTurns += 1;
        putChildInArray(parents,child);
        updateScore(path,score, plyId);
    }

    /**
     * Updates of the score and finished turns count of all the nodes in the given list
     * @param path(List<Node>): the path of nodes to go from the root of the tree to the child that is going to be 
     * created
     * @param score (Score): the score obtained with the random turn of the child
     */
    private static void updateScore(List<Node> path, Score score, PlayerId plyId) {

        for (int i=0; i< path.size(); ++i) {
            if (i==0) {
                path.get(i).scoreForNode += score.turnPoints(plyId.team().other());
                path.get(i).finishedTurns+=1;
            }
            else {
                path.get(i).scoreForNode  += score.turnPoints(path.get(i-1).nodeTurnState.nextPlayer().team());
                path.get(i).finishedTurns+=1;
            }
        }
    }

    /**
     * Find the node to explore and add a child (if possible) to this node
     * @param root (Node): the root of the tree
     */
    private static void explorer(Node root, PlayerId plyId, SplittableRandom rng ){
        List<Node> path = new ArrayList<Node>();

        Node nodeToExplore =root;

        while(nodeToExplore.cardSetofInexistantNodes.isEmpty()&& !nodeToExplore.nodeTurnState.isTerminal()) {
            path.add(nodeToExplore);
            nodeToExplore= nodeToExplore.childsOfNode[nodeToExplore.findBestChild(VALUE_C)];
        }

        path.add(nodeToExplore);
        addChild(nodeToExplore, path, plyId, rng);
    }

    /**
     * Simulated a random turn from the given TurnState where all the players play a random card from the unplayed cards of 
     * the TurnState or from its hand if it's the MctsPlayer that play
     * @param mctsHand (CardSet): the hand of cards of the MctsPlayer
     * @param state (TurnState): the state of the game (to be able to simulate a turn from this state of the game)
     * @return (Score): the score obtained at the end of the simulated turn
     */
    private static Score randomTurn(CardSet mctsHand, TurnState turnState, PlayerId plyId, SplittableRandom rng) {
        TurnState turn = turnState;
        CardSet hand = mctsHand;

        // Makes all the different players play until the end of the turn 
        while(!turn.isTerminal()) {
            if(turn.nextPlayer() == plyId) {
                CardSet playableCardOfPlayer = turn.trick().playableCards(hand);
                Card cardToPlay = playableCardOfPlayer.get(rng.nextInt(playableCardOfPlayer.size()));
                turn = turn.withNewCardPlayedAndTrickCollected(cardToPlay);
                hand= hand.remove(cardToPlay);
            }
            else {
                CardSet playableCard =turn.unplayedCards().difference(hand);
                playableCard = turn.trick().playableCards(playableCard);

                turn = turn.withNewCardPlayedAndTrickCollected(playableCard.get(rng.nextInt(playableCard.size())));
            }
        }

        return turn.score();
    }

    /**
     * Put the given child node in the array of child of the given parent node
     * @param parent (Node): the parents node 
     * @param child (Child): the child node
     */
    private static void putChildInArray(Node parent, Node child) {
        int index = (parent.childsOfNode.length)-(parent.cardSetofInexistantNodes.size()+1);
        parent.childsOfNode[index]=child;
    }
    
    
    
    
    
    /**
     * Represent a node of the tree that the Mcts Algorithm builds
     */
    private static class Node {

        private TurnState nodeTurnState;
        private Node[] childsOfNode;
        private CardSet cardSetofInexistantNodes;
        private int scoreForNode;
        private int finishedTurns;
        private CardSet playerHand;


        /**
         * Constructor of Node
         * @param turnState (TurnState): the turnstate of the node
         * @param Hand (CardSet): the hand of the Player when the node is created
         * @param Id (PlayerId): the Id of the Player
         */
        private Node(TurnState turnState, CardSet Hand,PlayerId id){
            nodeTurnState = turnState; 

            // Determination of the CardSet of all the cards corresponding to an inexistent node depending on the next player
            if (nodeTurnState.nextPlayer()==id) {
                if(!turnState.isTerminal()){
                    cardSetofInexistantNodes = nodeTurnState.trick().playableCards(Hand);
                }else {
                    cardSetofInexistantNodes= CardSet.EMPTY;
                }
            }
            else {
                if(!turnState.isTerminal()){
                    cardSetofInexistantNodes = nodeTurnState.trick().playableCards(nodeTurnState.unplayedCards().difference(Hand));
                }
                else {
                    cardSetofInexistantNodes= CardSet.EMPTY;
                }
            }

            childsOfNode = new Node[cardSetofInexistantNodes.size()];
            scoreForNode=0;
            finishedTurns=0;
            this.playerHand=Hand;
        }

        /**
         * Calculate a parameter (called the "value V") useful to compare nodes with each other
         * @param parent (Node): the parent node
         * @param c (double): A parameter used in the formula to calculate the value "V" 
         * @return (double): the value V for this node
         */
        private double calculateV(Node parent, double c) {
            if (finishedTurns <= 0) {
                return Double.POSITIVE_INFINITY;
            }
            else {
                double score = (double)scoreForNode;
                double turns = (double)finishedTurns;
                double v = (score/turns)+ c*Math.sqrt( (2*Math.log(((double)parent.finishedTurns)))/turns);
                return v;
            } 
        }

        /**
         * Calculate the value "V" of all the child of the node and return the index the one with the biggest value
         * @param c (double): A parameter used in the formula to calculate the value "V" 
         * @return (int): the index of the best child 
         */
        private int findBestChild(double c) {
            double bestV=0;
            int index =0;
            for (int i =0; i<childsOfNode.length; ++i) {
                if (childsOfNode[i]!= null) {
                    double tmpV = childsOfNode[i].calculateV(this, c);
                    if (tmpV >bestV) {
                        bestV= tmpV;
                        index=i;
                    }
                }
            }
            return index;
        }
    }
    
}
