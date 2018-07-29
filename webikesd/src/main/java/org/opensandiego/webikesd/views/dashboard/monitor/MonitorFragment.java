package org.opensandiego.webikesd.views.dashboard.monitor;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.opensandiego.webikesd.R;
import org.opensandiego.webikesd.di.ActivityScoped;

import javax.inject.Inject;

import butterknife.BindView;
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

  @BindView(R.id.fragment_monitor_trip_time_value_tv)
  TextView mTripTimeTv;

  @BindView(R.id.fragment_monitor_trip_speed_value_tv)
  TextView mTripSpeedTv;

  @BindView(R.id.fragment_monitor_trip_distance_value_tv)
  TextView mTripDistanceTv;

  @BindView(R.id.fragment_monitor_trip_status_value_tv)
  TextView mTripStatusTv;

  @BindView(R.id.fragment_monitor_trip_location_status_value_tv)
  TextView mTripLocationStatusTv;

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
    View rootView = inflater.inflate(R.layout.fragment_monitor, container, false);
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
    mTripTimeTv.setText(String.valueOf(duration));
  }

  @Override
  public void showTripSpeed(double speed) {
    mTripSpeedTv.setText(String.valueOf(speed));
  }

  @Override
  public void showTripDistance(double distance) {
    mTripDistanceTv.setText(String.valueOf(distance));
  }

  @Override
  public void showTripStatus(String status) {
    mTripStatusTv.setText(status);
  }

  @Override
  public void showLocationStatus(String status) {
    mTripLocationStatusTv.setText(status);
  }

  @Override
  public boolean isActive() {
    return isAdded();
  }
}
