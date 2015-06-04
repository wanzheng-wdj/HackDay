package com.wdj.hackday.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wdj.hackday.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShotActivityFragment extends Fragment {

  public ShotActivityFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_shot, container, false);
    view.findViewById(R.id.action_shot).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onShot();
      }
    });
    return view;
  }

  private void onShot() {
    startActivity(new Intent(getActivity(), ResultActivity.class));
  }
}
