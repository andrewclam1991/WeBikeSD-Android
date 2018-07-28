package org.opensandiego.webikesd.views.monitor;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.opensandiego.webikesd.R;
import org.opensandiego.webikesd.di.ActivityScoped;

import javax.inject.Inject;

import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;


/**
 * A simple {@link Fragment} subclass.
 * <p>
 * This {@link MonitorContract.View} implementation is responsible for
 * React to model updates (eg. Trip data) and show user trip status.
 */
@ActivityScoped
public class MonitorFragment extends DaggerFragment implements MonitorContract.View {

  @Inject
  MonitorContract.Presenter mPresenter;

  @Inject
  public MonitorFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View rootView = inflater.inflate(R.layout.fragment_tracking, container, false);
    ButterKnife.bind(this, rootView);
    return rootView;
  }

  @Override
  public void onStart() {
    super.onStart();
    mPresenter.setView(this);
  }

  @Override
  public void onStop() {
    super.onStop();
    mPresenter.dropView();
  }

  @Override
  public void showTripTime(long duration) {

  }

  @Override
  public void showTripSpeed(double speed) {

  }

  @Override
  public void showTripDistance(double distance) {

  }

  @Override
  public void showTripStatus(String status) {

  }

  @Override
  public void showLocationStatus(String status) {

  }

  @Override
  public boolean isActive() {
    return isAdded();
  }
}
