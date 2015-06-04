package com.wdj.hackday;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Environment;
import android.view.Surface;

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
  }

  public static File getOutputMediaFile() {
    File dir = new File(Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES), Const.ALBUM_NAME);
    dir.mkdirs();
    return new File(dir, String.valueOf(System.nanoTime() / 1000000) + ".jpg");
  }

  public static Camera openCamera(Activity activity) {
    Camera camera = Camera.open();
//    Camera.Parameters param = camera.getParameters();
//    param.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
//    camera.setParameters(param);
    setCameraDisplayOrientation(activity, Camera.CameraInfo.CAMERA_FACING_BACK, camera);
    return camera;
  }
}
