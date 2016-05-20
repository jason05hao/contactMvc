package com.contactMvc;


public class AppException extends RuntimeException {
	private String errorMsg;

	public AppException(String errorMsg){
		this.errorMsg = errorMsg;
	}

	public String getErrorMsg(){
		return this.errorMsg;
	}

	public String getErrorCode(){
		return "-1";
	}

}