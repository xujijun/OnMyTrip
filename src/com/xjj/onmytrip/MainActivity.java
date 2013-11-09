package com.xjj.onmytrip;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends FragmentActivity{

	TextView textViewCurrentTripInfo;
	Button buttonSearchHistory;
	Button buttonGoToMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		textViewCurrentTripInfo = (TextView) findViewById(R.id.textViewCurrentTripInfo);
		
		buttonSearchHistory = (Button) findViewById(R.id.buttonSearchHistory);
		buttonSearchHistory.setOnClickListener(new OnClickListener() { //查询历史
			@Override
			public void onClick(View v) {

			}
		});

		buttonGoToMap = (Button) findViewById(R.id.buttonGoToMap);
		buttonGoToMap.setOnClickListener(new OnClickListener() { //打开地图
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, MapActivity.class));
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
