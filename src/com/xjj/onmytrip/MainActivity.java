package com.xjj.onmytrip;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xjj.onmytrip.model.Trip;
import com.xjj.onmytrip.model.User;

public class MainActivity extends FragmentActivity{

	TextView textViewCurrentTripInfo;
	Button buttonSearchHistory;
	Button buttonGoToMap;
	EditText EditTextNewTripName;
	
	String currentTripName;
	int currentTripID;
	String currentUserID;
	String currentUserName;
	
	User currentUser;
	Trip currentTrip;
	
	SharedPreferences pref;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//initialize variables
		pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		//currentTripName = pref.getString("currentTripName", "default_TripName");
		currentTripID = pref.getInt("currentTripID", -1);
		currentUserID = pref.getString("currentUserID", "default_userID");
		//currentUserName = pref.getString("currentUserName", "default_userName");
		
		currentUser = User.findUserByID(this, currentUserID);
		currentTrip = Trip.findCurrentTripByUserID(this, currentUserID);
		
		
		textViewCurrentTripInfo = (TextView) findViewById(R.id.textViewCurrentTripInfo);
		//textViewCurrentTripInfo.setText("当前行程：" + currentTripName);
		
		
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
				//startActivity(new Intent(MainActivity.this, MapActivity.class)); //Google地图
				startActivity(new Intent(MainActivity.this, AMapActivity.class)); //高德地图
			}
		});

		
		
		//test... new user register:
//		User u = new User();
//		u.setUserID("xujijun");
//		u.setNickName("许");
//		u.setPassword("Gle250532");
//
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
		//menu.add(1, 1, 0, "退出");
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	switch(item.getItemId()){
    	case R.id.action_new_trip:  //Open a dialog to enter new trip name
    		newTripNameDialog();
    		return true;
    		
    	case R.id.action_check_map:
			Intent intent = new Intent(MainActivity.this, AMapMarkersActivity.class);
			intent.putExtra("tripID", (long)currentTripID);  
			
			startActivity(intent);
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
				Trip t = new Trip(MainActivity.this);
				t.setTripName(newTripName);
				t.setUserID(currentUserID);


				//Save into database
				if(t.saveTrip()){
					int id = Trip.getMaxTripID(MainActivity.this); //if insert into db succeeded, retrieve the new trip id to be used.
					if (id !=-1){
						t.setId(id);
						//currentTrip.cloneFrom(t);
						currentTrip = t;
						
						currentTripName = newTripName;
						currentTripID = id;
						
						//Save into preference
						Editor editor = pref.edit();
						editor.putInt("currentTripID", id);
						editor.commit();
						
						currentUser.setCurrentTripID(currentTrip.getId());
						currentUser.saveCurrentTripID(); //update the current user's current trip id
						
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
		String info = "";
		
		if(currentTrip != null)
			info += "当前行程：" + currentTrip.getTripName() + "；\n";
		
		if(currentUser != null)
			info += "当前用户：" + currentUser.toString();
			
		textViewCurrentTripInfo.setText(info);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			AlertDialog.Builder ab=new AlertDialog.Builder(this);
			  ab.setTitle("退出提示");//设定标题
			  ab.setMessage("您真的要退出吗?");//设定内容
			  ab.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
			  ab.setPositiveButton("确定", new DialogInterface.OnClickListener()
			  {
	         	@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
	         		//dialog.cancel();
					//exit();
				}
			  });
			  ab.setNegativeButton("取消", null);
			  ab.show();
			  return true;//已经处理了用户的按键这样就不会退出
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	
	
}
