package com.rush.cycletome;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.rush.cycletome.BitmapUtils;
import com.rush.cycletome.DataEndpoint;
import com.rush.cycletome.Executor;
import com.rush.cycletome.INotifier;
import com.rush.cycletome.Localize;
import com.rush.cycletome.Log;
import com.rush.cycletome.MethodInvoker2;
import com.rush.cycletome.Observable;
import com.rush.cycletome.R;
import com.rush.cycletome.VM;
import com.rush.cycletome.Validator;
import com.rush.cycletome.Validators;

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
		m_Obj.put(m_Tag, v);			
	}

	@SuppressWarnings("unchecked")
	public Type get() {		
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
	private ObjectProxy<String> UserId; 
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

		UserId = new ObjectProxy<String>(m_CloudData, "userId");
		UserId.set(VM.LoginViewModel.FacebookId.get());

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
				Executor.Execute(new MethodInvoker2<Integer, ParseFile>() {
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
	
	private int filesSaved = 0; // When second file is saved, the whole object will be saved
	private void OnFileSaved(final INotifier<Integer> progress) {
		filesSaved++;		

		if (filesSaved < 2)
			return;
		
		saveObject(progress);
	}

	private void saveObject(final INotifier<Integer> progress) {
		Log.d("Saving new bike object.");
		m_CloudData.saveEventually(new SaveCallback() 
		{		
			@Override
			public void done(ParseException arg0) {
				Log.d("Bike object save completed.");

				IsNewObject = false;		
				filesSaved = 0;
								
				PictureBuffers.get(0).ResetChanged();
				PictureBuffers.get(1).ResetChanged();

				if (progress != null) {
					progress.OnValueChanged(100);
				}							
			}
		});
	}

	public void Save(final INotifier<Integer> progress) {				
		int index = 0;
		for (Observable<byte[]> picBuffer : PictureBuffers) {
			if (picBuffer != null && picBuffer.IsChanged()) {
				Log.d("Creating new file for pic" + index);

				final ParseFile file = new ParseFile("pic" + index + ".png", picBuffer.get());

				// Save file in background. User Executor just to pass the params
				Executor.Execute(new MethodInvoker2<Integer, ParseFile>() 
						{
					@Override
					public void Call(final Integer index, final ParseFile file) throws Exception 
					{
						file.saveInBackground(new SaveCallback() {							
							@Override
							public void done(ParseException arg0) {
								PictureFiles.get(index).set(file);								
								OnFileSaved(progress);
							}
						});		
					}
						}, index, file);								
			}
			else {
				Log.d("No need to create new file for pic" + index);
				OnFileSaved(progress);
			}

			index++;
		}
	}

	public void Delete() {		
		m_CloudData.deleteEventually();
		DataEndpoint.ClearCachedBikes();
	}	
}

class BikeViewModel {	

	private BikeModel m_ModelData;
	public boolean IsNewObject() { return m_ModelData.IsNewObject; }

	public Observable<String> SerialNumber = new Observable<String>("", Validators.RequiredString);	
	public Observable<String> Model = new Observable<String>("", Validators.RequiredString);	
	public Observable<Boolean> Stolen = new Observable<Boolean>(false);
	public Observable<Boolean> Sold = new Observable<Boolean>(false);

	public Observable<String> Error = new Observable<String>("");

	public List<Observable<Bitmap>> PictureCaches = new ArrayList<Observable<Bitmap>>(2) {		
		private static final long serialVersionUID = 915518789255069377L;
		{
			add(new Observable<Bitmap>(null, Validators.RequiredBitmap));
			add(new Observable<Bitmap>(null, Validators.RequiredBitmap));		
		}};

		// Force those fields to be filled in before saving
		public Observable<Boolean> IsValid = new Validator(SerialNumber, Model, PictureCaches.get(0), PictureCaches.get(1)).IsValid;

		public Observable<Boolean> IsSaving = new Observable<Boolean>(false);

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
			SerialNumber.load(m_ModelData.SerialNumber.get());
			Model.load(m_ModelData.Model.get());
			Stolen.load(m_ModelData.Stolen.get());

			int index = 0;
			for (Observable<Bitmap> picCache : PictureCaches) {
				Observable<byte[]> picBuffer = m_ModelData.PictureBuffers.get(index);
				if (picBuffer != null && picBuffer.get() != null) {				
					picCache.load(BitmapUtils.fromByteArray(picBuffer.get()));				
				}

				index++;
			}
		}

		public void Reset() {
			loadFromModel();
		}	

		public void Commit(final INotifier<Boolean> onCompleted) {
			IsSaving.set(true);
			Error.set("");
			
			boolean checkSerialUnique = IsNewObject() || (!m_ModelData.SerialNumber.get().equals(SerialNumber.get()));

			if (checkSerialUnique == true) {
				validateModel(new INotifier<Boolean>() {					
					@Override
					public void OnValueChanged(Boolean value) {						
						if (value == true) {
							updateModel(onCompleted);
						}
						else {
							Error.set(Localize.Id(R.string.msg_serial_exists));
							IsSaving.set(false);
							
							if (onCompleted != null)
								onCompleted.OnValueChanged(false);
						}
					}
				});
			} 
			else {
				updateModel(onCompleted);
			}
		}

		private void validateModel(final INotifier<Boolean> notifier) {
			if (notifier == null)
				return;		

			ParseQuery<ParseObject> qFindSerial = new ParseQuery<ParseObject>(BikeModel.Class);
			qFindSerial.whereEqualTo("serial", this.SerialNumber.get());
			
			Log.d("Check for unique serial " + this.SerialNumber.get());
			qFindSerial.findInBackground(new FindCallback<ParseObject>() {
				@Override
				public void done(List<ParseObject> arg0, ParseException arg1) {				
					if (arg0 == null || (arg0 != null && arg0.size() == 0)) {
						notifier.OnValueChanged(true);
					} 
					else {					
						Log.d("Check for unique serial returned results");
						notifier.OnValueChanged(false);
					}
				}
			});	
		}

		private void updateModel(final INotifier<Boolean> notifier) {
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

			m_ModelData.Save(new INotifier<Integer>() {			
				@Override
				public void OnValueChanged(Integer value) {															
					IsSaving.set(false);
					
					PictureCaches.get(0).ResetChanged();
					PictureCaches.get(1).ResetChanged();
					
					if (notifier != null)
						notifier.OnValueChanged(true);
				}
			});
		}

		public void Delete() {
			if (m_ModelData == null)
				return;

			m_ModelData.Delete();
			m_ModelData = null;
		}

}
