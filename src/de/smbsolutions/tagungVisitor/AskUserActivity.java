package de.smbsolutions.tagungVisitor;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;

public class AskUserActivity extends Activity {

TextView txtRoomName;
String roomName;
private static final int REQUEST_CODE_EMAIL = 1;
private TextView email;
private Database database;
private Presentation presentation;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_askuser);

		txtRoomName = (TextView) findViewById(R.id.txtRoomName);
		email = (TextView) findViewById(R.id.email);
		roomName = getIntent().getExtras().getString("roomName");
		
		database = Database.getInstance(this);
		
		presentation = identifyCurrentPresentation();
		
		
		if (presentation != null) {
			
			  try {
			        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
			                new String[] { GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE }, false, null, null, null, null);
			        startActivityForResult(intent, REQUEST_CODE_EMAIL);
			    } catch (ActivityNotFoundException e) {
			        // TODO
			    }
			
			
		} else {
			txtRoomName.setText("In Raum " + roomName + " findet momentan leider kein Vortrag statt");
		}


	  


	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}
	
	  @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        if (requestCode == REQUEST_CODE_EMAIL && resultCode == RESULT_OK) {
	            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
	           
	            
	            txtRoomName.setText("Sie haben sich erfolgreich für den Vortrag " + presentation.getTopic() + 
						" (von "+ presentation.getTime_from() + " bis " + presentation.getTime_to() + "Uhr) in Raum " + roomName + " eintragen"

								);
	            
	            email.setText("Damit erhalten Sie im Anschluss den Foliensatz an folgende Adresse: " + accountName);
	           database.saveMailAdress(accountName, presentation);
	        }
	    }
	  
	  
	  private Presentation identifyCurrentPresentation(){
		  
		  String roomID = database.getRoomIDtoName(roomName);
		  
		 if (roomID != null)
			 database.loadPresentations(roomID);
		  
		 return database.getCurrentPresentation();
			
		}
		


}
