package com.xjj.onmytrip;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.xjj.onmytrip.db.DBManager;
import com.xjj.onmytrip.model.Trip;

public class TripListActivity extends ListActivity{

    private DBManager dbm;
    private SimpleCursorAdapter mAdapter;
    private ListView lv;  
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trip_list);
		
		dbm = new DBManager(TripListActivity.this);
		Cursor cursor = Trip.getAllTrips(dbm.getDb(), null);;
		
		if(cursor == null)
			return;
		
		String[] from = new String[]{"_id", "trip_name", "user_id", "start_time"};
		int[] to = new int[]{R.id.trip_id, R.id.trip_name, R.id.user_id, R.id.start_time};
        mAdapter = new SimpleCursorAdapter(TripListActivity.this, R.layout.trip_item, cursor, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER); 

        //setListAdapter(mAdapter);
        
        lv = (ListView)findViewById(android.R.id.list);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new ListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				//open the FootprintActivity to display the footprints for the selected trip
				Intent intent = new Intent(TripListActivity.this, FootprintListActivity.class);
				intent.putExtra("tripID", arg3);//这里的这个arg3对应的就是数据库中_id的值  
				
				startActivity(intent);
			}
        	
        });
        
        lv.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				
				return false;
			}
        	
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.trip_list, menu);
		return true;
	}
	
	
}
