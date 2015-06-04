package com.wdj.hackday.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.wdj.hackday.API;
import com.wdj.hackday.Const;
import com.wdj.hackday.R;
import com.wdj.hackday.Utils;
import com.wdj.hackday.VolleyFactory;
import com.wdj.hackday.widget.CameraPreviewSurfaceView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShotActivityFragment extends Fragment implements Camera.PictureCallback {
  private Context context;
  private Camera camera;
  private FrameLayout previewContainer;
  private CameraPreviewSurfaceView previewView;

  public ShotActivityFragment() {
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    context = activity;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_shot, container, false);
    previewContainer = (FrameLayout) view.findViewById(R.id.preview_container);

    view.findViewById(R.id.action_shot).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.d(Const.TAG, "on shot button clicked");
        camera.takePicture(null, null, ShotActivityFragment.this);
      }
    });
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    Bundle args = getArguments();
    if (args != null) {
      String path = args.getString(ResultActivity.EXTRA_FILEPATH);
      Log.d(Const.TAG, "path = " + path);
    }
  }

  @Override
  public void onResume() {
    super.onResume();

    camera = Utils.openCamera(getActivity());
    previewView = new CameraPreviewSurfaceView(context, camera);
    previewContainer.addView(previewView);
  }

  @Override
  public void onPause() {
    super.onPause();

    previewContainer.removeView(previewView);
    previewView = null;
    camera.stopPreview();
    camera.release();
    camera = null;
  }

  @Override
  public void onDetach() {
    super.onDetach();
    context = null;
  }

  @Override
  public void onPictureTaken(byte[] data, Camera camera) {
    Log.d(Const.TAG, "onPictureTaken, size=" + data.length);

    // TODO: show progress bar
    new SaveAndSendTask(this).execute(data);
  }

  private void sendPhoto(final String path) throws FileNotFoundException {
    Log.d(Const.TAG, "send photo: " + path);
    InputStream template = new FileInputStream(path);
    InputStream photo = new FileInputStream(path);

    API.ScoreRequest request = new API.ScoreRequest(template, photo,
        new Response.Listener<API.Result>() {
          @Override
          public void onResponse(API.Result response) {
            Log.d(Const.TAG, "score: " + response.toString());

            Intent intent = new Intent(context, ResultActivity.class)
                .putExtra(ResultActivity.EXTRA_FILEPATH, path)
                .putExtra(ResultActivity.EXTRA_SCORE, response.score)
                .putExtra(ResultActivity.EXTRA_LEVEL, response.level)
                .putExtra(ResultActivity.EXTRA_WATERMARK, response.warterMarkUrl)
                .putExtra(ResultActivity.EXTRA_AUDIO, response.audioUrl)
                .putExtra(ResultActivity.EXTRA_COMMENT, response.commentText)
                .putExtra(ResultActivity.EXTRA_PHOTO, path);
            startActivity(intent);
          }
        },
        new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {
            Log.e(Const.TAG, "send failed: " + error.toString());
            Toast.makeText(context, R.string.toast_send_file_error, Toast.LENGTH_LONG).show();
          }
        });
    VolleyFactory.get(context).getRequestQueue().add(request);
  }

  private static class SaveAndSendTask extends AsyncTask<byte[], Void, Boolean> {
    private WeakReference<ShotActivityFragment> refFragment;
    private String path;

    public SaveAndSendTask(ShotActivityFragment fragment) {
      refFragment = new WeakReference<>(fragment);
    }

    @Override
    protected Boolean doInBackground(byte[]... params) {
      byte[] data = params[0];
      File pictureFile = Utils.getOutputMediaFile();
      Log.d(Const.TAG, "saving at " + pictureFile + " ...");
      path = pictureFile.getAbsolutePath();
      try {
        FileOutputStream fos = new FileOutputStream(pictureFile);
        fos.write(data);
        fos.close();
        return true;
      } catch (Exception e) {
        Log.d(Const.TAG, "Error accessing file: " + e.getMessage());
        return false;
      }
    }

    @Override
    protected void onPostExecute(Boolean success) {
      Log.d(Const.TAG, "save " + (success ? "ok" : "failed"));

      ShotActivityFragment fragment = refFragment.get();
      if (fragment == null) {
        return;
      }
      if (!success) {
        Toast.makeText(fragment.context, R.string.toast_save_file_error, Toast.LENGTH_LONG).show();
        return;
      }

      try {
        fragment.sendPhoto(path);
      } catch (FileNotFoundException e) {
        Toast.makeText(fragment.context, R.string.toast_open_file_error, Toast.LENGTH_LONG).show();
      }
    }
  }
}
