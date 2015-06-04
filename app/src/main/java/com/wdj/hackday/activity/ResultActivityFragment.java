package com.wdj.hackday.activity;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.wdj.hackday.API;
import com.wdj.hackday.R;
import com.wdj.hackday.VolleyFactory;

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
    View view = inflater.inflate(R.layout.fragment_result, container, false);
    badgeView = (ImageView) view.findViewById(R.id.icon_badge);
    waterMarkView = (NetworkImageView) view.findViewById(R.id.watermark);
    scoreView = (TextView) view.findViewById(R.id.score);
    commentView = (TextView) view.findViewById(R.id.comment);
    imageView = (ImageView) view.findViewById(R.id.photo);
    templateView = (ImageView) view.findViewById(R.id.image_template);
    return view;
  }

  private static int[] audioList = new int[] {
      R.raw.audio_0,
      R.raw.audio_1,
      R.raw.audio_2,
      R.raw.audio_3,
  };

  private static int[] badgeList = new int[] {
      R.drawable.badge_0,
      R.drawable.badge_1,
      R.drawable.badge_2,
      R.drawable.badge_3,
  };

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    Bundle args = getArguments();

    API.Result result = new API.Result();
    if (args != null) {
      result.score = args.getInt(ResultActivity.EXTRA_SCORE);
      result.warterMarkUrl = args.getString(ResultActivity.EXTRA_WATERMARK);
      result.audioUrl = args.getString(ResultActivity.EXTRA_AUDIO);
      result.commentText = args.getString(ResultActivity.EXTRA_COMMENT);
      result.photoUri = args.getString(ResultActivity.EXTRA_PHOTO);
      result.level = args.getInt(ResultActivity.EXTRA_LEVEL);
    } else {
      result.score = 65;
      result.warterMarkUrl = "http://100.64.77.154:8080/multimedia/level1/images/2.png";
      result.audioUrl = "";
      result.commentText = "五行缺土，天生欠练，蝴蝶袖和小肚腩更配哦~";
      result.photoUri = "/sdcard/Pictures/hackday/157990650.jpg";
      result.level = 3;
    }
    templateView.setImageResource(R.drawable.model_1);

    scoreView.setText(String.valueOf(result.score));
    commentView.setText(result.commentText);
    waterMarkView.setImageUrl(result.warterMarkUrl,
        VolleyFactory.get(getActivity()).getImageLoader());
    imageView.setImageURI(Uri.parse(result.photoUri));
    badgeView.setImageResource(badgeList[result.level % badgeList.length]);

    MediaPlayer.create(context, audioList[result.level % audioList.length]).start();
  }

  @Override
  public void onStart() {
    super.onStart();
  }

  @Override
  public void onDetach() {
    super.onDetach();
    context = null;
  }
}
