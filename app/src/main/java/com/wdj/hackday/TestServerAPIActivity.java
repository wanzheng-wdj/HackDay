package com.wdj.hackday;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestServerAPIActivity extends AppCompatActivity {
  private static final String TAG = "TEST";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test_server_api);
    findViewById(R.id.action_upload).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        AsyncTask.execute(new Runnable() {
          @Override
          public void run() {
            try {
              doPost();
            } catch (IOException e) {
              Log.e(TAG, "doPost() failed: " + e);
            }
          }
        });
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_test_server_api, menu);
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

  private void doPost() throws IOException {
    Log.d(TAG, "doPost()");
    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    builder.setCharset(MIME.UTF8_CHARSET);

    InputStream in = getAssets().open("ic_launcher.png");
    builder.addBinaryBody("template", in);

    InputStream in2 = getAssets().open("ic_launcher.png");
    builder.addBinaryBody("template", in2);

    HttpEntity entity = builder.build();

    String url = "http://www.baidu.com";
    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
    conn.setDoOutput(true);
    conn.addRequestProperty(entity.getContentType().getName(), entity.getContentType().getValue());

    OutputStream out = conn.getOutputStream();
    entity.writeTo(out);
    out.close();
    conn.connect();

    int responseCode = conn.getResponseCode();
    Log.d(TAG, "response = " + responseCode);
  }
}
