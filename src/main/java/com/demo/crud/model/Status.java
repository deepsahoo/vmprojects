package com.demo.crud.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Status {

	@JsonProperty("id")
	private int id;
	@JsonProperty("description")
	private String description;
	@JsonProperty("url")
	private String url;

	public Status(int id, String description, String url) {
		this.id = id;
		this.description = description;
		this.url = url;
	}
}
