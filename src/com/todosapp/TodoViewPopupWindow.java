package com.todosapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.todosapp.data.TodosTable;

/*
 * TodoViewPopupWindow displays a summary of a chosen todo in a popup window
 * 
 */
public class TodoViewPopupWindow implements OnClickListener {
	Context context;
	Activity activity;
	Uri uri;

	/**
	 * Constructor
	 * @param context
	 */
	public TodoViewPopupWindow (Context context, Activity activity, Uri uri) {
		this.context = context;
		this.activity = activity;
		this.setUri(uri);
	}

	/**
	 * Setter for the content uri
	 * @param contentUri
	 */
	private void setUri(Uri contentUri) {
		this.uri = contentUri;
	}

	/**
	 * Method responsible for setting up and showing the popup
	 */
	public void showPopUp() {

		int popupWidth = 300;
		int popupHeight = 150;

		//using the custom layout
		LayoutInflater layoutInflater = (LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View popupView = layoutInflater.inflate(R.layout.todo_view, null);
		//getting the values from the database
		String[] projection = { TodosTable.COLUMN_DESCRIPTION, TodosTable.COLUMN_DUEDATE, TodosTable.COLUMN_PRIORITY, TodosTable.COLUMN_STATUS };
		Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
		cursor.moveToFirst();
		String desc = cursor.getString(cursor.getColumnIndexOrThrow(TodosTable.COLUMN_DESCRIPTION));
		String date = cursor.getString(cursor.getColumnIndexOrThrow(TodosTable.COLUMN_DUEDATE));
		String priority = cursor.getString(cursor.getColumnIndexOrThrow(TodosTable.COLUMN_PRIORITY));
		String status = cursor.getString(cursor.getColumnIndexOrThrow(TodosTable.COLUMN_STATUS));

		// Creating the PopupWindow
		final PopupWindow popup = new PopupWindow(popupView);
		TextView viewDesc = (TextView) popup.getContentView().findViewById(R.id.todo_view_description);
		TextView viewDate = (TextView) popup.getContentView().findViewById(R.id.todo_view_date);
		TextView viewPriority = (TextView) popup.getContentView().findViewById(R.id.todo_view_priority);
		TextView viewStatus = (TextView) popup.getContentView().findViewById(R.id.todo_view_status);

		//feeding the fields with the current todo's information
		viewDesc.setText(desc);
		viewDate.setText(date);
		switch(Integer.valueOf(priority)) {
		case 5: priority="Very important";
			break;
		case 4: priority="Important";
			break;
		case 3: priority="Moderately important";
			break;
		case 2: priority="Slightly important";
			break;
		case 1: priority="Not that important";
			break;
		}
		viewPriority.setText(priority);
		viewStatus.setText(status);

		popup.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.white_bg));
		popup.setWidth(popupWidth);
		popup.setHeight(popupHeight);
		popup.setFocusable(true);

		// Clear the default translucent background
		//popup.setBackgroundDrawable(new BitmapDrawable());

		popup.showAtLocation(activity.findViewById(R.id.todos_overview), Gravity.CENTER, 0, 0);

		// Getting a reference to Close button, and close the popup when clicked.
		//Button close = (Button) layout.findViewById(R.id.close);
		//close.setOnClickListener(new OnClickListener() {

		//@Override
		//public void onClick(View v) {
		//popup.dismiss();
		//}
		//});
		popup.update();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub

	}

}