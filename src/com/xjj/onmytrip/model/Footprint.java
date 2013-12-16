package com.xjj.onmytrip.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.xjj.onmytrip.db.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

public class Footprint {
	//create table if not exists footprints(id integer primary key, date_time datetime, latitude decimal(10,6), longitude decimal(10,6), address varchar, trip_id integer)
	
	private int id;
	private String dateTime;
	private String latitude;
	private String longitude;
	private String address;
	private int tripID;
	
	private static SQLiteDatabase db;
	
	/**
	 * Set the dateTime as current time when a new footprint is created.
	 */
	public Footprint(Context context) {
		db = DBHelper.getDB(context);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
		dateTime = sdf.format(new Date());
	}

	//public boolean saveFootprint(SQLiteDatabase db){
	public boolean saveFootprint(){
		//ContentValues以键值对的形式存放数据  
        ContentValues values = new ContentValues();  
        //ListViewFootprintsListViewFootprints.put("id", id);  
        values.put("date_time", dateTime);
        values.put("latitude", latitude);  
        values.put("longitude", longitude);
        values.put("address", address);  
        values.put("trip_id", tripID);  
        
        //插入ContentValues中的数据  
		long rs;
		try {
			rs = db.insert("footprints", null, values);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rs = -1;
		}  
		
		return (rs != -1); //if rs==-1, return false.
	}
	
	public static boolean deleteFootprintByID(Context context, long id){
		db = DBHelper.getDB(context);
		int rs = db.delete("footprints", "id=?", new String[]{String.valueOf(id)});
		return (rs !=0 ); //if rs==0, return false.
	}
	
	public static Cursor getAllFootprints(Context context, String tripID){
		db = DBHelper.getDB(context);
		Cursor cursor;
		
		if(tripID == null)
			 cursor = db.rawQuery("select id as _id, date_time, latitude, longitude, address, trip_id from footprints order by _id desc", null);  
		else
			cursor = db.rawQuery("select id as _id, date_time, latitude, longitude, address, trip_id from footprints where trip_id=? order by _id desc", new String[]{tripID});  
	    
	 
		 // 这里数据库不能关闭
	     return cursor;  
	}
	
	public void setLocation(Location loc){
		latitude = String.valueOf(loc.getLatitude());
		longitude = String.valueOf(loc.getLongitude());
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dataTime) {
		this.dateTime = dataTime;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getTripID() {
		return tripID;
	}
	public void setTripID(int trip_id) {
		this.tripID = trip_id;
	}
	
	
	
}
