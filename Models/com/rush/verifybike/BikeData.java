package com.rush.verifybike;

import java.io.Serializable;
import java.util.Arrays;

import android.os.Parcel;
import android.os.Parcelable;

public class BikeData {
	
	public String SerialNumber;
	public String Model;	
	public Integer NoOfPics;	
	public Boolean Stolen;
	
	public BikeData() {}
	public BikeData(String serial, String model, Integer pics, Boolean stolen) {
		SerialNumber = serial;
		Model = model;
		NoOfPics = pics;
		Stolen = stolen;
	}	
}

class BikeDataViewModel implements Parcelable {
		
	public Observable<String> SerialNumber = new Observable<String>("", Validators.RequiredString);
	public Observable<String> Model = new Observable<String>("", Validators.RequiredString);
	public Observable<Integer> NoOfPics = new Observable<Integer>(0);
	public Observable<Boolean> Stolen = new Observable<Boolean>(false);
	public Observable<Boolean> Sold = new Observable<Boolean>(false);
	
	private BikeData m_ModelData;
	
	public Observable<Boolean> IsValid = new Validator(SerialNumber, Model).IsValid;
	
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
	}
	
	public void Destroy() {
		Bindings.Remove(SerialNumber);
		Bindings.Remove(Model);
		Bindings.Remove(NoOfPics);
		Bindings.Remove(Stolen);
		Bindings.Remove(Sold);
	}
	
	private BikeDataViewModel(Parcel in) {
        SerialNumber.set(in.readString());
        Model.set(in.readString());
        NoOfPics.set(in.readInt());
        
        boolean[] array = new boolean[2];
        in.readBooleanArray(array);
        Stolen.set(array[0]);
        Sold.set(array[1]);
        
        Save();
    }
	
	public BikeDataViewModel(BikeData _model) {
		m_ModelData = _model;
		
		SerialNumber.set(m_ModelData.SerialNumber);
		Model.set(m_ModelData.Model);
		NoOfPics.set(m_ModelData.NoOfPics);
		Stolen.set(m_ModelData.Stolen);
		Sold.set(false);
	}
	
	public void UpdateViewModel(BikeDataViewModel source) {
		SerialNumber.set(source.SerialNumber.get());
		Model.set(source.Model.get());
		NoOfPics.set(source.NoOfPics.get());
		Stolen.set(source.Stolen.get());
		Sold.set(source.Sold.get());
	}
	
	public void Save() {
		if (m_ModelData == null)
			m_ModelData = new BikeData();
		
		m_ModelData.SerialNumber = SerialNumber.get();
		m_ModelData.Model = Model.get();
		m_ModelData.NoOfPics = NoOfPics.get();
		m_ModelData.Stolen = Stolen.get();
	}

}
