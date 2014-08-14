package de.smbsolutions.tagungVisitor;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.LinkedHashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class WriterTask extends

		AsyncTask<String, Void, String> {
	


	 Context context;
	 NdefMessage message;
	 Tag tag;
	 
	public WriterTask(Context context, NdefMessage message, Tag tag ) {
		

      this.context = context;
      this.message = message;
      this.tag = tag;
	}



	@Override
	protected String doInBackground(String... arg0) {
		
		Ndef ndef = Ndef.get(tag);
		try {
			
			// Nur wenn es kein readOnly-Tag ist und die neue Tag message
			// auch auf das Tag passt,
			// kann der Schreibevorgang beginnen
			if (ndef.isWritable()
					&& ndef.getMaxSize() > message.toByteArray().length) {
				ndef.connect();
				ndef.writeNdefMessage(message); // msg ist die NdefMessage
				Toast.makeText(context,
						"Message has been written to Ndef tag",
						Toast.LENGTH_LONG).show();
			}

		} catch (FormatException e) {
			Log.e("demo", "Unable to write to tag due to FormatException",
					e);
			Toast.makeText(context,
					"Unable to write to tag due to FormatException.",
					Toast.LENGTH_SHORT).show();
		} catch (TagLostException e) {
			Toast.makeText(context, "Tag wurde beim Beschreiben entfernt",
					Toast.LENGTH_SHORT).show();

		} catch (IOException e) {
			Log.e("demo", "Unable to write to tag.", e);
			Toast.makeText(context, "Unable to write to tag." + e.toString(),
					Toast.LENGTH_LONG).show();

		} finally {
			try {
				ndef.close();
			} catch (IOException e) {
			}
		}
		
		return "test";
	
	
	}

}
