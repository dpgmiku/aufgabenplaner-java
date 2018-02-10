package l1_ui;

import javafx.application.Application;
import javafx.stage.Stage;
import l2_lg.LgSession;
import l2_lg.LgSessionImpl;

/**
 * Created by miku on 16.01.17.
 */
public class ClientFxApplication extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        final LgSession lgSession = new LgSessionImpl();


        final UiAufgabenTabelle uiAufgabe = new UiAufgabenTabelle("Meine Aufgaben", lgSession.alleOberstenAufgabenLiefern());

        primaryStage.setTitle(uiAufgabe.titelAufgaben);
        primaryStage.setScene(uiAufgabe.myScene);
        primaryStage.show();
    }

    public static void main(String[] args) {

        try {
            launch(args);
        } catch
                (Exception ex) {
            multex.Msg.printReport(ex);
        }

    }
}