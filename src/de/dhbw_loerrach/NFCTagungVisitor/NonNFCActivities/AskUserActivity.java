package de.dhbw_loerrach.NFCTagungVisitor.NonNFCActivities;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;

import de.dhbw_loerrach.NFCTagungVisitor.R;
import de.dhbw_loerrach.NFCTagungVisitor.Database.Database;
import de.dhbw_loerrach.NFCTagungVisitor.Database.Presentation;

/**
 * Diese Activity fragt den User welche E-Mail Adresse er verwenden m�chte, und
 * speichert diese dann in der Datenbank falls in dem abgescannten Raum-Tag
 * gerade ein Vortrag statfindet
 * 
 * @author Mirko
 * 
 */
public class AskUserActivity extends Activity {

	TextView txtRoomName;
	String roomName;
	private static final int REQUEST_CODE_EMAIL = 1;
	private TextView email;
	private Database database;
	private Presentation presentation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_askuser);

		txtRoomName = (TextView) findViewById(R.id.txtRoomName);
		email = (TextView) findViewById(R.id.email);
		roomName = getIntent().getExtras().getString("roomName");

		database = Database.getInstance(this);

		// Es wird geschaut, ob momentan gerade ein Vortrag l�uft
		presentation = identifyCurrentPresentation();

		// Wenn ja, kann der Besucher eine E-Mail-Adresse ausw�hlen
		if (presentation != null) {

			try {
				Intent intent = AccountPicker.newChooseAccountIntent(null,
						null,
						new String[] { GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE },
						false, null, null, null, null);
				startActivityForResult(intent, REQUEST_CODE_EMAIL);
			} catch (ActivityNotFoundException e) {
			}

			// Ansonsten kommt eine Fehlermeldung
		} else {
			txtRoomName.setText("In Raum " + roomName
					+ " findet momentan leider kein Vortrag statt.");
		}

	}

	/**
	 * Wenn der Benuter die E-Mail-Adresse ausgew�hlt hat, wird sie in der
	 * Datenbank gespeichert und eine Erfolgsmeldung ausgegeben
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUEST_CODE_EMAIL && resultCode == RESULT_OK) {
			String accountName = data
					.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

			//Erfolgsmeldung
			txtRoomName.setText(Html
					.fromHtml("Sie haben sich erfolgreich f�r den Vortrag <b> "
							+ presentation.getTopic() + " </b> (von "
							+ presentation.getTime_from() + " bis "
							+ presentation.getTime_to() + "Uhr) in Raum <b> "
							+ roomName + "</b> eingetragen."

					));

			email.setText(Html
					.fromHtml("Damit erhalten Sie im Anschluss den Foliensatz an folgende Adresse: <b> "
							+ accountName + "</b>"));
			
			//Mail-Adresse wird gespeichert
			database.saveMailAdress(accountName, presentation);
		}
	}

	
	
	private Presentation identifyCurrentPresentation() {

		String roomID = database.getRoomIDtoName(roomName);

		if (roomID != null)
			database.loadPresentations(roomID);

		return database.getCurrentPresentation();

	}

}
