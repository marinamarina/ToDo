package com.todosapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.todosapp.data.TodoContentProvider;
import com.todosapp.data.TodosTable;

/*
 * TaskCreateEditActivity allows user to enter a new task item 
 * or to change an existing
 */
public class TodoSummaryFragment extends Activity implements OnClickListener {
	private TextView descText;
	private Uri taskUri;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.todo_view);

		descText = (TextView) findViewById(R.id.todo_view_desc);

		Bundle extras = getIntent().getExtras();
		// check for extra data passed from the other activity
		if (extras != null) {
			taskUri = extras
					.getParcelable(TodoContentProvider.CONTENT_ITEM_TYPE);
			fillData(taskUri);
		}
	}
	/** 
	 * Filling the data for a single todo from database
	 */
	@SuppressWarnings("unchecked")
	private void fillData(Uri uri) {
		String[] projection = { TodosTable.COLUMN_DESCRIPTION, TodosTable.COLUMN_DUEDATE, TodosTable.COLUMN_PRIORITY, TodosTable.COLUMN_STATUS };
		Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
		String currentPriority;
		String currentStatus;
		ArrayAdapter<String> priorityAdapter;
		ArrayAdapter<String> statusAdapter;
		int priorityDropdownPosition;
		int statusDropdownPosition;

		if (cursor != null) {
			cursor.moveToFirst();

			// Fill the form with the current data from the database when user edits a single todo 
			//description view
			descText.setText(cursor.getString(cursor.getColumnIndexOrThrow(TodosTable.COLUMN_DESCRIPTION)));

			// Close the cursor
			cursor.close();
		}
	}


	
	/**
	 * On Click Listeners 
	 */
	@Override
	public void onClick(View v) {

	
	}
}

