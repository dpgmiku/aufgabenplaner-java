package platform;

import l4_dm.DmSchritt;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.*;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by s841569 on 18.10.2016.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EntityManagerTest {
    /**
     * Probiert einige Operationen des JPA-EntityManager aus und protokolliert deren Ergebnisse.
     */


    private static final String persistenceUnitName = "aufgabenplaner"; //as specified in src/main/resources/META-INF/persistence.xml

    //createEntityManagerFactory ist eine sehr aufwändige Operation! Daher static. Die EntityManagerFactory muss am Ende manuell geschlossen werden!
    private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);


    @Test
    public void t01_entityPersistierenOhneRollback() {
        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        final DmSchritt schritt = new DmSchritt();
        schritt.setTitel("Post abholen");
        final Long jVorher = schritt.getId();
        assertEquals(null, jVorher);
        //Hierdurch wird schritt zu einer "managed entity"im Zustand PERSISTENT:
        entityManager.persist(schritt);
        final Long jNachher = schritt.getId();
        assertEquals(Long.valueOf(1), jNachher);
        //Hierdurch werden alle "managed entities" in die Datenbank geschrieben:
        transaction.commit();
        //Macht alle verwalteten Entities DETACHED vom Persistenzkontext und schließt den EntityManager:
        entityManager.close();
    }

    @Test
    public void t02_wirkungRollback() {
        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        final DmSchritt schritt = new DmSchritt();
        schritt.setTitel("Ein problematischer Schritt");
        entityManager.persist(schritt);
        final Long idOfFailed = schritt.getId();
        assertEquals(Long.valueOf(2), idOfFailed);
        assertEquals("Ein problematischer Schritt", schritt.getTitel());
        //logger.info("Schritt nach persist: ID=" + idOfFailed + ", titel=" + schritt.getTitel());
        //Hierdurch werden alle Veränderungen an "managed entities" seit dem letzten commit in der Datenbank rückabgewickelt.
        //Im Arbeitsspeicher aber bleiben sie bestehen. Die Transaktion wird beendet.
        transaction.rollback();
        final Long jNachher = schritt.getId();
        assertEquals(Long.valueOf(2), jNachher);
        assertEquals("Ein problematischer Schritt", schritt.getTitel());
        // logger.info("Schritt nach rollback: ID=" + schritt.getId() + ", titel=" + schritt.getTitel());
        //Macht alle verwalteten Entities DETACHED vom Persistenzkontext:
        entityManager.close();

        //Ab hier alle Transaktionen redundanzfrei mit untenstehender Template-Methode _transaction und Lambda-Ausdruck für die _Action.
        // Für Typ Void muss null zurückgegeben werden:
        _transaction(em -> {
            final DmSchritt schritt2 = em.find(DmSchritt.class, idOfFailed);
            assertEquals(null, schritt2);
            //logger.info("Schritt nach rollback und Holen: " + schritt2);
            return null;
        });
    }

    @Test
    public void t03_entityWiederHolen() {
        _transaction(em -> { //3. Entity mit id wieder holen und ändern:
            final DmSchritt schritt = em.find(DmSchritt.class, 1L);
            assertEquals(Long.valueOf(1), schritt.getId());
            assertEquals("Post abholen", schritt.getTitel());
            assertEquals(0, schritt.getIstStunden());
            //logger.info("Schritt in neuer TX nach find: ID=" + schritt.getId() + ", titel=" + schritt.getTitel() + ", istStunden=" + schritt.getIstStunden());
            schritt.setRestStunden(0);
            schritt.setIstStunden(2);
            return null;
            //Die Änderungen sollen durch das commit am Ende der Transaktion gespeichert werden.
        });
    }

    @Test
    public void t04_detachMerge() { //4. detach und merge prüfen:
        final DmSchritt schritt;
        {
            schritt = _transaction(em -> {
                final DmSchritt result = em.find(DmSchritt.class, 1L);
                assertEquals(Long.valueOf(1), result.getId());
                assertEquals("Post abholen", result.getTitel());
                assertEquals(2, result.getIstStunden());
               /* logger.info("Schritt nach close(true) und Änderung in neuer TX: ID="
                        + result.getId() + ", titel=" + result.getTitel()
                        + ", istStunden=" + result.getIstStunden());*/
                return result;
            });
            //Alle Entities sind jetzt DETACHED: Weitere Änderungen werden nicht mehr automatisch gespeichert:
        }
        //Diese Änderungen werden normalerweise nicht mehr gespeichert:
        schritt.setIstStunden(3);
        _transaction(em -> {
            //Die Entity schritt wieder MANAGED machen. Aktueller Zustand von ihr wird bei Transaktionsende gespeichert:
            em.merge(schritt);
            assertEquals(3, schritt.getIstStunden());
            //logger.info("Schritt-IstStunden nach detach, Änderung und merge in neuer TX: " + schritt.getIstStunden());
            return null;
        });
    }

    @Test
    public void t05_entityWiederHolenMitId() { //5. Entity wieder mit id holen und prüfen:
        final DmSchritt schritt = _transaction(em -> em.find(DmSchritt.class, 1L));
        assertEquals(Long.valueOf(1), schritt.getId());
        assertEquals("Post abholen", schritt.getTitel());
        assertEquals(3, schritt.getIstStunden());
        //  logger.info("Schritt nach neuer TX nach close,Ändern,merge,close,find: ID=" + schritt.getId() + ", titel=" + schritt.getTitel() + ", istStunden=" + schritt.getIstStunden());
    }

    @Test
    public void t06_schritteMitJpqlHolen() { //6. Alle Schritte mit Java Persistence Query Language (JPQL) holen:
        final List<DmSchritt> results = _transaction(em -> {
            //TypedQuery<T> wurde in JPA 2 eingeführt, um den Cast nach T zu vermeiden.
            final TypedQuery<DmSchritt> q = em.createQuery("SELECT o FROM " + DmSchritt.class.getName() + " o ORDER BY id DESC", DmSchritt.class);
            return q.getResultList();
        });
        final StringBuilder out = new StringBuilder();
        for (final DmSchritt s : results) {
            out.append("Schritt: id=").append(s.getId()).append(", titel=").append(s.getTitel()).append("\n");
        }
        assertEquals("Schritt: id=1, titel=Post abholen\n", out.toString());
        //logger.info("Alle Schritte: " + out);
    }

    @Test
    public void t07_entityMitIdHolenUndLoeschen() {
        _transaction(em -> { //7. Entity wieder mit id holen und dann löschen:
            final DmSchritt schritt = em.find(DmSchritt.class, 1L);
            assertEquals(Long.valueOf(1), schritt.getId());
            assertEquals("Post abholen", schritt.getTitel());
            // logger.info("Schritt in neuer TX vor Löschen: ID=" + schritt.getId() + ", titel=" + schritt.getTitel());
            em.remove(schritt);
            assertEquals(Long.valueOf(1), schritt.getId());
            assertEquals("Post abholen", schritt.getTitel());
            //logger.info("Schritt in neuer TX nach Löschen: ID=" + schritt.getId() + ", titel=" + schritt.getTitel());
            return null;
        });
    }

    @Test
    public void t08_entityNachLoeschenHolen() {
        _transaction(em -> { //8. Entity nach Löschen holen:
            final DmSchritt schritt = em.find(DmSchritt.class, 1L);
            assertEquals(null, schritt);
            //logger.info("Schritt nach Löschen und Holen: schritt=" + schritt);
            return null;
        });
    }

    @AfterClass
    public static void aufraeumen() { //Aufräumen:
        entityManagerFactory.close();
    }

    /**
     * Vereinfachte, ergebnisliefernde Transaktion zur Verwendung in Testtreibern.
     * Diese Transaktion macht immer ein abschließendes commit().
     * Die Nutzaktion muss durch Übergabe eines _Action-Objekts mit einer apply-Methode definiert werden.
     * Dafür sollte ein Lambda-Ausdruck der Form em -> result beziehungsweise em -> {... return result;} verwendet werden.
     * Wenn der Ausdruck nichts zurückgeben soll (wegen Ergebnistyp Void) muss er stattdessen null zurückgeben,
     * da es einen Void-Wert in Java nicht gibt.
     */
    private <RESULT> RESULT _transaction(final _Action<RESULT> action) {
        /**Der {@link EntityManager} mit Transaction Scope für die weiteren Datenzugriffsoperationen.*/
        final EntityManager em = entityManagerFactory.createEntityManager();
        final EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        final RESULT result = action.apply(em); //Aufruf der Nutzaktion
        transaction.commit();
        em.close();
        return result;
    }

    @FunctionalInterface
    private interface _Action<RESULT> {

        /**
         * Führt die Nutzaktion der _transaction aus. Diese liefert ein Ergebnis vom Typ RESULT.
         */
        RESULT apply(EntityManager em);

    }
}
