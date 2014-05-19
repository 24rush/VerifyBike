package com.rush.verifybike;

import java.io.ByteArrayOutputStream;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

class BikeDataViewModel implements Parcelable {	
	public final static String Class = "Bike";	
	private ParseObject m_ModelData;				
	
	private final static String SerialNumberTag = "serial";
	public Observable<String> SerialNumber;
	
	private final static String ModelTag = "model";	
	public Observable<String> Model;
	
	private final static String StolenTag = "status";
	public Observable<Boolean> Stolen;
	
	public Observable<String> PictureURL_0 = new Observable<String>("", Validators.RequiredString);	
	private final static String Pic0Tag = "pic0";
	public Observable<Bitmap> PictureData_0;
	
	public Observable<String> PictureURL_1 = new Observable<String>("", Validators.RequiredString);	
	private final static String Pic1Tag = "pic1";
	public Observable<Bitmap> PictureData_1;
	
	public Observable<String> PictureURL_2 = new Observable<String>("", Validators.RequiredString);
	private final static String Pic2Tag = "pic2";
	public Observable<Bitmap> PictureData_2;	
	
	public Observable<Boolean> Sold = new Observable<Boolean>(false);	
	public Observable<Boolean> IsValid;
		
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	

	public static final Parcelable.Creator<BikeDataViewModel> CREATOR = new Parcelable.Creator<BikeDataViewModel>() {
		public BikeDataViewModel createFromParcel(Parcel in) {
			return new BikeDataViewModel(in);
		}

		public BikeDataViewModel[] newArray(int size) {
			return new BikeDataViewModel[size];
		}
	};

	public void Destroy() {
		Bindings.Remove(SerialNumber);
		Bindings.Remove(Model);
		Bindings.Remove(Stolen);
		Bindings.Remove(Sold);
		Bindings.Remove(PictureURL_0);
		Bindings.Remove(PictureURL_1);
		Bindings.Remove(PictureURL_2);
	}

	//
	// Constructors 
	//	
	
	@Override
	public void writeToParcel(Parcel out, int arg1) {
		out.writeString(SerialNumber.get());
		out.writeString(Model.get());
		
		out.writeString(PictureURL_0.get());
		out.writeString(PictureURL_1.get());
		out.writeString(PictureURL_2.get());
		
		out.writeValue(PictureData_0.get());
		out.writeValue(PictureData_1.get());
		out.writeValue(PictureData_2.get());					

		boolean[] array = new boolean[2];		
		array[0] = Stolen.get();
		array[1] = Sold.get();
		out.writeBooleanArray(array);
	}
	
	private BikeDataViewModel(Parcel in) {
		this();
		
		SerialNumber.set(in.readString());
		Model.set(in.readString());
	
		PictureURL_0.set(in.readString());
		PictureURL_1.set(in.readString());
		PictureURL_2.set(in.readString());

		PictureData_0.set((Bitmap)in.readValue(Bitmap.class.getClassLoader()));
		PictureData_1.set((Bitmap)in.readValue(Bitmap.class.getClassLoader()));
		PictureData_2.set((Bitmap)in.readValue(Bitmap.class.getClassLoader()));
		
		boolean[] array = new boolean[2];
		in.readBooleanArray(array);
		Stolen.set(array[0]);
		Sold.set(array[1]);	
	}

	public BikeDataViewModel() {
		this(new ParseObject(Class));
	}
	
	public BikeDataViewModel(BikeDataViewModel src) {
		this(src.m_ModelData);
	}
	
	public BikeDataViewModel(ParseObject _model) {
		m_ModelData = _model;		
		buildObservables(m_ModelData);

		Sold.set(false);
	}
	
	private <Type> Observable<Type> createModelObservable(final String tag, Type initialValue, IValidator<Type> validator) {
		return new Observable<Type>(initialValue, validator) {
			@SuppressWarnings("unchecked")
			@Override
			public Type get() {
				Log.d("MyApp", "Get " + tag + " " + m_ModelData.get(tag));
				// Don't overwrite default value
				if (m_ModelData.get(tag) == null)
					return m_Value;
				
				m_Value = (Type) m_ModelData.get(tag);
				return m_Value;
			}
			
			@Override
			public void set(Type v) {
				super.set(v);
				m_Value = v;
				m_ModelData.put(tag, v);
			}
			
			@Override
			public String toString() {
				return get().toString();
			}
		};
	}
	
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
	}
		
	private void buildObservables(ParseObject modelData) {		
		SerialNumber = createModelObservable(SerialNumberTag, "", Validators.RequiredString);				
		Model = createModelObservable(ModelTag, "", Validators.RequiredString);		
		Stolen = createModelObservable(StolenTag, false, null);
		
		PictureData_0 = createModelPictureObservable(Pic0Tag);
		PictureData_1 = createModelPictureObservable(Pic1Tag);
		PictureData_2 = createModelPictureObservable(Pic2Tag);
				
		IsValid = new Validator(SerialNumber, Model, PictureURL_0, PictureURL_1).IsValid;		
	}
	
	public void CopyFrom(BikeDataViewModel source) {
		SerialNumber.set(source.SerialNumber.get());
		Model.set(source.Model.get());		
		Stolen.set(source.Stolen.get());
		Sold.set(source.Sold.get());

		PictureURL_0.set(source.PictureURL_0.get());
		PictureURL_1.set(source.PictureURL_1.get());
		PictureURL_2.set(source.PictureURL_2.get());
		
		PictureData_0.set(source.PictureData_0.get());
		PictureData_1.set(source.PictureData_1.get());
		PictureData_2.set(source.PictureData_2.get());
	}
	
	public void Save() {
		m_ModelData.put("userId", MainScreen.LoginViewModel.FacebookId.get());
		
		try {
			m_ModelData.save();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
