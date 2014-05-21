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
import com.parse.SaveCallback;

//
// Mediator class to a ParseObject
//

class ObjectProxy<Type> {		
	private ParseObject m_Obj;
	private String m_Tag;
		
	public ObjectProxy(ParseObject obj, String tag) {
		m_Obj = obj;
		m_Tag = tag;
	}
	
	public void set(Type v) {
		Log.d("MyApp", "set " + m_Tag);
		m_Obj.put(m_Tag, v);			
	}
	
	@SuppressWarnings("unchecked")
	public Type get() {
		Log.d("MyApp", "get " + m_Tag + " " + m_Obj.get(m_Tag));
		return (Type) m_Obj.get(m_Tag);
	}	
}

class BikeModel {
	public final static String Class = "BikeData";
	
	public boolean IsNewObject = true;	
	
	public ObjectProxy<String> SerialNumber;	
	public ObjectProxy<String> Model;	
	public ObjectProxy<Boolean> Stolen;
	public List<Observable<byte[]>> PictureBuffers;
	
	private ParseObject m_CloudData;
	private List<ObjectProxy<ParseFile>> PictureFiles;		

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
		
		PictureFiles = new ArrayList<ObjectProxy<ParseFile>>(2);
		PictureFiles.add(new ObjectProxy<ParseFile>(m_CloudData, "pic0"));
		PictureFiles.add(new ObjectProxy<ParseFile>(m_CloudData, "pic1"));		
		
		PictureBuffers = new ArrayList<Observable<byte[]>>(2);
		PictureBuffers.add(new Observable<byte[]>());		
		PictureBuffers.add(new Observable<byte[]>());		

		// Load byte[] from ParseFile
		int index = 0;
		for (ObjectProxy<ParseFile> picFile : PictureFiles) {			
			if (picFile.get() != null) {								
				ExceptionInhibitor.Execute(new MethodInvoker2<Integer, ParseFile>() {
					@Override
					public void Call(Integer index, ParseFile picFile) throws Exception {													
						PictureBuffers.get(index).set(picFile.getData());
						PictureBuffers.get(index).ResetChanged();
					}
				}, index, picFile.get());							
			}			
			index++;
		}
	}
	
	private int filesSaved = 0;
	private void OnFileSaved() {
		filesSaved++;
		
		if (filesSaved < 1)
			return;
		
		Log.d("MyApp", "saving");
		m_CloudData.saveEventually();
		Log.d("MyApp", "saved");
		
		IsNewObject = false;
		
		filesSaved = 0;
	}
	
	public void Save() {
		m_CloudData.put("userId", MainScreen.LoginViewModel.FacebookId.get());
		
		int index = 0;
		for (Observable<byte[]> picBuffer : PictureBuffers) {
			if (picBuffer != null && picBuffer.IsChanged()) {
				Log.d("MyApp", "Creating new file ");
				
				final ParseFile file = new ParseFile("pic" + index + ".png", picBuffer.get());
				
				// save file in background
				ExceptionInhibitor.Execute(new MethodInvoker2<Integer, ParseFile>() 
				{
					@Override
					public void Call(final Integer index, final ParseFile file) throws Exception 
					{
						file.saveInBackground(new SaveCallback() 
						{							
							@Override
							public void done(ParseException arg0) {
								PictureFiles.get(index).set(file);
								
								OnFileSaved();
							}
						});		
					}
				}, index, file);								
			}
			else
				Log.d("MyApp", "No need to create new file ");
			
			index++;
		}
	}

	public void Delete() {		
		m_CloudData.deleteEventually();
	}	
}

class BikeViewModel {	
		
	private BikeModel m_ModelData;
	public boolean IsNewObject() { return m_ModelData.IsNewObject; }
	
	public Observable<String> SerialNumber = new Observable<String>("", Validators.RequiredString);	
	public Observable<String> Model = new Observable<String>("", Validators.RequiredString);	
	public Observable<Boolean> Stolen = new Observable<Boolean>(false);
	public Observable<Boolean> Sold = new Observable<Boolean>(false);
				
	public List<Observable<Bitmap>> PictureCaches = new ArrayList<Observable<Bitmap>>(2) {{
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
			Observable<byte[]> picBuffer = m_ModelData.PictureBuffers.get(index);
			if (picBuffer != null) {

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inMutable = true;
			
				Bitmap bmp = BitmapFactory.decodeByteArray(picBuffer.get(), 0, picBuffer.get().length, options);				
				picCache.load(bmp);				
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
			if (picBmp.get() != null && picBmp.IsChanged()) {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				picBmp.get().compress(Bitmap.CompressFormat.PNG, 100, stream);
				byte[] byteArray = stream.toByteArray();
				
				m_ModelData.PictureBuffers.get(index).set(byteArray);
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
	}

	public void Delete() {
		if (m_ModelData == null)
			return;
			
		m_ModelData.Delete();
		m_ModelData = null;
	}

}
