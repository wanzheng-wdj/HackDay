package com.wdj.hackday.test;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.wdj.hackday.API;
import com.wdj.hackday.Const;
import com.wdj.hackday.R;
import com.wdj.hackday.VolleyFactory;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestServerAPIActivity extends AppCompatActivity {
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
              doPost2();
            } catch (Exception e) {
              Log.e(Const.TAG, "doPost() failed: " + e);
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
    Log.d(Const.TAG, "doPost()");
    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    builder.setCharset(MIME.UTF8_CHARSET);

    InputStream in = getAssets().open("ic_launcher.png");
    builder.addBinaryBody("template", in, ContentType.APPLICATION_OCTET_STREAM, "a.png");

    InputStream in2 = getAssets().open("ic_launcher.png");
    builder.addBinaryBody("photo", in2, ContentType.APPLICATION_OCTET_STREAM, "b.png");

    HttpEntity entity = builder.build();

    String url = "http://100.64.77.154:8080/image/upload";
    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
    conn.setDoOutput(true);
    conn.addRequestProperty(entity.getContentType().getName(), entity.getContentType().getValue());

    OutputStream out = conn.getOutputStream();
    entity.writeTo(out);
    in.close();
    in2.close();
    out.close();
    conn.connect();

    int responseCode = conn.getResponseCode();
    Log.d(Const.TAG, "response = " + responseCode);
  }

  private void doPost2() throws IOException {
    Log.d(Const.TAG, "doPost2()");
    InputStream photo = getAssets().open("model1.png");

    API.ScoreRequest request = new API.ScoreRequest(API.URL_UPLOAD, 1,
        photo,
        new Response.Listener<API.Result>() {
          @Override
          public void onResponse(API.Result response) {
            Log.d(Const.TAG, "response: " + response.toString());
          }
        }, new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {
            Log.e(Const.TAG, "error: " + error.toString());
          }
        });

    VolleyFactory.get(this).getRequestQueue().add(request);
  }
}
