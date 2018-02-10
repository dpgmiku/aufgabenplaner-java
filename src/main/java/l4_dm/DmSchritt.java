package l4_dm;

import java.sql.Date;
import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

/**Datenmodellklasse für einen Schritt eines Vorhabens. Ohne Prüfungen und Folgeaktionen.*/
@Entity
public class DmSchritt extends DmAufgabe {

	private int restStunden;
	private int istStunden;
	private Date erledigtZeitpunkt;

	@Override
	public int getRestStunden() {
		return restStunden;
	}

	@Override
	public int getIstStunden() {
		return istStunden;
	}

	@Override
	public int getAnzahlTeile() {
		return 0;
	}

	@Override
	public List<DmAufgabe> getTeile(){
		return Collections.<DmAufgabe>emptyList();
	}

	/**Liefert den Zeitpunkt, an dem diese Aufgabe als erledigt gebucht wurde.*/
	public Date getErledigtZeitpunkt() {
		return erledigtZeitpunkt;
	}
	
	public void setRestStunden(int restStunden) {
		this.restStunden = restStunden;
	}

	public void setIstStunden(int istStunden) {
		this.istStunden = istStunden;
	}

	public void setErledigtZeitpunkt(final Date erledigtZeitpunkt) {
		this.erledigtZeitpunkt = erledigtZeitpunkt;
	}

	@Override
	public DmAufgabeStatus getStatus() {
		if(getId()==createdId){
			return DmAufgabeStatus.neu;
		}else if(erledigtZeitpunkt!=null){
			return DmAufgabeStatus.erledigt;
		}else{
			return DmAufgabeStatus.inBearbeitung;
		}
	}

}
