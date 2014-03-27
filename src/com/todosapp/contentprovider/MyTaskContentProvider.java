package com.todosapp.contentprovider;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class MyTaskContentProvider extends ContentProvider {

	  // database
	  private TaskDatabaseHelper database;

	  // constants used to distinguish two types of database operation
	  // performed by the application
	  // TASKS used to designate an operation which does not specify primary key
	  private static final int TASKS = 1;
	  // TASKS_ID used to designate an operation which does specify primary key
	  private static final int TASK_ID = 2;

	  private static final String AUTHORITY = "com.todosapp.contentprovider";

	  private static final String BASE_PATH = "tasks";
	  public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
	      + "/" + BASE_PATH);

	  public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
	      + "/tasks";
	  public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
	      + "/task";

	  private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	  static {
	    sURIMatcher.addURI(AUTHORITY, BASE_PATH, TASKS);
	    sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", TASK_ID);
	  }

	  @Override
	  public boolean onCreate() {
	    database = new TaskDatabaseHelper(getContext());
	    return true;
	  }

	  @Override
	  public Cursor query(Uri uri, String[] projection, String selection,
	      String[] selectionArgs, String sortOrder) {

	    // Uisng SQLiteQueryBuilder instead of query() method
	    SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

	    // Check if the caller has requested a column which does not exist
	    checkColumns(projection);

	    // Set the table
	    queryBuilder.setTables(TaskTable.TABLE_TASK);

	    int uriType = sURIMatcher.match(uri);
	    switch (uriType) {
	    case TASKS:
	      break;
	    case TASK_ID:
	      // Adding the ID to the original query
	      queryBuilder.appendWhere(TaskTable.COLUMN_ID + "="
	          + uri.getLastPathSegment());
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }

	    SQLiteDatabase db = database.getReadableDatabase();
	    Cursor cursor = queryBuilder.query(db, projection, selection,
	        selectionArgs, null, null, sortOrder);
	    // Make sure that potential listeners are notified
	    cursor.setNotificationUri(getContext().getContentResolver(), uri);

	    return cursor;
	  }

	  @Override
	  public String getType(Uri uri) {
	    return null;
	  }

	  @Override
	  public Uri insert(Uri uri, ContentValues values) {
	    int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = database.getWritableDatabase();
	    long id = 0;
	    switch (uriType) {
	    case TASKS:
	      id = sqlDB.insert(TaskTable.TABLE_TASK, null, values);
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return Uri.parse(BASE_PATH + "/" + id);
	    /*Uri newUri = ContentUris.withAppendedId(uri, id);
	    getContext().getContentResolver().notifyChange(newUri, null);
	    return newUri;*/
	  }

	  @Override
	  public int delete(Uri uri, String selection, String[] selectionArgs) {
	    int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = database.getWritableDatabase();
	    int rowsDeleted = 0;
	    switch (uriType) {
	    case TASKS:
	      rowsDeleted = sqlDB.delete(TaskTable.TABLE_TASK, selection,
	          selectionArgs);
	      break;
	    case TASK_ID:
	      String id = uri.getLastPathSegment();
	      if (TextUtils.isEmpty(selection)) {
	        rowsDeleted = sqlDB.delete(TaskTable.TABLE_TASK,
	            TaskTable.COLUMN_ID + "=" + id, 
	            null);
	      } else {
	        rowsDeleted = sqlDB.delete(TaskTable.TABLE_TASK,
	            TaskTable.COLUMN_ID + "=" + id 
	            + " and " + selection,
	            selectionArgs);
	      }
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsDeleted;
	  }

	  @Override
	  public int update(Uri uri, ContentValues values, String selection,
	      String[] selectionArgs) {

	    int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = database.getWritableDatabase();
	    int rowsUpdated = 0;
	    switch (uriType) {
	    case TASKS:
	      rowsUpdated = sqlDB.update(TaskTable.TABLE_TASK, 
	          values, 
	          selection,
	          selectionArgs);
	      break;
	    case TASK_ID:
	      String id = uri.getLastPathSegment();
	      if (TextUtils.isEmpty(selection)) {
	        rowsUpdated = sqlDB.update(TaskTable.TABLE_TASK, 
	            values,
	            TaskTable.COLUMN_ID + "=" + id, 
	            null);
	      } else {
	        rowsUpdated = sqlDB.update(TaskTable.TABLE_TASK, 
	            values,
	            TaskTable.COLUMN_ID + "=" + id 
	            + " and " 
	            + selection,
	            selectionArgs);
	      }
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsUpdated;
	  }

	  private void checkColumns(String[] projection) {
	    String[] available = { TaskTable.COLUMN_DESCRIPTION,
	        TaskTable.COLUMN_ID };
	    if (projection != null) {
	      HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
	      HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
	      // Check if all columns which are requested are available
	      if (!availableColumns.containsAll(requestedColumns)) {
	        throw new IllegalArgumentException("Unknown columns in projection");
	      }
	    }
	  }

}

