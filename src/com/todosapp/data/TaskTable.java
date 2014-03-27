package com.todosapp.data;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TaskTable {
	// Database table
	public static final String TABLE_TASK = "task";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_DUEDATE = "due_date";
	public static final String COLUMN_PRIORITY = "priority";
	public static final String COLUMN_STATUS = "status";
	
	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table " 
	    + TABLE_TASK
	    + "(" 
	    + COLUMN_ID + " integer primary key autoincrement, " 
	    + COLUMN_DESCRIPTION
	    + " text not null, "
	    + COLUMN_DUEDATE
	    + " long not null, "
	    + COLUMN_PRIORITY
	    + " text not null, "
	    + COLUMN_STATUS
	    + " text not null"
	    + ");";
	
	public static void onCreate(SQLiteDatabase database) {
	  Log.w("CAT", "creating");
	  database.execSQL(DATABASE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
	    int newVersion) {
	  Log.w(TaskTable.class.getName(), "Upgrading database from version "
	      + oldVersion + " to " + newVersion
	      + ", which will destroy all old data");
	  database.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
	  onCreate(database);
	}
}

