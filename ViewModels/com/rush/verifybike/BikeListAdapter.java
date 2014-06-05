package com.rush.verifybike;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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

		Bindings Bindings = new Bindings();
		
		List<BikeViewModel> bikes = VM.BikesViewModel.Bikes();
		final BikeViewModel objCurrent = bikes.get(arg0);		
		
		Bindings.BindText(convertedView.findViewById(R.id.lbl_bike_model), objCurrent.Model);
		Bindings.BindText(convertedView.findViewById(R.id.lbl_serial_number), objCurrent.SerialNumber);			
		Bindings.BindImageBitmap((ImageView) convertedView.findViewById(R.id.img_bike_preview), objCurrent.PictureCaches.get(0));
		
		Bindings.BindVisible(convertedView.findViewById(R.id.btn_stolen), objCurrent.Stolen, Mode.Invert);
		Bindings.BindVisible(convertedView.findViewById(R.id.lbl_stolen), objCurrent.Stolen);
		
		Bindings.BindVisible(convertedView.findViewById(R.id.btn_sold), objCurrent.Stolen, Mode.Invert);
		Bindings.BindVisible(convertedView.findViewById(R.id.btn_recovered), objCurrent.Stolen);
		
		Bindings.BindCommand((Button) convertedView.findViewById(R.id.btn_stolen), new ICommand<BikeViewModel>() {
			public void Execute(final BikeViewModel context) {
				Log.d("Bike " + context.Model.get() + " marked as stolen.");
				MessageBox.Show(m_Activity, "We are sorry...", "Your bike has been marked as stolen in our database", 
								new OnClickListener() {
									// On OK
									@Override
									public void onClick(DialogInterface dialog, int which) {
										context.Stolen.set(true);
										context.Commit(null);										
									}
								}, 
								null);				
			}
		}, objCurrent);

		Bindings.BindCommand((Button) convertedView.findViewById(R.id.btn_sold), new ICommand<BikeViewModel>() {
			public void Execute(final BikeViewModel context) {
				Log.d("Bike " + context.Model.get() + " was sold.");
				MessageBox.Show(m_Activity, "Bike was sold", "When your bike is sold, it will be removed from our databases being the duty of the new owner to register it again", 
						new OnClickListener() {
							// On OK
							@Override
							public void onClick(DialogInterface dialog, int which) {
								VM.BikesViewModel.RemoveBike(context);										
							}
						}, 
						new OnClickListener() {
							// On Cancel
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
							}
						});							
			}
		}, objCurrent);				

		Bindings.BindCommand((Button) convertedView.findViewById(R.id.btn_recovered), new ICommand<BikeViewModel>() {
			public void Execute(BikeViewModel context) {
				Log.d("Bike " + context.Model.get() + " marked as recovered.");
			
				context.Stolen.set(false);
				context.Commit(null);
			}
		}, objCurrent);
		
		return convertedView;
	}

}
