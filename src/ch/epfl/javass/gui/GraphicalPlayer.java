package ch.epfl.javass.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * This class represent the graphical interface of the game 
 * @author Liam Mouzaoui (295797)
 * @author Remi Delacourt (300849)
 *
 */
public class GraphicalPlayer  {  


    private static final int CARD_IMAGE_WIDTH =120, CARD_IMAGE_HEIGHT =180; 
    private static final int TRUMP_IMAGE_SIZE =101;
    private static final int HANDCARD_IMAGE_WIDTH =80, HANDCARD_IMAGE_HEIGHT =120;
    private static final int PLAYABLE_CARD_OPACITY=1 ;
    private static final double UNPLAYABLE_CARD_OPACITY=0.2;

    private static final int BLUR_EFFECT_RADIUS = 4;
    private final ObservableMap<Card, Image> imageCardsMap = createImageCardMap();
    private final ObservableMap<Color, Image> imageTrumpMap = createImageTrumpMap();
    private final ObservableMap<Card, Image> imageHandCardsMap = createImageHandCardMap();

  
    private StackPane mainPane;

    /**
     * Constructor of the class GraphicalPlayer
     * @param ownId (PlayerId): the id of the player using the graphical interface 
     * @param names (Map<PlayerId, String>): the map making each player correspond with his name
     * @param score (ScoreBean): The bean of the score of the game 
     * @param trick (TrickBean): The bean of the trick of the game 
     * @param hand (HandBean): The bean of the hand of the player 
     * @param queue (BlockingQueue<Card>): the queue to communicate the card the player wants to play
     * @param (boolean) help: if the player need help or not
     * @param helpMap (Map<PlayerId, String>): the map making each card correspond with its recommendation
     */
    public GraphicalPlayer(PlayerId ownId, Map<PlayerId, String> names, ScoreBean score, TrickBean trick, HandBean hand, BlockingQueue<Card> queue, boolean help, ObservableMap <Card,String> helpMap) { 
       
       
        GridPane trickGrid = createTrickPane(ownId, names, trick);
        GridPane scorePane = createScorePane(score, names);
        List<BorderPane> victoryPanes= createVictoryPanes(names, score);
        HBox handPane = createHandPane( hand, queue, help, helpMap);
        
        Image tapis = new Image("/tapis_cartes.png");
        ImageView fondEcranTrick = new ImageView(tapis);
        
        Image tapisMain = new Image("/tapis_main.png");
        ImageView fondEcranHand= new ImageView(tapisMain);
        
        Image pannBois = new Image("/panneau.png");
        ImageView fondEcranScore= new ImageView(pannBois);
        
        fondEcranHand.setFitWidth(770);
        fondEcranHand.setFitHeight(150);
        
        fondEcranTrick.setFitWidth(770);
        fondEcranTrick.setFitHeight(600);
        
        fondEcranScore.setFitWidth(770);
        fondEcranScore.setFitHeight(100);
        fondEcranScore.setStyle("-fx-background-color: lightgray; ");

        StackPane stackTrick= new StackPane(fondEcranTrick,trickGrid);
        StackPane stackhand= new StackPane(fondEcranHand,handPane);
        StackPane stackScore= new StackPane(fondEcranScore,scorePane);
        
        
        BorderPane InterfacePane = new BorderPane(stackTrick, stackScore, null,stackhand , null);
        

        mainPane = new StackPane(InterfacePane,victoryPanes.get(0),victoryPanes.get(1));
    }

    /**
     * Creates the stage of the graphical interface
     * @return (Stage): the stage of the graphical interface
     */
    public Stage createStage() {
        Stage primaryStage = new Stage();
        Scene scene = new Scene(mainPane);
        primaryStage.setScene(scene);
        return primaryStage;
    }

    /**
     * Creates the pane containing all the card of the player's hand and allowing him to play
     * @param hand (HandBean): The bean of the hand of the player
     * @param queue (BlockingQueue<Card>): The queue to communicate the card the player wants to play
     * @param (boolean) help: if the player need help or not
     * @param helpMap (Map<PlayerId, String>): the map making each card correspond with its recommendation
     * @return (HBox): The pane containing all the card of the player's hand and allowing him to play
     */
    private HBox createHandPane(HandBean hand, BlockingQueue<Card> queue, boolean help,ObservableMap <Card,String> helpMap) {
        HBox handPane = new HBox();

        handPane.setStyle("-fx-background-color: transparent; -fx-spacing: 5px; -fx-padding: 5px;");

        for (int i=0; i< hand.hand().size(); ++i) {


            ImageView cardImage = new ImageView();
            cardImage.setFitWidth(HANDCARD_IMAGE_WIDTH);
            cardImage.setFitHeight(HANDCARD_IMAGE_HEIGHT);
            
            Text helpNumber = new Text();
            ObjectProperty<Card> cardIndex = new SimpleObjectProperty<>();
            cardIndex.bind(Bindings.valueAt(hand.hand(), i));
            cardImage.imageProperty().bind(Bindings.valueAt(imageHandCardsMap, cardIndex));
            
            VBox cardbox;
            Rectangle cache = new Rectangle(HANDCARD_IMAGE_WIDTH-3, HANDCARD_IMAGE_HEIGHT-3);    
            cache.setStyle("-fx-arc-width: 10; -fx-arc-height: 10; -fx-fill: lightgray; -fx-stroke: lightgray; -fx-stroke-width: 3; -fx-opacity: 1; -fx-alignment: center;");   
           
            final int thisIndex = i;

            cardImage.setOnMouseClicked(e->{ 

                queue.add(hand.hand().get(thisIndex));
                cache.opacityProperty().set(0);

            });
            BooleanBinding isPlayable = Bindings.createBooleanBinding(()-> hand.playableCards().contains(hand.hand().get(thisIndex)),hand.hand(), hand.playableCards());

            cardImage.opacityProperty().bind((Bindings.when(isPlayable).then(PLAYABLE_CARD_OPACITY).otherwise(UNPLAYABLE_CARD_OPACITY)));

            cardImage.disableProperty().bind(isPlayable.not());
            //If the help is active, show the recommendation
            if (help) {
               
                helpNumber.textProperty().bind(Bindings.valueAt(helpMap, cardIndex));
                helpNumber.setStyle("-fx-fill: white;");
                StackPane stackCard= new StackPane(cache,cardImage);
                cardbox = new VBox(helpNumber,stackCard );
                helpNumber.setTextAlignment(TextAlignment.CENTER);
            }
            else {
                StackPane stackCard= new StackPane(cache,cardImage);
                cardbox = new VBox(stackCard );
            }
            cardbox.alignmentProperty().set(Pos.CENTER);
            
            handPane.getChildren().add(cardbox);
        }
        return handPane;
    }

    /**
     * Creates the panes announcing the final score when a team win
     * @param names (Map<PlayerId, String>): the map making each player correspond with his name
     * @param score (ScoreBean): The bean of the score of the game 
     * @return List<BorderPane>: the list containing the panes announcing the final score when a team win for each team
     */
    private List<BorderPane> createVictoryPanes(Map<PlayerId, String> names, ScoreBean score) {

        List<BorderPane> victoryPaneList = new ArrayList<>();

        for (TeamId t: TeamId.ALL) {
            Text text = new Text();
            if (t==TeamId.TEAM_1) {
                text.textProperty().bind(Bindings.format("%s et %s ont gagné avec %d points contre %d",names.get(PlayerId.PLAYER_1),names.get(PlayerId.PLAYER_3), score.totalPointsProperty(TeamId.TEAM_1),score.totalPointsProperty(TeamId.TEAM_2))); 
            }
            else {
                text.textProperty().bind(Bindings.format("%s et %s ont gagné avec %d points contre %d", names.get(PlayerId.PLAYER_2),names.get(PlayerId.PLAYER_4), score.totalPointsProperty(TeamId.TEAM_2), score.totalPointsProperty(TeamId.TEAM_1)));
            }
            BorderPane victoryPane = new BorderPane(text);
            victoryPane.setStyle("-fx-font: 16 Optima; -fx-background-color: white;");
            victoryPane.visibleProperty().bind(score.winningTeamProperty().isEqualTo(t));
            victoryPaneList.add(victoryPane);
        }
        return victoryPaneList;
    }

    /**
     * Creates the pane that shows the scores
     * @param score (ScoreBean): The bean of the score of the game
     * @param playerNames (Map<PlayerId, String>): the map making each player correspond with his name
     * @return (GridPane): the pane that shows the scores
     */
    private GridPane createScorePane(ScoreBean score, Map<PlayerId, String> playerNames) {

        GridPane scorePane = new GridPane();

        for(int i=0;i<TeamId.COUNT;++i) {

            //The name of the teams
            Text teamNames = new Text(playerNames.get(PlayerId.ALL.get(i))+" et "+playerNames.get(PlayerId.ALL.get(i+2))+" : ");
            teamNames.setStyle( "-fx-fill: white;");
            scorePane.addRow(i, teamNames);
            GridPane.setHalignment(teamNames, HPos.RIGHT);

            //The points won during the turn
            Text turnPoints = new Text("0"); 
            turnPoints.setStyle( "-fx-fill: white;");
            IntegerProperty copyTps = new SimpleIntegerProperty();
            score.turnPointsProperty(TeamId.ALL.get(i)).addListener((o, oV, nV) -> {
                copyTps.set(nV.intValue());
                turnPoints.setText(Bindings.convert(copyTps).get());
            });
            scorePane.addRow(i, turnPoints);
            GridPane.setHalignment(turnPoints, HPos.RIGHT);

            //The points won in the last trick
            Text additionalTrickPoints = new Text();
            additionalTrickPoints.setStyle( "-fx-fill: white;");
            IntegerProperty lastTps = new SimpleIntegerProperty();
            score.turnPointsProperty(TeamId.ALL.get(i)).addListener((o, oV, nV) -> {
                lastTps.set(nV.intValue() - oV.intValue());

                if(nV.intValue() == oV.intValue() || (nV.intValue() - oV.intValue()) < 0) { 
                    lastTps.set(0);
                    additionalTrickPoints.setText(" ");
                }
                else {
                    lastTps.set(nV.intValue() - oV.intValue());
                    additionalTrickPoints.setText(" (+"+lastTps.getValue()+") ");
                }

            });
            scorePane.addRow(i, additionalTrickPoints);

            Text total = new Text(" / Total : ");
            total.setStyle( "-fx-fill: white;");
            scorePane.addRow(i, total);

            //The Game points
            Text gamePoints = new Text(Bindings.convert(score.gamePointsProperty(TeamId.ALL.get(i))).get());
            gamePoints.setStyle( "-fx-fill: white;");
            IntegerProperty gameTps = new SimpleIntegerProperty();
            score.gamePointsProperty(TeamId.ALL.get(i)).addListener((o, oV, nV) -> {
                gameTps.set(nV.intValue());
                gamePoints.setText(Bindings.convert(gameTps).get());
            });
            scorePane.addRow(i, gamePoints);
            GridPane.setHalignment(gamePoints, HPos.RIGHT);

        }

        scorePane.setStyle("-fx-font: 19 Times; -fx-font-weight: 300; "
                + "-fx-background-color: transparent; "
                + "-fx-padding: 5px; "
                + "-fx-alignment: center;");
        return scorePane;
    }


    /**
     * Creates the pane showing the cards played by each player during the trick
     * @param ownId (PlayerId): the id of the player using the graphical interface 
     * @param names (Map<PlayerId, String>): the map making each player correspond with his name
     * @param trick (TrickBean): The bean of the trick of the game
     * @return (GridPane): the pane showing the cards played by each player during the trick
     */
    private GridPane createTrickPane (PlayerId ownId, Map<PlayerId, String> names,  TrickBean trick) {
        GridPane trickPane = new GridPane();

        trickPane.setStyle("-fx-background-color: transparent; -fx-padding: 5px; -fx-border-width: 3px 0px; -fx-border-style: solid; -fx-border-color: gray; -fx-alignment: center;");

        for(int i=0; i<3;++i) {
            trickPane.addRow(i);
            trickPane.addColumn(i);
        }

        List<VBox> vBoxList = new ArrayList<>();

        for (int i=0; i<PlayerId.COUNT; ++i) {

            PlayerId player =(PlayerId.ALL.get((ownId.ordinal()+i)%4));

            // Creation of the text containing the name
            Text name = new Text (names.get(player));
            name.setStyle("-fx-font: 16 Optima; -fx-font-weight: bold; -fx-fill: white");

            // Creation of the imageView of the card    
            ImageView cardImage = new ImageView();
            cardImage.setFitHeight(CARD_IMAGE_HEIGHT);
            cardImage.setFitWidth(CARD_IMAGE_WIDTH);

            // Bindings of the imageProperty of the imageView of the card with the corresponding card in the trick 
            SimpleObjectProperty<Card> cardIndex = new SimpleObjectProperty<>();
            cardIndex.bind(Bindings.valueAt(trick.trick(), player));
            cardImage.imageProperty().bind(Bindings.valueAt(imageCardsMap,cardIndex));


            // Creation of the red rectangle around the best card  
            Rectangle halo = new Rectangle(cardImage.getFitWidth(), cardImage.getFitHeight());    
            halo.setStyle("-fx-arc-width: 20; -fx-arc-height: 20; -fx-fill: transparent; -fx-stroke: lightpink; -fx-stroke-width: 5; -fx-opacity: 0.85;");   
            halo.setEffect(new GaussianBlur(BLUR_EFFECT_RADIUS)); 
            halo.visibleProperty().bind(trick.winningPlayer().isEqualTo(player));
            
            //Creation of the blue rectangle around the next player
            Rectangle cadre = new Rectangle(cardImage.getFitWidth(), 23);
            cadre.setStyle("-fx-arc-width: 30; -fx-arc-height: 20; -fx-fill: transparent; -fx-stroke: gold; -fx-stroke-width: 3; -fx-opacity: 0.8;");   
            cadre.setEffect(new GaussianBlur(4));
            cadre.visibleProperty().bind(trick.nextPlayer().isEqualTo(player));

            
            
            
            // Creation of the StackPanes 
            StackPane nameStack = new StackPane(name,cadre);
            StackPane imageStack = new StackPane(cardImage,halo);

            // Creation of the VBox with the different node
            VBox b = new VBox();
            b.setStyle("-fx-padding: 5px; -fx-alignment: center;");
            if (player == ownId) {
                b.getChildren().addAll(imageStack,nameStack);
            }
            else {
                b.getChildren().addAll(nameStack,imageStack);
            }

            vBoxList.add(b);
        }

        ImageView trump = new ImageView(); 

        trump.imageProperty().bind(Bindings.valueAt(imageTrumpMap, trick.trump()));

        trump.setFitHeight(TRUMP_IMAGE_SIZE);
        trump.setFitWidth(TRUMP_IMAGE_SIZE);


        //Positioning of the vBox corresponding to ownId card (in the bottom)
        trickPane.add(vBoxList.get(0), 1, 2);

        //Positioning of the vBox corresponding to the card of the player at the left of ownId
        trickPane.add(vBoxList.get(1), 2, 0, 1, 3);

        //Positioning of the vBox corresponding to the card of the player in front of ownId
        trickPane.add(vBoxList.get(2), 1, 0);

        //Positioning of the vBox corresponding to the card of the player at the right of ownId
        trickPane.add(vBoxList.get(3), 0, 0, 1, 3);

        //Positioning of the vBox corresponding to the image of the trump in the center
        trickPane.add(trump, 1, 1);
        
        

        GridPane.setHalignment(trump, HPos.CENTER);
        

        return trickPane;
    }

    /**
     * Create a map associating each card with its image of size 240×360
     * @return (ObservableMap<Card, Image>): the map associating each card with its image
     */
    private static ObservableMap<Card, Image> createImageCardMap() {
        ObservableMap <Card, Image> map = FXCollections.observableHashMap();

        for (int i=0; i<CardSet.ALL_CARDS.size(); ++i) {
            Card c = CardSet.ALL_CARDS.get(i);
            map.put(c, new Image("/card_"+c.color().ordinal()+"_"+c.rank().ordinal()+"_240.png"));
        }
        return map;        
    }

    /**
     *Create a map associating each card with its image of size 160×240
     * @return (ObservableMap<Card, Image>): the map associating each card with its image
     */
    private static ObservableMap<Card, Image> createImageHandCardMap() {
        ObservableMap <Card, Image> map = FXCollections.observableHashMap();

        for (int i=0; i<CardSet.ALL_CARDS.size(); ++i) {
            Card c = CardSet.ALL_CARDS.get(i);
            map.put(c, new Image("/card_"+c.color().ordinal()+"_"+c.rank().ordinal()+"_160.png"));
        }
        return map;        
    }

    /**
     * Create a map associating each Trump color with its image of size 202×202
     * @return (ObservableMap<Card, Image>): the map associating each Trump color with its image
     */
    private static ObservableMap<Color, Image> createImageTrumpMap(){
        ObservableMap <Color, Image> map = FXCollections.observableHashMap();
        for (int i=0; i<Card.Color.COUNT; ++i) {
            Color c = Card.Color.ALL.get(i);
            map.put(c, new Image("/trump_"+c.ordinal()+".png"));
        }
        return map;
    }
    
  

   

}
