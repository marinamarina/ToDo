package com.todosapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.todosapp.DatePickerFragment;

import com.todosapp.R;
import com.todosapp.data.MyTaskContentProvider;
import com.todosapp.data.TaskTable;

/*
 * TaskCreateEditActivity allows user to enter a new task item 
 * or to change an existing
 */
public class TaskCreateEditActivity extends Activity implements OnClickListener, OnItemSelectedListener {
  private EditText descText;
  private TextView dateView;
  private Spinner priorityDropdown;
  private Spinner statusDropdown;

  private Uri taskUri;

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    setContentView(R.layout.task_edit);

    descText = (EditText) findViewById(R.id.todo_edit_description);
    dateView = (TextView) findViewById(R.id.todo_edit_time);
    priorityDropdown = (Spinner) findViewById(R.id.todo_edit_priority);
	statusDropdown = (Spinner) findViewById(R.id.todo_edit_status);
    Button confirmButton = (Button) findViewById(R.id.todo_edit_button);
    
    dateView.setOnClickListener(this);
    confirmButton.setOnClickListener(this);

    Bundle extras = getIntent().getExtras();
    // check for extra data passed from the other activity
    if (extras != null) {
      taskUri = extras
          .getParcelable(MyTaskContentProvider.CONTENT_ITEM_TYPE);
      fillData(taskUri);
    }

  }

  @SuppressWarnings("unchecked")
  private void fillData(Uri uri) {
    String[] projection = { TaskTable.COLUMN_DESCRIPTION, TaskTable.COLUMN_PRIORITY, TaskTable.COLUMN_STATUS };
    Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
    String currentPriority;
    String currentStatus;
    ArrayAdapter<String> priorityAdapter;
    ArrayAdapter<String> statusAdapter;
    int priorityDropdownPosition;
    int statusDropdownPosition;
    
    if (cursor != null) {
      cursor.moveToFirst();
      
      // Fill the form with the current data from the database 
      //description field
      descText.setText(cursor.getString(cursor.getColumnIndexOrThrow(TaskTable.COLUMN_DESCRIPTION)));
     
      //priority field   
      currentPriority = cursor.getString(cursor.getColumnIndexOrThrow(TaskTable.COLUMN_PRIORITY));   
      priorityAdapter = (ArrayAdapter<String>) priorityDropdown.getAdapter(); //cast to an ArrayAdapter
      priorityDropdownPosition = priorityAdapter.getPosition(currentPriority);
      priorityDropdown.setSelection(priorityDropdownPosition);
      
    //status field
      currentStatus = cursor.getString(cursor.getColumnIndexOrThrow(TaskTable.COLUMN_STATUS));      
      statusAdapter = (ArrayAdapter<String>) statusDropdown.getAdapter(); //cast to an ArrayAdapter
      statusDropdownPosition = statusAdapter.getPosition(currentStatus); //
      statusDropdown.setSelection(statusDropdownPosition);
      Log.w("LISI", String.valueOf(statusAdapter.toString() ));
      
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

    // Add simple validation
    if (description.length() == 0 ) {
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

  @SuppressLint("DefaultLocale")
private void makeToast(String field) {
    Toast.makeText(TaskCreateEditActivity.this, "Field " + field + " can't be empty",
        Toast.LENGTH_LONG).show();
  }
  /**
	* On Click Listeners 
    */
  @Override
  public void onClick(View v) {
	  switch(v.getId()) {
	  //Adding validation
	  	case R.id.todo_edit_button: 
	  		if (TextUtils.isEmpty(descText.getText().toString())) {
	  			makeToast("description");
	  		} else if( priorityDropdown.getSelectedItem().equals(getResources().getStringArray(R.array.priorities)[0])) {
	  			makeToast("priority");
	  		} else if( statusDropdown.getSelectedItem().equals(getResources().getStringArray(R.array.status)[0])) {
	  			makeToast("status");
	  		} else {
	  			setResult(RESULT_OK);
	  			finish();
	  		}
	  		break;
	  	case R.id.todo_cancel_button:
	  		this.finishThis();
	  		break;
	  	case R.id.todo_edit_time: 
	  		DialogFragment newFragment = new DatePickerFragment();
        	newFragment.show(getFragmentManager(), "datePicker");
        	break;
	  }
	
  }
private boolean finishThis() {
	this.finish();
	return true;
}
@Override
public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	// TODO Auto-generated method stub
	
}

@Override
public void onNothingSelected(AdapterView<?> arg0) {
	// TODO Auto-generated method stub
	
}
} 

