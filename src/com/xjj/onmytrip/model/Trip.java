/**
 * 
 */
package com.xjj.onmytrip.model;

import java.util.Date;

public class Trip {

	private int id;
	private String trip_name;
	private String user_id;
	private Date start_time;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTrip_name() {
		return trip_name;
	}
	public void setTrip_name(String trip_name) {
		this.trip_name = trip_name;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public Date getStart_time() {
		return start_time;
	}
	public void setStart_time(Date start_time) {
		this.start_time = start_time;
	}
	

}
