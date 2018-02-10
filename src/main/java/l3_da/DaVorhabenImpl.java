package l3_da;

import l4_dm.DmVorhaben;

import javax.persistence.EntityManager;

/**
 * Created by Doebert on 07.11.2016.
 */
public class DaVorhabenImpl extends DaGenericImpl<DmVorhaben> implements DaVorhaben {


    public DaVorhabenImpl(EntityManager entityManager) {
        super(DmVorhaben.class, entityManager);
    }
}
