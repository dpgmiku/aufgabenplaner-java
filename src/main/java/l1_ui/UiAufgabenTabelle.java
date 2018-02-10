package l1_ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import l2_lg.LgSession;
import l2_lg.LgSessionImpl;
import l4_dm.DmAufgabe;
import l4_dm.DmSchritt;
import l4_dm.DmVorhaben;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static multex.MultexUtil.create;

/**
 * Created by Doebert on 16.12.2016.
 */
public class UiAufgabenTabelle implements Initializable {
    @FXML
    private Button schrittErfassen;
    @FXML

    private Button vorhabenErfassen;
    @FXML

    private Button aendern;
    @FXML

    private Button obersteAufgaben;
    @FXML

    private TableView<DmAufgabe> tableView;
    @FXML

    private TableColumn<DmAufgabe, String> id;
    @FXML

    private TableColumn<DmAufgabe, String> titel;
    @FXML

    private TableColumn<DmAufgabe, Integer> teile;
    @FXML

    private TableColumn<DmAufgabe, String> status;

    public ObservableList<DmAufgabe> tableContent;
    private final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("aufgabenTabelleView.fxml"));

    final String titelAufgaben;
    private List<DmAufgabe> anzuzeigendeAufgaben;
    final Scene myScene;
    LgSession lgSession = new LgSessionImpl();

    public UiAufgabenTabelle(final String titel, final List<DmAufgabe> anzuzeigendeAufgaben) throws IOException {
        this.anzuzeigendeAufgaben = anzuzeigendeAufgaben;
        titelAufgaben = titel;
        fxmlLoader.setController(this);
        myScene = new Scene(fxmlLoader.load());

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureTable();
        showAufgaben();
        schrittErfassen.setOnAction(schrittAction);
        obersteAufgaben.setOnAction(showAction);

        vorhabenErfassen.setOnAction(vorhabenAction);

        aendern.setOnAction(aendernAction);
    }


    public void showAufgaben() {

        tableContent.clear();

        tableContent.addAll(lgSession.alleOberstenAufgabenLiefern());

    }

    public void showTeilaufgaben(List<DmAufgabe> list) {

        tableContent.clear();

        tableContent.addAll(list);

    }

    private void configureTable() {
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        titel.setCellValueFactory(new PropertyValueFactory<>("titel"));
        teile.setCellValueFactory(new PropertyValueFactory<>("anzahlTeile"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        tableContent = FXCollections.observableArrayList();
        tableView.setItems(tableContent);
    }


    private final ExceptionReportingFxAction schrittAction = new ExceptionReportingFxAction("Schritt erfassen", ev -> {
        new UiAufgabeDemo(new UiSchritt(new DmSchritt(), lgSession.alleVorhabenLiefern(), this, lgSession)).showUiAufgabe();

    });

    private final ExceptionReportingFxAction vorhabenAction = new ExceptionReportingFxAction("Vorhaben erfassen", ev -> {
        new UiAufgabeDemo(new UiVorhaben(new DmVorhaben(), lgSession.alleVorhabenLiefern(), this, lgSession)).showUiAufgabe();
    });


    private final ExceptionReportingFxAction aendernAction = new ExceptionReportingFxAction("Aendern", ev -> {
        DmAufgabe dmAufgabe = tableView.getSelectionModel().getSelectedItem();
        if (dmAufgabe instanceof DmVorhaben) {
            new UiAufgabeDemo(new UiVorhaben(dmAufgabe, lgSession.alleVorhabenLiefern(), this, lgSession)).showUiAufgabe();
        } else {

            new UiAufgabeDemo(new UiSchritt(dmAufgabe, lgSession.alleVorhabenLiefern(), this, lgSession)).showUiAufgabe();

        }    });
    private final ExceptionReportingFxAction showAction = new ExceptionReportingFxAction("Aufgaben zeigen", ev -> {
        tableContent.clear();

        tableContent.addAll(lgSession.alleOberstenAufgabenLiefern());   });
    }


