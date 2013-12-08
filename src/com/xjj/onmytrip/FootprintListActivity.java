package com.xjj.onmytrip;

import com.xjj.onmytrip.db.DBManager;
import com.xjj.onmytrip.model.Footprint;
import com.xjj.onmytrip.model.Trip;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class FootprintListActivity extends ListActivity {

    private DBManager dbm;
    private SimpleCursorAdapter mAdapter;
    private ListView lv;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_footprint_list);
		
		dbm = new DBManager(FootprintListActivity.this);
		Cursor cursor = Footprint.getAllFootprints(dbm.getDb(), null);;
		
		if(cursor == null)
			return;
		
		String[] from = new String[]{"_id", "trip_id", "date_time", "latitude", "longitude", "address"};
		int[] to = new int[]{R.id.footprint_id, R.id.trip_id, R.id.date_time, R.id.latitude, R.id.longitude, R.id.address};
        mAdapter = new SimpleCursorAdapter(FootprintListActivity.this, R.layout.footprint_item, cursor, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER); 

        setListAdapter(mAdapter);
        
        lv = (ListView)findViewById(R.id.ListViewTrips);
        
        lv.setOnItemClickListener(new ListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO open a new activity to display the footprints belong to this trip
				int selectedFootprintID = (int) arg3;//这里的这个arg3对应的就是数据库中_id的值  
				
				
			}
        	
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.footprint_list, menu);
		return true;
	}

}
