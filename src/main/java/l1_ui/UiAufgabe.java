package l1_ui;

//import com.sun.deploy.util.SessionState;
import com.sun.net.httpserver.Authenticator;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import l2_lg.LgSession;
import l2_lg.LgSessionImpl;
import l3_da.DaAufgabe;
import l3_da.DaAufgabeImpl;
import l4_dm.DmAufgabe;
import l4_dm.DmSchritt;
import l4_dm.DmVorhaben;

import javax.persistence.Persistence;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.List;
import java.util.ResourceBundle;

public class UiAufgabe implements Initializable {

    @FXML
    protected TextField restStundenTextField;
    @FXML

    protected TextField istStundenTextField;
    @FXML

    protected TextField statusTextField;
    @FXML

    private ChoiceBox teilVonAuswahl;
    @FXML

    private Button speichernButton;
    @FXML

    private Button loeschenButton;
    @FXML

    private TextField idTextField;
    @FXML

    private TextField titelTextField;
    @FXML

    private TextField beschreibungTextField;
    @FXML

    protected HBox buttonBox;
    public final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("schrittView.fxml"));


    public DmAufgabe aufgabe;
    public List<DmVorhaben> listeDmVorhaben;
    public String titel;
    Scene myScene;
    UiAufgabenTabelle owner;
    private LgSession lgSession;

    public UiAufgabe(DmAufgabe aufgabe, List<DmVorhaben> listeDmVorhaben, UiAufgabenTabelle owner, LgSession lgSession) throws IOException {
        this.aufgabe = aufgabe;
        this.listeDmVorhaben = listeDmVorhaben;
        fxmlLoader.setController(this);
        this.lgSession = lgSession;
        this.owner = owner;

        myScene = new Scene(fxmlLoader.load());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        if (aufgabe.getId() != null) {
            idTextField.setText(aufgabe.getId().toString());
            titelTextField.setText(aufgabe.getTitel());


            istStundenTextField.setText("" + aufgabe.getIstStunden());
            restStundenTextField.setText("" + aufgabe.getRestStunden());
            beschreibungTextField.setText(aufgabe.getBeschreibung());
        }
        if (listeDmVorhaben != null) {
            final ObservableList<DmVorhaben> dmVorhabenObservableList;
            dmVorhabenObservableList = FXCollections.observableArrayList();
            dmVorhabenObservableList.add(null);
            dmVorhabenObservableList.addAll(listeDmVorhaben);
            teilVonAuswahl.getSelectionModel().select(aufgabe.getGanzes());
            teilVonAuswahl.setItems(FXCollections.observableArrayList(dmVorhabenObservableList));

        }

        idTextField.setDisable(true);
        statusTextField.setDisable(true);
        speichernButton.setOnAction(saveAction);
        loeschenButton.setOnAction(clearAction);

    }



    private final ExceptionReportingFxAction saveAction = new ExceptionReportingFxAction("Speichern", ev -> {
        System.out.println("Speichern des Schritts " + titelTextField.getText());

        aufgabe.setBeschreibung(beschreibungTextField.getText());
        aufgabe.setTitel(titelTextField.getText());
        if (aufgabe instanceof DmSchritt) {
            ((DmSchritt) aufgabe).setIstStunden(Integer.parseInt(istStundenTextField.getText()));
            ((DmSchritt) aufgabe).setRestStunden(Integer.parseInt(restStundenTextField.getText()));
        }
        if (aufgabe instanceof DmVorhaben) {
            ((DmVorhaben) aufgabe).setEndTermin(new java.sql.Date(15112019));

        }
        DmVorhaben temp = ((DmVorhaben) teilVonAuswahl.getSelectionModel().getSelectedItem());
        aufgabe.setGanzes(temp);


        lgSession.speichern(aufgabe);
        owner.showAufgaben();
        idTextField.setText(aufgabe.getId().toString());

    });

    private final ExceptionReportingFxAction clearAction = new ExceptionReportingFxAction("Löschen", ev -> {
        System.out.println("Löschen des Schritts " + titelTextField.getText());
        lgSession.loeschen(aufgabe.getId());
        owner.showAufgaben();
    });

    protected final ExceptionReportingFxAction erledigenAction = new ExceptionReportingFxAction("Löschen", ev -> {
        lgSession.erledigen(((DmSchritt) aufgabe));
        owner.showAufgaben();
    });


}



