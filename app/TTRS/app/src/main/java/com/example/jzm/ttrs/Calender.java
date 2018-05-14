package com.example.jzm.ttrs;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;

public class Calender extends AppCompatActivity
    implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);
        View view = findViewById(R.id.activity_calendar);
        view.setBackgroundColor(Color.parseColor("#90000000"));
        CalendarView calendarView = findViewById(R.id.calendar);
        calendarView.setTextColor(Color.parseColor("#FF0000"),
                                  Color.parseColor("#FFFFFF"),
                                  Color.parseColor("#A9A9A9"),
                                  Color.parseColor("#87CEFA"),
                                  Color.parseColor("#A9A9A9"));
        calendarView.setSelectedColor(Color.parseColor("#80cfcfcf"),
                                      Color.parseColor("#FFC0CB"),
                                      Color.parseColor("#FFC0CB"));

        Button confirm = findViewById(R.id.calendar_confirm);
        Button cancel = findViewById(R.id.calendar_cancel);
        Button yearButton = findViewById(R.id.calendar_year);
        Button monthButton = findViewById(R.id.calendar_month);
        confirm.setOnClickListener(this);
        cancel.setOnClickListener(this);

        Intent intent = getIntent();
        String day = intent.getStringExtra("day");
        String month = intent.getStringExtra("month");
        String year = intent.getStringExtra("year");
        monthButton.setText(month + "月");
        yearButton.setText(year + "年");
        calendarView.scrollToCalendar(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day));

        calendarView.setOnYearChangeListener(new CalendarView.OnYearChangeListener() {
            @Override
            public void onYearChange(int year) {
                TextView textViewYear = findViewById(R.id.calendar_year);
                textViewYear.setText(String.valueOf(year));
            }
        });

        calendarView.setOnMonthChangeListener(new CalendarView.OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month) {
                TextView textViewMonth = findViewById(R.id.calendar_month);
                TextView textViewYear = findViewById(R.id.calendar_year);
                textViewYear.setText(String.valueOf(year) + "年");
                textViewMonth.setText(String.valueOf(month) + "月");
            }
        });
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.calendar_confirm : {
                CalendarView calendarView = findViewById(R.id.calendar);
                Calendar result = calendarView.getSelectedCalendar();
                String day = String.valueOf(result.getDay());
                String month = String.valueOf(result.getMonth());
                String year = String.valueOf(result.getYear());
                Intent intent = new Intent(Calender.this, ContentFragment_train_query.class);
                intent.putExtra("day", day);
                intent.putExtra("month", month);
                intent.putExtra("year", year);
                setResult(RESULT_OK, intent);
                finish();
                break;
            }
            case R.id.calendar_cancel : {
                finish();
                break;
            }
            default:{
                break;
            }
        }
    }

}
