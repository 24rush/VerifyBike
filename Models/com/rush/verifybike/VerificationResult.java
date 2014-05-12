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
	
	public VerificationResult(BikeStatus status, String model) {
		this.Status = status;
		this.Model = model;
	}
}
