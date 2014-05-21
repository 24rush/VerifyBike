package com.rush.verifybike;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

public class BikeListAdapter extends BaseAdapter{

	private Activity m_Activity;
	private LayoutInflater m_LayoutInflater;

	public BikeListAdapter(Activity _parent) {
		m_Activity = _parent;
		m_LayoutInflater = (LayoutInflater) m_Activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
	}

	@Override
	public int getCount() {		
		return MainScreen.BikesViewModel.Bikes().size();
	}

	@Override
	public Object getItem(int arg0) {
		Log.d("MyApp", "getItem " + MainScreen.BikesViewModel.Bikes().get(arg0).Model);
		return MainScreen.BikesViewModel.Bikes().get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View convertedView, ViewGroup parentView) {		
		Log.d("MyApp", arg0 + " " + convertedView);

		if (convertedView == null)					    	
			convertedView = m_LayoutInflater.inflate(R.layout.bikelistitem, parentView, false);		

		List<BikeViewModel> bikes = MainScreen.BikesViewModel.Bikes();
		final BikeViewModel objCurrent = bikes.get(arg0);
		final ImageView bikePreview = (ImageView) convertedView.findViewById(R.id.imageView1);

		Log.d("MyApp", "getView " + objCurrent.Model.get());
		Bindings.BindText(convertedView.findViewById(R.id.lbl_bike_model), objCurrent.Model);
		Bindings.BindText(convertedView.findViewById(R.id.lbl_serial_number), objCurrent.SerialNumber);			

		INotifier<Bitmap> picNotifier = new INotifier<Bitmap>() {			
			@Override
			public void OnValueChanged(Bitmap value) {												
				bikePreview.setImageBitmap(value);
			}
		};
				
		objCurrent.PictureCaches.get(0).addObserver(picNotifier);		
		if (objCurrent.PictureCaches.get(0).get() != null)
			picNotifier.OnValueChanged(objCurrent.PictureCaches.get(0).get());

		Bindings.BindCommand((Button) convertedView.findViewById(R.id.btn_stolen), new ICommand<BikeViewModel>() {
			public void Execute(BikeViewModel context) {
				Log.d("MyApp", "Stolen " + context.Model.get());
				MessageBox.Show(m_Activity, "We are sorry...", "Your bike has been marked as stolen in our database");
				context.Stolen.set(true);
				context.Commit();
			}
		}, objCurrent);

		Bindings.BindCommand((Button) convertedView.findViewById(R.id.btn_sold), new ICommand<BikeViewModel>() {
			public void Execute(BikeViewModel context) {
				Log.d("MyApp", "Sold " + context.Model.get());
				MainScreen.BikesViewModel.RemoveBike(context);
			}
		}, objCurrent);				

		return convertedView;
	}

}
