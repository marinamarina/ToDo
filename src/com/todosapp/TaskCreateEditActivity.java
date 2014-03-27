package com.todosapp;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.todosapp.DatePickerFragment;

import com.todosapp.R;
import com.todosapp.contentprovider.MyTaskContentProvider;
import com.todosapp.contentprovider.TaskTable;

/*
 * TaskCreateEditActivity allows user to enter a new task item 
 * or to change an existing
 */
public class TaskCreateEditActivity extends Activity {
  private EditText descText;
  private TextView dateView;
  private Spinner priorityDropdown;
  private Spinner statusDropdown;

  private Uri taskUri;

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    setContentView(R.layout.task_edit);

    descText = (EditText) findViewById(R.id.task_edit_description);
    dateView = (TextView) findViewById(R.id.task_edit_time);
    priorityDropdown = (Spinner) findViewById(R.id.task_edit_priority);
	statusDropdown = (Spinner) findViewById(R.id.task_edit_status);
    Button confirmButton = (Button) findViewById(R.id.task_edit_button);
    
    dateView.setOnClickListener(new OnClickListener() {	
		@Override
		public void onClick(View v) {
	        	DialogFragment newFragment = new DatePickerFragment();
	        	newFragment.show(getFragmentManager(), "datePicker");			
		}
	});

    Bundle extras = getIntent().getExtras();
    // check for extra data passed from the other activity
    if (extras != null) {
      taskUri = extras
          .getParcelable(MyTaskContentProvider.CONTENT_ITEM_TYPE);
      fillData(taskUri);
    }

    confirmButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        if (TextUtils.isEmpty(descText.getText().toString())) {
          makeToast();
        } else {
          setResult(RESULT_OK);
          finish();
        }
      }

    });
  }

  private void fillData(Uri uri) {
    String[] projection = { TaskTable.COLUMN_DESCRIPTION, TaskTable.COLUMN_PRIORITY, TaskTable.COLUMN_STATUS };
    Cursor cursor = getContentResolver().query(uri, projection, null, null,
        null);
    if (cursor != null) {
      cursor.moveToFirst();
      descText.setText(cursor.getString(cursor
          .getColumnIndexOrThrow(TaskTable.COLUMN_DESCRIPTION)));
      // Always close the cursor
      cursor.close();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    saveState();
  }


  private void saveState() {
    String description = descText.getText().toString();
    String date = dateView.getText().toString();
    String priority = (String) priorityDropdown.getSelectedItem();
	String status = (String) statusDropdown.getSelectedItem();

    // Only save if description is available
    if (description.length() == 0) {
      return;
    }

    ContentValues values = new ContentValues();
    values.put(TaskTable.COLUMN_DESCRIPTION, description);
    values.put(TaskTable.COLUMN_DUEDATE, date);
    values.put(TaskTable.COLUMN_PRIORITY, priority);
	values.put(TaskTable.COLUMN_STATUS, status);

    if (taskUri == null) {
      // New task
      taskUri = getContentResolver().insert(MyTaskContentProvider.CONTENT_URI, values);
    } else {
      // Update task
      getContentResolver().update(taskUri, values, null, null);
    }
  }

  private void makeToast() {
    Toast.makeText(TaskCreateEditActivity.this, "Description can't be empty",
        Toast.LENGTH_LONG).show();
  }
} 

