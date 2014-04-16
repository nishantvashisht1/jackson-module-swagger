package com.fasterxml.jackson.module.swagger.model;

public class AllowableValue {
	private String id;

	public AllowableValue() { }
	public AllowableValue(String id) {
		this.id = id;
	}

	public void setId(String id) { this.id = id; }
	public String getId() { return id; }
	
	@Override
	public String toString()  {
		return id;
	}
}

