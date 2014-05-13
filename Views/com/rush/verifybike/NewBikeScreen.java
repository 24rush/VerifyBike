package com.rush.verifybike;

import java.util.ArrayList;

import com.rush.verifybike.Bindings.Mode;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;

public class NewBikeScreen extends Activity {

	private static final int SELECT_PICTURE = 1;
	
	private ArrayList<ImageView> m_ImageViews = new ArrayList<ImageView>(); 
	private Controls m_Controls = new Controls(this);
	private BikeDataViewModel m_ViewModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_bike_screen);
				
		m_ImageViews.add((ImageView) m_Controls.get(R.id.img_bike_pic0));
		m_ImageViews.add((ImageView) m_Controls.get(R.id.img_bike_pic1));
		m_ImageViews.add((ImageView) m_Controls.get(R.id.img_bike_pic2));
		
		m_ViewModel =  (BikeDataViewModel) getIntent().getParcelableExtra("com.rush.verifybike.BikeViewModel");
		
		Bindings.BindText(m_Controls.get(R.id.edt_bike_model), m_ViewModel.Model, Mode.TWO_WAY);
		Bindings.BindText(m_Controls.get(R.id.edt_bike_serial), m_ViewModel.SerialNumber, Mode.TWO_WAY);
		
		Bindings.BindVisible(m_Controls.get(R.id.btn_save_bike), m_ViewModel.IsValid);
	
		IContextNotifier<String> observerURI = new IContextNotifier<String>() {			
			@Override
			public void OnValueChanged(String value, Object context) {
				 ((LinearLayout) findViewById(getLayoutIdForIndex((Integer) context))).setVisibility(View.GONE);				
			}
		};
		
		Bindings.BindImageURI(m_ImageViews.get(0), m_ViewModel.PictureURL_0);
		m_ViewModel.PictureURL_0.addObserverContext(observerURI, 0);
		
		Bindings.BindImageURI(m_ImageViews.get(1), m_ViewModel.PictureURL_1);
		m_ViewModel.PictureURL_1.addObserverContext(observerURI, 1);
		
		Bindings.BindImageURI(m_ImageViews.get(2), m_ViewModel.PictureURL_2);
		m_ViewModel.PictureURL_2.addObserverContext(observerURI, 2);
		
		Log.d("MyApp", m_ViewModel.toString());
	}

	private int m_CurrentPicture = -1;

	public void onLoadPicture0(View v) {
		m_CurrentPicture = 0;
		LaunchIntent();
	}

	public void onLoadPicture1(View v) {
		m_CurrentPicture = 1;
		LaunchIntent();
	}

	public void onLoadPicture2(View v) {
		m_CurrentPicture = 2;
		LaunchIntent();
	}

	private void LaunchIntent() {		
		Intent intent = new Intent();		  

		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);      
	}

	private int getLayoutIdForIndex(int index) {
		switch (index) {
			case 0: return R.id.layout_pic_0;
			case 1: return R.id.layout_pic_1;
			case 2: return R.id.layout_pic_2;
			default: return -1;
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_PICTURE) {
				Uri selectedImageUri = data.getData();
				String selectedImagePath = getPath(selectedImageUri);

				Log.d("MyApp", m_CurrentPicture + " " + selectedImagePath);				
				
				Observable<String> pict = m_ViewModel.PictureURL_0;
				
				if (m_CurrentPicture == 1)
					pict = m_ViewModel.PictureURL_1;			
				else
					if (m_CurrentPicture == 2)
						pict = m_ViewModel.PictureURL_2;
				
				pict.set(selectedImageUri.toString());				
			}
		}
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		
		Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
		if (cursor != null)
		{
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			
			return cursor.getString(column_index);
		}
		
		return null;
	}
	
	public void onSaveBike(View v) {
		setResult(RESULT_OK, getIntent());
		finish();
	}
}
