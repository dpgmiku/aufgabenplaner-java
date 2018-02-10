package l1_ui;

import l2_lg.LgSessionImpl;
import l3_da.DaFactory;
import l3_da.DaFactoryForJPA;
import l4_dm.DmAufgabeStatus;
import l4_dm.DmSchritt;
import l4_dm.DmVorhaben;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by s841569 on 06.12.2016.
 */
public class UiDemoFixtureDaniel {


    public final DmSchritt schritt;
    public final DmVorhaben dmVorhaben;
    private List<DmVorhaben> liste = new ArrayList<>();
    private static final DaFactory daFactory = new DaFactoryForJPA();

    final LgSessionImpl lg = new LgSessionImpl(daFactory);


    public UiDemoFixtureDaniel() {
        schritt = new DmSchritt();
        schritt.setTitel("Bücherregal neu ordnen");
        schritt.setBeschreibung("Dieser Schritt ist viel Arbeit");
        schritt.setRestStunden(7);
        schritt.setIstStunden(0);
        lg.speichern(schritt);

        dmVorhaben = new DmVorhaben();
        dmVorhaben.setTitel("Wohnzimmer aufräumen");
        dmVorhaben.setBeschreibung("Regal, Tisch und Couch aufräumen, Staubsaugen");
        dmVorhaben.setRestStunden(10);
        dmVorhaben.setIstStunden(1);
        dmVorhaben.setStatus(DmAufgabeStatus.inBearbeitung);
        lg.speichern(dmVorhaben);
        liste.add(dmVorhaben);

        DmVorhaben dmVorhaben2 = new DmVorhaben();
        dmVorhaben2.setTitel("17 Wohnzimmer aufräumen");
        dmVorhaben2.setBeschreibung("keine Ahnung");
        dmVorhaben2.setRestStunden(4);
        dmVorhaben2.setIstStunden(5);
        dmVorhaben2.setStatus(DmAufgabeStatus.inBearbeitung);
        lg.speichern(dmVorhaben2);

        liste.add(dmVorhaben2);

    }

    public List<DmVorhaben> getListe() {
        return liste;

    }

}
