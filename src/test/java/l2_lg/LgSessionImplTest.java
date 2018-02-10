package l2_lg;

import junit.framework.Assert;
import l3_da.DaFactory;
import l3_da.DaFactoryForJPA;
import l3_da.DaGeneric;
import l3_da.DaVorhaben;
import l4_dm.DmAufgabe;
import l4_dm.DmAufgabeStatus;
import l4_dm.DmSchritt;
import l4_dm.DmVorhaben;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;

/**
 * Created by s841569 on 08.11.2016.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LgSessionImplTest {
    private static final DaFactory daFactory = new DaFactoryForJPA();
    final LgSessionImpl lg = new LgSessionImpl(daFactory);

    @Test
    public void t01_save() {
        final DmVorhaben vorhaben1 = new DmVorhaben();
        vorhaben1.setTitel("Hausaufgabe");
        vorhaben1.setEndTermin(new java.sql.Date(15112017));
        assertEquals(null, vorhaben1.getId());

        lg.speichern(vorhaben1);
        assertEquals(Long.valueOf(1), vorhaben1.getId());
        assertEquals("Hausaufgabe", vorhaben1.getTitel());
        final DmSchritt schritt = new DmSchritt();
        schritt.setTitel("Post abholen");
        schritt.setGanzes(vorhaben1);
        schritt.setRestStunden(3);
        schritt.setIstStunden(12);


        assertEquals(null, schritt.getId());
        assertEquals("neu", schritt.getStatus().toString());
        lg.speichern(schritt);

        assertEquals(Long.valueOf(2), schritt.getId());
        assertEquals("Post abholen", schritt.getTitel());
        assertEquals("inBearbeitung", schritt.getStatus().toString());
        assertEquals(3, schritt.getRestStunden());

        final DmSchritt schritt2 = new DmSchritt();
        schritt2.setTitel("Paket abholen");
        schritt2.setGanzes(vorhaben1);
        schritt2.setRestStunden(7);
        schritt2.setIstStunden(10);

        assertEquals(null, schritt2.getId());
        assertEquals("neu", schritt2.getStatus().toString());


        lg.speichern(schritt2);
        assertEquals("inBearbeitung", schritt2.getStatus().toString());

    }

    @Test
    public void t02_delete() {
        final DmVorhaben vorhaben2 = new DmVorhaben();
        vorhaben2.setTitel("Löschen");
        vorhaben2.setEndTermin(new java.sql.Date(15112017));
        lg.speichern(vorhaben2);
        assertEquals(Long.valueOf(4), vorhaben2.getId());
        lg.loeschen(vorhaben2.getId());
        try {
            // assertEquals("[Vorhaben(1, Hausaufgabe)]", lg.alleOberstenAufgabenLiefern().toString());
            lg.loeschen(vorhaben2.getId());
            fail("DaGeneric.IdNotFoundExc expected");
        } catch (DaGeneric.IdNotFoundExc expected) {
        }


    }

    @Test
    public void t03_erledigen() {
        final DmVorhaben vorhaben = new DmVorhaben();
        vorhaben.setTitel("Übung 1");
        vorhaben.setEndTermin(new java.sql.Date(15112017));
        assertEquals(null, vorhaben.getId());
        lg.speichern(vorhaben);
        assertEquals(Long.valueOf(5), vorhaben.getId());
        assertEquals("Übung 1", vorhaben.getTitel());
        final DmSchritt schritt = new DmSchritt();
        schritt.setTitel("Post abholen");
        schritt.setGanzes(vorhaben);
        schritt.setRestStunden(3);
        schritt.setIstStunden(12);
        assertEquals(null, schritt.getId());
        assertEquals("neu", schritt.getStatus().toString());
        lg.erledigen(schritt);
        assertEquals("erledigt", schritt.getStatus().toString());
        assertEquals(Long.valueOf(6), schritt.getId());
        assertEquals("Post abholen", schritt.getTitel());
        assertEquals(0, schritt.getRestStunden());
        assertEquals(java.sql.Date.valueOf(LocalDate.now()), schritt.getErledigtZeitpunkt());
    }

    @Test
    public void t04_alleOberstenAufgabenLiefern() {
        lg.alleOberstenAufgabenLiefern();
        assertEquals("[Vorhaben(5, Übung 1), Vorhaben(1, Hausaufgabe)]", lg.alleOberstenAufgabenLiefern().toString());
        final DmVorhaben dmVorhaben = (DmVorhaben) lg.alleOberstenAufgabenLiefern().get(1);
        assertEquals("Hausaufgabe", dmVorhaben.getTitel());
        assertEquals(10, dmVorhaben.getRestStunden());
        assertEquals(22, dmVorhaben.getIstStunden());
        assertEquals(DmAufgabeStatus.inBearbeitung, dmVorhaben.getStatus());
        final DmVorhaben dmVorhaben2 = (DmVorhaben) lg.alleOberstenAufgabenLiefern().get(0);
        assertEquals("Übung 1", dmVorhaben2.getTitel());
        assertEquals(0, dmVorhaben2.getRestStunden());
        assertEquals(12, dmVorhaben2.getIstStunden());
        assertEquals(DmAufgabeStatus.erledigt, dmVorhaben2.getStatus());

    }

    @Test
    public void t05_alleVorhabenLiefern() {
        final DmVorhaben vorhaben = new DmVorhaben();
        vorhaben.setTitel("Übung 2");
        vorhaben.setEndTermin(new java.sql.Date(15112017));
        assertEquals(null, vorhaben.getId());
        lg.speichern(vorhaben);
        assertEquals(Long.valueOf(7), vorhaben.getId());
        assertEquals("Übung 2", vorhaben.getTitel());

        final DmVorhaben vorhaben2 = new DmVorhaben();
        vorhaben2.setTitel("Übung 3");
        vorhaben2.setEndTermin(new java.sql.Date(17112017));
        vorhaben2.setIstStunden(2);
        vorhaben2.setRestStunden(25);
        vorhaben2.setGanzes(vorhaben);
        assertEquals(null, vorhaben2.getId());
        lg.speichern(vorhaben2);
        assertEquals(Long.valueOf(8), vorhaben2.getId());
        assertEquals("Übung 3", vorhaben2.getTitel());

        lg.alleVorhabenLiefern();
        assertEquals("[Vorhaben(8, Übung 3), Vorhaben(7, Übung 2), Vorhaben(5, Übung 1), Vorhaben(1, Hausaufgabe)]", lg.alleVorhabenLiefern().toString());

    }
    @Test
    public void t06_rekursionError() {
        DmVorhaben dmVorhaben = new DmVorhaben();
        dmVorhaben.setTitel("Übung 3");
        dmVorhaben.setIstStunden(2);
        dmVorhaben.setRestStunden(25);
        dmVorhaben.setEndTermin(new java.sql.Date(17112017));
        lg.speichern(dmVorhaben);
        dmVorhaben.setGanzes(dmVorhaben);
        try {
            lg.speichern(dmVorhaben);
            fail("LgSession.VorhabenRekursionExc expected");
        }
        catch (LgSession.VorhabenRekursionExc expected){};
    }
}