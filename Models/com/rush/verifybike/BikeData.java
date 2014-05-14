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
		
		PictureURL_0.set(source.PictureURL_0.get());
		PictureURL_1.set(source.PictureURL_1.get());
		PictureURL_2.set(source.PictureURL_2.get());
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
