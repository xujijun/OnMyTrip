/**
 * 
 */
package com.xjj.onmytrip.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.xjj.onmytrip.db.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Trip {
	//id integer primary key autoincrement, trip_name varchar, user_id varchar, start_time datetime
	private int id;
	private String tripName;
	private String userID;
	private String startTime;
	
	private static SQLiteDatabase db;
	
	/**
	 * Set the current time as start time when a new trip is created
	 */
	public Trip(Context context) {
		db = DBHelper.getDB(context);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
		startTime = sdf.format(new Date());
	}

	/**
	 * Clone content from another trip
	 * @param Trip to be cloned to here
	 */
	public void cloneFrom(Trip t){
		this.id = t.getId();
		this.tripName = t.getTripName();
		this.userID = t.getUserID();
		this.startTime = t.getStartTime();
	}
	
	/**
	 * Get all trips
	 * @param db
	 * @param userID: search those belong to a specified userID
	 * @return
	 */
	//public static Cursor getAllTrips(SQLiteDatabase db, String userID){
	public static Cursor getAllTrips(Context context, String userID){
		Cursor cursor;
		db = DBHelper.getDB(context);
		
		if(userID == null)
			 cursor = db.rawQuery("select id as _id, trip_name, user_id, start_time from trips", null);  
		else
			cursor = db.rawQuery("select id as _id, trip_name, user_id, start_time from trips where user_id=?", new String[]{userID});  
	    
		 
		 // 这里数据库不能关闭
	     return cursor;  
	}
	
	
	/**
	 * Search a user's current trip.
	 * @param userID
	 * @return a Trip
	 */
	//public static Trip findCurrentTripByUserID(SQLiteDatabase db, String userID){
	public static Trip findCurrentTripByUserID(Context context, String userID){
		db = DBHelper.getDB(context);
		Trip t = null;
		Cursor c = db.rawQuery("SELECT * FROM trips where id=(select current_trip_id from users where user_id=?)", new String[]{userID});
		if(c.moveToNext()){
			t = new Trip(context);
			t.setId(c.getInt(c.getColumnIndex("id")));
			t.setTripName(c.getString(c.getColumnIndex("trip_name")));
			t.setStartTime(c.getString(c.getColumnIndex("start_time")));
			t.setUserID(userID);
		}	
		return t;
	}
	
	public static Trip findTripByID(Context context, int tripID){
		db = DBHelper.getDB(context);
		Trip t = null;
		Cursor c = db.rawQuery("SELECT * FROM trips where id=?", new String[]{String.valueOf(tripID)});
		if(c.moveToNext()){
			t = new Trip(context);
			t.setId(tripID);
			t.setTripName(c.getString(c.getColumnIndex("trip_name")));
			t.setStartTime(c.getString(c.getColumnIndex("start_time")));
			t.setUserID(c.getString(c.getColumnIndex("user_id")));
		}	
		return t;
	}
	
	/**
	 * insert this trip into the database table trips
	 * @return true if succeeded, false if failed
	 */
	public boolean saveTrip(){

        //ContentValues以键值对的形式存放数据  
        ContentValues values = new ContentValues();  
        values.put("trip_name", tripName);  
        values.put("user_id", userID);
        values.put("start_time", startTime);  
        
        //插入ContentValues中的数据  
		long rs;
		try {
			rs = db.insert("trips", null, values);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rs = -1;
		}  
		
		return (rs != -1); //if rs==-1, return false.
	}

	/**
	 * Delete a trip for the specified ID
	 * @param db
	 * @param id: id to be deleted
	 * @return
	 */
	public static boolean deleteTripByID(Context context, long id){
		db = DBHelper.getDB(context);
		
		int rs = db.delete("trips", "id=?", new String[]{String.valueOf(id)});
		
		return (rs !=0 ); //if rs==0, return false.
		
	}


	/**
	 * get the max (i.e. latest) Trip ID from table trips
	 * @return
	 */
	public static int getMaxTripID(Context context){
		db = DBHelper.getDB(context);
		int id = -1;
		Cursor c = db.rawQuery("SELECT max(id) FROM trips", null);
		if(c.moveToNext()){
			id = c.getInt(0);
		}
		return id;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTripName() {
		return tripName;
	}
	public void setTripName(String tripName) {
		this.tripName = tripName;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	


}
