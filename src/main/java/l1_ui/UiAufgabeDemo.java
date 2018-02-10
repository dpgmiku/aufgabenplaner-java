package l1_ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Stage;
import l1_ui.UiAufgabe;

/**
 * Created by s841569 on 13.12.2016.
 */
public class UiAufgabeDemo  {
    UiAufgabe uiAufgabe;

    public UiAufgabeDemo(UiAufgabe uiAufgabe) {
        this.uiAufgabe = uiAufgabe;

    }

    public void showUiAufgabe()  {
        Stage aufgabeStage = new Stage();

        aufgabeStage.setTitle(uiAufgabe.titel);
        aufgabeStage.setScene(uiAufgabe.myScene);
        aufgabeStage.show();
    }

}
