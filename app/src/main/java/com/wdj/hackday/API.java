package com.wdj.hackday;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

/**
 * @author wanzheng@wandoujia.com (Zheng Wan)
 */
public class API {
  private static final String URL = "http://100.64.77.154:8080/image/upload";
  public static class Result implements Serializable{
    public int score;
    public String warterMarkUrl;
    public String commentText;
    public String audioUrl;

    @Override
    public String toString() {
      return "Result{" +
          "score=" + score +
          ", warterMarkUrl='" + warterMarkUrl + '\'' +
          ", commentText='" + commentText + '\'' +
          ", audioUrl='" + audioUrl + '\'' +
          '}';
    }
  }

  public static class ScoreRequest extends Request<Result> {
    private final InputStream template;
    private final InputStream photo;
    private final Response.Listener<Result> listener;
    private HttpEntity entity;

    public ScoreRequest(InputStream template, InputStream photo,
                        Response.Listener<Result> listener, Response.ErrorListener errorListener) {
      super(Method.POST, URL, errorListener);
      this.template = template;
      this.photo = photo;
      this.listener = listener;
    }

    public HttpEntity getEntity() {
      if (entity == null) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setCharset(MIME.UTF8_CHARSET);
        builder.addBinaryBody(Const.NAME_TEMPLATE, template, ContentType.APPLICATION_OCTET_STREAM, "template.png");
        builder.addBinaryBody(Const.NAME_PHOTO, photo, ContentType.APPLICATION_OCTET_STREAM, "photo.jpg");
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
        try {
          template.close();
          photo.close();
        } catch (Exception e) {
          // null
        }
      }
    }

    @Override
    protected Response<Result> parseNetworkResponse(NetworkResponse response) {
      try {
        String jsonString = new String(response.data,
            HttpHeaderParser.parseCharset(response.headers, "utf-8"));
        JSONObject json = new JSONObject(jsonString);

        Result result = new Result();
        result.score = json.getInt("score");
        result.warterMarkUrl = json.getString("waterMarkUrl");
        result.audioUrl = json.getString("audioUrl");
        result.commentText = json.getString("commentText");

        return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
      } catch (UnsupportedEncodingException e) {
        return Response.error(new ParseError(e));
      } catch (JSONException je) {
        return Response.error(new ParseError(je));
      }
    }

    @Override
    protected void deliverResponse(Result response) {
      listener.onResponse(response);
    }
  }
}
