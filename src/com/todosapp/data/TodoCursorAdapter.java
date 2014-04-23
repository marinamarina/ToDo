package com.todosapp.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.todosapp.R;
import com.todosapp.TodosOverviewActivity;

public class TodoCursorAdapter extends CursorAdapter {

	//layout and the views within the layout
	private int todo_row_layout;
	private ImageView checkmarkView;
	private TextView labelView;
	private TextView secondLabelView;
	private TextView dateView;

	public TodoCursorAdapter (Context context, int layout, Cursor c, int flags) {
		super(context, c, flags);
		this.todo_row_layout = layout;
	}
	
	/**
	 * Create a new view for a single row
     */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(todo_row_layout, parent, false);
		
		return view;
	}
	
	/**
	 * Customise the views within the row
     */
	@SuppressLint("DefaultLocale")
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		labelView = (TextView) view.findViewById(R.id.label);
		secondLabelView = (TextView) view.findViewById(R.id.second_label);
		checkmarkView = (ImageView) view.findViewById(R.id.checkboxView);
		dateView = (TextView) view.findViewById(R.id.date);
		
		//values from the database
		String todoDescription = cursor.getString(cursor.getColumnIndex(TodosTable.COLUMN_DESCRIPTION));
		boolean todoCompleted = cursor.getString(cursor.getColumnIndex(TodosTable.COLUMN_STATUS)).equals("Completed");
		String todoDate = cursor.getString(cursor.getColumnIndex(TodosTable.COLUMN_DUEDATE));
		
		//colour the rows
		if(cursor.getPosition()%2==1) {
			view.setBackgroundColor(context.getResources().getColor(R.color.white));
		}
		else {
			view.setBackgroundColor(context.getResources().getColor(R.color.lightgrey));
		}		

		//Feeding the image view (grey or green check mark depending on whether the todo is completed)
		 checkmarkView.setImageResource((todoCompleted)? R.drawable.checkmark_green: R.drawable.checkmark);
		
		//Feeding the first text view with the todo description
		if (todoDescription != null) {
			labelView.setText(todoDescription); 
			if (todoCompleted) {
				labelView.setPaintFlags(labelView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			} else {
				labelView.setPaintFlags( labelView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
			}
		}
		
		//Feeding the date field
		if(todoDate!=""){
			todoDate=todoDate.substring(0, todoDate.indexOf(','));;
			dateView.setText(todoDate);
		}
		
		//Feeding the second text view with the data 
		//depending on a sort order
		if(TodosOverviewActivity.sortBy!="") {
			//sort order
			String sortBy_lc=TodosOverviewActivity.sortBy.toLowerCase();
			int secondLabelIndex;
			if(sortBy_lc.contains("description")) {
				//strike through the description if the todo is completed
				if (todoCompleted) {
					labelView.setPaintFlags(labelView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
				} else {
					labelView.setPaintFlags( labelView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
				}
				//set text on the second text view
				secondLabelView.setText("");
				//change the check mark to green, if the todo is completed
				checkmarkView.setImageResource((todoCompleted)? R.drawable.checkmark_green: R.drawable.checkmark);

			} else if (sortBy_lc.contains("date")) {
				//strike through the description if the todo is completed
				if (todoCompleted) {
					labelView.setPaintFlags(labelView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
				} else {
					labelView.setPaintFlags( labelView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
				}
				//set text on the second text view
				secondLabelIndex = cursor.getColumnIndex(TodosOverviewActivity.sortBy);
				String status = "Completion date: " + cursor.getString(secondLabelIndex);
				checkmarkView.setImageResource((todoCompleted)? R.drawable.checkmark_green: R.drawable.checkmark);
				secondLabelView.setText(status);
				//change the check mark to green, if the todo is completed
				if (todoCompleted) {
					labelView.setPaintFlags(labelView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
				}
			} else if ((sortBy_lc.contains("priority"))) {
				//strike through the description if the todo is completed
				if (todoCompleted) {
					labelView.setPaintFlags(labelView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
				} else {
					labelView.setPaintFlags( labelView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
				}
				secondLabelIndex = cursor.getColumnIndex(TodosTable.COLUMN_PRIORITY);
				//set text on the second text view
				String status = "Priority: " + cursor.getString(secondLabelIndex);
				secondLabelView.setText(status);
				//change the check mark to green, if the todo is completed
				checkmarkView.setImageResource((todoCompleted)? R.drawable.checkmark_green: R.drawable.checkmark);
				
			}

		}

	}

}
