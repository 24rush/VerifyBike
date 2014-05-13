package com.rush.verifybike;

import java.util.ArrayList;
import java.util.List;

public class Validator {
	class ValidationCache {
		public boolean IsValid;
		public Observable<?> Observable;
	}
	
	private List<ValidationCache> m_ValidationCache = new ArrayList<ValidationCache>(); 
			
	public Observable<Boolean> IsValid = new Observable<Boolean>(false);
	
	public Validator(Observable<?>...observables) {
		for (final Observable<?> observable : observables) {
			final ValidationCache cacheData = new ValidationCache();
			m_ValidationCache.add(cacheData);
			
			observable.addTypelessObserver(new INotifier<Object>() {				
				public void OnValueChanged(Object value) {
					cacheData.IsValid = observable.IsValid();
				
					CheckValidation();
				}
			});
		}
	}
	
	private void CheckValidation() {
		boolean finalResult = true;
		
		for (ValidationCache observableCache : m_ValidationCache) {
			finalResult &= observableCache.IsValid;
		}
	
		IsValid.set(finalResult);
	}
}

class Validators {
	public static RequiredString RequiredString = new RequiredString();
}

class RequiredString implements IValidator<String> {

	@Override
	public boolean IsValid(String value) {
		return value != null && !value.equals("");
	}
}

interface IValidator<Type> {
	public boolean IsValid(Type value);
}
