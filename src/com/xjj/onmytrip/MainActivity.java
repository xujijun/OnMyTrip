package com.xjj.onmytrip;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.xjj.onmytrip.db.DBManager;
import com.xjj.onmytrip.model.Trip;
import com.xjj.onmytrip.model.User;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
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
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity{

	TextView textViewCurrentTripInfo;
	Button buttonSearchHistory;
	Button buttonGoToMap;
	EditText EditTextNewTripName;
	
	String currentTripName;
	String currentUserID;
	String currentUserName;
	
	User currentUser;
	Trip currentTrip;
	
	DBManager dbm;
	SharedPreferences pref;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		
		//initialize database access
		dbm = new DBManager(MainActivity.this);
		
		//initialize variables
		pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		currentTripName = pref.getString("currentTripName", "default_TripName");
		currentUserID = pref.getString("currentUserID", "default_userID");
		currentUserName = pref.getString("currentUserName", "default_userName");
		
		currentUser = User.findUserByID(dbm.getDb(), currentUserID);
		currentTrip = Trip.findCurrentTripByUserID(dbm.getDb(), currentUserID);
		
		textViewCurrentTripInfo = (TextView) findViewById(R.id.textViewCurrentTripInfo);
		textViewCurrentTripInfo.setText("当前行程：" + currentTripName);
		
		
		buttonSearchHistory = (Button) findViewById(R.id.buttonSearchHistory);
		buttonSearchHistory.setOnClickListener(new OnClickListener() { //查询历史
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, TripListActivity.class));
			}
		});

		buttonGoToMap = (Button) findViewById(R.id.buttonGoToMap);
		buttonGoToMap.setOnClickListener(new OnClickListener() { //打开地图
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, MapActivity.class));
			}
		});

		
		
		//test... new user register:
//		User u = new User();
//		u.setUserID("MyNewName6");
//		u.setNickName("My Nick Name");
//		u.setPassword("My Password");
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
//		u.setRegisterDate(sdf.format(new Date()));
//		if(currentTrip!=null)
//			u.setCurrentTripID(currentTrip.getId());
//		
//		if(!u.saveUser(dbm.getDb())){
//			Toast.makeText(getApplicationContext(), "用户名已经存在。", Toast.LENGTH_LONG).show();
//		}else{
//			currentUserID = u.getUserID();
//			
//			//Save into preference
//			Editor editor = pref.edit();
//			editor.putString("currentUserID", currentUserID);
//			editor.commit();
//		}
//		
//		currentUser = User.findUserByID(dbm.getDb(), u.getUserID());
		
		updateView();
		//textViewCurrentTripInfo.append("；当前用户：" + u2.toString());
		//end of test
		
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

				//initialize new trip info
				Trip t = new Trip();
				t.setTripName(newTripName);
				t.setUserID(currentUserID);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
				t.setStartTime(sdf.format(new Date()));

				//Save into database
				if(t.saveTrip(dbm.getDb())){
					int id = Trip.getMaxTripID(dbm.getDb()); //if insert into db succeeded, retrieve the new trip id to be used.
					if (id !=-1){
						t.setId(id);
						//currentTrip.cloneFrom(t);
						currentTrip = t;
						
						currentTripName = newTripName;
						
						//Save into preference
						Editor editor = pref.edit();
						editor.putString("currentTripID", String.valueOf(id));
						editor.commit();
						
						currentUser.setCurrentTripID(currentTrip.getId());
						currentUser.saveCurrentTripID(dbm.getDb()); //update the current user's current trip id
						
						updateView();
					}else{
						Toast.makeText(getApplicationContext(), "读取Trip ID时发生错误！", Toast.LENGTH_LONG).show();
					}
					
				}else{
					Toast.makeText(getApplicationContext(), "存储行程信息到数据库时发生错误。", Toast.LENGTH_LONG).show();
				}
				
			}
		});
		
		builder.create().show();
	}

	private void updateView(){
		textViewCurrentTripInfo.setText("当前行程：" + currentTripName + "；当前用户：" + currentUser.toString());
	}
	
}
