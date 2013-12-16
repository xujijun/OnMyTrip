package com.xjj.onmytrip;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.xjj.onmytrip.db.DBManager;
import com.xjj.onmytrip.model.Footprint;

public class MapActivity extends FragmentActivity
	implements 
	ConnectionCallbacks,
    OnConnectionFailedListener,
    LocationListener,
    OnMyLocationButtonClickListener {

	TextView textViewMessage;
	SharedPreferences pref;

	int currentTripID;

		
	//Note that this may be null if the Google Play services APK is not available.
    private GoogleMap mMap;
    
    private LocationClient mLocationClient;
    
    // These settings are the same as the settings for the map. They will in fact give you updates
    // at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);//设置返回按钮
		
		textViewMessage = (TextView) findViewById(R.id.textViewMessage);
		
		pref = PreferenceManager.getDefaultSharedPreferences(MapActivity.this);
		currentTripID = pref.getInt("currentTripID", -1);

        //setUpMapIfNeeded(); //done in onResume
	}
	
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        setUpLocationClientIfNeeded();
        mLocationClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLocationClient != null) {
            mLocationClient.disconnect();
        }
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	
	
    @Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	
    	switch(item.getItemId()){
    	case R.id.action_settings:  //启动设置Activity
    		//open settings
    		//startActivity(new Intent(MapActivity.this, SettingsActivity.class));

    		return true;
    	
    	case R.id.action_my_location:	//移到我的位置
    		if (mLocationClient != null && mLocationClient.isConnected()) {
                String msg = "我的位置:" + mLocationClient.getLastLocation();
                //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                textViewMessage.setText(msg);
            }
    		
    		return true;
    		
    	case R.id.action_record_my_location: //记录我的位置
    		if (mLocationClient != null && mLocationClient.isConnected()) {
    			Footprint fp = new Footprint(this);
    			fp.setLocation(mLocationClient.getLastLocation());
    			fp.setTripID(currentTripID);
    			
    			if(fp.saveFootprint()){
    				Toast.makeText(getApplicationContext(), "成功保存了当前位置。", Toast.LENGTH_LONG).show();
    			}else{
    				Toast.makeText(getApplicationContext(), "当前位置保存失败！", Toast.LENGTH_LONG).show();
    			}
    		}
    		
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
	}

	private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(23, 113)).title("Marker"));
        
        mMap.getUiSettings().setCompassEnabled(true);
 		mMap.setMyLocationEnabled(true);
		mMap.setOnMyLocationButtonClickListener(this);
    }

    private void setUpLocationClientIfNeeded() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(
                    getApplicationContext(),
                    this,  // ConnectionCallbacks
                    this); // OnConnectionFailedListener
        }
    }
    
	@Override
	public boolean onMyLocationButtonClick() {

		Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
		
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
		return false;
	}

	@Override
	public void onLocationChanged(Location arg0) {
		textViewMessage.setText("Location = " + arg0);
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Toast.makeText(this, "链接错误：" + arg0.toString(), Toast.LENGTH_SHORT).show();
	}

	//Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
	@Override
	public void onConnected(Bundle arg0) {
		 mLocationClient.requestLocationUpdates(
	                REQUEST,
	                this);  // LocationListener
	}

	@Override
	public void onDisconnected() {
		Toast.makeText(this, "链接断开", Toast.LENGTH_SHORT).show();
	}
}
