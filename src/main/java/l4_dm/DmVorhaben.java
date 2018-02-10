package l4_dm;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

/**Datenmodellklasse für ein Vorhaben, welches aus mehreren Aufgaben besteht. Ohne Prüfungen und Folgeaktionen.*/
@Entity
public class DmVorhaben extends DmAufgabe {
	
	/**In der Datenbank zu speichernder End-Termin*/
	private Date endTermin;
	
	/**Enthält die Teil-Aufgaben, welche zu diesem Vorhaben gehören. 
	 * Wird nicht persistiert, sondern aus der inversen, persistierten Referenz ganzes berechnet.*/
	protected transient List<DmAufgabe> teile = new ArrayList<DmAufgabe>();
	
	/**Enthält die Reststunden, die voraussichtlich noch für dieses Vorhaben aufgewendet werden müssen.
	 * Wird nicht persistiert, sondern aus den Reststunden der Teil-Aufgaben berechnet.*/
	private transient int restStunden = -999999;
	
	/**Enthält die Iststunden, die bisher für dieses Vorhaben aufgewendet wurden.
	 * Wird nicht persistiert, sondern aus den Iststunden der Teil-Aufgaben berechnet.*/
	private transient int istStunden = -999999;
	
	/**Enthält den Status dieses Vorhabens.
	 * Wird nicht persistiert, sondern aus den Statuswerten der Teil-Aufgaben berechnet.*/
	private transient DmAufgabeStatus status = null;

	/**Liefert den Termin, bis zu dem diese Aufgabe erledigt werden soll.*/
	public Date getEndTermin() {
		return endTermin;
	}

	public void setEndTermin(Date endTermin) {
		this.endTermin = endTermin;
	}

	@Override
	public int getRestStunden() {
		return restStunden;
	}

	@Override
	public int getIstStunden() {
		return istStunden;
	}
	
	public void setRestStunden(int restStunden) {
		this.restStunden = restStunden;
	}

	public void setIstStunden(int istStunden) {
		this.istStunden = istStunden;
	}


	@Override
	public int getAnzahlTeile() {
		return teile.size();
	}

	/**Liefert die Teil-Aufgaben, welche zu diesem Vorhaben gehören.*/
	public List<DmAufgabe> getTeile(){
		return teile;
	}

	@Override
	public DmAufgabeStatus getStatus() {
		return status;
	}
	
	public void setStatus(DmAufgabeStatus status){
		this.status = status;
	}
	
}
