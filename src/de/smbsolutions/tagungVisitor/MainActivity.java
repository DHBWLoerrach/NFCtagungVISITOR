package de.smbsolutions.tagungVisitor;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends Activity {

	NfcAdapter mNfcAdapter;
	ListView listViewTagInfos;
	TextView textViewReadTag;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		
	

		// Check for available NFC Adapter
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mNfcAdapter == null) {
			Toast.makeText(this,
					"NFC ist auf Ihrem Gerät leider nicht verfügbar",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		if (!mNfcAdapter.isEnabled()) {
			Toast.makeText(this, "Bitte aktivieren Sie NFC", Toast.LENGTH_SHORT)
					.show();
			startActivity(new Intent(
					android.provider.Settings.ACTION_WIRELESS_SETTINGS));
		}

		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// onResume gets called after this to handle the intent
		mNfcAdapter.disableForegroundDispatch(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		handleIntent(getIntent());

		// Aufruf in onResume
		Intent i = new Intent(this, this.getClass());
		i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(this, 0, i, 0);
		mNfcAdapter.enableForegroundDispatch(this, intent, null, null);

	}

	// Dies wird aufgerufen wenn Aktivität resumed (Ausgelöst, durch das
	// Intent.FLAG_ACTIVITY_SINGLE_TOP
	// An dieser Stelle kann dann auf das gleiche Tag zugegriffen werden
	@Override
	public void onNewIntent(Intent intent) {
		
		performNFCaction(intent);
		
	}
	
	
	//HIER HÄNG ICH, perfrom NFCaction schlägt fehl wenn es über AAR kommt
	private void handleIntent(Intent intent) {
	    String action = intent.getAction();
	    
	   
    	Log.e("teest",  "jiha");
	    	performNFCaction(intent);
	 
	   
	    
	    
	
	    
	}
	
 private void performNFCaction(Intent intent){
	 
	 try {
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			Toast.makeText(this, "Read NFC in onNewIntent", Toast.LENGTH_SHORT)
					.show();

			Ndef ndef1 = Ndef.get(tag);
			NdefMessage message = ndef1.getCachedNdefMessage();
			
			String roomName = null;

			// Flag, ob es sich um einen von "unseren" Tags handelt
			boolean flagValid = false;

			for (NdefRecord record : message.getRecords()) { // work with
				// record.getPayload()
				// Toast.makeText(this, new String(record.getPayload()),
				// Toast.LENGTH_SHORT).show();
				if (new String(record.getPayload())
						.equals("de.smbsolutions.tagungVisitor")) {
					flagValid = true;

				}
				//
				// Hier wird identifiziert ob der Tag für uns auswertbaren Text
				// enthält.
				// MUSS IRGENDWIE AUCH DIREKT ÜBER DEN TYP GEHEN (TNF_TEXT)
				if (record.getTnf() == NdefRecord.TNF_MIME_MEDIA
						&& new String(record.getType()).equals("text/plain")) {
					Toast.makeText(this, "ich bin text", Toast.LENGTH_SHORT)
							.show();

					roomName = new String(record.getPayload());
	
				}

			}
			
			if (flagValid == true){
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

}
