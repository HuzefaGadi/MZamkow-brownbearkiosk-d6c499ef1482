package com.huzefagadi.brownbear;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "MyDBName.db";
	public static final String TABLE_NAME = "Mobilenumbers";
	public static final String COLUMN_MOBILENUMBER = "mobilenumber";



	private HashMap hp;

	public DBHelper(Context context)
	{
		super(context, DATABASE_NAME , null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(
				"create table "+TABLE_NAME+ "("+COLUMN_MOBILENUMBER+" text primary key)"
				);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS contacts");
		onCreate(db);
	}

	public int insertContact  (String phone)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(COLUMN_MOBILENUMBER, phone);
		try {
			db.insertOrThrow(TABLE_NAME, null, contentValues);
		} catch (SQLiteConstraintException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("ERROR");
			return 1;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return 2;
		}
		return 0;
	}
	/*public Cursor getData(int id){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor res =  db.rawQuery( "select * from contacts where id="+id+"", null );
		return res;
	}*/
	public int numberOfRows(){
		SQLiteDatabase db = this.getReadableDatabase();
		int numRows = (int) DatabaseUtils.queryNumEntries(db,TABLE_NAME);
		return numRows;
	}
	/*public boolean updateContact (Integer id, String name, String phone, String email, String street,String place)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("name", name);
		contentValues.put("phone", phone);
		contentValues.put("email", email);
		contentValues.put("street", street);
		contentValues.put("place", place);
		db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
		return true;
	}*/

	public Integer deleteAllContact ()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		return db.delete(TABLE_NAME, 
				"1", 
				null);
	}
	public ArrayList getAllCotacts()
	{
		ArrayList array_list = new ArrayList();
		//hp = new HashMap();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor res =  db.rawQuery( "select * from "+TABLE_NAME, null );
		res.moveToFirst();
		while(res.isAfterLast() == false){
			array_list.add(res.getString(res.getColumnIndex(COLUMN_MOBILENUMBER)));
			res.moveToNext();
		}
		return array_list;
	}
}