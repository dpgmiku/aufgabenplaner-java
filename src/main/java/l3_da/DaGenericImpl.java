package l3_da;

import l4_dm.DmAufgabe;
import l4_dm.DmSchritt;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import static multex.MultexUtil.create;

/**
 * Created by s841569 on 25.10.2016.
 */
public class DaGenericImpl<E extends DmAufgabe> implements DaGeneric<E> {

    private final Class<E> managedClass;
    private final EntityManager entityManager;

    public DaGenericImpl(final Class<E> managedClass, final EntityManager entityManager) {
        this.managedClass = managedClass;
        this.entityManager = entityManager;
    }

    @Override
    public boolean save(final E entity) {
        if (entity.getId() == null) {
            entityManager.persist(entity);
            return true;
        } else
            entityManager.merge(entity);


        return false;
    }

    @Override
    public void delete(E entity) {
        if (entity.getId() != null) {

            entityManager.remove(entity);
        }

    }

    @Override
    public E find(final Long id) throws IdNotFoundExc {
        if (id == null) {
            throw new IllegalArgumentException("id ist null");
        }
        E entity = entityManager.find(managedClass, id);
        if (entity==null) {
            throw create(IdNotFoundExc.class, managedClass.getName(), id);
        }

        return entity;
    }

    @Override
    public List<E> findAll() {

        final TypedQuery<E> q = entityManager.createQuery("SELECT o FROM " + managedClass.getName() + " o ORDER BY id DESC", managedClass);
        final List<E>  results = q.getResultList();

        return results;
    }


    /////////////
    @Override
    public List<E> findByField(String fieldName, Object fieldValue) {
        return null;
    }

    @Override
    public List<E> findByWhere(String whereClause, Object... args) {
        return null;
    }

    @Override
    public List<E> findByExample(E example) {
        return null;
    }
}
