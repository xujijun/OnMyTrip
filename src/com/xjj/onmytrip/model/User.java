package com.xjj.onmytrip.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xjj.onmytrip.db.DBHelper;
import com.xjj.onmytrip.util.MD5;

public class User {
	//user_id varchar primary key, password char(32), nick_name varchar, register_date datetime, current_trip_id integer
	private String userID;
	private String password;
	private String nickName;
	private String registerDate;
	int     currentTripID;
	
	private static SQLiteDatabase db;
	
	/**
	 * Set the register date as current time when a user is created
	 */
	public User(Context context) {
		db = DBHelper.getDB(context);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
		registerDate = sdf.format(new Date());
	}



	public String toString(){
		//return "userID: " + userID + " password: " + password + " nickName: " + nickName + " registerDate: " + registerDate + " currentTripID: " + String.valueOf(currentTripID);
		return "userID: " + userID + " 昵称: " + nickName + " 注册日期: " + registerDate + " 当前的TripID: " + String.valueOf(currentTripID);
	}
	


	/**
	 * Insert this user into database table users
	 * @return true: 成功, false: 失败
	 */
	public boolean saveUser(){
		//db.execSQL("insert into users values(?, ?, ?, ?, null)", new Object[]{u.getUserID(), u.getPassword(), u.getNickName(), u.getRegisterDate()});
		
        //ContentValues以键值对的形式存放数据  
        ContentValues values = new ContentValues();  
        values.put("user_id", userID);  
        values.put("password", MD5.getMD5(password));
        values.put("nick_name", nickName);  
        values.put("register_date", registerDate);
        values.put("current_trip_id", currentTripID);  
        
        //插入ContentValues中的数据  
		long rs;
		try {
			rs = db.insert("users", null, values);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rs = -1;
		}  
		
		return (rs != -1); //if rs==-1, return false.
	}
	

	public static boolean deleteUserByID(Context context, long id){
		db = DBHelper.getDB(context);
		
		int rs = db.delete("users", "user_id=?", new String[]{String.valueOf(id)});
		
		return (rs !=0 ); //if rs==0, return false.
	}

	/**
	 * Save the user's current trip ID into database
	 * @return true if update succeeded, false if failed
	 */
	public boolean saveCurrentTripID(){
        
		//ContentValues以键值对的形式存放数据  
        ContentValues values = new ContentValues();  
        values.put("current_trip_id", currentTripID);  
        
        //更新ContentValues中的数据  
		int rs;
		try {
			rs = db.update("users", values, "user_id=?", new String[]{userID});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rs = 0;
		}  
		
		return (rs > 0); //if rs<=0, return false.
	}
	

	
	/**
	 * Search a User from DB using a user_id
	 * @param userID
	 * @return User
	 */
	public static User findUserByID(Context context, String userID){
		db = DBHelper.getDB(context);
		User u = null;
		Cursor c = db.rawQuery("SELECT * FROM users where user_id=?", new String[]{userID});
		if(c.moveToNext()){
			u = new User(context);
			u.setUserID(userID);
			u.setPassword(c.getString(c.getColumnIndex("password")));
			u.setNickName(c.getString(c.getColumnIndex("nick_name")));
			u.setRegisterDate(c.getString(c.getColumnIndex("register_date")));
			u.setCurrentTripID(c.getInt(c.getColumnIndex("current_trip_id")));
		}
		
		return u;
	}
	

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(String registerDate) {
		this.registerDate = registerDate;
	}

	public int getCurrentTripID() {
		return currentTripID;
	}

	public void setCurrentTripID(int currentTripID) {
		this.currentTripID = currentTripID;
	}

	
	
}
