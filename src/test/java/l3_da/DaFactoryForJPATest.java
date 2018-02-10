package l3_da;

import l4_dm.DmSchritt;
import l4_dm.DmVorhaben;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by s841569 on 25.10.2016.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DaFactoryForJPATest {

    private static final DaFactory daFactory = new DaFactoryForJPA();


    @Test
    public void t01_saveDmSchritt() {

        daFactory.beginTransaction();
        final DaSchritt daSchritt = daFactory.getSchrittDA();
        final DmSchritt schritt = new DmSchritt();
        schritt.setTitel("Post abholen");

        assertEquals(null, schritt.getId());
        daSchritt.save(schritt);

        assertEquals(Long.valueOf(1), schritt.getId());
        assertEquals("Post abholen", schritt.getTitel());
        daFactory.endTransaction(true);
    }

    @Test
    public void t02_deleteEntity() {


        daFactory.beginTransaction();
        final DaSchritt daSchritt = daFactory.getSchrittDA();
        final DmSchritt schritt = new DmSchritt();
        schritt.setTitel("Post abholen");

        assertEquals(null, schritt.getId());

        daSchritt.save(schritt);

        assertEquals(Long.valueOf(2), schritt.getId());

        daSchritt.delete(schritt);

        assertEquals(Long.valueOf(2), schritt.getId());

        daFactory.endTransaction(true);

    }

    @Test
    public void t03_findSchritt() {


        daFactory.beginTransaction();
        final DaSchritt daSchritt = daFactory.getSchrittDA();
        final DmSchritt schritt = new DmSchritt();
        schritt.setTitel("Post abholen");
        daSchritt.save(schritt);
        daSchritt.find(Long.valueOf(schritt.getId()));
        assertEquals("Schritt(3, Post abholen)", schritt.toString());
        daFactory.endTransaction(true);

    }

    @Test
    public void t04_saveDmVorhaben() {
        daFactory.beginTransaction();
        final DmVorhaben vorhaben = new DmVorhaben();
        vorhaben.setTitel("Hausaufgabe");
        final DaVorhaben daVorhaben = daFactory.getVorhabenDA();

        assertEquals(null, vorhaben.getId());
        daVorhaben.save(vorhaben);

        assertEquals(Long.valueOf(4), vorhaben.getId());
        assertEquals("Hausaufgabe", vorhaben.getTitel());
        daFactory.endTransaction(true);

    }

    @Test
    public void t05_findAllDmVorhaben() {
        daFactory.beginTransaction();
        final DaVorhaben daVorhaben = daFactory.getVorhabenDA();
        final DmVorhaben vorhaben = new DmVorhaben();
        vorhaben.setTitel("Hausaufgabe");
        assertEquals(null, vorhaben.getId());
        final List<DmVorhaben> results = daVorhaben.findAll();
        final StringBuilder out = new StringBuilder();
        for (final DmVorhaben s : results) {
            out.append("Vorhaben: id=").append(s.getId()).append(", titel=").append(s.getTitel()).append("\n");

        }

        assertEquals("Vorhaben: id=4, titel=Hausaufgabe\n", out.toString());
        daFactory.endTransaction(true);

    }

    @Test public void t06_entityMitIdHolen(){
        daFactory.beginTransaction();
        final DaVorhaben daVorhaben = daFactory.getVorhabenDA();
        final DmVorhaben vorhaben= daVorhaben.find(Long.valueOf(4));
        assertEquals(Long.valueOf(4), vorhaben.getId());
        assertEquals("Hausaufgabe", vorhaben.getTitel());
        daFactory.endTransaction(true);
    }

    @Test
    public void t07_dontSaveDmSchrittRollback() {

        daFactory.beginTransaction();
        final DaSchritt daSchritt = daFactory.getSchrittDA();
        final DmSchritt schritt = new DmSchritt();
        schritt.setTitel("Ein problematischer Schritt");

        daSchritt.save(schritt);
        final Long idOfFailed = schritt.getId();
        assertEquals(Long.valueOf(5), idOfFailed);

        daFactory.endTransaction(false);
        final Long jNachher = schritt.getId();
        assertEquals(Long.valueOf(5), jNachher);
        assertEquals("Ein problematischer Schritt", schritt.getTitel());
    }

    @Test public void t080_deleteAndSearch(){
        daFactory.beginTransaction();
        final DaVorhaben daVorhaben = daFactory.getVorhabenDA();
        final DmVorhaben vorhaben= daVorhaben.find(4L);
        assertEquals(Long.valueOf(4), vorhaben.getId());
        assertEquals("Hausaufgabe", vorhaben.getTitel());
        daVorhaben.delete(vorhaben);
        try {
            assertEquals(null, daVorhaben.find(Long.valueOf(4)));
            fail("DaGeneric.IdNotFoundExc expected");
        } catch ( DaGeneric.IdNotFoundExc expected ){}
        daFactory.endTransaction(true);
    }

}
