package com.huzefagadi.brownbear;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends Activity {

	EditText shopname,password,tagline;
	Editor editor;
	Button save,share;
	SharedPreferences sharedpreferences;
	DBHelper db ;
	File path;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_settings);
		sharedpreferences= getSharedPreferences("HUZEFA", Context.MODE_PRIVATE);		
		editor = sharedpreferences.edit();
		db = new DBHelper(getApplicationContext());

		shopname = (EditText) findViewById(R.id.editText1);
		tagline = (EditText) findViewById(R.id.tagline);
		save = (Button) findViewById(R.id.button1);
		share = (Button) findViewById(R.id.share);
		password = (EditText) findViewById(R.id.password);

		shopname.setText(sharedpreferences.getString("shopname", ""));
		tagline.setText(sharedpreferences.getString("tagline", ""));
		save.setOnClickListener(new View.OnClickListener() {

			@SuppressLint("NewApi") @Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if(!password.getText().toString().isEmpty())
				{
					editor.putString("password", password.getText().toString());
				}
				editor.putString("shopname", shopname.getText().toString());
				editor.putString("tagline", tagline.getText().toString());
				
				editor.commit();
				Intent intent = new Intent(getApplicationContext(),MainActivity.class);
				startActivity(intent);
				finish();
			}

		});
		path = Environment.getExternalStorageDirectory();
		path = new File(path,"Data");
		share.setOnClickListener(new View.OnClickListener() {

			@SuppressLint("NewApi") @Override
			public void onClick(View arg0) {

				if(!path.exists())
				{
					path.mkdir();
				}
				// TODO Auto-generated method stub
				StringBuilder stringBuilder = new StringBuilder();
				ArrayList list = db.getAllCotacts();
				for(Object number: list)
				{
					stringBuilder.append(number).append(",");
				}
				try {
					File myFile = new File(path,"PhoneNumumbers.txt");
					myFile.createNewFile();
					FileOutputStream fOut = new FileOutputStream(myFile);
					OutputStreamWriter myOutWriter = 
							new OutputStreamWriter(fOut);

					myOutWriter.append(stringBuilder.toString());
					myOutWriter.close();
					fOut.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Intent emailIntent = new Intent(Intent.ACTION_SEND);
				emailIntent.setType("text/plain");
				emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"email@example.com"});
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "subject here");
				emailIntent.putExtra(Intent.EXTRA_TEXT, "body text");
				 
				 
				File file = new File(path, "PhoneNumumbers.txt");
				if (!file.exists() || !file.canRead()) {
					return;
				}
				Uri uri = Uri.fromFile(file);
				emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
				startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
				db.deleteAllContact();

			}

		});





	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
