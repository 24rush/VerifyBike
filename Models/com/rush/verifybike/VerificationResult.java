package com.rush.verifybike;

import java.io.Serializable;

public class VerificationResult implements Serializable {

	public enum BikeStatus {
		NotInDatabase,
		Stolen,
		Owned
	}
	
	private static final long serialVersionUID = 1L;
	
	public BikeStatus Status;
	public String Model;
	public byte[] BikePreview;
	
	public String OwnerPhone;
	public String OwnerEmail;
	
	public VerificationResult(BikeStatus status, String model) {
		this.Status = status;
		this.Model = model;		
	}
	
	public VerificationResult(BikeStatus status, String model, byte[] preview, String phone, String email) {
		this(status, model);
		
		this.BikePreview = preview;
		this.OwnerPhone = phone;
		this.OwnerEmail = email;
	}
}
