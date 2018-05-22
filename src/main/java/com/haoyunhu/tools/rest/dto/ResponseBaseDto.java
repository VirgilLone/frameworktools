package com.haoyunhu.tools.rest.dto;

import java.io.Serializable;

public class ResponseBaseDto implements Serializable{

	private String message = "";
	private String success = "";
	private String errorCode = "";
	private String responseTime = "";

	public ResponseBaseDto() {
		super();
	}

	public ResponseBaseDto(String message, String success) {
		super();
		this.message = message;
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public String getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(String responseTime) {
		this.responseTime = responseTime;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
}
