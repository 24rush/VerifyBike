package com.rush.verifybike;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.rush.verifybike.Bindings.Mode;

public class BikeListAdapter extends BaseAdapter{

	private Activity m_Activity;
	private LayoutInflater m_LayoutInflater;

	public BikeListAdapter(Activity _parent) {
		m_Activity = _parent;
		m_LayoutInflater = (LayoutInflater) m_Activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
	}

	@Override
	public int getCount() {
		Log.d("MyApp", "getCount " + MainScreen.BikesViewModel.Bikes().size());
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

		List<BikeDataViewModel> bikes = MainScreen.BikesViewModel.Bikes();
		BikeDataViewModel objCurrent = bikes.get(arg0);
		Log.d("MyApp", "getView " + objCurrent.Model);
		Bindings.BindText(convertedView.findViewById(R.id.lbl_bike_model), objCurrent.Model, Mode.TWO_WAY);
		Bindings.BindText(convertedView.findViewById(R.id.lbl_serial_number), objCurrent.SerialNumber, Mode.TWO_WAY);			
		
		Bindings.BindCommand((Button) convertedView.findViewById(R.id.btn_stolen), new ICommand<BikeDataViewModel>() {
			public void Execute(BikeDataViewModel context) {
				Log.d("MyApp", "Stolen " + context.Model.get());
				context.Stolen.set(true);
			}
		}, objCurrent);
		
		Bindings.BindCommand((Button) convertedView.findViewById(R.id.btn_sold), new ICommand<BikeDataViewModel>() {
			public void Execute(BikeDataViewModel context) {
				Log.d("MyApp", "Sold " + context.Model.get());
				MainScreen.BikesViewModel.RemoveBike(context);
			}
		}, objCurrent);				
				
		return convertedView;
	}

}
