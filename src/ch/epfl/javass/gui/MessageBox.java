package ch.epfl.javass.gui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public final class MessageBox {
    
    public static void display(String message, String buttonMessage, String title) {
        BorderPane window =new BorderPane();
        
        Button validate = new Button(buttonMessage);
        
        
        
        Text mess = new Text(message+"\n");
        window.setBottom(validate);
        window.setCenter(mess);
        Stage primaryStage = new Stage();
        Scene scene = new Scene(window);
        primaryStage.setScene(scene);
        primaryStage.setTitle(title);
        primaryStage.show();
        validate.setOnMouseClicked(e->primaryStage.close());
        
    }

}
