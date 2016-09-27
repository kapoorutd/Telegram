package org.telegram.socialuser.model;


public class CZResponse {
	private int responseCode;
	private String responseString;
	
	
	public CZResponse(int responseCode, String responseString) {
		super();
		this.responseCode = responseCode;
		this.responseString = responseString;
	}
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseString() {
		return responseString;
	}
	public void setResponseString(String responseString) {
		this.responseString = responseString;
	}
	
	

}
