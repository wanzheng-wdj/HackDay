package com.wdj.hackday;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;

/**
 * @author wanzheng@wandoujia.com (Zheng Wan)
 */
public class API {
  public static final String URL_UPLOAD = "http://100.64.77.154:8080/image/upload";
  public static final String URL_DISPLAY = "http://100.64.77.154:8080/image/display";
  public static class Result implements Serializable{
    public int score;
    public int level;
    public String warterMarkUrl;
    public String commentText;
    public String audioUrl;

    @Override
    public String toString() {
      return "Result{" +
          "score=" + score +
          ", level=" + level +
          ", warterMarkUrl='" + warterMarkUrl + '\'' +
          ", commentText='" + commentText + '\'' +
          ", audioUrl='" + audioUrl + '\'' +
          '}';
    }
  }

  public static class ScoreRequest extends Request<Result> {
    private final int templateId;
    private final InputStream photo;
    private final Response.Listener<Result> listener;
    private HttpEntity entity;

    public ScoreRequest(String url, int templateId, InputStream photo,
                        Response.Listener<Result> listener, Response.ErrorListener errorListener) {
      super(Method.POST, url, errorListener);
      this.templateId = templateId;
      this.photo = photo;
      this.listener = listener;
    }

    public HttpEntity getEntity() {
      if (entity == null) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setCharset(MIME.UTF8_CHARSET);
        if (templateId >= 0) {
          builder.addTextBody("id", String.valueOf(templateId));
        }
        if (photo != null) {
          builder.addBinaryBody(Const.NAME_PHOTO, photo, ContentType.APPLICATION_OCTET_STREAM, "photo.jpg");
        }
        entity = builder.build();
      }
      return entity;
    }

    @Override
    public String getBodyContentType() {
      return getEntity().getContentType().getValue();
    }

    @Override
    public byte[] getBody() {
      try {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        getEntity().writeTo(outputStream);
        return outputStream.toByteArray();
      } catch (Exception e) {
        return null;
      } finally {
        if (photo != null) {
          try {
            photo.close();
          } catch (Exception e) {
            // null
          }
        }
      }
    }

    @Override
    protected Response<Result> parseNetworkResponse(NetworkResponse response) {
      Result result = new Result();
      try {
        String jsonString = new String(response.data,
            HttpHeaderParser.parseCharset(response.headers, "utf-8"));
        Log.d(Const.TAG, "response = " + jsonString);

        JSONObject json = new JSONObject(jsonString);
        result.score = json.getInt("score");
        result.warterMarkUrl = json.getString("waterMarkUrl");
        result.audioUrl = json.getString("audioUrl");
        result.commentText = json.getString("commentText");
        result.level = json.getInt("level");
      }catch (Exception e) {
        Log.d(Const.TAG, "Error parsing: " + e);
      }

      return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(Result response) {
      if (listener != null) {
        listener.onResponse(response);
      }
    }
  }
}
