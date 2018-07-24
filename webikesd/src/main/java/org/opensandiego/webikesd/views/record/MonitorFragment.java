package org.opensandiego.webikesd.views.record;


import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import org.opensandiego.webikesd.R;
import org.opensandiego.webikesd.di.ActivityScoped;

import javax.annotation.Nullable;
import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;


/**
 * A simple {@link Fragment} subclass.
 * <p>
 * This {@link MonitorContract.View} implementation is responsible for
 * 1. Starts and ends a background service for tracking location updates.
 * 2. Delegate user startTrip/pauseTrip/cancelTrip/completeTrip trip requests to a
 * background service.
 * 3. React to service updates (eg. Trip data) and show user trip status.
 * <p>
 * This {@link TrackingContract.View} implementation is responsible for
 * 1. Request necessary user permissions with prompts.
 * 2. Check if location settings satisfies requirements.
 */
@ActivityScoped
public class MonitorFragment extends DaggerFragment implements TrackingContract.View,
    MonitorContract.View {

  @BindView(R.id.fragment_monitor_start_trip_btn)
  View mStartTripBtn;

  @BindView(R.id.fragment_monitor_pause_trip_btn)
  View mPauseTripBtn;

  @BindView(R.id.fragment_monitor_cancel_trip_btn)
  View mCancelTripBtn;

  @BindView(R.id.fragment_monitor_complete_trip_btn)
  View mCompleteTripBtn;

  @Inject
  MonitorContract.Presenter mPresenter;

  // Details that defines callbacks for service binding, passed to bindService()
  private boolean mBound = false;

  @Nullable
  private TrackingContract.Service mService;

  @Nullable
  private ServiceConnection mServiceConnection;

  public MonitorFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View rootView = inflater.inflate(R.layout.fragment_monitor, container, false);
    ButterKnife.bind(this, rootView);

    mStartTripBtn.setOnClickListener(v -> {
      if (mService != null && mBound) {
        mService.startTrip();
      }
    });

    mPauseTripBtn.setOnClickListener(v -> {
      if (mService != null && mBound) {
        mService.pauseTrip();
      }
    });

    mCancelTripBtn.setOnClickListener(v -> {
      if (mService != null && mBound) {
        mService.cancelTrip();
      }
    });

    mCompleteTripBtn.setOnClickListener(v -> {
      if (mService != null && mBound) {
        mService.completeTrip();
      }
    });

    return rootView;
  }

  @Override
  public void onStart() {
    super.onStart();
    mPresenter.setView(this);

    if (getActivity() != null) {
      // Bind service
      Intent intent = new Intent(getActivity(), TrackingService.class);
      getActivity().bindService(intent, getServiceConnection(), Context.BIND_AUTO_CREATE);
      // Start the service, if not already started
      TrackingService.startService(getActivity());
    }

  }

  @Override
  public void onStop() {
    super.onStop();
    mPresenter.dropView();

    if (getActivity() != null) {
      // Unbind service
      getActivity().unbindService(getServiceConnection());
    }

    if (mService != null && mBound) {
      mService.dropView();
      mBound = false;
    }
  }

  /**
   * Defines the behaviors when the {@link ServiceConnection} is called
   *
   * @return instance of a {@link ServiceConnection} callback
   */
  private ServiceConnection getServiceConnection() {
    if (mServiceConnection == null) {
      mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
          TrackingService.ServiceBinder binder = (TrackingService.ServiceBinder) service;
          mService = binder.getService();
          mService.setView(MonitorFragment.this);
          mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) { mBound = false; }
      };
    }
    return mServiceConnection;
  }

  /**
   * Check if user has given the app the necessary permissions
   */
  private static final int REQUEST_LOCATION_PERMISSION = 1001;

  @Override
  public void checkLocationPermissions() {
    if (getActivity() == null) { return; }
    // Permission is not granted
    // Should we show an explanation?
    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
        Manifest.permission.ACCESS_FINE_LOCATION)) {
      // Show an explanation to the user *asynchronously* -- don't block
      // this thread waiting for the user's response! After the user
      // sees the explanation, try again to request the permission.
    } else {
      // No explanation needed; request the permission
      ActivityCompat.requestPermissions(getActivity(),
          new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
          REQUEST_LOCATION_PERMISSION);
    }
  }

  /**
   * Check if user's device location settings satisfies request requirements
   */
  private static final int REQUEST_CHECK_SETTINGS = 1002;

  @Override
  public void checkLocationSettings() {
    if (getActivity() == null) { return; }
    if (mService == null || !mBound) { return; }

    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
        .addLocationRequest(mService.getLocationRequest());

    SettingsClient client = LocationServices.getSettingsClient(getActivity());
    Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

    task.addOnSuccessListener(response -> {
      /* location setting satisfied */
      if (mService == null || !mBound) { return; }
      TrackingService.startService(getActivity());
      mService.startTrip();
    });

    task.addOnFailureListener(e -> {
      if (e instanceof ResolvableApiException) {
        /* location setting not satisfied */
        // but this can be fixed by showing the user a dialog.
        try {
          // Show the dialog by calling startResolutionForResult(),
          // and check the result in onActivityResult().
          ResolvableApiException resolvable = (ResolvableApiException) e;
          resolvable.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
        } catch (IntentSender.SendIntentException sendEx) {
          // Ignore the error.
        }
      }
    });
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case REQUEST_LOCATION_PERMISSION:
        checkLocationPermissions();
        break;
      case REQUEST_CHECK_SETTINGS:
        checkLocationSettings();
        break;
    }
    super.onActivityResult(requestCode, resultCode, data);
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
