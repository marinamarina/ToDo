package com.todosapp;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

@SuppressLint("NewApi")
public  class DatePickerFragment extends DialogFragment
implements DatePickerDialog.OnDateSetListener {
	Calendar c;
	int year;
	int month;
	int day;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH); 

		//Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {

		GregorianCalendar calTime = new GregorianCalendar(year, month, day, 0, 0);
		Date date = calTime.getTime();
		view.getCalendarView().setMinDate(c.getTimeInMillis() - 1000);

		String dateAsString = DateFormat.getDateInstance().format(date);
		((TextView)(getActivity().findViewById(R.id.todo_edit_time))).setText(dateAsString);		
	}
}