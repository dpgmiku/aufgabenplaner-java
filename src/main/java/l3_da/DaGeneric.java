package l3_da;

import l4_dm.DmAufgabe;

import java.util.List;

/** Generic Data Access Object (DAO) for entities of class E.
 * When implementing this interface you should provide a constructor(Class<E> type) in order to select the managed entity class.
 * @author Christoph Knabe
 * @since 2015-11-17
 **/
public interface DaGeneric<E extends DmAufgabe> {
	
	/**Inserts the entity into the database or updates it there according to the passed argument.
	 * @return true if the entity was newly inserted into the database, but false if it was only updated in the database.*/
	boolean save(E entity);

	/**Deletes the entity with the same ID as the passed argument in the database.*/
	void delete(E entity);

	/**Searches by ID and returns the entity of the class, for which this data access service is responsible.*/
	E find(Long id) throws IdNotFoundExc;

	/**Searches and returns all entities of the class, for which this data access service is responsible.*/
	List<E> findAll();

	//Von hier ab optional. Nicht notwendig fuer die Übungsaufgabe "DataAccess Service".
	
	/**Searches and returns all entities of the class, for which this data access service is responsible, whose attribute with the given fieldName has the given fieldValue.*/
	List<E> findByField(String fieldName, Object fieldValue);

	/**Searches and returns all entities of the class, for which this data access service is responsible, which fulfill the given JPA whereClause with the given positional arguments.*/
	List<E> findByWhere(String whereClause, Object... args);

	/**Searches and returns all entities of the class, for which this data access service is responsible, which have the same attributes as the given example filled with an equal value.
	 * null attributes are ignored.*/
	List<E> findByExample(E example);
	
	/**Kein Objekt der Klasse {0} mit ID {1} gefunden*/
	class IdNotFoundExc extends multex.Exc {}
	
}
