package com.todosapp.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.todosapp.R;
import com.todosapp.R.color;
import com.todosapp.R.id;
import com.todosapp.TodosOverviewActivity;

public class TodoCursorAdapter extends CursorAdapter {
	  
	  private int layout;
	  private TextView labelEl;
	  private TextView secondLabelEl;
	  
	  public TodoCursorAdapter (Context context, int layout, Cursor c, int flags) {
	        super(context, c, flags);
	        this.layout = layout;

	    }
	  @Override
	  public View newView(Context context, Cursor cursor, ViewGroup parent) {
		   final LayoutInflater inflater = LayoutInflater.from(context);
	        View v = inflater.inflate(layout, parent, false);
	        return v;
	   
	  }
	  @SuppressLint("DefaultLocale")
	  @Override
	  public void bindView(View view, Context context, Cursor cursor) {
		  labelEl = (TextView) view.findViewById(R.id.label);
		  secondLabelEl = (TextView) view.findViewById(R.id.second_label);
		  
	   if(cursor.getPosition()%2==1) {
	    view.setBackgroundColor(context.getResources().getColor(R.color.white));
	   }
	   else {
	    view.setBackgroundColor(context.getResources().getColor(R.color.lightgrey));
	   }
	     
	   
	   String todoDescription = cursor.getString(cursor.getColumnIndex(TodosTable.COLUMN_DESCRIPTION));
	   
	   //Feeding the first text field with the todo description
	   if (todoDescription != null) {
	       labelEl.setText(todoDescription); 
	    }
	   //Feeding the second text field with the data 
	   //depending on a sort order
	   if(TodosOverviewActivity.sortBy!="") {
		   String sortBy_lc=TodosOverviewActivity.sortBy.toLowerCase();
		   int secondLabelIndex;
		   
		   if(sortBy_lc.contains("description")) {
			   secondLabelEl.setText("");
		   } else if (sortBy_lc.contains("date")) {
			   secondLabelIndex = cursor.getColumnIndex(TodosOverviewActivity.sortBy);
			   String status = "Completion date: " + cursor.getString(secondLabelIndex);
			   secondLabelEl.setText(status);
		   } else if ((sortBy_lc.contains("priority"))) {
			   secondLabelIndex = cursor.getColumnIndex(TodosTable.COLUMN_PRIORITY);
			   String status = "Priority: " + cursor.getString(secondLabelIndex);
			   secondLabelEl.setText(status);
		   }
		   
	   }

	  }
	  
	 }
