package de.dhbw_loerrach.NFCTagungVisitor.NfcRelevant;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import de.dhbw_loerrach.NFCTagungVisitor.R;
import de.dhbw_loerrach.NFCTagungVisitor.NonNFCActivities.AskUserActivity;

/**
 * Diese Activity wird entweder direkt vom Tag aufgerufen oder startet (wenn
 * �ber den Launcher ge�ffnet den Foreground-Dispatch Modus) Wenn ein Tag
 * erkannt wurde, wird dessen Raumname ausgelesen und zur weiteren Verarbeitung
 * die n�chste Activity (AskUserActivty) aufgerufen
 * 
 * @author Mirko
 * 
 */
public class MainActivity extends Activity {

	NfcAdapter mNfcAdapter;
	ListView listViewTagInfos;
	TextView textViewReadTag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Nur wenn NFC aktiviert ist und eine Internetverbindung besteht,
		// funktioniert die App
		checkForNFCandWIFI();

	}

	private void checkForNFCandWIFI() {

		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mNfcAdapter == null) {
			Toast.makeText(this,
					"NFC ist auf Ihrem Ger�t leider nicht verf�gbar",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		if (!mNfcAdapter.isEnabled()) {
			Toast.makeText(this, "Bitte aktivieren Sie NFC", Toast.LENGTH_LONG)
					.show();
			startActivity(new Intent(
					android.provider.Settings.ACTION_WIRELESS_SETTINGS));
		}

		if (haveNetworkConnection() == false) {
			Toast.makeText(this,
					"F�r diese App ist eine Internetverbindung n�tig",
					Toast.LENGTH_LONG).show();
			startActivity(new Intent(
					android.provider.Settings.ACTION_WIFI_SETTINGS));
		}

	}

	@Override
	protected void onPause() {

		super.onPause();
		// Wenn die Activity pausiert, muss der Dispatcher deaktiviert werden
		mNfcAdapter.disableForegroundDispatch(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Wenn die App via Android Application Record aufgerufen wurde, wird
		// diese Methode das erkennens
		handleIntent(getIntent());

		// Sobald die Aktivit�t in den Vordergrund kommt, wird der
		// Foreground-Dispatcher aktiviert.
		// Von nun an werden alle NFC Tags automatisch an diese App
		// weitergeleitet
		Intent i = new Intent(this, this.getClass());
		i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(this, 0, i, 0);
		mNfcAdapter.enableForegroundDispatch(this, intent, null, null);

	}

	// Dies wird aufgerufen ein NFCIntent vom Foreground-Dispatcher ausgel�st
	// wird.
	// An dieser Stelle kann dann auf das gleiche Tag zugegriffen werden
	@Override
	public void onNewIntent(Intent intent) {

		performNFCaction(intent);

	}

	private void handleIntent(Intent intent) {
		String action = intent.getAction();

		// Wenn ein NDEF-Tag via Intenterkannt wurde
		// Deshalb wurde zus�tzlich zum Android Application Record auch ein
		// URI-Record gespeichert.
		// Ansonsten liese sich das nicht eindeutig dem NDEF-Event via Intent
		// zuordnen
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

			performNFCaction(intent);
		}

	}

	/**
	 * Das NFC Tag wird ausgelesen und die n�chste Activity gestaret
	 * 
	 * @param intent
	 */
	private void performNFCaction(Intent intent) {

		try {
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

			Ndef ndef = Ndef.get(tag);
			NdefMessage message = ndef.getCachedNdefMessage();

			String roomName = null;

			// Flag, ob es sich um einen von "unseren" Tags handelt
			boolean flagValid = false;

			for (NdefRecord record : message.getRecords()) {

				if (new String(record.getPayload())
						.equals("de.smbsolutions.tagungVisitor")) {
					flagValid = true;

				}
				//
				// Hier wird identifiziert ob der Tag f�r uns auswertbaren Text
				// enth�lt.
				if (record.getTnf() == NdefRecord.TNF_MIME_MEDIA
						&& new String(record.getType()).equals("text/plain")) {
					Toast.makeText(this, "ich bin text", Toast.LENGTH_SHORT)
							.show();
					roomName = new String(record.getPayload());

				}

			}

			// Wenn es einer unserer Flag ist, wird die n�chste Aktivity
			// gestartet
			if (flagValid == true) {
				Intent intentNext = new Intent(getApplicationContext(),
						AskUserActivity.class);
				intentNext.putExtra("roomName", roomName);
				startActivity(intentNext);
				finish();

			}

		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}

	}

	private boolean haveNetworkConnection() {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
	}

}
