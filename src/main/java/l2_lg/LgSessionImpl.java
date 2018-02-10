package l2_lg;

import l3_da.*;
import l4_dm.DmAufgabe;
import l4_dm.DmAufgabeStatus;
import l4_dm.DmSchritt;
import l4_dm.DmVorhaben;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static multex.MultexUtil.create;

/**
 * Created by s841569 on 08.11.2016.
 */
public class LgSessionImpl implements LgSession {

   // private DaFactory factory = new DaFactoryForJPA();

    private DaFactory factory = new DaFactoryForJPA(true);

    public LgSessionImpl(DaFactory fa) {
        this.factory = fa;
    }

    public LgSessionImpl() {
    }


    @Override
    public <A extends DmAufgabe> A speichern(A aufgabe) throws TitelExc, RestStundenExc, IstStundenExc, EndTerminExc, VorhabenRekursionExc {
        if (aufgabe.getTitel().length() < 10 && aufgabe.getTitel().length() > 200) {
            throw create(TitelExc.class, aufgabe.getTitel().length(), aufgabe.getTitel());
        }
        if (aufgabe instanceof DmSchritt)
            if (aufgabe.getRestStunden() < 0) {
                throw create(RestStundenExc.class, aufgabe.getRestStunden());
            }
        if (aufgabe instanceof DmSchritt)
            if (aufgabe.getIstStunden() < 0) {
                throw create(IstStundenExc.class, aufgabe.getIstStunden());
            }
       if (aufgabe.getGanzes() != null) {
            if (aufgabe.getGanzes().getEndTermin().after(Date.valueOf(LocalDate.now()))) {
                throw create(EndTerminExc.class,  aufgabe.getId());
            }
      }
         vorhabenRekursiv(aufgabe.getGanzes(), aufgabe);

        return _transaction(() -> {
                    final DaAufgabe daAufgabe = factory.getAufgabeDA();
                    daAufgabe.save(aufgabe);
                    return aufgabe;
                }
        );


    }

   /* private void vorhabenRekursionPruefen(DmAufgabe aufgabe) {
        LinkedList<DmAufgabe> liste = new LinkedList<>();
        if (aufgabe.getGanzes() != null) {
            DmAufgabe dmAufgabe = aufgabe.getGanzes();
            Boolean ok = true;
            while (ok) {
                liste.add(dmAufgabe);
                dmAufgabe = dmAufgabe.getGanzes();
                if (dmAufgabe == null) {
                    ok = false;
                }
            }

            if (liste.contains(aufgabe)) {
                throw create(VorhabenRekursionExc.class, DmAufgabe.class, aufgabe.getId());
            }

        }
    }*/

    private void vorhabenRekursiv(final DmVorhaben dmVorhaben, final DmAufgabe aufgabe){
        if (dmVorhaben==null)
        {
            return;
        }
        if(dmVorhaben.equals(aufgabe)){
            throw create(VorhabenRekursionExc.class, aufgabe.getId(), aufgabe.getTitel(), dmVorhaben.getId());
        }
        vorhabenRekursiv(dmVorhaben.getGanzes(), aufgabe);
    }


    @Override
    public void loeschen(final Long aufgabenId) throws DaGeneric.IdNotFoundExc, LoeschenTeileExc {
        if (aufgabenId == null) {
            throw create(DaGeneric.IdNotFoundExc.class, DmAufgabe.class, aufgabenId);
        }

        _transaction(() -> {
                    final DaAufgabe daAufgabe = factory.getAufgabeDA();
                    DmAufgabe dmAufgabe = daAufgabe.find(aufgabenId);
                    if (dmAufgabe.getAnzahlTeile() > 0) {
                        throw create(LoeschenTeileExc.class, DmAufgabe.class, aufgabenId, dmAufgabe.getTitel(), dmAufgabe.getAnzahlTeile());
                    }
                    daAufgabe.delete(dmAufgabe);
                    return null;
                }
        );

    }

    @Override
    public DmSchritt erledigen(DmSchritt schritt) throws TitelExc, IstStundenExc {
        schritt.setRestStunden(0);
        schritt.setErledigtZeitpunkt(Date.valueOf(LocalDate.now()));
        return speichern(schritt);

    }

    @Override
    public List<DmAufgabe> alleOberstenAufgabenLiefern() {
        return _transaction(() -> {
            final DaAufgabe daAufgabe = factory.getAufgabeDA();
            final List<DmAufgabe> alleAufgaben = daAufgabe.findAll();
            final List<DmAufgabe> result = new ArrayList<>();
            for (final DmAufgabe aufgabe : alleAufgaben) {
                final DmVorhaben ganzes = aufgabe.getGanzes();
                if (ganzes == null) {
                    result.add(aufgabe);
                } else {
                    ganzes.getTeile().add(aufgabe);
                }
            }
            for (final DmAufgabe aufgabe : result) {
                transienteDatenRekursivBerechnen(aufgabe);
            }
            return result;
        });
    }


    private void transienteDatenRekursivBerechnen(final DmAufgabe aufgabe) {
        if (aufgabe instanceof DmVorhaben){
            final DmVorhaben vorhaben = (DmVorhaben) aufgabe;
        vorhaben.setRestStunden(restStundenBerechnen(aufgabe));
        vorhaben.setIstStunden(istStundenBerechnen(aufgabe));
        vorhaben.setStatus(statusBerechnen(aufgabe));
        }
    }

    private int restStundenBerechnen(final DmAufgabe aufgabe) {
        if (aufgabe instanceof DmSchritt) {
            return aufgabe.getRestStunden();
        }
        final DmVorhaben dmVorhaben = (DmVorhaben) aufgabe;
        int restStunden = 0;
        for (DmAufgabe d : dmVorhaben.getTeile()) {
            restStunden = restStunden + restStundenBerechnen(d);
        }
        return restStunden;
    }

    private int istStundenBerechnen(final DmAufgabe aufgabe) {
        if (aufgabe instanceof DmSchritt) {
            return aufgabe.getIstStunden();
        }
        final DmVorhaben dmVorhaben = (DmVorhaben) aufgabe;
        int istStunden = 0;
        for (DmAufgabe d : dmVorhaben.getTeile()) {
            istStunden = istStunden + istStundenBerechnen(d);
        }
        return istStunden;
    }


    private DmAufgabeStatus statusBerechnen(final DmAufgabe aufgabe) {
        if (aufgabe instanceof DmSchritt) {
            return aufgabe.getStatus();
        }
        final DmVorhaben dmVorhaben = (DmVorhaben) aufgabe;
        int anzahlTeile = dmVorhaben.getAnzahlTeile();
        DmAufgabeStatus status;
        if (anzahlTeile == 0) {
            return DmAufgabeStatus.neu;
        } else {
            int neuzaehler = 0;
            int erledigtzaehler = 0;
            for (DmAufgabe vorhaben : dmVorhaben.getTeile()) {
                status = statusBerechnen(vorhaben);
                if (status.equals(DmAufgabeStatus.neu)) {
                    neuzaehler++;
                }

                if (status.equals(DmAufgabeStatus.erledigt)) {
                    erledigtzaehler++;
                }
            }
            if (neuzaehler == anzahlTeile) {
                status = DmAufgabeStatus.neu;

            } else {
                if (erledigtzaehler == anzahlTeile) {
                    status = DmAufgabeStatus.erledigt;
                } else {
                    status = DmAufgabeStatus.inBearbeitung;
                }
            }
        }

        return status;
    }


    @Override
    public List<DmVorhaben> alleVorhabenLiefern() {
        return _transaction(() -> {
            final DaVorhaben daVorhaben = factory.getVorhabenDA();
            final List<DmVorhaben> alleVorhaben = daVorhaben.findAll();
            return alleVorhaben;
        });

    }

    private <RESULT> RESULT _transaction(final _Action<RESULT> action) {

        factory.beginTransaction();
        boolean ok = false;
        try {
            final RESULT result = action.apply(); //Aufruf der Nutzaktion
            ok = true;
            return result;
        } finally {
            factory.endTransaction(ok);
        }

    }

    @FunctionalInterface
    private interface _Action<RESULT> {

        /**
         * Führt die Nutzaktion der _transaction aus. Diese liefert ein Ergebnis vom Typ RESULT.
         */
        RESULT apply();

    }
}