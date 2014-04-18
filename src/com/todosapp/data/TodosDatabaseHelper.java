package com.todosapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TodosDatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "tasks.db";
	private static final int DATABASE_VERSION = 3;

	/**
	 * Constructor 
	 */
	public TodosDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Create the SQLite database 
	 */
	@Override
	public void onCreate(SQLiteDatabase database) {
		TodosTable.onCreate(database);
	}

	/**
	 * Upgrade the SQLite database, 
	 * e.g. change the database structure (fields) and the version number 
	 */
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		TodosTable.onUpgrade(database, oldVersion, newVersion);
	}
}