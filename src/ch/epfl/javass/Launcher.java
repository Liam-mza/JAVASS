package ch.epfl.javass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringJoiner;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.PacedPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.net.RemotePlayerClient;
import ch.epfl.javass.net.StringSerializer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Used to launch a Game
 * @author Liam Mouzaoui (295797)
 * @author Remi Delacourt (300849)
 *
 */
public final class Launcher extends Application {

    private final static int INDEX_FOR_SEED = 4;
    private final static int ARG_SIZE_NO_SEED= 4, ARG_SIZE_WITH_SEED= 5, MAX_PLAYER_ARG_SIZE =3, REMOTE_ARG_SIZE_WITH_HELP=4, ARG_SIZE_WITH_NAME=2;

    private final static int INDEX_FOR_PLAYERTYPE= 0,INDEX_FOR_ARGUMENT2= 1,INDEX_FOR_ARGUMENT3= 2, INDEX_FOR_ARGUMENT4=3;
    private final static int RETURN_EXIT_VALUE=1, DEFAULT_ITERATIONS=10000, MIN_ITERATION=10, MIN_TIME_TO_PLAY=2, PAUSE_BEFORE_COLLECTION=1000;

    private final static String DEFAULT_HOST= "localhost";
    private final static String[] DEFAULT_NAMES= defaultNames();

    private GridPane[] plyGrid= {new GridPane(),new GridPane(),new GridPane(),new GridPane()};


    /**
     * Launch the application
     * @param args: The running parameters of the game
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Creates the different players, the different graphical interfaces, and the game
     * primaryStage (Stage): the primary stage of the application
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        Platform.setImplicitExit(false);
        
        BorderPane borderPane= new BorderPane();
       
        Text title =new Text("JAVASS");
        title.setStyle("-fx-font: 90 Times; -fx-font-weight: bold; -fx-fill: white;");
        borderPane.setTop(title);
        BorderPane.setAlignment(title, Pos.CENTER);
        
        GridPane window = new GridPane();
        
        for (int i=0;i<plyGrid.length;++i) {
            window.addRow(i, createPlayerSelection(i)); 
        }
        
        Text rootText= new Text("   Seed: ");
        rootText.setStyle("-fx-fill: white;");
        TextField root= new TextField();
        GridPane rootPane = new GridPane();
        rootPane.addRow(0, rootText,root);
        
        
        window.addRow(5,rootPane);
        
        window.setVgap(20);
        window.setHgap(40);
        window.setStyle("-fx-background-color: transparent; ");

        Image tapis = new Image("/tapis_cartes.png");
        ImageView fondEcran = new ImageView(tapis);
        StackPane welcomePane= new StackPane(fondEcran,borderPane);
        fondEcran.setFitWidth(1000);
        fondEcran.setFitHeight(600);
        
        
        Button but= new Button("START GAME");
        but.setStyle("-fx-background-color: whitesmoke; -fx-font: 30 Optima;");
        
        borderPane.setCenter(window);
        borderPane.setBottom(but);
        BorderPane.setAlignment(but, Pos.CENTER);
        
        BorderPane.setMargin(title, new Insets(0, 30, 0, 0));
        BorderPane.setMargin(window, new Insets(100, 50, 0, 40));
        BorderPane.setMargin(but, new Insets(0, 0, 50, 0));
        

        Stage primaryStage2= new Stage();
        Scene scene = new Scene(welcomePane);
        primaryStage2.setScene(scene);
        primaryStage2.setTitle("START");
        primaryStage2.show();

        but.setOnMouseClicked(e->{
            StringJoiner str= new StringJoiner(" ");
            for(int i=0;i<4;++i) {

                GridPane select =(GridPane) window.getChildren().get(i);
                @SuppressWarnings("unchecked")
                ChoiceBox<String> v=  (ChoiceBox<String>) select.getChildren().get(1);
                StringBuilder b =new StringBuilder();
                if (v.getSelectionModel().getSelectedIndex()==0) {


                    @SuppressWarnings("unchecked")
                    ChoiceBox<String> typeBox= ((ChoiceBox<String>)select.getChildren().get(1));
                    b.append(typeBox.getSelectionModel().getSelectedItem().charAt(0)+":");

                    b.append( ((TextField)plyGrid[i].getChildren().get(1)).getText()+":");

                    @SuppressWarnings("unchecked")
                    ChoiceBox<String> helpBox= ((ChoiceBox<String>)plyGrid[i].getChildren().get(3));
                    int indexH = helpBox.getSelectionModel().getSelectedIndex();
                    if (indexH==-1) {b.append(0);}
                    else {b.append(indexH);}
                }
                else if (v.getSelectionModel().getSelectedIndex()==1) {

                    @SuppressWarnings("unchecked")
                    ChoiceBox<String> typeBox= ((ChoiceBox<String>)select.getChildren().get(1));
                    b.append(typeBox.getSelectionModel().getSelectedItem().charAt(0)+":");

                    b.append( ((TextField)plyGrid[i].getChildren().get(1)).getText()+":");

                    b.append( ((TextField)plyGrid[i].getChildren().get(3)).getText()+":");
                    
                    @SuppressWarnings("unchecked")
                    ChoiceBox<String> helpBox= ((ChoiceBox<String>)plyGrid[i].getChildren().get(5));
                    int indexH = helpBox.getSelectionModel().getSelectedIndex();
                    if (indexH==-1) {b.append(0);}
                    else {b.append(indexH);}

                }
                else if (v.getSelectionModel().getSelectedIndex()==2) {

                    @SuppressWarnings("unchecked")
                    ChoiceBox<String> typeBox= ((ChoiceBox<String>)select.getChildren().get(1));
                    b.append(typeBox.getSelectionModel().getSelectedItem().charAt(0)+":");

                    b.append( ((TextField)plyGrid[i].getChildren().get(1)).getText()+":");

                }
                else {
                    if(i==0) {
                       b.append("h") ;
                    }else
                    b.append("s");
                }
                str.add(b.toString());
            }
            
            
            if(!root.getText().isEmpty()) {
                str.add(root.getText());
            }
            
            String[] split= StringSerializer.split(str.toString(), " ");
            List<String> arguments = new ArrayList<>();
            for(int i=0;i<split.length;++i) {
                arguments.add(split[i]);
            }
            
            primaryStage2.close();
            getStarted(primaryStage,arguments);

        });

    }


    /**
     * Create the GridPane to set the arguments for the index-th player 
     * @param index: the index of the player that the gridPane will represent
     * @return (GridPane): the GridPane to set the arguments for the index-th player
     */
    public GridPane createPlayerSelection(int index) {
        GridPane window =new GridPane();
        
        //Creation of the player type selection
        Text typeText= new Text("Type of Player "+(index+1)+": ");
        typeText.setStyle("-fx-fill: white;");
        ChoiceBox<String> typeBox= new ChoiceBox<>(FXCollections.observableArrayList("human", "remote","simulated"));
        typeBox.setMinWidth(100);

        typeBox.setOnAction(e->{
            
            //Creation of the selection pane for a human player
            if (typeBox.getSelectionModel().getSelectedIndex()==0) {

                Text nameText= new Text("   name: ");
                TextField name= new TextField();
                nameText.setStyle("-fx-fill: white;");

                Text helpText= new Text("    needHelp ");
                helpText.setStyle("-fx-fill: white;");
                ChoiceBox<String> helpBox= new ChoiceBox<>(FXCollections.observableArrayList("no", "yes"));

                plyGrid[index].getChildren().clear();
                plyGrid[index].addRow(0, nameText,name,helpText,helpBox);


            }
            
          //Creation of the selection pane for a remote player
            else if(typeBox.getSelectionModel().getSelectedIndex()==1) {
                Text nameText= new Text("   name: ");
                TextField name= new TextField();
                nameText.setStyle("-fx-fill: white;");

                Text IpText= new Text("    Adresse Ip: ");
                IpText.setStyle("-fx-fill: white;");
                TextField Ip= new TextField();


                Text helpText= new Text("   needHelp: ");
                helpText.setStyle("-fx-fill: white;");
                ChoiceBox<String> helpBox= new ChoiceBox<>(FXCollections.observableArrayList("no", "yes"));
                
                plyGrid[index].getChildren().clear();
                plyGrid[index].addRow(0, nameText,name,IpText,Ip, helpText,helpBox);

            }
            
          //Creation of the selection pane for a simulated player
            else if(typeBox.getSelectionModel().getSelectedIndex()==2) {

                Text nameText= new Text("   name: ");
                TextField name= new TextField();
                nameText.setStyle("-fx-fill: white;");

                Text iterationText= new Text("    Nombre d'itérations: ");
                TextField iteration= new TextField();
                iterationText.setStyle("-fx-fill: white;");

                plyGrid[index].getChildren().clear();
                plyGrid[index].addRow(0, nameText,name,iterationText,iteration);
            }
        });
       
        plyGrid[index].setHgap(5);
        
        //adding the selection pane to the window
        window.addRow(0, typeText,typeBox,plyGrid[index]);
        window.setHgap(5);
        return window;

    }

    /**
     * Starts the game 
     * @param primaryStage (Stage): the primary stage 
     * @param arg (List<String>): the arguments for the game
     */
    public  void getStarted(Stage primaryStage, List<String> arg) {
        List<String> arguments = new ArrayList<>();
        Map<PlayerId, Player> playerMap = new HashMap<>();
        Map<PlayerId, String> playerNames = new HashMap<>();
        Map<PlayerId, Boolean> helpMap = new HashMap<>();

        arguments = arg;


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
                printAndClose("Error: invalid long value for the seed: "+createNotice());
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

                if(ply.length>=MAX_PLAYER_ARG_SIZE) {
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
        return "Utilisation of Launcher to start a new game:\n" + 
                
                "To set the 4 the 4 players:\n" + 
                "A player has a type:" + 
                "\n \n" + 
                "    - <human>: To have a local human player \n" + 
                "    - <simulated>: to have a local simulated player (MctsPlayer)\n" + 
                "    - <remote>: To have a remote (human) player \n \n" + 
                "-The second argument is the name of the player but is optional (a default name will be used if you don’t give one).\n" + 
                "    If you want to use the default name the structure is: <type>::<optional arguments>\n \n" + 
                "-The third argument is optional and depends on the type of the player:\n" + 
                "    -If it’s a human player, you need to choose if you want help (by default no) \n" + 
                "    -If it’s a simulated player, you can give as third argument the number of iterations you want for the \n" + 
                "     Mcts algorithm. If you don’t give this third argument the number of iterations will be by default 10 000.\n" + 
                "    -If it’s a remote player, you should give as third argument the IP adress of the computer running \n" + 
                "     the server. If not given, the IP will be by default «localhost».\n \n" + 
                "-The fourth argument for a remote player is if you want help or not (by default no):\n" + 
                "The fifth argument is the seed that is going to be used by the random generators for the game. \n"+
                "This seed is optional and will be chosen randomly if not given. \n"+
                "Note that using the same seed always lead to the exact same game.\n \n" + 
                "Now you’re all set!\n \n" + 
                "Enjoy the game!";
    }
}
