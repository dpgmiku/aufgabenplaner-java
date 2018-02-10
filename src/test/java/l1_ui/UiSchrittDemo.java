package l1_ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by s841569 on 13.12.2016.
 */
public class UiSchrittDemo extends Application {

    @Override
    public void start(final Stage primaryStage) throws Exception {
        final UiDemoFixtureDaniel demo = new UiDemoFixtureDaniel();

       // final UiAufgabe uiAufgabe = new UiSchritt(demo.schritt, demo.getListe());

      //  primaryStage.setTitle(uiAufgabe.titel);
        //primaryStage.setScene(uiAufgabe.myScene);
       // primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}

