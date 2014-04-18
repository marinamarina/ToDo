package com.todosapp.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.todosapp.R;
import com.todosapp.TodosOverviewActivity;

public class TodoCursorAdapter extends CursorAdapter {

	private int todo_row_layout;
	private TextView labelView;
	private TextView secondLabelView;

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
		View v = inflater.inflate(todo_row_layout, parent, false);
		return v;
	}
	
	@SuppressLint("DefaultLocale")
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		//views within a todo row
		labelView = (TextView) view.findViewById(R.id.label);
		secondLabelView = (TextView) view.findViewById(R.id.second_label);

		if(cursor.getPosition()%2==1) {
			view.setBackgroundColor(context.getResources().getColor(R.color.white));
		}
		else {
			view.setBackgroundColor(context.getResources().getColor(R.color.lightgrey));
		}


		String todoDescription = cursor.getString(cursor.getColumnIndex(TodosTable.COLUMN_DESCRIPTION));

		//Feeding the first text field with the todo description
		if (todoDescription != null) {
			labelView.setText(todoDescription); 
		}
		//Feeding the second text field with the data 
		//depending on a sort order
		if(TodosOverviewActivity.sortBy!="") {
			String sortBy_lc=TodosOverviewActivity.sortBy.toLowerCase();
			int secondLabelIndex;

			if(sortBy_lc.contains("description")) {
				secondLabelView.setText("");
			} else if (sortBy_lc.contains("date")) {
				secondLabelIndex = cursor.getColumnIndex(TodosOverviewActivity.sortBy);
				String status = "Completion date: " + cursor.getString(secondLabelIndex);
				secondLabelView.setText(status);
			} else if ((sortBy_lc.contains("priority"))) {
				secondLabelIndex = cursor.getColumnIndex(TodosTable.COLUMN_PRIORITY);
				String status = "Priority: " + cursor.getString(secondLabelIndex);
				secondLabelView.setText(status);
			}

		}

	}

}
