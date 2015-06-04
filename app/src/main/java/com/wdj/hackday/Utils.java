package com.wdj.hackday;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.os.Environment;
import android.view.Surface;
import android.view.View;

import java.io.File;

/**
 * @author wanzheng@wandoujia.com (Zheng Wan)
 */
public class Utils {
  public static void setCameraDisplayOrientation(
      Activity activity, int cameraId, android.hardware.Camera camera) {
    android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
    android.hardware.Camera.getCameraInfo(cameraId, info);
    int rotation = activity.getWindowManager().getDefaultDisplay()
        .getRotation();
    int degrees = 0;
    switch (rotation) {
      case Surface.ROTATION_0: degrees = 0; break;
      case Surface.ROTATION_90: degrees = 90; break;
      case Surface.ROTATION_180: degrees = 180; break;
      case Surface.ROTATION_270: degrees = 270; break;
    }

    int result;
    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
      result = (info.orientation + degrees) % 360;
      result = (360 - result) % 360;  // compensate the mirror
    } else {  // back-facing
      result = (info.orientation - degrees + 360) % 360;
    }
    camera.setDisplayOrientation(result);

    Camera.Parameters params = camera.getParameters();
    params.setRotation(result);
    camera.setParameters(params);
  }

  public static File getOutputMediaFile() {
    File dir = new File(Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES), Const.ALBUM_NAME);
    dir.mkdirs();
    return new File(dir, String.valueOf(System.nanoTime() / 1000000) + ".jpg");
  }

  public static Camera openCamera(Activity activity) {
    Camera camera = Camera.open();
    setCameraDisplayOrientation(activity, Camera.CameraInfo.CAMERA_FACING_BACK, camera);
    return camera;
  }

  public static Bitmap loadBitmapFromView(View v, int width, int height) {
    Bitmap b = Bitmap.createBitmap(width , height, Bitmap.Config.ARGB_8888);
    Canvas c = new Canvas(b);
    v.layout(0, 0, v.getWidth(), v.getHeight());
    v.draw(c);
    return b;
  }
}
