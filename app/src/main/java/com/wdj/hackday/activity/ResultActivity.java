package com.wdj.hackday.activity;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.wdj.hackday.R;

public class ResultActivity extends AppCompatActivity {
  public static final String EXTRA_SCORE = "extra.score";
  public static final String EXTRA_WATERMARK = "extra.watermark";
  public static final String EXTRA_COMMENT = "extra.comment";
  public static final String EXTRA_AUDIO = "extra.audio";
  public static final String EXTRA_PHOTO = "extra.photo";
  public static final String EXTRA_TEMPLATE_ID = "extra.templateId";
  public static final String EXTRA_LEVEL = "extra.level";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_result);
    Bundle args = getIntent().getExtras();
    Fragment fragment = Fragment.instantiate(this, ResultActivityFragment.class.getName(), args);
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.fragment, fragment)
        .commit();
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_result, menu);
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
