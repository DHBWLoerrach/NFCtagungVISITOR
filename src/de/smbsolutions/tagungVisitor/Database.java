package de.smbsolutions.tagungVisitor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.sax.StartElementListener;
import android.text.format.Time;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;


public class Database {

	public ArrayList<Room> arrayListRooms;
	public ArrayList<Presentation> arrayListPresentation;
	private static Database db_object = null;

	private Context context;

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

		this.context = context;

		Parse.initialize(context, "TKlxT9rAYg75PYw19N7zsTqDPkggZuv8HddJdLqR",
				"ekGUAASuWTKDasYzVBWImO9IbFBE38e08WjjkTqx");

		arrayListPresentation = new ArrayList<Presentation>();

	}

	public String getRoomIDtoName(String roomName) {

		String roomID = null;

		// //Get values of DB
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Rooms");
		query.whereEqualTo("name", roomName);

		// Einträge werden gesucht
		List<ParseObject> objects = null;
		try {
			objects = query.find();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (ParseObject resultEntry : objects) {

			roomID = (String) resultEntry.getObjectId();

		}

		return roomID;
	}

	// IN der VisitorApp werden die Präsentationen nicht asynchron geladen, da
	// das Ergebnis auf jedefnall da sein muss um damit weiterzuarbeiten
	public void loadPresentations(String roomID) {

		
		arrayListPresentation.clear();
		//Alte Einträge werden gelöscht
		

		// //Get values of DB
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Presentations");
		query.whereEqualTo("room_id", roomID);

		// Einträge werden gesucht

		List<ParseObject> objects = null;
		try {
			objects = query.find();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
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

	public Presentation getCurrentPresentation() {

		Presentation currentPresentation = null;

		for (Presentation presentation : arrayListPresentation) {

			Calendar c = Calendar.getInstance();
			Date currentDate = c.getTime();
			Date endDate = null;
			Date startDate = null;
		
		    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy-HH:mm");
			try {
				 startDate = format.parse(presentation.getDate()+"-"+ presentation.getTime_from());
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				endDate = format.parse(presentation.getDate()+"-"+ presentation.getTime_to());
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (currentDate.after(startDate) && currentDate.before(endDate)){
				currentPresentation = presentation;
			}

//			int day = c.get(Calendar.DAY_OF_MONTH);
//			int month = c.get(Calendar.MONTH);
//			int year = c.get(Calendar.YEAR);
//			String date = day + "." + month + "." + year;
//
//			Toast.makeText(context, date, Toast.LENGTH_LONG).show();
//
//			if (presentation.getDate().equals(date)) {
//								currentPresentation = presentation;

//			}

		}

		return currentPresentation;

	}

	public ArrayList<Room> getRooms() {

		return arrayListRooms;
	}
	
	
	public void saveMailAdress(String mail, Presentation presentation) {
		// Saving a room
		ParseObject roomObject = new ParseObject("Subscribers");
		roomObject.put("mail", mail);
		roomObject.put("presentation_id", presentation.getObjectId());
				
		//Uns ist egal wann es gespeichert wird (Gut bei schlechter Verbindung)
		roomObject.saveEventually();
	}

}
