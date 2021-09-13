package ch.epfl.javass;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.net.RemotePlayerServer;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * This class is the launcher for a remote player 
 * @author Liam Mouzaoui (295797)
 * @author Remi Delacourt (300849)
 */
public final class RemoteMain extends Application {

    /**
     * Launches the application 
     * @param args: The running parameters of the game
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Creates the graphical interface of the remote player and run the server 
     * @throws Exception: when error occurred during the creation of the  window
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Thread serverThread = new Thread(() -> {
            System.out.println("La partie commencera à la connexion du client…");
            RemotePlayerServer server = new RemotePlayerServer(new GraphicalPlayerAdapter());
            server.run();
        });
        serverThread.setDaemon(true);
        serverThread.start();
    }
}
