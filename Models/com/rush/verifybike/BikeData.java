package com.rush.verifybike;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

class ObjectProxy<Type> {		
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
	
	public ObjectProxy<String> SerialNumber;	
	public ObjectProxy<String> Model;	
	public ObjectProxy<Boolean> Stolen;
	
	public List<ObjectProxy<ParseFile>> PictureFiles;		
	public List<byte[]> PictureBuffers;
	
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
		
		PictureFiles = new ArrayList<ObjectProxy<ParseFile>>(3);
		PictureFiles.add(new ObjectProxy<ParseFile>(m_CloudData, "pic0"));
		PictureFiles.add(new ObjectProxy<ParseFile>(m_CloudData, "pic1"));
		PictureFiles.add(new ObjectProxy<ParseFile>(m_CloudData, "pic2"));
		
		PictureBuffers = new ArrayList<byte[]>(3);
		PictureBuffers.add(null);
		PictureBuffers.add(null);
		PictureBuffers.add(null);		

		int index = 0;
		for (final ObjectProxy<ParseFile> picFile : PictureFiles) {
			if (picFile.get() != null) {				
				ExceptionInhibitor.Execute(new MethodInvoker2<Integer, ParseFile>() {
					@Override
					public void Call(Integer index, ParseFile picFile) throws ParseException {
						PictureBuffers.set(index, picFile.getData());											
					}
				}, index, picFile);							
			}
			
			index++;
		}
	}
	
	public void Save() {
		m_CloudData.put("userId", MainScreen.LoginViewModel.FacebookId.get());
		
		int index = 0;
		for (byte[] picBuffer : PictureBuffers) {
			if (picBuffer != null) {
				ParseFile file = new ParseFile(picBuffer);
				try {
					file.save();
					
					PictureFiles.get(index).set(file);
				} 
				catch (ParseException e) {				
					e.printStackTrace();
				}
			}
			
			index++;
		}
		
		try {
			m_CloudData.save();
			
			IsNewObject = false;
		} 
		catch (ParseException e) {		
			e.printStackTrace();
		}
	}	
}

class BikeViewModel {	
		
	private BikeModel m_ModelData;
	public boolean IsNewObject() { return m_ModelData.IsNewObject; }
	
	public Observable<String> SerialNumber = new Observable<String>("", Validators.RequiredString);	
	public Observable<String> Model = new Observable<String>("", Validators.RequiredString);	
	public Observable<Boolean> Stolen = new Observable<Boolean>(false);
	public Observable<Boolean> Sold = new Observable<Boolean>(false);
				
	public List<Observable<Bitmap>> PictureCaches = new ArrayList<Observable<Bitmap>>(3) {{
		add(new Observable<Bitmap>(null, Validators.RequiredBitmap));
		add(new Observable<Bitmap>(null, Validators.RequiredBitmap));
		add(new Observable<Bitmap>(null, Validators.RequiredBitmap));
	}};
	
	public Observable<Boolean> IsValid = new Validator(SerialNumber, Model, PictureCaches.get(0), PictureCaches.get(1)).IsValid;
			
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
		
		int index = 0;
		for (Observable<Bitmap> picCache : PictureCaches) {
			byte[] picBuffer = m_ModelData.PictureBuffers.get(index);
			if (picBuffer != null) {

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inMutable = true;
			
				Bitmap bmp = BitmapFactory.decodeByteArray(picBuffer, 0, picBuffer.length, options);				
				picCache.set(bmp);
			}
			
			index++;
		}
	}
	
	public void Reset() {
		loadFromModel();
	}	
		
	public void Commit() {		
		m_ModelData.SerialNumber.set(SerialNumber.get());
		m_ModelData.Model.set(Model.get());	
		m_ModelData.Stolen.set(Stolen.get());
		
		int index = 0;
		for (Observable<Bitmap> picBmp : PictureCaches) {
			if (picBmp.get() != null) {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				picBmp.get().compress(Bitmap.CompressFormat.PNG, 100, stream);
				byte[] byteArray = stream.toByteArray();
				
				m_ModelData.PictureBuffers.set(index, byteArray);
			}
			
			index++;
		}
		
		m_ModelData.Save();
	}
	
	public void Destroy() {
		Bindings.Remove(SerialNumber);
		Bindings.Remove(Model);
		Bindings.Remove(Stolen);
		Bindings.Remove(Sold);
		Bindings.Remove(PictureCaches.get(0));
		Bindings.Remove(PictureCaches.get(1));
		Bindings.Remove(PictureCaches.get(2));
	}

}
