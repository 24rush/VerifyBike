package com.rush.verifybike;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

class ObjectProxy<Type> implements Serializable {	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ParseObject m_Obj;
	private String m_Tag;
	
	public ObjectProxy(ParseObject obj, String tag) {
		m_Obj = obj;
		m_Tag = tag;
	}
	
	public void set(Type v) {		
		m_Obj.put(m_Tag, v);
	}
	
	@SuppressWarnings("unchecked")
	public Type get() {
		Log.d("MyApp", "get " + m_Tag + " " + m_Obj.get(m_Tag));
		return (Type) m_Obj.get(m_Tag);
	}
}

class BikeModel {
	public final static String Class = "Bike";
	
	private ParseObject m_CloudData;
	
	public boolean IsNewObject = true;
	
	public BikeModel() {
		this(new ParseObject(Class));			
		IsNewObject = true;
	}
	
	public BikeModel(ParseObject source) {
		IsNewObject = false;		
		m_CloudData = source;
		
		SerialNumber = new ObjectProxy<String>(m_CloudData, "serial");
		Model = new ObjectProxy<String>(m_CloudData, "model");
		Stolen = new ObjectProxy<Boolean>(m_CloudData, "status");
	}
	
	public void Save() {
		m_CloudData.put("userId", MainScreen.LoginViewModel.FacebookId.get());
		
		try {
			m_CloudData.save();
			
			IsNewObject = false;
		} 
		catch (ParseException e) {		
			e.printStackTrace();
		}
	}
	
	public ObjectProxy<String> SerialNumber;	
	public ObjectProxy<String> Model;	
	public ObjectProxy<Boolean> Stolen;
	
	private final static String Pic0Tag = "pic0";
	private final static String Pic1Tag = "pic1";
	private final static String Pic2Tag = "pic2";
}

class BikeViewModel implements Parcelable {	
		
	private BikeModel m_ModelData;
	public boolean IsNewObject() { return m_ModelData.IsNewObject; }
	
	public Observable<String> SerialNumber = new Observable<String>("", Validators.RequiredString);	
	public Observable<String> Model = new Observable<String>("", Validators.RequiredString);	
	public Observable<Boolean> Stolen = new Observable<Boolean>(false);
	public Observable<Boolean> Sold = new Observable<Boolean>(false);
	
	public Observable<String> PictureURL_0 = new Observable<String>("", Validators.RequiredString);			
	public Observable<String> PictureURL_1 = new Observable<String>("", Validators.RequiredString);				
	public Observable<String> PictureURL_2 = new Observable<String>("", Validators.RequiredString);		
		
	public Observable<Boolean> IsValid = new Validator(SerialNumber, Model, PictureURL_0, PictureURL_1).IsValid;
		
	@Override
	public int describeContents() {	return 0; }
	
	public static final Parcelable.Creator<BikeViewModel> CREATOR = new Parcelable.Creator<BikeViewModel>() {
		public BikeViewModel createFromParcel(Parcel in) {
			return new BikeViewModel(in);
		}

		public BikeViewModel[] newArray(int size) {
			return new BikeViewModel[size];
		}
	};
	
	//
	// Constructors 
	//	
	public BikeViewModel(BikeModel src) {
		m_ModelData = src;
		
		loadFromModel();
	}
	
	public BikeViewModel() {
		m_ModelData = new BikeModel();			
	}
	
	private void loadFromModel() {
		SerialNumber.set(m_ModelData.SerialNumber.get());
		Model.set(m_ModelData.Model.get());
		Stolen.set(m_ModelData.Stolen.get());		
	}
	
	@Override
	public void writeToParcel(Parcel out, int arg1) {
		out.writeValue(m_ModelData);
		out.writeString(SerialNumber.get());
		out.writeString(Model.get());
		
		out.writeString(PictureURL_0.get());
		out.writeString(PictureURL_1.get());
		out.writeString(PictureURL_2.get());		

		boolean[] array = new boolean[2];		
		array[0] = Stolen.get();
		array[1] = Sold.get();
		out.writeBooleanArray(array);
	}
	
	private BikeViewModel(Parcel in) {
		in.readValue(BikeModel.class.getClassLoader());
		SerialNumber.set(in.readString());
		Model.set(in.readString());
	
		PictureURL_0.set(in.readString());
		PictureURL_1.set(in.readString());
		PictureURL_2.set(in.readString());
		
		boolean[] array = new boolean[2];
		in.readBooleanArray(array);
		Stolen.set(array[0]);
		Sold.set(array[1]);	
	}
	
	
	
	/*
	private Observable<Bitmap> createModelPictureObservable(final String tag) {
		return new Observable<Bitmap>() {
			@SuppressWarnings("unchecked")
			@Override
			public Bitmap get() {
				if (m_ModelData.get(tag) == null)
					return Bitmap.createBitmap(1, 1, Config.ARGB_8888);
				
				ParseFile file = (ParseFile) m_ModelData.get(tag);
				
				Bitmap bmp = null;
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inMutable = true;
				try {
					bmp = BitmapFactory.decodeByteArray(file.getData(), 0, file.getData().length, options);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				m_Value = bmp;
				return m_Value;
			}
			
			@Override
			public void set(Bitmap v) {
				super.set(v);
				
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				v.compress(Bitmap.CompressFormat.PNG, 100, stream);
				byte[] byteArray = stream.toByteArray();
				
				ParseFile imageFile = new ParseFile(byteArray);
				try {
					imageFile.save();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				m_ModelData.put(tag, imageFile);
			}
			
			@Override
			public String toString() {
				return get().toString();
			}
		};
	}		*/
		
	public void Commit() {		
		m_ModelData.SerialNumber.set(SerialNumber.get());
		m_ModelData.Model.set(Model.get());	
		m_ModelData.Stolen.set(Stolen.get());
		
		m_ModelData.Save();
	}
	
	public void Destroy() {
		Bindings.Remove(SerialNumber);
		Bindings.Remove(Model);
		Bindings.Remove(Stolen);
		Bindings.Remove(Sold);
		Bindings.Remove(PictureURL_0);
		Bindings.Remove(PictureURL_1);
		Bindings.Remove(PictureURL_2);
	}

}
