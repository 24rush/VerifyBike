package com.rush.verifybike;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import com.rush.verifybike.Bindings.Mode;

import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class NewBikeScreen extends Activity {

	private static final int SELECT_PICTURE = 1;

	private ArrayList<ImageView> m_ImageViews = new ArrayList<ImageView>(); 
	private Controls m_Controls = new Controls(this);
	private BikeViewModel m_ViewModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_bike_screen);

		m_ImageViews.add((ImageView) m_Controls.get(R.id.img_bike_pic0));
		m_ImageViews.add((ImageView) m_Controls.get(R.id.img_bike_pic1));		
		
		m_ViewModel =  (BikeViewModel) DataTransfer.get("com.rush.verifybike.BikeViewModel");
				
		Bindings.BindText(m_Controls.get(R.id.edt_bike_model), m_ViewModel.Model, Mode.TWO_WAY);
		Bindings.BindText(m_Controls.get(R.id.edt_bike_serial), m_ViewModel.SerialNumber, Mode.TWO_WAY);

		Bindings.BindVisible(m_Controls.get(R.id.btn_save_bike), m_ViewModel.IsValid);		

		ICommand<Observable<Bitmap>> observerRemBikePic = new ICommand<Observable<Bitmap>>() {
			public void Execute(Observable<Bitmap> context) {			
				context.set(null);
			}
		};

		Bindings.BindCommand(m_Controls.get(R.id.img_bike_rem_pic0), observerRemBikePic, m_ViewModel.PictureCaches.get(0));
		Bindings.BindCommand(m_Controls.get(R.id.img_bike_rem_pic1), observerRemBikePic, m_ViewModel.PictureCaches.get(1));		

		IContextNotifier<Bitmap> observerURI = new IContextNotifier<Bitmap>() {			
			@Override
			public void OnValueChanged(Bitmap value, Object context) {					
				Boolean imageAvail = value != null;
				
				Integer index = (Integer) context;
				setLayoutVisibility(index, imageAvail);				

				if (imageAvail) {					
					m_ImageViews.get(index).setImageBitmap(value);												
				}
			}
		};

		int index = 0;
		for (Observable<Bitmap> picURL : m_ViewModel.PictureCaches) {
			picURL.addObserverContext(observerURI, index);
			observerURI.OnValueChanged(m_ViewModel.PictureCaches.get(index).get(), index);
			
			index++;
		}			
	}

	private int m_CurrentPicture = -1;

	private void setLayoutVisibility(int index, boolean imageAvail) {		
		((LinearLayout) findViewById(getAddBikeLayoutForIndex(index))).setVisibility(!imageAvail ? View.VISIBLE : View.GONE);				
		((RelativeLayout) findViewById(getBikeLayoutForIndex(index))).setVisibility(!imageAvail ? View.GONE : View.VISIBLE);
	}
	
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

	private int getAddBikeLayoutForIndex(int index) {
		switch (index) {
		case 0: return R.id.layout_pic_0;
		case 1: return R.id.layout_pic_1;
		case 2: return R.id.layout_pic_2;
		default: return -1;
		}
	}

	private int getBikeLayoutForIndex(int index) {
		switch (index) {
		case 0: return R.id.lay_show_bike_0;
		case 1: return R.id.lay_show_bike_1;
		case 2: return R.id.lay_show_bike_2;
		default: return -1;
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_PICTURE) {				
				Bitmap scaledBm = BitmapUtils.decodeSampledBitmapFromResource(getPath(data.getData()), 240, 240);
				m_ViewModel.PictureCaches.get(m_CurrentPicture).set(scaledBm);
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
