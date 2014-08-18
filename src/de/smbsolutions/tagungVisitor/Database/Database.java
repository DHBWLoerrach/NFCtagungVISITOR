package de.smbsolutions.tagungVisitor.Database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Diese Klasse repräsentiert die Parse Online Datenbank
 * 
 * @author Mirko
 * 
 */
public class Database {

	public ArrayList<Presentation> arrayListPresentation;
	private static Database db_object = null;


	/**
	 * Liefert die Instanz zurück (Singleton)
	 */
	public static Database getInstance(Context context) {
		if (db_object == null)
			db_object = new Database(context);
		return db_object;
	}

	/**
	 * Privater Konstruktor -> nur ein Singleton kann erzeugt werden
	 */
	private Database(Context context) {

		Parse.initialize(context, "TKlxT9rAYg75PYw19N7zsTqDPkggZuv8HddJdLqR",
				"ekGUAASuWTKDasYzVBWImO9IbFBE38e08WjjkTqx");

		arrayListPresentation = new ArrayList<Presentation>();

	}

	/**
	 * Zum vom Tag ausgelesen Name wird die RoomID ausgelesen
	 * 
	 * @param roomName
	 * @return
	 */
	public String getRoomIDtoName(String roomName) {

		String roomID = null;

		ParseQuery<ParseObject> query = ParseQuery.getQuery("Rooms");
		query.whereEqualTo("name", roomName);

		// Einträge werden gesucht
		List<ParseObject> objects = null;
		try {
			objects = query.find();
		} catch (ParseException e) {

			e.printStackTrace();
		}

		for (ParseObject resultEntry : objects) {

			// ID wird in den Output-Parameter geschrieben
			roomID = (String) resultEntry.getObjectId();

		}

		return roomID;
	}

	/**
	 * In der VisitorApp werden die Präsentationen nicht asynchron geladen, da
	 * das Ergebnis auf jedefnall da sein muss um damit weiterzuarbeiten
	 * 
	 * @param roomID
	 */
	public void loadPresentations(String roomID) {

		// Alte Einträge werden gelöscht
		arrayListPresentation.clear();

		ParseQuery<ParseObject> query = ParseQuery.getQuery("Presentations");
		query.whereEqualTo("room_id", roomID);

		// Einträge werden gesucht
		List<ParseObject> objects = null;
		try {
			objects = query.find();
		} catch (ParseException e) {
	
			e.printStackTrace();
		}

		for (ParseObject resultEntry : objects) {

			Presentation presentation = new Presentation(
					(String) resultEntry.getObjectId(),
					(String) resultEntry.get("Referent"),
					(String) resultEntry.get("date"),
					(String) resultEntry.get("room_id"),
					(String) resultEntry.get("time_from"),
					(String) resultEntry.get("time_to"),
					(String) resultEntry.get("topic"));
			arrayListPresentation.add(presentation);

		}

	}

	/**
	 * Die gerade aktuell laufende Präsentation, falls vorhanden, wird ermittelt
	 * @return
	 */
	public Presentation getCurrentPresentation() {

		Presentation currentPresentation = null;

		for (Presentation presentation : arrayListPresentation) {

			Calendar c = Calendar.getInstance();
			Date currentDate = c.getTime();
			Date endDate = null;
			Date startDate = null;

			SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy-HH:mm");
			try {
				startDate = format.parse(presentation.getDate() + "-"
						+ presentation.getTime_from());
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}
			try {
				endDate = format.parse(presentation.getDate() + "-"
						+ presentation.getTime_to());
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}

			if (currentDate.after(startDate) && currentDate.before(endDate)) {
				currentPresentation = presentation;
			}

		}

		return currentPresentation;

	}

	public void saveMailAdress(String mail, Presentation presentation) {
		//Die Mail Adresse wird gespeichert
		ParseObject roomObject = new ParseObject("Subscribers");
		roomObject.put("mail", mail);
		roomObject.put("presentation_id", presentation.getObjectId());

		// Uns ist egal wann es gespeichert wird (Gut bei schlechter Verbindung)
		roomObject.saveEventually();
	}

}
