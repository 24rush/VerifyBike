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
	
	public BikeData(String serial, String model, Integer pics, Boolean stolen) {
		SerialNumber = serial;
		Model = model;
		NoOfPics = pics;
		Stolen = stolen;
	}	
}

class BikeDataViewModel implements Parcelable {
		
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

	public Observable<String> SerialNumber = new Observable<String>("");
	public Observable<String> Model = new Observable<String>("");
	public Observable<Integer> NoOfPics = new Observable<Integer>(0);
	public Observable<Boolean> Stolen = new Observable<Boolean>(false);
	public Observable<Boolean> Sold = new Observable<Boolean>(false);
	
	private BikeData m_ModelData;
	
	public BikeDataViewModel() {
		
	}
	
	private BikeDataViewModel(Parcel in) {
        SerialNumber.set(in.readString());
        Model.set(in.readString());
        NoOfPics.set(in.readInt());
        
        boolean[] array = new boolean[2];
        in.readBooleanArray(array);
        Stolen.set(array[0]);
        Sold.set(array[1]);
    }
	
	public BikeDataViewModel(BikeData _model) {
		m_ModelData = _model;
	}
	
	public BikeDataViewModel(String serial, String model, Integer pics, Boolean stolen) {
		SerialNumber.set(serial);
		Model.set(model);
		NoOfPics.set(pics);
		Stolen.set(stolen);
		Sold.set(false);
	}
	
	public BikeDataViewModel GetViewModel() {		
		return new BikeDataViewModel(m_ModelData.SerialNumber, m_ModelData.Model, m_ModelData.NoOfPics, m_ModelData.Stolen);	
	}
	
	public void SaveModelData() {
		m_ModelData.SerialNumber = SerialNumber.get();
		m_ModelData.Model = Model.get();
		m_ModelData.NoOfPics = NoOfPics.get();
		m_ModelData.Stolen = Stolen.get();
	}

}
