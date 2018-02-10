package l3_da;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by s841569 on 25.10.2016.
 */
public class DaFactoryForJPA implements DaFactory {

    private static final String persistenceUnitName = "aufgabenplaner"; //as specified in src/main/resources/META-INF/persistence.xml

    //createEntityManagerFactory ist eine sehr aufwändige Operation! Daher static. Die EntityManagerFactory muss am Ende manuell geschlossen werden!
    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    public DaFactoryForJPA() {
        entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
    }


    public DaFactoryForJPA( final boolean fileBased){

            final String persistenceUnitName = "aufgabenplaner";
            final Map<String,String> fileBasedProperties = new TreeMap<String,String>()
            {{
                put("javax.persistence.jdbc.url", "jdbc:derby:directory:aufgabe-DB;create=true");
            }};
            this.entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName, fileBased ? fileBasedProperties : null
            );

    }
    @Override
    public DaAufgabe getAufgabeDA() {
        return new DaAufgabeImpl(entityManager);
    }

    @Override
    public DaSchritt getSchrittDA() {

        return new DaSchrittImpl(entityManager);
    }

    @Override
    public DaVorhaben getVorhabenDA() {

        return new DaVorhabenImpl(entityManager);
    }

    @Override
    public void beginTransaction() {
        entityManager = entityManagerFactory.createEntityManager();
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
    }

    @Override
    public void endTransaction(boolean ok) {
        if (ok) {
            entityManager.getTransaction().commit();
        } else {
            entityManager.getTransaction().rollback();
        }
        entityManager.close();
        entityManager=null;
    }
}
