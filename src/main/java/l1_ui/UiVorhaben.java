package l1_ui;

import javafx.scene.control.Button;
import l2_lg.LgSession;
import l4_dm.DmAufgabe;
import l4_dm.DmVorhaben;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by s841569 on 13.12.2016.
 */
public class UiVorhaben extends UiAufgabe {


    public UiVorhaben(DmAufgabe aufgabe, List<DmVorhaben> listeDmVorhaben, UiAufgabenTabelle owner, LgSession lgSession) throws IOException {
        super(aufgabe, listeDmVorhaben,owner, lgSession);
        if (aufgabe.getId()!=null) {
            super.titel = "Vorhaben ändern";
        }
        else
        {
            super.titel="Neu Vorhaben";

        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        super.initialize(location, resources);
        super.istStundenTextField.setDisable(true);
        super.restStundenTextField.setDisable(true);
        final Button teilAufgabenButton= new Button("Zu den Teil-Aufgaben");
        buttonBox.getChildren().add(teilAufgabenButton);

        teilAufgabenButton.setOnAction(e -> {
               owner.showTeilaufgaben(aufgabe.getTeile());

        });
    }



}
