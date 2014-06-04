package com.rush.verifybike;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class MessageBox {

	public static void Show(Activity _activity, String title, String message, DialogInterface.OnClickListener onOK, DialogInterface.OnClickListener onCancel) {
		AlertDialog alertDialog = new AlertDialog.Builder(_activity).create();
		
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", onOK);
		
		if (onCancel != null) {
			alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", onCancel);	
		}
	
		alertDialog.show();
	}
}
