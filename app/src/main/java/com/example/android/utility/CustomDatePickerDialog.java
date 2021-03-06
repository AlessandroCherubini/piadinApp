package com.example.android.utility;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CustomDatePickerDialog extends DatePickerDialog implements DatePicker.OnDateChangedListener {

    private DatePickerDialog mDatePicker;

    @SuppressLint("NewApi")
    public CustomDatePickerDialog(Context context, int theme, OnDateSetListener callBack,
                                  int year, int monthOfYear, int dayOfMonth) {
        super(context, theme,callBack, year, monthOfYear, dayOfMonth);
        mDatePicker = new DatePickerDialog(context,theme,callBack, year, monthOfYear, dayOfMonth);

        mDatePicker.getDatePicker().init(2013, 7, 16, this);

        updateTitle(year, monthOfYear);

    }
    public void onDateChanged(DatePicker view, int year,
                              int month, int day) {
        updateTitle(year, month);
    }
    private void updateTitle(int year, int month) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
//       mCalendar.set(Calendar.DAY_OF_MONTH, day);
        mDatePicker.setTitle(getFormat().format(mCalendar.getTime()));

    }

    public DatePickerDialog getPicker(){

        return this.mDatePicker;
    }
    /*
     * the format for dialog tile,and you can override this method
     */
    public SimpleDateFormat getFormat(){
        return new SimpleDateFormat("MMM, yyyy");
    };
}
