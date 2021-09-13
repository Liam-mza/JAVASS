package ch.epfl.javass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.gui.MessageBox;
import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.PacedPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.net.RemotePlayerClient;
import ch.epfl.javass.net.StringSerializer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Main class of the game that is used to launch a game 
 * @author Liam Mouzaoui (295797)
 * @author Remi Delacourt (300849)
 */
public final class LocalMain extends Application {

    private final static int INDEX_FOR_SEED = 4;
    private final static int ARG_SIZE_NO_SEED= 4, ARG_SIZE_WITH_SEED= 5, MAX_PLAYER_ARG_SIZE =3, REMOTE_ARG_SIZE_WITH_HELP=4, ARG_SIZE_WITH_NAME=2;

    private final static int INDEX_FOR_PLAYERTYPE= 0,INDEX_FOR_ARGUMENT2= 1,INDEX_FOR_ARGUMENT3= 2, INDEX_FOR_ARGUMENT4=3;
    private final static int RETURN_EXIT_VALUE=1, DEFAULT_ITERATIONS=10000, MIN_ITERATION=10, MIN_TIME_TO_PLAY=2, PAUSE_BEFORE_COLLECTION=1000;

    private final static String DEFAULT_HOST= "localhost";
    private final static String[] DEFAULT_NAMES= defaultNames();
    
    
    /**
     * Launch the application
     * @param args: The running parameters of the game
     */
    public static void main(String[] args) {

        launch(args);
    }
    
    /**
     * Creates the different players, the different graphical interfaces, and the game
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
      

        List<String> arguments = new ArrayList<>();
        Map<PlayerId, Player> playerMap = new HashMap<>();
        Map<PlayerId, String> playerNames = new HashMap<>();
        Map<PlayerId, Boolean> helpMap = new HashMap<>();

        arguments = this.getParameters().getRaw();
        

        if(arguments.size()!= ARG_SIZE_NO_SEED && arguments.size()!=ARG_SIZE_WITH_SEED) {
            System.err.println(createNotice());
            System.exit(RETURN_EXIT_VALUE);
        }

        Random rng ;
        if(arguments.size() == ARG_SIZE_WITH_SEED) {
            try {
                Long seed =Long.parseLong(arguments.get(INDEX_FOR_SEED));
                rng = new Random(seed);
            }
            catch (NumberFormatException e){
                printAndClose("Error: invalid long value for the seed: "+arguments.get(INDEX_FOR_SEED));
                throw e;
            } 
        }
        else {
            rng = new Random();
        }

        Long seedForJassGame= rng.nextLong();

        for (int i=0; i<PlayerId.COUNT;++i) {
            String[] ply= StringSerializer.split(arguments.get(i), ":");

            if (ply.length>=ARG_SIZE_WITH_NAME && !ply[INDEX_FOR_ARGUMENT2].isEmpty()) {
                playerNames.put(PlayerId.ALL.get(i), ply[INDEX_FOR_ARGUMENT2]);
            }
            else {
                playerNames.put(PlayerId.ALL.get(i), DEFAULT_NAMES[i]);
            }

            switch (ply[INDEX_FOR_PLAYERTYPE]) {

            case "h":

                if(ply.length>MAX_PLAYER_ARG_SIZE) {
                    printAndClose("Error: To much arguments for the player "+i+": "+arguments.get(i));
                }
                if(ply.length==MAX_PLAYER_ARG_SIZE) {
                    helpMap.put(PlayerId.ALL.get(i), ply[INDEX_FOR_ARGUMENT3].equals("1"));
                }else {
                    helpMap.put(PlayerId.ALL.get(i), false);
                }

                playerMap.put(PlayerId.ALL.get(i), new GraphicalPlayerAdapter());

                break;   

            case "s":
                if (ply.length>MAX_PLAYER_ARG_SIZE) {
                    printAndClose("Error: To much arguments for the player "+i+": " +arguments.get(i));
                }
                helpMap.put(PlayerId.ALL.get(i), false);

                if(ply.length==MAX_PLAYER_ARG_SIZE && !ply[INDEX_FOR_ARGUMENT3].isEmpty()) {

                    try {
                        int iteration= Integer.parseInt(ply[INDEX_FOR_ARGUMENT3]);

                        if(iteration<MIN_ITERATION) {
                            printAndClose("Error: The number of iterations for the player: "+i+" is smaller than 10: "+ply[INDEX_FOR_ARGUMENT3]);
                        }

                        playerMap.put(PlayerId.ALL.get(i), new PacedPlayer(new MctsPlayer(PlayerId.ALL.get(i), rng.nextLong(), iteration), MIN_TIME_TO_PLAY) );

                    }catch (NumberFormatException e){
                        printAndClose("Error: invalid int value for the number of iterations for the player: "+i+": "+ply[INDEX_FOR_ARGUMENT3]);
                    } 
                }
                else {
                    playerMap.put(PlayerId.ALL.get(i),  new PacedPlayer(new MctsPlayer(PlayerId.ALL.get(i), rng.nextLong(),DEFAULT_ITERATIONS), MIN_TIME_TO_PLAY));
                }
                break;

            case "r":
                if (ply.length>REMOTE_ARG_SIZE_WITH_HELP) {
                    printAndClose("Error: To much arguments for the player "+i+": " +arguments.get(i));
                }
                
                if(ply.length>=MAX_PLAYER_ARG_SIZE && !ply[INDEX_FOR_ARGUMENT3].isEmpty()) {
                    try {
                        playerMap.put(PlayerId.ALL.get(i), new RemotePlayerClient(ply[INDEX_FOR_ARGUMENT3])) ;
                    }
                    catch( IOException e){
                        printAndClose("Connection error with the player: "+i+": "+ply[INDEX_FOR_ARGUMENT3]);
                    }
                }
                else {
                    try {
                        playerMap.put(PlayerId.ALL.get(i), new RemotePlayerClient(DEFAULT_HOST));
                    }
                    catch( IOException e){
                        printAndClose("Connection error with the player: "+i+": "+DEFAULT_HOST);
                    }
                }
                if(ply.length==REMOTE_ARG_SIZE_WITH_HELP) {
                    helpMap.put(PlayerId.ALL.get(i), ply[INDEX_FOR_ARGUMENT4].equals("1"));
                }else {
                    helpMap.put(PlayerId.ALL.get(i), false);
                }
                break;
            default: 
                printAndClose("Error: Invalid player type for the player "+i+": "+ ply[INDEX_FOR_PLAYERTYPE]);
                break;
            }
           
        }
        Thread gameThread = new Thread(() -> {
            JassGame jass = new JassGame(seedForJassGame, playerMap, playerNames, helpMap);
            while(!jass.isGameOver()) {
                jass.advanceToEndOfNextTrick();
                try { Thread.sleep(PAUSE_BEFORE_COLLECTION); } catch (Exception e) {}
            }
        });
        gameThread.setDaemon(true);
        gameThread.start();
    }

    /**
     * Prints the given message and closes the application 
     * @param message: the message to print
     */
    private void printAndClose(String message) {
        System.err.println(message);
        System.exit(RETURN_EXIT_VALUE);
    }

    /**
     * Creates an array containing the default names for the players
     * @return (String[]): the array containing the default names for the players
     */
    private static String[] defaultNames() {
        String[] names = {"Aline", "Bastien","Colette","David"};
        return names;
    }

    /**
     * Create the notice of the game
     * @return (String): The notice of the game
     */
    private static String createNotice() {
        return "Utilisation of LocalMain to start a new game:\n" + 
                "\n" + 
                "Note: In the following explanation the symbols ‘’<‘’ and ‘’>’’ around the arguments are just here to make things clearer \n" + 
                "      and should not appear in your configuration arguments.\n" + 
                "\n" + 
                "The program needs 4 to 5 arguments to start (the 5th being optional) .\n" + 
                "Format of the arguments: <j1> <j2> <j3> <j4> <seed>\n" + 
                "\n" + 
                "The 4 first arguments represent the 4 players.\n" + 
                "A player needs to be configured respecting the following structure: <type>:<names (optional)>:<optional arguments>\n" + 
                "Where:\n \n" + 
                "-The first argument is the type of player which is compulsory and which can be: \n" + 
                "    - <h>: To have a local human player \n" + 
                "    - <s>: to have a local simulated player (MctsPlayer)\n" + 
                "    - <r>: To have a remote (human) player \n \n" + 
                "-The second argument is the name of the player but is optional (a default name will be used if you don’t give one).\n" + 
                "    If you want to use the default name the structure is: <type>::<optional arguments>\n \n" + 
                "-The third argument is optional and depends on the type of the player:\n" + 
                "    -If it’s a human player, no third argument is needed and every third argument will cause an error. \n" + 
                "    -If it’s a simulated player, you can give as third argument the number of iterations you want for the \n" + 
                "     Mcts algorithm. If you don’t give this third argument the number of iterations will be by default 10 000.\n" + 
                "    -If it’s a remote player, you should give as third argument the IP adress of the computer running \n" + 
                "     the server. If not given, the IP will be by default «localhost».\n \n" + 
                "If you don’t want to give a third argument, use the following structures: <type>  or <type>:<name> \n \n" + 
                "The fifth argument is the seed that is going to be used by the random generators for the game. \n"+
                "This seed is optional and will be chosen randomly if not given. \n"+
                "Note that using the same seed always lead to the exact same game.\n \n" + 
                "Now you’re all set!\n \n" + 
                "Enjoy the game!";
    }
}

