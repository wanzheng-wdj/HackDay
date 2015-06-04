package com.wdj.hackday.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.TextView;

import com.wdj.hackday.R;

import java.text.Format;

public class TestCalendarActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test_calendar);

    CalendarView calendarView = (CalendarView) findViewById(R.id.calendar);
    final TextView date = (TextView) findViewById(R.id.date);

    calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
      @Override
      public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
        String s = String.format("%04d-%02d-%02d", year, month, dayOfMonth);
        date.setText(s);
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_test_calendar, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
