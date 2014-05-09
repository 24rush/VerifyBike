package com.rush.verifybike;

import com.rush.verifybike.VerificationResult;
import android.os.Bundle;
import android.app.Activity;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchResults extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);
		
		TextView txtStatus = (TextView) findViewById(R.id.lblBikeStatus);
		TextView txtModel = (TextView) findViewById(R.id.txt_Model);
		ImageView imgStatus = (ImageView) findViewById(R.id.img_Status);
		
		VerificationResult result = (VerificationResult) getIntent().getSerializableExtra(Intents.Intent_VerificationResult);
		
		if (result != null) {
			
			int lblId = R.string.txt_bikeStatusNotInDb;
			int imageStatusId = R.drawable.orange_alert;
			
			switch (result.Status) {
			case Owned:
				lblId = R.string.txt_bikeStatusOwned;
				imageStatusId = R.drawable.red_alert;
				break;
			case Stolen:
				lblId = R.string.txt_bikeStatusStolen;
				imageStatusId = R.drawable.red_alert;
			default:
				break;
			}
			
			txtStatus.setText(getString(lblId));	
			txtModel.setText(result.Model);
			imgStatus.setImageResource(imageStatusId);			
		}
	}
}
