package l1_ui;

import javafx.application.Application;
import javafx.stage.Stage;
import l2_lg.LgSession;
import l2_lg.LgSessionImpl;

import java.io.IOException;

/**
 * Created by Doebert on 17.12.2016.
 */
public class UiStartDemo extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        LgSession lgSession=new LgSessionImpl();

        UiAufgabenTabelle uiAufgabe = new UiAufgabenTabelle("Meine Aufgaben",lgSession.alleOberstenAufgabenLiefern());

        primaryStage.setTitle(uiAufgabe.titelAufgaben);
        primaryStage.setScene(uiAufgabe.myScene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
