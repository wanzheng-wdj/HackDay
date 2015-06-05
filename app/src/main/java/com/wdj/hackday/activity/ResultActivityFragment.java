package com.wdj.hackday.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.wdj.hackday.API;
import com.wdj.hackday.Const;
import com.wdj.hackday.R;
import com.wdj.hackday.Utils;
import com.wdj.hackday.VolleyFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A placeholder fragment containing a simple view.
 */
public class ResultActivityFragment extends Fragment {
  private Context context;
  private ImageView badgeView;
  private ImageView imageView;
  private NetworkImageView waterMarkView;
  private TextView scoreView;
  private TextView commentView;
  private ImageView templateView;

  public ResultActivityFragment() {
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    context = activity;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_result, container, false);
    badgeView = (ImageView) view.findViewById(R.id.icon_badge);
    waterMarkView = (NetworkImageView) view.findViewById(R.id.watermark);
    scoreView = (TextView) view.findViewById(R.id.score);
    commentView = (TextView) view.findViewById(R.id.comment);
    imageView = (ImageView) view.findViewById(R.id.photo);
    templateView = (ImageView) view.findViewById(R.id.image_template);

    view.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        v.findViewById(R.id.notice).setVisibility(View.GONE);
        sendSnapShot(v);
      }
    });

    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    Bundle args = getArguments();

    API.Result result = new API.Result();
    String photoUri;
    int templateId;
    if (args != null) {
      result.score = args.getInt(ResultActivity.EXTRA_SCORE);
      result.warterMarkUrl = args.getString(ResultActivity.EXTRA_WATERMARK);
      result.audioUrl = args.getString(ResultActivity.EXTRA_AUDIO);
      result.commentText = args.getString(ResultActivity.EXTRA_COMMENT);
      photoUri = args.getString(ResultActivity.EXTRA_PHOTO);
      result.level = args.getInt(ResultActivity.EXTRA_LEVEL);
      templateId = args.getInt(ResultActivity.EXTRA_TEMPLATE_ID);
    } else {
      result.score = 65;
      result.warterMarkUrl = "http://100.64.77.154:8080/multimedia/level1/images/2.png";
      result.audioUrl = "";
      result.commentText = "五行缺土，天生欠练，蝴蝶袖和小肚腩更配哦~";
      photoUri = "/sdcard/Pictures/hackday/157990650.jpg";
      result.level = 3;
      templateId = 0;
    }
    templateView.setImageResource(Const.templateList[templateId % Const.templateList.length]);

    scoreView.setText(String.valueOf(result.score));
    commentView.setText(result.commentText);
    waterMarkView.setImageUrl(result.warterMarkUrl,
        VolleyFactory.get(getActivity()).getImageLoader());
    imageView.setImageURI(Uri.parse(photoUri));
    badgeView.setImageResource(Const.badgeList[result.level % Const.badgeList.length]);

    MediaPlayer.create(context, Const.audioList[result.level % Const.audioList.length]).start();
  }

  @Override
  public void onDetach() {
    super.onDetach();
    context = null;
  }

  private void sendSnapShot(View v) {
    Log.d(Const.TAG, "start taking snapshot");
    Bitmap bitmap = Utils.loadBitmapFromView(v, 0.4f);
    Log.d(Const.TAG, "bitmap created: " + bitmap);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.PNG, 60, output);
    Log.d(Const.TAG, "bitmap compressed");
    byte[] buf = output.toByteArray();
    Log.d(Const.TAG, "png size: " + buf.length);
    InputStream input = new ByteArrayInputStream(buf);

    API.ScoreRequest request = new API.ScoreRequest(API.URL_DISPLAY, -1, input,
        new Response.Listener<API.Result>() {
          @Override
          public void onResponse(API.Result response) {
            if (context != null) {
              Toast.makeText(context, R.string.toast_screenshot_uploaded, Toast.LENGTH_LONG).show();
            }
          }
        },
        new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {
            Log.e(Const.TAG, "Error request: " + error);
          }
        });
    VolleyFactory.get(context).getRequestQueue().add(request);

    try {
      saveFile(buf);
    } catch (Exception e) {
      Toast.makeText(context, R.string.toast_save_file_error, Toast.LENGTH_LONG).show();
    }
  }

  private void saveFile(byte[] buf) throws IOException {
    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    File file = new File(dir, String.valueOf(System.nanoTime() / 1000000) + ".png");
    FileOutputStream out = new FileOutputStream(file);
    out.write(buf);
    out.close();
  }
}
