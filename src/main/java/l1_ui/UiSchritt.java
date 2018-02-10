package l1_ui;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import l2_lg.LgSession;
import l4_dm.DmAufgabe;
import l4_dm.DmSchritt;
import l4_dm.DmVorhaben;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by s841569 on 13.12.2016.
 */
public class UiSchritt extends UiAufgabe {



    public UiSchritt(DmAufgabe aufgabe, List<DmVorhaben> listeDmVorhaben, UiAufgabenTabelle owner, LgSession lgSession) throws IOException {
        super(aufgabe, listeDmVorhaben,owner,lgSession);
        if (aufgabe.getId()!=null) {
            super.titel = "Schritt ändern";
        }
        else
        {
          super.titel="Neu Schritt";

        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
       // super.erledigenButton.setText("Erledigen");

        statusTextField.setText(aufgabe.getStatus().toString());

        final Button erledigenButton= new Button("Erledigen");
        buttonBox.getChildren().add(erledigenButton);
        erledigenButton.setOnAction(super.erledigenAction);
    }










}
