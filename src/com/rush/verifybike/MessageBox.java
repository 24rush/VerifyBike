package com.rush.verifybike;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class MessageBox {

	public static void Show(Activity _activity, String title, String message) {
		AlertDialog alertDialog = new AlertDialog.Builder(_activity, android.R.style.Theme_Holo_Dialog).create();
		
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {	
			}	
		});
	
		alertDialog.show();
	}
}
