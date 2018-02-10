package platform;

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import l4_dm.DmSchritt;

/*Aufgabe: Erstellen Sie einen JUnit-Testtreiber, der die Operationen des JPA-EntityManager testet.
 * Überprüfen Sie in Zusicherungen mindestens die Daten auf erwartete Werte,
 * die in dieser Beispielanwendung durch die logger-Aufrufe ausgegeben werden.
 * Machen Sie zur Übersichtlichkeit aus jedem nummerierten Abschnitt eine eigene @Test-Methode.
 * @author Christoph Knabe
 * @since 2015-10-20
 * @version 2016-10-06
 */
public class EntityManagerProbe {
	
	/**Probiert einige Operationen des JPA-EntityManager aus und protokolliert deren Ergebnisse.*/
	public static void main(String[] args) throws Exception {
        new EntityManagerProbe().execute();
	}
	
	private static final String persistenceUnitName = "aufgabenplaner"; //as specified in src/main/resources/META-INF/persistence.xml

	//createEntityManagerFactory ist eine sehr aufwändige Operation! Daher static. Die EntityManagerFactory muss am Ende manuell geschlossen werden!
    private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);

	private final Logger logger = Logger.getLogger(getClass().getName());	

	private void execute() throws Exception {	    
	    { //1. Entity persistieren in manueller Transaktion mit "transaction-scoped persistence context" ohne Rollback:
	        final EntityManager entityManager = entityManagerFactory.createEntityManager();
			final EntityTransaction transaction = entityManager.getTransaction();
			transaction.begin();
			final DmSchritt schritt = new DmSchritt();
			schritt.setTitel("Post abholen");
            //schritt ist im Zustand TRANSIENT und hat keine ID.
			logger.info("Schritt-Id vor persist: " + schritt.getId());
			//Hierdurch wird schritt zu einer "managed entity"im Zustand PERSISTENT:
			entityManager.persist(schritt);
			logger.info("Schritt-Id nach persist: " + schritt.getId());
			//Hierdurch werden alle "managed entities" in die Datenbank geschrieben:
			transaction.commit();
			//Macht alle verwalteten Entities DETACHED vom Persistenzkontext und schließt den EntityManager:
			entityManager.close();
		}
	    
	    { //2. Wirkung des rollback ausprobieren:
	        final EntityManager entityManager = entityManagerFactory.createEntityManager();
			final EntityTransaction transaction = entityManager.getTransaction();
			transaction.begin();
			final DmSchritt schritt = new DmSchritt();
			schritt.setTitel("Ein problematischer Schritt");
			entityManager.persist(schritt);
			final Long idOfFailed = schritt.getId();
			logger.info("Schritt nach persist: ID=" + idOfFailed + ", titel=" + schritt.getTitel());
			//Hierdurch werden alle Veränderungen an "managed entities" seit dem letzten commit in der Datenbank rückabgewickelt.
			//Im Arbeitsspeicher aber bleiben sie bestehen. Die Transaktion wird beendet.
			transaction.rollback();
			logger.info("Schritt nach rollback: ID=" + schritt.getId() + ", titel=" + schritt.getTitel());
			//Macht alle verwalteten Entities DETACHED vom Persistenzkontext:
			entityManager.close();
			
			//Ab hier alle Transaktionen redundanzfrei mit untenstehender Template-Methode _transaction und Lambda-Ausdruck für die _Action.
            // Für Typ Void muss null zurückgegeben werden:
            _transaction(em -> {
                final DmSchritt schritt2 = em.find(DmSchritt.class, idOfFailed);
                logger.info("Schritt nach rollback und Holen: " + schritt2);
                return null;
            });
		}

	    _transaction(em -> { //3. Entity mit id wieder holen und ändern:
			final DmSchritt schritt = em.find(DmSchritt.class, 1L);
			logger.info("Schritt in neuer TX nach find: ID=" + schritt.getId() + ", titel=" + schritt.getTitel() + ", istStunden=" + schritt.getIstStunden());
			schritt.setRestStunden(0);
			schritt.setIstStunden(2);
            return null;
			//Die Änderungen sollen durch das commit am Ende der Transaktion gespeichert werden.
		});

        { //4. detach und merge prüfen:
            final DmSchritt schritt;
            {
                schritt = _transaction(em -> {
                    final DmSchritt result = em.find(DmSchritt.class, 1L);
                    logger.info("Schritt nach close(true) und Änderung in neuer TX: ID="
                            + result.getId() + ", titel=" + result.getTitel()
                            + ", istStunden=" + result.getIstStunden());
                    return result;
                });
                //Alle Entities sind jetzt DETACHED: Weitere Änderungen werden nicht mehr automatisch gespeichert:
            }
            //Diese Änderungen werden normalerweise nicht mehr gespeichert:
            schritt.setIstStunden(3);
            _transaction(em -> {
                //Die Entity schritt wieder MANAGED machen. Aktueller Zustand von ihr wird bei Transaktionsende gespeichert:
                em.merge(schritt);
                logger.info("Schritt-IstStunden nach detach, Änderung und merge in neuer TX: " + schritt.getIstStunden());
                return null;
            });
        }
	    
	    { //5. Entity wieder mit id holen und prüfen:
			final DmSchritt schritt = _transaction(em -> em.find(DmSchritt.class, 1L));
			logger.info("Schritt nach neuer TX nach close,Ändern,merge,close,find: ID=" + schritt.getId() + ", titel=" + schritt.getTitel() + ", istStunden=" + schritt.getIstStunden());
	    }
	    
	    { //6. Alle Schritte mit Java Persistence Query Language (JPQL) holen:
            final List<DmSchritt> results = _transaction(em -> {
                //TypedQuery<T> wurde in JPA 2 eingeführt, um den Cast nach T zu vermeiden.
                final TypedQuery<DmSchritt> q = em.createQuery("SELECT o FROM " + DmSchritt.class.getName() + " o ORDER BY id DESC", DmSchritt.class);
                return q.getResultList();
            });
            final StringBuilder out = new StringBuilder();
            for(final DmSchritt s: results){
            	out.append("Schritt: id=").append(s.getId()).append(", titel=").append(s.getTitel()).append("\n");
            }
            logger.info("Alle Schritte: " + out);	    	
	    }
	    
	    _transaction(em -> { //7. Entity wieder mit id holen und dann löschen:
			final DmSchritt schritt = em.find(DmSchritt.class, 1L);
			logger.info("Schritt in neuer TX vor Löschen: ID=" + schritt.getId() + ", titel=" + schritt.getTitel());
			em.remove(schritt);
			logger.info("Schritt in neuer TX nach Löschen: ID=" + schritt.getId() + ", titel=" + schritt.getTitel());
			return null;
	    });

        _transaction(em -> { //8. Entity nach Löschen holen:
			final DmSchritt schritt = em.find(DmSchritt.class, 1L);
			logger.info("Schritt nach Löschen und Holen: schritt=" + schritt);
			return null;
	    });
	    
	    { //Aufräumen:
	    	entityManagerFactory.close();
	    }
		
	}

    /**Vereinfachte, ergebnisliefernde Transaktion zur Verwendung in Testtreibern.
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

        /**Führt die Nutzaktion der _transaction aus. Diese liefert ein Ergebnis vom Typ RESULT.*/
        RESULT apply(EntityManager em);

    }

}
