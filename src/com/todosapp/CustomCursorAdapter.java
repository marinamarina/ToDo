package com.todosapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.todosapp.data.TaskTable;

public class CustomCursorAdapter extends CursorAdapter {
	  
	  private int layout;
	  
	  public CustomCursorAdapter (Context context, int layout, Cursor c, int flags) {
	        super(context, c, flags);
	        this.layout = layout;

	    }
	  @Override
	  public View newView(Context context, Cursor cursor, ViewGroup parent) {
		   final LayoutInflater inflater = LayoutInflater.from(context);
	        View v = inflater.inflate(layout, parent, false);
	
	        return v;
	   
	  }
	  @Override
	  public void bindView(View view, Context context, Cursor cursor) {
	  
	   if(cursor.getPosition()%2==1) {
	    view.setBackgroundColor(context.getResources().getColor(R.color.white));
	   }
	   else {
	    view.setBackgroundColor(context.getResources().getColor(R.color.lightgrey));
	   }
	  
	   int descCol = cursor.getColumnIndex(TaskTable.COLUMN_DESCRIPTION);
     int statusCol = cursor.getColumnIndex(TaskTable.COLUMN_STATUS);
     String desc = cursor.getString(descCol);
     String status = cursor.getString(statusCol);

     /**
      * Next set the name of the entry.
      */    
     TextView desc_text = (TextView) view.findViewById(R.id.label);
     TextView name_text1 = (TextView) view.findViewById(R.id.second_label);
     if (desc_text != null) {
         desc_text.setText(desc);
        name_text1.setText(status);
     }
	  
	  }
	  
	 }
