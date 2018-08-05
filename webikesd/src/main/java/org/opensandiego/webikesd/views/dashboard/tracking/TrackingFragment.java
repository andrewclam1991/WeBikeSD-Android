package org.opensandiego.webikesd.views.dashboard.tracking;


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
import org.opensandiego.webikesd.views.FragmentServiceBinderListener;

import javax.annotation.Nullable;
import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;
import timber.log.Timber;


/**
 * A simple {@link DaggerFragment} subclass.
 * This {@link TrackingContract.View} implementation is responsible for
 * 1. Request necessary user permissions with prompts.
 * 2. Check if location settings satisfies requirements.
 * 3. Starts and ends a background service for tracking location updates.
 * 4. Delegate user startTrip/pauseTrip/cancelTrip/completeTrip trip requests to a
 * background service.
 */
@ActivityScoped
public class TrackingFragment extends DaggerFragment implements TrackingContract.View {

  @BindView(R.id.fragment_tracking_start_trip_btn)
  View mStartTripBtn;

  @BindView(R.id.fragment_tracking_pause_trip_btn)
  View mPauseTripBtn;

  @BindView(R.id.fragment_tracking_cancel_trip_btn)
  View mCancelTripBtn;

  @BindView(R.id.fragment_tracking_complete_trip_btn)
  View mCompleteTripBtn;

  // Details that defines callbacks for service binding, passed to bindService()
  private boolean mBound = false;

  @Nullable
  private FragmentServiceBinderListener mServiceBinderListener;

  @Nullable
  private TrackingContract.Service mService;

  @Nullable
  private ServiceConnection mServiceConnection;

  @Inject
  public TrackingFragment() {
    // Required empty public constructor
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof FragmentServiceBinderListener) {
      mServiceBinderListener = (FragmentServiceBinderListener) context;
    } else {
      throw new IllegalArgumentException("Context must implement " +
          FragmentServiceBinderListener.class.getSimpleName());
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mServiceBinderListener = null;
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View rootView = inflater.inflate(R.layout.fragment_tracking, container, false);
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
    Timber.d("View onStart(), prepare to bind View to TrackingService...");
    if (mServiceBinderListener != null) {
      mServiceBinderListener.onRequestBindService(TrackingService.class,
          getServiceConnection(), Context.BIND_AUTO_CREATE);
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    Timber.d("View onStop(), prepare to unbind View from TrackingService...");

    if (mService != null && mService.isActive()) {
      mService.dropView();
      Timber.d("Service is active, called to drop View.");
    }

    if (mServiceBinderListener != null && mBound) {
      mServiceBinderListener.onRequestUnbindService(getServiceConnection());
      mBound = false;
    }
  }

  /**
   * Defines the behaviors when the {@link ServiceConnection} is called
   *
   * @return instance of a {@link ServiceConnection} callback
   */
  @NonNull
  private ServiceConnection getServiceConnection() {
    if (mServiceConnection == null) {
      mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
          TrackingService.ServiceBinder binder = (TrackingService.ServiceBinder) service;
          mService = binder.getService();
          mService.setView(TrackingFragment.this);
          mBound = true;
          Timber.d("View bound to TrackingService.");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
          mBound = false;
          Timber.d("View is unbound from TrackingService.");
        }
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
  public boolean isActive() {
    return isAdded();
  }
}
