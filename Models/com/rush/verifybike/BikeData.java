package com.rush.verifybike;

import java.io.Serializable;
import java.util.Arrays;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class BikeData {
	public final static String Class = "Bike";

	private ParseObject _cloudObj;
	
	public BikeData() { 
		_cloudObj = new ParseObject(Class);
	}
	public BikeData(ParseObject cloudObj) {
		_cloudObj = cloudObj;		
	}	
	
	public void Save() {
		
		try {
			_cloudObj.save();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String SerialNumber() { return _cloudObj.getString("serial"); }
	public BikeData SerialNumber(String v) { 
		_cloudObj.put("serial", v);
		return this;
	}

	public String Model() { return _cloudObj.getString("model"); }
	public BikeData Model(String v) { 
		_cloudObj.put("model", v);
		return this;
	}

	public Integer NoOfPics() { return _cloudObj.getInt("pics"); };
	public BikeData NoOfPics(Integer v) { 
		_cloudObj.put("pics", v);
		return this;
	}

	public Boolean Stolen() { return _cloudObj.getBoolean("status"); };
	public BikeData Stolen(Boolean v) { 
		_cloudObj.put("status", v);
		return this;
	}
	
	public BikeData UserId(String s) {
		_cloudObj.put("userId", s);
		return this;
	}
}

class BikeDataViewModel implements Parcelable {

	public Observable<String> SerialNumber = new Observable<String>("", Validators.RequiredString);
	public Observable<String> Model = new Observable<String>("", Validators.RequiredString);

	public Observable<String> PictureURL_0 = new Observable<String>("", Validators.RequiredString);
	public Observable<String> PictureURL_1 = new Observable<String>("", Validators.RequiredString);
	public Observable<String> PictureURL_2 = new Observable<String>("", Validators.RequiredString);

	public Observable<Integer> NoOfPics = new Observable<Integer>(0);
	public Observable<Boolean> Stolen = new Observable<Boolean>(false);
	public Observable<Boolean> Sold = new Observable<Boolean>(false);

	private BikeData m_ModelData;

	public Observable<Boolean> IsValid = new Validator(SerialNumber, Model, PictureURL_0, PictureURL_1).IsValid;

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int arg1) {
		out.writeString(SerialNumber.get());
		out.writeString(Model.get());
		out.writeInt(NoOfPics.get());
		out.writeString(PictureURL_0.get());
		out.writeString(PictureURL_1.get());
		out.writeString(PictureURL_2.get());

		boolean[] array = new boolean[2];
		array[0] = Stolen.get();
		array[1] = Sold.get();
		out.writeBooleanArray(array);
	}

	public static final Parcelable.Creator<BikeDataViewModel> CREATOR = new Parcelable.Creator<BikeDataViewModel>() {
		public BikeDataViewModel createFromParcel(Parcel in) {
			return new BikeDataViewModel(in);
		}

		public BikeDataViewModel[] newArray(int size) {
			return new BikeDataViewModel[size];
		}
	};

	public BikeDataViewModel clone() {
		BikeDataViewModel clone = new BikeDataViewModel(m_ModelData);    	
		clone.UpdateViewModel(this);

		return clone;
	}

	public BikeDataViewModel() {
		m_ModelData = new BikeData();
	}

	public void Destroy() {
		Bindings.Remove(SerialNumber);
		Bindings.Remove(Model);
		Bindings.Remove(NoOfPics);
		Bindings.Remove(Stolen);
		Bindings.Remove(Sold);
		Bindings.Remove(PictureURL_0);
		Bindings.Remove(PictureURL_1);
		Bindings.Remove(PictureURL_2);
	}

	private BikeDataViewModel(Parcel in) {		
		SerialNumber.set(in.readString());
		Model.set(in.readString());
		NoOfPics.set(in.readInt());

		PictureURL_0.set(in.readString());
		PictureURL_1.set(in.readString());
		PictureURL_2.set(in.readString());

		boolean[] array = new boolean[2];
		in.readBooleanArray(array);
		Stolen.set(array[0]);
		Sold.set(array[1]);	
	}

	public BikeDataViewModel(BikeData _model) {
		m_ModelData = _model;

		SerialNumber.set(m_ModelData.SerialNumber());
		Model.set(m_ModelData.Model());
		NoOfPics.set(m_ModelData.NoOfPics());
		Stolen.set(m_ModelData.Stolen());
		Sold.set(false);
	}

	public void UpdateViewModel(BikeDataViewModel source) {
		SerialNumber.set(source.SerialNumber.get());
		Model.set(source.Model.get());
		NoOfPics.set(source.NoOfPics.get());
		Stolen.set(source.Stolen.get());
		Sold.set(source.Sold.get());

		PictureURL_0.set(source.PictureURL_0.get());
		PictureURL_1.set(source.PictureURL_1.get());
		PictureURL_2.set(source.PictureURL_2.get());			
	}

	private void updateModel() {
		if (m_ModelData == null)
			m_ModelData = new BikeData();

		Integer noPics = 0;
		if (!PictureURL_0.get().isEmpty())
			noPics++;
		if (!PictureURL_1.get().isEmpty())
			noPics++;
		if (!PictureURL_2.get().isEmpty())
			noPics++;			
		
		m_ModelData.SerialNumber(SerialNumber.get())
					.Model(Model.get())
					.NoOfPics(noPics)
					.Stolen(Stolen.get())
					.UserId(MainScreen.LoginViewModel.FacebookId.get());
	}
	
	public void Save() {
		updateModel();		
		m_ModelData.Save();
	}
}
