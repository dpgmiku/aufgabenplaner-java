package l3_da;

/**Eine Factory für typisierte Data Access Objects. Teuer zu erzeugen. Kapselt z.B. eine JPA-EntityManagerFactory.*/
public interface DaFactory {
	
	/**Liefert ein Data Access Object für polymorphen Zugriff auf Aufgaben.*/
	DaAufgabe getAufgabeDA();	

	/**Liefert ein Data Access Object für Schritte.*/
	DaSchritt getSchrittDA();

	/**Liefert ein Data Access Object für Schritte.*/
	DaVorhaben getVorhabenDA();
	
	/**Startet eine Datenbanktransaktion. Es dürfen nicht mehrere Transaktionen in derselben DaFactory offen sein.*/
	void beginTransaction();

	/**Beendet die in dieser DaFactory laufende Datenbanktransaktion. Wenn true übergeben wird, wird commit, andernfalls rollback ausgeführt.*/
	void endTransaction(boolean ok);
	
}
