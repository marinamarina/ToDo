package com.todosapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
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
public class TodoInsertEditDialogFragment extends DialogFragment {
    private EditText descText;
    private TextView dateView;
    private Spinner priorityDropdown;
    private Spinner statusDropdown;
    private Button confirmButton;

    private Uri taskUri;

    /**
     * Creates a new instance of todoinsertedit dialog 
     * @return
     */
    static TodoInsertEditDialogFragment newInstance() {
        TodoInsertEditDialogFragment fragment = new TodoInsertEditDialogFragment();
        return fragment;
    }

    @Override 
    public Dialog onCreateDialog (Bundle savedInstanceState) { 
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.todo_edit, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);

        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Confirm", null);
        

        descText = (EditText) dialogView.findViewById(R.id.todo_edit_description);
        dateView = (TextView) dialogView.findViewById(R.id.todo_edit_time);
        priorityDropdown = (Spinner) dialogView.findViewById(R.id.todo_edit_priority);
        statusDropdown = (Spinner) dialogView.findViewById(R.id.todo_edit_status);

        //bind on click listeners
        //dateView.setOnClickListener(this);
        //confirmButton.setOnClickListener(this);

        //Bundle extras = getIntent().getExtras();
        // check for extra data passed from the other activity
        //if (extras != null) {
            //taskUri = extras.getParcelable(TodoContentProvider.CONTENT_ITEM_TYPE);
            //fillData(taskUri);
        //}
        return builder.create();
    }
    /** 
     * Filling the data for a single todo from database
     */
    @SuppressWarnings("unchecked")
    private void fillData(Uri uri) {
        //repeating all the time!!
        String[] projection = { TodosTable.COLUMN_DESCRIPTION, TodosTable.COLUMN_DUEDATE, TodosTable.COLUMN_PRIORITY, TodosTable.COLUMN_STATUS };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
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

            //date view
            dateView.setText(cursor.getString(cursor.getColumnIndexOrThrow(TodosTable.COLUMN_DUEDATE)));

            //priority view   
            currentPriority = cursor.getString(cursor.getColumnIndexOrThrow(TodosTable.COLUMN_PRIORITY));   
            priorityAdapter = (ArrayAdapter<String>) priorityDropdown.getAdapter(); //cast to an ArrayAdapter
            priorityDropdownPosition = priorityAdapter.getPosition(currentPriority);
            priorityDropdown.setSelection(priorityDropdownPosition);

            //status view
            currentStatus = cursor.getString(cursor.getColumnIndexOrThrow(TodosTable.COLUMN_STATUS));      
            statusAdapter = (ArrayAdapter<String>) statusDropdown.getAdapter(); //cast to an ArrayAdapter
            statusDropdownPosition = statusAdapter.getPosition(currentStatus); //
            statusDropdown.setSelection(statusDropdownPosition);

            // Close the cursor
            cursor.close();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        saveState();
    }


    private void saveState() {
        String description = descText.getText().toString();
        String date = dateView.getText().toString();
        String priority = (String) priorityDropdown.getSelectedItem();
        String status = (String) statusDropdown.getSelectedItem();
        ContentValues values = new ContentValues();

        values.put(TodosTable.COLUMN_DESCRIPTION, description);
        values.put(TodosTable.COLUMN_DUEDATE, date);
        values.put(TodosTable.COLUMN_PRIORITY, priority);
        values.put(TodosTable.COLUMN_STATUS, status);

        // Add simple validation
        if (description.length() == 0 ) {
            return;
        }
        if (taskUri == null) {
            // New todo
            taskUri = getActivity().getContentResolver().insert(TodoContentProvider.CONTENT_URI, values);
        } else {
            // Edit todo
            getActivity().getContentResolver().update(taskUri, values, null, null);
        }
    }

    
    private void makeToast(String field) {
        Toast.makeText(getActivity().getBaseContext(), "Field " + field + " can't be empty",
                Toast.LENGTH_LONG)
                .show();
    }
    /**
     * On Click Listeners 
     */
    //@Override
//  public void onClick(View v) {
//      switch(v.getId()) {
//
//      //Adding validation
//      //All fields are required
//      case R.id.todo_edit_button:
//          //description
//          if (TextUtils.isEmpty(descText.getText().toString())) {
//              makeToast("description");
//          } else if( TextUtils.isEmpty(dateView.getText()) ) {
//              //date
//              makeToast("date");
//          } else if( priorityDropdown.getSelectedItem().equals(getResources().getStringArray(R.array.priorities)[0])) {
//              //priority
//              makeToast("priority");
//          } else if( statusDropdown.getSelectedItem().equals(getResources().getStringArray(R.array.status)[0])) {
//              //status
//              makeToast("status");
//          } else {
//              setResult(RESULT_OK);
//
//          }
//          break;
//      case R.id.todo_edit_time: 
//          DialogFragment dp = new DatePickerFragment();
//          dp.show(getFragmentManager(), "datePicker");
//          break;
//      }
//  }

} 

