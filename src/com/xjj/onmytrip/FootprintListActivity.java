package com.xjj.onmytrip;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.xjj.onmytrip.db.DBManager;
import com.xjj.onmytrip.model.Footprint;

public class FootprintListActivity extends ListActivity {

    private DBManager dbm;
    private SimpleCursorAdapter mAdapter;
    private ListView lv;
    
    Cursor cursor;
    long selectedID;
    long currentTripID;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_footprint_list);
		
		Intent intent = getIntent();  
        currentTripID = intent.getLongExtra("tripID", -1); //Note: long
		
		dbm = new DBManager(FootprintListActivity.this);
		cursor = Footprint.getAllFootprints(dbm.getDb(), String.valueOf(currentTripID));
		
		if(cursor == null)
			return;
		
		String[] from = new String[]{"_id", "trip_id", "date_time", "latitude", "longitude", "address"};
		int[] to = new int[]{R.id.footprint_id, R.id.trip_id, R.id.date_time, R.id.latitude, R.id.longitude, R.id.address};
        mAdapter = new SimpleCursorAdapter(FootprintListActivity.this, R.layout.footprint_item, cursor, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER); 

        //setListAdapter(mAdapter);
        
        lv = (ListView)findViewById(android.R.id.list);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new ListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				// TODO open the map activity to display the footprints on the map
				//int selectedFootprintID = (int) arg3;//这里的这个arg3对应的就是数据库中_id的值  
				
				//open the MAP to display the footprints for the selected trip
//				Intent intent = new Intent(FootprintListActivity.this, AMapActivity.class);
//				intent.putExtra("tripID", currentTripID);//这里的这个arg3对应的就是数据库中_id的值  
//				
//				startActivity(intent);
				
				
			}
        	
        });
        
        lv.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				
				selectedID = arg3;
				cursor.moveToPosition(arg2);
				String selectedFootprintName = String.valueOf(cursor.getInt(cursor.getColumnIndex("_id")));
				
				//Toast.makeText(getApplicationContext(), String.valueOf(arg2) + ";" + String.valueOf(arg3), Toast.LENGTH_LONG).show();
				
				AlertDialog.Builder builder = new AlertDialog.Builder(FootprintListActivity.this);
				builder.setTitle("删除：" + selectedFootprintName + " ?");
				builder.setIcon(android.R.drawable.ic_menu_delete);
				builder.setNegativeButton("取消", null);
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){

					@SuppressWarnings("deprecation")
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if(Footprint.deleteFootprintByID(dbm.getDb(), selectedID)){
							Toast.makeText(getApplicationContext(), "足迹" + String.valueOf(selectedID) + "已经被删除。", Toast.LENGTH_LONG).show();
							cursor.requery();
							mAdapter.notifyDataSetChanged();
						}
					}
					
				});
				
				builder.create().show();
				
				return true;
			}
        	
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.footprint_list, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	switch(item.getItemId()){
    	case R.id.action_settings:  
//    		Location l = aMap.getMyLocation();
//    		double lat = l.getLatitude();
//    		double lon = l.getLongitude();
//    		Toast.makeText(this,
//    		"Lat: " + String.valueOf(lat) + "Long: " + String.valueOf(lon), Toast.LENGTH_LONG).show();
    		return true;
    	case R.id.action_check_map:
			Intent intent = new Intent(FootprintListActivity.this, AMapMarkersActivity.class);
			intent.putExtra("tripID", currentTripID);  
			
			startActivity(intent);
    	    return true;
    	    
    	default:
    		return super.onMenuItemSelected(featureId, item);
    	}
	}
}
