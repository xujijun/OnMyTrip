package com.xjj.onmytrip.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper  {

    private static final String DATABASE_NAME = "onMyTrip.db";  
    private static final int DATABASE_VERSION = 2;  
    
    private static DBHelper helper;  
    private static SQLiteDatabase db; 
      
    public DBHelper(Context context) {  
        //CursorFactory设置为null,使用默认值  
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    } 
    
    public static SQLiteDatabase getDB(Context context){
    	if(helper == null)
    		helper = new DBHelper(context);
    	if(db == null)
    		db = helper.getWritableDatabase();
    	
    	return db;
    }
    
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table if not exists trips(id integer primary key autoincrement, trip_name varchar, user_id varchar, start_time datetime)";
		db.execSQL(sql);
		
		sql = "create table if not exists users(user_id varchar primary key, password char(32), nick_name varchar, register_date datetime, current_trip_id integer)";
		db.execSQL(sql);
		
		sql = "create table if not exists footprints(id integer primary key, date_time datetime, latitude decimal, longitude decimal, address varchar, trip_id integer)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String sql = "create table if not exists footprints(id integer primary key, date_time datetime, latitude decimal, longitude decimal, address varchar, trip_id integer)";
		db.execSQL(sql);
		
	}

}
