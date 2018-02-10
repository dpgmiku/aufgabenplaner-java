package l2_lg;

import java.util.List;

import l3_da.DaGeneric;
import l4_dm.DmAufgabe;
import l4_dm.DmSchritt;
import l4_dm.DmVorhaben;

/** Eine Logik-Sitzung: Service-Objekt, welches Zugriff auf die Geschäftslogik der Aufgabenverwaltung bietet. 
 * Jede Methode dieses Interfaces muss als Transaktion (ganz oder gar nicht) durchgeführt werden.*/
public interface LgSession {

	/**Speichert die übergebene Aufgabe (Schritt oder Vorhaben) in die Datenbank.
     * Die transienten Attribute der Aufgabe werden dabei ignoriert, da sie redundant sind und aus den persisten Attributen abgeleitet werden können.
	 * Wenn die übergebene Aufgabe noch keine ID hat, wird eine neue ID durch das System vergeben.
	 * Wenn die übergebene Aufgabe schon eine ID hat, überschreibt sie die Version in der Datenbank mit derselben ID.
	 * Prüft vor dem Speichern die fachliche Zulässigkeit und wirft bei Verstoß eine der angegebenen fachlichen Ausnahmen.
	 * Die jeweilige Bedingung für eine fachliche Ausnahme ist im Hauptkommentar der Ausnahmeklasse dokumentiert,
	 * welcher gleichzeitig der zugehörige Meldungstext ist.
	 * @return dasselbe Aufgaben-Objekt mit gesetzter ID
     */
	<A extends DmAufgabe> A speichern(
			A aufgabe
	) throws TitelExc, RestStundenExc, IstStundenExc, EndTerminExc, VorhabenRekursionExc;

	/**Löscht die Aufgabe (Schritt oder Vorhaben) mit der übergebenen ID in der Datenbank.
     * Prüft vor dem Löschen die fachliche Zulässigkeit und wirft bei Verstoß eine der angegebenen fachlichen Ausnahmen.
     * Die jeweilige Bedingung für eine fachliche Ausnahme ist im Hauptkommentar der Ausnahmeklasse dokumentiert,
     * welcher gleichzeitig der zugehörige Meldungstext ist.
     * @since 2016-04-25
     * @param aufgabenId
     */
	void loeschen(
			Long aufgabenId
	) throws DaGeneric.IdNotFoundExc, LoeschenTeileExc;
	
	/**Setzt im übergebenen Schritt die restStunden auf 0 und den erledigtZeitpunkt auf das aktuelle Datum+Uhrzeit. Speichert ihn dann mit der Methode speichern. */
	DmSchritt erledigen(DmSchritt schritt) throws TitelExc, IstStundenExc;

	/**Liest alle Aufgaben aus der Datenbank und liefert diese in Waldform als Liste von obersten Aufgaben.
	 * Nur diese Methode liefert eine konsistente Sicht auf die Aufgaben im System.
	 * Sie muss also vor jeder Anzeige der Aufgabenliste aufgerufen werden.
	 * Eine Aufgabe ist oberste Aufgabe, wenn sie nicht Teil von einem Vorhaben ist, d.h. nicht auf ein Vorhaben als Ganzes verweist,
     * d.h. wenn ihre ganzes-Referenz null ist.
	 * Alle transienten Attribute aller Aufgaben werden dabei aus den anderen Aufgaben gefüllt. Nach dem Füllen gilt:
	 * Ein Vorhaben enthält als Teile alle die Aufgaben, die das Vorhaben als Ganzes angeben.
	 * Ein Vorhaben enthält als Reststunden die Summe aller Reststunden aller seiner Teile.
	 * Ein Vorhaben enthält als Iststunden die Summe aller Iststunden aller seiner Teile.
	 * Ein Vorhaben enthält als Status Folgendes:
	 * DmAufgabeStatus.neu, wenn es keine Teile enthält oder alle seine Teile den Status neu haben; andernfalls
	 * DmAufgabeStatus.erledigt, wenn alle seine Teile den Status erledigt haben; andernfalls
	 * DmAufgabeStatus.inBearbeitung.
     * @since 2015-12-01 als Teil von Aufgabe 6.
	 * */
	List<DmAufgabe> alleOberstenAufgabenLiefern();

    /**
     * Liefert alle Vorhaben aus der Datenbank. Deren transiente Daten sind nicht notwendigerweise gefüllt.
     * Eine konsistente Sicht auf die Aufgaben im System wird nur von der Methode {@link #alleOberstenAufgabenLiefern()} geliefert.
     * @since 2016-04-25 zwecks Vereinfachung
     */
    List<DmVorhaben> alleVorhabenLiefern();

	/**Titel muss zwischen 10 und 200 Zeichen lang sein! Länge: {0}, Titel: {1}*/
	class TitelExc extends multex.Exc {}

	/**Rest-Stundenanzahl darf nicht negativ sein! Wert: {0} Stunden*/
	class RestStundenExc extends multex.Exc {}

	/**Ist-Stundenanzahl darf nicht negativ sein! Wert: {0} Stunden*/
	class IstStundenExc extends multex.Exc {}

	/**Das in der Aufgabe angegebene Ganzes-Vorhaben mit ID {0} und Titel "{1}" ist seinerseits direkt oder indirekt Teil dieser Aufgabe mit ID {2}. Solche Rekursion ist verboten!*/
	class VorhabenRekursionExc extends multex.Exc {}

	/**Der angegebene End-Termin {0, date, short} liegt in der Vergangenheit!*/
	class EndTerminExc extends multex.Exc {}

	/**Das Vorhaben mit ID {0} und Titel "{1}" enthält noch {2} Teil(e) und kann daher nicht gelöscht werden!*/
	class LoeschenTeileExc extends multex.Exc {}

}
