package com.rush.verifybike;

import com.rush.verifybike.VerificationResult;
import android.os.Bundle;
import android.app.Activity;
import android.widget.ImageView;

public class SearchResults extends Activity {

	private SearchBikeViewModel m_SearchBikeViewModel = new SearchBikeViewModel(this);
	private Controls m_Controls = new Controls(this);
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);
		
		Bindings.BindText(m_Controls.get(R.id.lblBikeStatus), m_SearchBikeViewModel.Status);
		Bindings.BindText(m_Controls.get(R.id.txt_Model), m_SearchBikeViewModel.Model);
		
		m_SearchBikeViewModel.Image.addObserver(new INotifier<Integer>() {			
			public void OnValueChanged(Integer value) {
				ImageView imgStatus = (ImageView) m_Controls.get(R.id.img_Status);
				imgStatus.setImageResource(value);				
			}
		});	
		
		VerificationResult result = (VerificationResult) getIntent().getSerializableExtra(Intents.Intent_VerificationResult);		
		m_SearchBikeViewModel.LoadData(result);
	}
}
