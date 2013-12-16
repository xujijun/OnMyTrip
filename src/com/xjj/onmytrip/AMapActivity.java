package com.xjj.onmytrip;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.MyTrafficStyle;
import com.xjj.onmytrip.db.DBManager;
import com.xjj.onmytrip.model.Footprint;
import com.xjj.onmytrip.model.Trip;

/**
 * AMapV2地图中简单介绍显示定位小蓝点
 */
public class AMapActivity extends Activity implements LocationSource,
		AMapLocationListener {
	private AMap aMap;
	private MapView mapView;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;

	TextView textViewMessage;
	SharedPreferences pref;

	int currentTripID;
	Trip currentTrip;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_amap);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);//设置返回按钮
		
		textViewMessage = (TextView) findViewById(R.id.textViewAMapMessage);
		textViewMessage.setText("高德地图");
		
		pref = PreferenceManager.getDefaultSharedPreferences(AMapActivity.this);
		currentTripID = pref.getInt("currentTripID", -1);
		
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
		
        currentTrip = Trip.findTripByID(this, currentTripID);

	}


	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
		
		MyTrafficStyle myTrafficStyle = new MyTrafficStyle();
		myTrafficStyle.setSeriousCongestedColor(Color.rgb(252, 90, 80));
		myTrafficStyle.setCongestedColor(Color.rgb(248, 184, 71));
		myTrafficStyle.setSlowColor(Color.rgb(242, 242, 236));
		myTrafficStyle.setSmoothColor(Color.rgb(38, 254, 31));
		aMap.setMyTrafficStyle(myTrafficStyle);
	}

	/**
	 * 设置一些amap的属性
	 */
	private void setUpMap() {
		// 自定义系统定位小蓝点
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_marker));// 设置小蓝点的图标
		myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
		// myLocationStyle.radiusFillColor(color)//设置圆形的填充颜色
		// myLocationStyle.anchor(int,int)//设置小蓝点的锚点
		myLocationStyle.strokeWidth(5);// 设置圆形的边框粗细
		aMap.setMyLocationStyle(myLocationStyle);
		//aMap.setMyLocationRotateAngle(180);
		
			aMap.setLocationSource(this);// 设置定位监听
		    aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		    aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		
		aMap.getUiSettings().setScaleControlsEnabled(true);//设置比例尺
		aMap.getUiSettings().setCompassEnabled(true);
		
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		deactivate();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	/**
	 * 此方法已经废弃
	 */
	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	/**
	 * 定位成功后回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		if (mListener != null && aLocation != null) {
			mListener.onLocationChanged(aLocation);// 显示系统小蓝点
			float bearing = aMap.getCameraPosition().bearing;
			aMap.setMyLocationRotateAngle(bearing);// 设置小蓝点旋转角度
		}
		
		if(currentTrip!=null)
			this.textViewMessage.setText("当前行程：" + currentTrip.getTripName() + "。");
		this.textViewMessage.append("位置更新！");
	}

	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			/*
			 * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
			 * API定位采用GPS和网络混合定位方式
			 * ，第一个参数是定位provider，第二个参数时间最短是5000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
			 */
			mAMapLocationManager.requestLocationUpdates(
					LocationProviderProxy.AMapNetwork, 5000, 10, this);
		}
	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.map, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	switch(item.getItemId()){
    	case R.id.action_settings:  
//    		Location l = aMap.getMyLocation();
//    		double lat = l.getLatitude();
//    		double lon = l.getLongitude();
//    		Toast.makeText(this, "Lat: " + String.valueOf(lat) + "Long: " + String.valueOf(lon), Toast.LENGTH_LONG).show();
    		break;
    		
    	case R.id.action_my_location:	//显示我的位置
    		if (aMap!=null) {
                String msg = "我的位置：" + aMap.getMyLocation();
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                textViewMessage.setText(msg);
            }
    		
    		break;
    		
    	case R.id.action_record_my_location: //记录我的位置
    		if (aMap!=null) {
    			Footprint fp = new Footprint(this);
    			Location myLoc = aMap.getMyLocation();
    			
    			if(myLoc == null){
    				String msg = "定位尚未成功，请稍后再试。" ;
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                    textViewMessage.setText(msg);
                    break;
    			}
    			
    			fp.setLocation(myLoc);
    			fp.setTripID(currentTripID);
    			fp.setAddress(myLoc.getExtras().getString("desc"));
    			
    			if(fp.saveFootprint()){
    				Toast.makeText(getApplicationContext(), "成功保存了当前位置。"+ myLoc, Toast.LENGTH_LONG).show();
    			}else{
    				Toast.makeText(getApplicationContext(), "当前位置保存失败！", Toast.LENGTH_LONG).show();
    			}
    		}
    		
    		break;
    	
    	case R.id.action_search_history:
    		startActivity(new Intent(AMapActivity.this, TripListActivity.class));
    		break;
    	    
    	case R.id.action_map_type_normal:
    		if(aMap != null)
    			aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
    		break;
    	 
    	case R.id.action_map_type_satellite:
    		if(aMap != null)
    			aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 卫星地图模式
    		break;
    		
    	case R.id.action_map_type_traffic:
    		if(aMap != null)
    			aMap.setTrafficEnabled(!aMap.isTrafficEnabled());// 显示实时交通状况
    		break;
    		
    	default:
    		//Do Nothing;
    		break;
    	}
		return super.onMenuItemSelected(featureId, item);

	}
	
}
