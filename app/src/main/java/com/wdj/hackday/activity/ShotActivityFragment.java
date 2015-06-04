package com.wdj.hackday.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.wdj.hackday.API;
import com.wdj.hackday.Const;
import com.wdj.hackday.R;
import com.wdj.hackday.Utils;
import com.wdj.hackday.VolleyFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShotActivityFragment extends Fragment implements Camera.PictureCallback, SurfaceHolder.Callback {
  private Context context;
  private Camera camera;
  private FrameLayout previewContainer;
  private SurfaceView previewView;
  private View shotButton;
  private ImageView templateView;

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
    camera = Utils.openCamera(getActivity());

    View view = inflater.inflate(R.layout.fragment_shot, container, false);
    templateView = (ImageView) view.findViewById(R.id.image_template);
    templateView.setImageResource(R.drawable.model_1);

    previewContainer = (FrameLayout) view.findViewById(R.id.preview_container);
    previewView = (SurfaceView) view.findViewById(R.id.preview);
    previewView.getHolder().addCallback(this);

    shotButton = view.findViewById(R.id.action_shot);
    shotButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.d(Const.TAG, "on shot button clicked");
        try {
          camera.takePicture(null, null, ShotActivityFragment.this);
          shotButton.setEnabled(false);
        } catch (Exception e) {
          Log.e(Const.TAG, "Failed to tack picture: " + e.toString());
        }
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
//
//    camera = Utils.openCamera(getActivity());
//
//    previewView = new CameraPreviewSurfaceView(context, camera);
//    previewContainer.addView(previewView);
//
//    Camera.Parameters param = camera.getParameters();
//    param.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//    camera.setParameters(param);
//    camera.autoFocus(this);
  }

  @Override
  public void onPause() {
    super.onPause();

//    previewContainer.removeView(previewView);
//    previewView = null;
//    camera.stopPreview();
//    camera.release();
//    camera = null;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    try {
      camera.release();
      camera = null;
    } catch (Exception e) {
      Log.e(Const.TAG, "Error releasing camera: " + e);
    }
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

  private void sendPhoto(final String path) throws IOException {
    Log.d(Const.TAG, "send photo: " + path);
    InputStream template = new FileInputStream(path);
    InputStream photo = context.getAssets().open("model1.png");

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
            onError(R.string.toast_send_file_error);
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
        fragment.onError(R.string.toast_save_file_error);
        return;
      }

      try {
        fragment.sendPhoto(path);
      } catch (IOException e) {
        fragment.onError(R.string.toast_open_file_error);
      }
    }
  }

  private void onError(int stringId) {
    Toast.makeText(context, stringId, Toast.LENGTH_LONG).show();
    camera.startPreview();
    shotButton.setEnabled(true);
  }

  @Override
  public void surfaceCreated(SurfaceHolder holder) {
    // The Surface has been created, now tell the camera where to draw the preview.
    try {
      Log.d(Const.TAG, "surface created");
      camera.setPreviewDisplay(holder);
      camera.startPreview();
      shotButton.setEnabled(true);

      Camera.Parameters params = camera.getParameters();
      params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);

      List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();
      List<Camera.Size> pictureSizes = params.getSupportedPictureSizes();

      Camera.Size size = chooseSize(previewSizes, 1280);
      Log.d(Const.TAG, "preview size: " + size.width + ", " + size.height);
      params.setPreviewSize(size.width, size.height);
      params.setPictureFormat(ImageFormat.JPEG);
      params.setJpegQuality(60);

      size = chooseSize(pictureSizes, 1600);
      Log.d(Const.TAG, "picture size: " + size.width + ", " + size.height);
      params.setPictureSize(size.width, size.height);

      camera.setParameters(params);

    } catch (IOException e) {
      Log.d(Const.TAG, "Error setting camera preview: " + e.getMessage());
    }
  }

  private Camera.Size chooseSize(List<Camera.Size> sizes, int maxWidth) {
    Camera.Size best = null;
    for (Camera.Size size : sizes) {
      if (size.width > maxWidth) {
        continue;
      }
      if (size.width * 3 != size.height * 4) {
        continue;
      }
      if (best == null || size.width > best.width) {
        best = size;
      }
    }
    return best != null ? best : sizes.get(0);
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder holder) {
    Log.d(Const.TAG, "surface destroyed");
    // empty. Take care of releasing the Camera preview in your activity.
    try {
      camera.stopPreview();
    } catch (Exception e) {
      Log.e(Const.TAG, "Error stop previewing: " + e);
    }
  }

  @Override
  public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    Log.d(Const.TAG, "surface changed");
    // If your preview can change or rotate, take care of those events here.
    // Make sure to stop the preview before resizing or reformatting it.

    if (holder.getSurface() == null){
      // preview surface does not exist
      return;
    }

    // stop preview before making changes
    try {
      camera.stopPreview();
    } catch (Exception e){
      // ignore: tried to stop a non-existent preview
    }

    // set preview size and make any resize, rotate or
    // reformatting changes here

    // start preview with new settings
    try {
      camera.setPreviewDisplay(holder);
      camera.startPreview();
      shotButton.setEnabled(true);

    } catch (Exception e){
      Log.d(Const.TAG, "Error starting camera preview: " + e.getMessage());
    }
  }
}
