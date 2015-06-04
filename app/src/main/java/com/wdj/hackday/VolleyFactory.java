package com.wdj.hackday;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * @author wanzheng@wandoujia.com (Zheng Wan)
 */
public class VolleyFactory {
  private static VolleyFactory sInstance;
  private RequestQueue requestQueue;
  private ImageLoader imageLoader;

  private VolleyFactory(Context appContext) {
    requestQueue = Volley.newRequestQueue(appContext);

    imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
      private final LruCache<String, Bitmap>
          cache = new LruCache<String, Bitmap>(20);
      @Override
      public Bitmap getBitmap(String key) {
        return cache.get(key);
      }

      @Override
      public void putBitmap(String key, Bitmap bitmap) {
        cache.put(key, bitmap);
      }
    });
  }

  public static VolleyFactory get(Context context) {
    if (sInstance == null) {
      sInstance = new VolleyFactory(context.getApplicationContext());
    }
    return sInstance;
  }

  public RequestQueue getRequestQueue() {
    return requestQueue;
  }

  public ImageLoader getImageLoader() {
    return imageLoader;
  }
}
