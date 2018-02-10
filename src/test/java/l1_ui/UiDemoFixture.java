package l1_ui;

import l4_dm.DmAufgabe;
import l4_dm.DmAufgabeStatus;
import l4_dm.DmSchritt;
import l4_dm.DmVorhaben;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

/**
 * Some data for demonstration of the user interface.
 * @author Christoph Knabe
 * @since 2016-11-18
 */
public class UiDemoFixture {


    /**Eine Schritt-Klasse für Demonstrationszwecke. Attribute ohne Setter können hier durch Konstruktorargumente gesetzt werden.*/
    /*Beerbung von DmSchritt durch benannte Klasse notwendig, da JavaFX mit Reflection arbeitet, welche keine Infos aus Objekten anonymer innerer Klassen herausziehen kann.*/
    public static class DemoSchritt extends DmSchritt {

        private final Long _id;
        private final DmAufgabeStatus _status;

        public DemoSchritt(final long id, final DmAufgabeStatus status) {
            _id = id;   _status = status;
        }

        @Override
        public Long getId() {
            return _id;
        }

        @Override
        public DmAufgabeStatus getStatus() {
            return _status;
        }
    }

    /**Eine Vorhaben-Klasse für Demonstrationszwecke. Attribute ohne Setter können hier durch Konstruktorargumente gesetzt werden.*/
    /*Beerbung von DmVorhaben durch benannte Klasse notwendig, da JavaFX mit Reflection arbeitet, welche keine Infos aus Objekten anonymer innerer Klassen herausziehen kann.*/
    public static class DemoVorhaben extends DmVorhaben {

        private final Long _id;
        private final DmAufgabeStatus _status;
        private final int _anzahlTeile;

        public DemoVorhaben(final long id, final DmAufgabeStatus status, final int anzahlTeile) {
            _id = id;   _status = status;   _anzahlTeile = anzahlTeile;
        }

        @Override
        public Long getId() {
            return _id;
        }

        @Override
        public DmAufgabeStatus getStatus() {
            return _status;
        }

        @Override
        public int getAnzahlTeile() {
            return _anzahlTeile;
        }

    }

    final DmVorhaben wohnzimmerAufraeumen = new DemoVorhaben(17, DmAufgabeStatus.inBearbeitung, 4);
    //dynamic initializer:
    {
        wohnzimmerAufraeumen.setTitel("Wohnzimmer aufräumen");
        wohnzimmerAufraeumen.setBeschreibung("Regal, Tisch und Couch aufräumen, Staubsaugen");
        wohnzimmerAufraeumen.setRestStunden(10);
        wohnzimmerAufraeumen.setIstStunden(1);
    }

    /**
     * Ein Schritt mit ID 99 und weiteren Beispieldaten.
     */
    public final DmSchritt regalOrdnen = new DemoSchritt(99, DmAufgabeStatus.neu);
    //dynamic initializer:
    {
        regalOrdnen.setTitel("Bücherregal neu ordnen");
        regalOrdnen.setBeschreibung("Dieser Schritt ist viel Arbeit.\nEr muss dennoch erledigt werden.");
        regalOrdnen.setRestStunden(7);
        regalOrdnen.setIstStunden(0);
    }

    public List<DmVorhaben> alleBspVorhaben() {
        final DmVorhaben persistenzLoesen = new DemoVorhaben(11, DmAufgabeStatus.neu, 2);
        persistenzLoesen.setTitel("Persistenzaufgabe lösen");

        final DmVorhaben patternsDurcharbeiten = new DemoVorhaben(25, DmAufgabeStatus.inBearbeitung, 30);
        patternsDurcharbeiten.setTitel("Design Patterns durcharbeiten");

        final List<DmVorhaben> result = Arrays.asList(persistenzLoesen, wohnzimmerAufraeumen, patternsDurcharbeiten);
        return result;
    }

    public List<DmAufgabe> gemischteAufgaben() {
        final DmAufgabe aufgabe1 = new DemoSchritt(1, DmAufgabeStatus.erledigt);
        aufgabe1.setTitel("Schritt 1: Vorbereiten");

        final DmVorhaben aufgabe2 = new DemoVorhaben(2, DmAufgabeStatus.inBearbeitung, 2);
        aufgabe2.setTitel("Vorhaben: Bearbeiten");

        final DmAufgabe aufgabe3 = new DemoSchritt(5, DmAufgabeStatus.neu);
        aufgabe3.setTitel("Schritt 3: Beenden");

        return Arrays.asList(aufgabe1, aufgabe2, aufgabe3);
    }

}
