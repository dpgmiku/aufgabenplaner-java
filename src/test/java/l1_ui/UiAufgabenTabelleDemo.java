package l1_ui;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by Doebert on 17.12.2016.
 */
public class UiAufgabenTabelleDemo extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        UiDemoFixture demo = new UiDemoFixture();

        UiAufgabenTabelle uiAufgabe = new UiAufgabenTabelle("Meine Aufgaben",demo.gemischteAufgaben());

        primaryStage.setTitle(uiAufgabe.titelAufgaben);
        primaryStage.setScene(uiAufgabe.myScene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
