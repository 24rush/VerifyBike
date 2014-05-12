package com.rush.verifybike;

import java.util.ArrayList;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_bike_screen);
		
		m_ImageViews.add((ImageView) m_Controls.get(R.id.img_bike_pic0));
		m_ImageViews.add((ImageView) m_Controls.get(R.id.img_bike_pic1));
		m_ImageViews.add((ImageView) m_Controls.get(R.id.img_bike_pic2));
		
		BikeDataViewModel viewModel =  (BikeDataViewModel) getIntent().getParcelableExtra("com.rush.verifybike.BikeViewModel");
		
		Bindings.BindText(m_Controls.get(R.id.edt_bike_model), viewModel.Model);
		Bindings.BindText(m_Controls.get(R.id.edt_bike_serial), viewModel.SerialNumber);
		
		Log.d("MyApp", viewModel.toString());
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
				
				m_ImageViews.get(m_CurrentPicture).setImageURI(selectedImageUri);
				m_ImageViews.get(m_CurrentPicture).setVisibility(View.VISIBLE);
				
				LinearLayout lay = (LinearLayout) findViewById(getLayoutIdForIndex(m_CurrentPicture));
				lay.setVisibility(View.GONE);
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
		finish();
	}
}
