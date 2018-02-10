package l3_da;

import l4_dm.DmAufgabe;

import javax.persistence.EntityManager;

/**
 * Created by Doebert on 07.11.2016.
 */
public class DaAufgabeImpl  extends DaGenericImpl<DmAufgabe> implements DaAufgabe {


    public DaAufgabeImpl(EntityManager entityManager) {
        super(DmAufgabe.class, entityManager);
    }


}
