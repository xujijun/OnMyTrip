package com.xjj.onmytrip.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
    private DBHelper helper;  
    private SQLiteDatabase db; 
    
	public DBManager(Context context) {
		 helper = new DBHelper(context);  
	     db = helper.getWritableDatabase();  
	}
	
	public SQLiteDatabase getDb() {
		return db;
	}


}
