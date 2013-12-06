package com.xjj.onmytrip;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity{

	TextView textViewCurrentTripInfo;
	Button buttonSearchHistory;
	Button buttonGoToMap;
	EditText EditTextNewTripName;
	String currentTripName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		currentTripName = pref.getString("currentTripName", "");
		
		textViewCurrentTripInfo = (TextView) findViewById(R.id.textViewCurrentTripInfo);
		textViewCurrentTripInfo.setText("当前行程：" + currentTripName);
		
		
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

	
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	switch(item.getItemId()){
    	case R.id.action_new_trip:  //Open a dialog to enter new trip name
    		newTripNameDialog();
    		return true;
    	default:
    		return super.onMenuItemSelected(featureId, item);
    	}
	}

	private void newTripNameDialog(){
		EditTextNewTripName = new EditText(MainActivity.this);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle("请输入新行程的名称：");
		builder.setIcon(android.R.drawable.ic_menu_compass);
		builder.setView(EditTextNewTripName);
		builder.setNegativeButton("取消", null);
		
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String newTripName = EditTextNewTripName.getText().toString();
				textViewCurrentTripInfo.setText("当前行程：" + newTripName);
				
				//TODO Save into preference and database
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
				Editor editor = pref.edit();
				editor.putString("currentTripName", newTripName);
				editor.commit();
				
			}
		});
		
		builder.create().show();

	}
	
}
