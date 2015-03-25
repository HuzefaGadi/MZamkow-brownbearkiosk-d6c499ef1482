package com.huzefagadi.brownbear;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.huzefagadi.brownbear.beans.PhotoResponse;
import com.huzefagadi.brownbear.fragments.ImagePagerFragment;
import com.nostra13.universalimageloader.utils.L;
 


public class MainActivity extends FragmentActivity {
	private static final String TEST_FILE_NAME = "Universal Image Loader @#&=+-_.,!()~'%20.png";
	 
	AlertDialog alertDialog;
	SharedPreferences sharedpreferences;
	boolean exit=false;
	Fragment fr;
	String tag;
	int titleRes;
	private AlarmManager alarmMgr;
	private PendingIntent alarmIntent;
	Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	/*	requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		WindowManager manager = ((WindowManager) getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE));
		WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
		localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
		localLayoutParams.gravity = Gravity.TOP;
		localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
				// this is to enable the notification to recieve touch events
				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
				// Draws over status bar
				WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		localLayoutParams.height = (int) (50 * getResources()
				.getDisplayMetrics().scaledDensity);
		localLayoutParams.format = PixelFormat.TRANSPARENT;
		customViewGroup view = new customViewGroup(this);
		manager.addView(view, localLayoutParams);*/
		setContentView(R.layout.activity_main);
		
		context = getApplicationContext();
		 
	    
	    
		alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, PhotoReceiver.class);
		alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

		// Set the alarm to start at 8:30 a.m.
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
		        1000 * 60 * 3, alarmIntent);
		
		
		
		

		sharedpreferences= getSharedPreferences("HUZEFA", Context.MODE_PRIVATE);		
		 
		File testImageOnSdCard = new File("/mnt/sdcard", TEST_FILE_NAME);
		if (!testImageOnSdCard.exists()) {
			copyTestImageToSdCard(testImageOnSdCard);
		}
		 
		
		
		if (savedInstanceState != null) {
	        // Restore value of members from saved state
			 
	        
	    }
		KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
		KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);      
		lock.disableKeyguard();
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.prompt, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);
		final EditText userInput = (EditText) promptsView.findViewById(R.id.editText1);
		// set dialog message
		alertDialogBuilder.setCancelable(false).setPositiveButton("OK",	new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// get user input and set it to result
				// edit text
				String userPassword=userInput.getText().toString();
				String password = sharedpreferences.getString("password", "000000");
				if(userPassword.equals(password))
				{
					if(exit)
					{
						finish();
					}
					else
					{
						Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
						startActivity(intent);
						finish();
					}

				}
				userInput.setText("");
			}
		})
		.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				userInput.setText("");
				dialog.cancel();
			}
		});

		// create alert dialog
		alertDialog = alertDialogBuilder.create();
		tag = ImagePagerFragment.class.getSimpleName();
		fr = getSupportFragmentManager().findFragmentByTag(tag);
		if (fr == null) {
			fr = new ImagePagerFragment();
			fr.setArguments(getIntent().getExtras());
		}
		titleRes = R.string.ac_name_image_pager;
		setTitle(titleRes);
		getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fr, tag).commit();

		 




	}
	
	private void copyTestImageToSdCard(final File testImageOnSdCard) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					InputStream is = getAssets().open(TEST_FILE_NAME);
					FileOutputStream fos = new FileOutputStream(testImageOnSdCard);
					byte[] buffer = new byte[8192];
					int read;
					try {
						while ((read = is.read(buffer)) != -1) {
							fos.write(buffer, 0, read);
						}
					} finally {
						fos.flush();
						fos.close();
						is.close();
					}
				} catch (IOException e) {
					L.w("Can't copy test image onto SD card");
				}
			}
		}).start();
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		 
		super.onSaveInstanceState(outState);
	};
	
		@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(!exit)
			startActivity(getIntent().addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));

	}
	
	public void showToast(String message)
	{
		Toast toast = Toast.makeText(getApplicationContext(), message , Toast.LENGTH_SHORT);
		LinearLayout linearLayout = (LinearLayout) toast.getView();
	    TextView messageTextView = (TextView) linearLayout.getChildAt(0);
	    messageTextView.setTextSize(35);
	    toast.show();
	}
 
	@Override
	public void onBackPressed() {
		// Do Here what ever you want do on back press;
		System.out.println("back pressed");
	}
	public void onAttachedToWindow() {
		// TODO Auto-generated method stub
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);

		super.onAttachedToWindow();
	}
	public class customViewGroup extends ViewGroup {

		public customViewGroup(Context context) {
			super(context);
		}


		@Override
		protected void onLayout(boolean changed, int l, int t, int r, int b) {
		}

		@Override
		public boolean onInterceptTouchEvent(MotionEvent ev) {
			Log.v("customViewGroup", "**********Intercepted");
			return true;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			// get prompts.xml view

			exit=false;
			// show it
			alertDialog.show();
			return true;
		} 
		else if (id == R.id.Exit) {
			// get prompts.xml view

			exit = true;
			// show it
			alertDialog.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
