package com.wdj.hackday.activity;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wdj.hackday.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class ResultActivityFragment extends Fragment {

  public ResultActivityFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_result, container, false);
  }
}
