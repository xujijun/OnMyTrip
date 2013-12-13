package com.xjj.onmytrip;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.LatLngBounds.Builder;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.xjj.onmytrip.db.DBManager;
import com.xjj.onmytrip.model.Footprint;
import com.xjj.onmytrip.model.Trip;

public class AMapMarkersActivity extends Activity implements OnMapLoadedListener, OnMarkerClickListener, OnInfoWindowClickListener {
	
	private AMap aMap;
	private MapView mapView;
	
	TextView textViewMessage;
    private DBManager dbm;
	SharedPreferences pref;
	Cursor cursor;

	int currentTripID;
	Trip currentTrip;

	LatLngBounds bounds;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_amap_markers);
		
		textViewMessage = (TextView) findViewById(R.id.textViewAMapMakersMessage);
		textViewMessage.setText("高德地图");
		
		dbm = new DBManager(AMapMarkersActivity.this);
		
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
		
		Intent intent = getIntent();  
        int temp =(int) intent.getLongExtra("tripID", -1); //Note: long
        if(temp!=-1){
        	currentTripID = temp;
        	cursor = Footprint.getAllFootprints(dbm.getDb(), String.valueOf(currentTripID));
        	drawMarkers(cursor);
        }
        
        currentTrip = Trip.findTripByID(dbm.getDb(), currentTripID);
	}
	
	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
	}
	
	/**
	 * 设置一些amap的属性
	 */
	private void setUpMap() {
		aMap.getUiSettings().setScaleControlsEnabled(true);//设置比例尺
		aMap.getUiSettings().setCompassEnabled(true);
		aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
		aMap.setOnMarkerClickListener(this); // 设置点击marker事件监听器
		aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器

	}
	
	/**
	 * 画标记点
	 * @param c
	 */
	public void drawMarkers(Cursor c) {
		if(!c.moveToLast()) return;
		
		int i=0;
		Builder builder = LatLngBounds.builder();
		
		do{
			i++;
			MarkerOptions mo = new MarkerOptions();
			LatLng latlng = new LatLng(Double.parseDouble(c.getString(c.getColumnIndex("latitude"))), Double.parseDouble(c.getString(c.getColumnIndex("longitude"))));
			mo.position(latlng);
			mo.title("第"+i);
			mo.snippet(c.getString(c.getColumnIndex("date_time")) + "，" + c.getString(c.getColumnIndex("address")));
			mo.perspective(true);//.draggable(true);
			//mo.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
			//mo.icon(BitmapDescriptorFactory.fromBitmap(getViewBitmap(getView(String.valueOf(i)))));
			mo.icon(BitmapDescriptorFactory.fromBitmap(getBitMap(String.valueOf(i))));
			
			aMap.addMarker(mo);
			
			builder.include(latlng);
		}while(c.moveToPrevious());
		
		bounds = builder.build();
	}
	
	public Bitmap getBitMap(String text) {
		Bitmap bitmap = BitmapDescriptorFactory.fromResource(
				R.drawable.custom_info_bubble).getBitmap();
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight());
		Canvas canvas = new Canvas(bitmap);
		TextPaint textPaint = new TextPaint();
		textPaint.setTextSize(32f);
		textPaint.setColor(Color.BLUE);
		canvas.drawText(text, 20, 30, textPaint);// 设置bitmap上面的文字位置
		return bitmap;
	}
	
	/**
	 * 监听amap地图加载成功事件回调
	 */
	@Override
	public void onMapLoaded() {
		// 设置所有maker显示在View中

		if(currentTrip!=null)
			textViewMessage.append("；行程名：" + currentTrip.getTripName());
		
		//aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10)); //10: padding
		aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
	}
	
	
	/**
	 * 对marker标注点点击响应事件
	 */
	@Override
	public boolean onMarkerClick(final Marker marker) {
		textViewMessage.setText("你点击的是：" + marker.getSnippet());
		//Toast.makeText(getApplicationContext(), "你点击的是：" + marker.getSnippet(), Toast.LENGTH_LONG).show();
		
		marker.showInfoWindow();
		return false;
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


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.amap_markers, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		
		switch (item.getItemId()) {
		/**
		 * 清空地图上所有已经标注的marker
		 */
		case R.id.action_clear_footprints:
			if (aMap != null) {
				aMap.clear();
			}
			break;
		/**
		 * 重新标注所有的marker
		 */
		case R.id.action_show_footprints:
			if (aMap != null) {
				aMap.clear();
				drawMarkers(cursor);
			}
			break;
		default:
			break;
		}
		
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onInfoWindowClick(Marker arg0) {
		// TODO Auto-generated method stub
		
	}
	
	

}
