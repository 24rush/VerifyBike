package com.rush.verifybike;

import java.util.List;

import android.app.Activity;
import android.content.Context;
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
		return VM.BikesViewModel.Bikes().size();
	}

	@Override
	public Object getItem(int arg0) {
		Log.d("getItem " + VM.BikesViewModel.Bikes().get(arg0).Model);
		return VM.BikesViewModel.Bikes().get(arg0);
	}

	@Override
	public long getItemId(int arg0) {	
		return arg0;
	}

	@Override
	public View getView(int arg0, View convertedView, ViewGroup parentView) {				
		if (convertedView == null) {					    	
			convertedView = m_LayoutInflater.inflate(R.layout.bikelistitem, parentView, false);
		}

		List<BikeViewModel> bikes = VM.BikesViewModel.Bikes();
		final BikeViewModel objCurrent = bikes.get(arg0);		
		
		Bindings.BindText(convertedView.findViewById(R.id.lbl_bike_model), objCurrent.Model);
		Bindings.BindText(convertedView.findViewById(R.id.lbl_serial_number), objCurrent.SerialNumber);			
		Bindings.BindImageBitmap((ImageView) convertedView.findViewById(R.id.img_bike_preview), objCurrent.PictureCaches.get(0));
		
		Bindings.BindCommand((Button) convertedView.findViewById(R.id.btn_stolen), new ICommand<BikeViewModel>() {
			public void Execute(BikeViewModel context) {
				Log.d("Bike " + context.Model.get() + " marked as stolen.");
				MessageBox.Show(m_Activity, "We are sorry...", "Your bike has been marked as stolen in our database");
				
				context.Stolen.set(true);
				context.Commit();
			}
		}, objCurrent);

		Bindings.BindCommand((Button) convertedView.findViewById(R.id.btn_sold), new ICommand<BikeViewModel>() {
			public void Execute(BikeViewModel context) {
				Log.d("Bike " + context.Model.get() + " was sold.");
				
				VM.BikesViewModel.RemoveBike(context);
			}
		}, objCurrent);				

		return convertedView;
	}

}
