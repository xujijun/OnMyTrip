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
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
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
import com.xjj.onmytrip.model.Footprint;
import com.xjj.onmytrip.model.Trip;

public class AMapMarkersActivity extends Activity implements OnMapLoadedListener, OnMarkerClickListener, OnInfoWindowClickListener, InfoWindowAdapter {
	
	private AMap aMap;
	private MapView mapView;
	
	TextView textViewMessage;
	SharedPreferences pref;
	Cursor cursor;

	int currentTripID;
	Trip currentTrip;

	LatLngBounds bounds;
	int numberOfFootprints = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_amap_markers);
		
		textViewMessage = (TextView) findViewById(R.id.textViewAMapMakersMessage);
		textViewMessage.setText("高德地图");
		
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
		
		Intent intent = getIntent();  
        int temp =(int) intent.getLongExtra("tripID", -1); //Note: long
        if(temp!=-1){
        	currentTripID = temp;
        	cursor = Footprint.getAllFootprints(this, String.valueOf(currentTripID));
        	drawMarkers(cursor);
        }
        
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
		aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
	}
	
	/**
	 * 画标记点
	 * @param c
	 */
	public void drawMarkers(Cursor c) {
		if(!c.moveToLast()){
			numberOfFootprints = 0;
			return;
		}
		
		numberOfFootprints = 0;
		Builder builder = LatLngBounds.builder();
		
		do{
			numberOfFootprints++;
			MarkerOptions mo = new MarkerOptions();
			LatLng latlng = new LatLng(Double.parseDouble(c.getString(c.getColumnIndex("latitude"))), Double.parseDouble(c.getString(c.getColumnIndex("longitude"))));
			mo.position(latlng);
			mo.title(c.getString(c.getColumnIndex("date_time")));
			mo.snippet(c.getString(c.getColumnIndex("address")));
			mo.perspective(true);//.draggable(true);
			mo.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
			//mo.icon(BitmapDescriptorFactory.fromBitmap(getViewBitmap(getView(String.valueOf(i)))));
			
			mo.icon(BitmapDescriptorFactory.fromBitmap(getBitMap(String.valueOf(numberOfFootprints))));//同时设置标记上面的数字
			
			aMap.addMarker(mo);
			
			builder.include(latlng);
		}while(c.moveToPrevious());
	
		bounds = builder.build();
	}
	
	public Bitmap getBitMap(String text) {
		//Bitmap bitmap = BitmapDescriptorFactory.fromResource(R.drawable.arrow).getBitmap();
		
		@SuppressWarnings("deprecation")
		Bitmap bitmap = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN).getBitmap();
		
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight());
		Canvas canvas = new Canvas(bitmap);
		TextPaint textPaint = new TextPaint();
		textPaint.setTextSize(32f);
		textPaint.setColor(Color.BLUE);
		canvas.drawText(text, 16, 40, textPaint);// 设置bitmap上面的文字位置
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
		if(numberOfFootprints == 0){
			textViewMessage.append("；该行程还没有足迹记录。");
		}else{
			aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
			textViewMessage.append("；该行程记录总数：" + numberOfFootprints);
		}	
	}
	
	
	/**
	 * 对marker标注点点击响应事件
	 */
	@Override
	public boolean onMarkerClick(final Marker marker) {
		//textViewMessage.setText("你点击的是：" + marker.getSnippet());
		//Toast.makeText(getApplicationContext(), "你点击的是：" + marker.getSnippet(), Toast.LENGTH_LONG).show();
		
		marker.showInfoWindow();
		return false;
	}
	
	/**
	 * 监听自定义infowindow窗口的infocontents事件回调
	 */
	@Override
	public View getInfoContents(Marker marker) {
//		TextView v = new TextView(this);
//		v.setText(marker.getSnippet());
//		return v;
		//if (radioOption.getCheckedRadioButtonId() != R.id.custom_info_contents) {
			return null;
		//}
		//View infoContent = getLayoutInflater().inflate(
		//		R.layout.custom_info_contents, null);
		//render(marker, infoContent);
		//return infoContent;
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
				aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));

			}
			break;
		default:
			break;
		}
		
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onInfoWindowClick(Marker arg0) {
		
	}

	@Override
	public View getInfoWindow(Marker marker) {
		TextView v = new TextView(this);
		v.setText(marker.getTitle() + "\n" + marker.getSnippet());
		return v;
		//return null;
	}
	
	

}
