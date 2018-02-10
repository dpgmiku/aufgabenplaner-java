package l3_da;

import l4_dm.DmSchritt;

import javax.persistence.EntityManager;

/**
 * Created by s841569 on 25.10.2016.
 */
public class DaSchrittImpl extends DaGenericImpl<DmSchritt> implements DaSchritt{


    public DaSchrittImpl(EntityManager entityManager){
        super(DmSchritt.class, entityManager);
    }
}
