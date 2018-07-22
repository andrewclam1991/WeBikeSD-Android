package org.opensandiego.webikesd.views.record;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import org.opensandiego.webikesd.R;

/**
 * This {@link MonitorContract.View} class is responsible for
 * 1. Starts and ends a background service for tracking location updates.
 * 2. Delegate user onTripStart/onTripPaused/onTripCancelled/onTripComplete trip requests to a
 * background service.
 * 3. React to service updates (eg. Trip data) and show user trip status.
 * <p>
 * This {@link TrackingContract.View} class is responsible for
 * 1. Request necessary user permissions with prompts.
 * 2. Check if location settings satisfies requirements.
 */
public class MonitorActivity extends AppCompatActivity implements TrackingContract.View,
    MonitorContract.View {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_record);
  }

  @Override
  protected void onStart() {
    super.onStart();
    Intent intent = new Intent(this, TrackingService.class);
    bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (mService != null && mBound) {
      mService.dropView();
      unbindService(mConnection);
      mBound = false;
    }
  }

  /**
   * Details that defines callbacks for service binding, passed to bindService()
   */
  private boolean mBound;
  private TrackingContract.Service mService;
  private ServiceConnection mConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {
      TrackingService.ServiceBinder binder = (TrackingService.ServiceBinder) service;
      mService = binder.getService();
      mService.setView(MonitorActivity.this);
      mBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) { mBound = false; }
  };

  /**
   * Check if user has given the app the necessary permissions
   */
  private static final int REQUEST_LOCATION_PERMISSION = 1001;

  @Override
  public void checkLocationPermissions() {
    // Permission is not granted
    // Should we show an explanation?
    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
        Manifest.permission.ACCESS_FINE_LOCATION)) {
      // Show an explanation to the user *asynchronously* -- don't block
      // this thread waiting for the user's response! After the user
      // sees the explanation, try again to request the permission.
    } else {
      // No explanation needed; request the permission
      ActivityCompat.requestPermissions(this,
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
    if (mService == null || !mBound) { return; }

    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
        .addLocationRequest(mService.getLocationRequest());

    SettingsClient client = LocationServices.getSettingsClient(this);
    Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

    task.addOnSuccessListener(response -> {
      /* location setting satisfied */
      if (mService == null || !mBound) { return; }
      mService.startLocationUpdates();
    });

    task.addOnFailureListener(e -> {
      if (e instanceof ResolvableApiException) {
        /* location setting not satisfied */
        // but this can be fixed by showing the user a dialog.
        try {
          // Show the dialog by calling startResolutionForResult(),
          // and check the result in onActivityResult().
          ResolvableApiException resolvable = (ResolvableApiException) e;
          resolvable.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
        } catch (IntentSender.SendIntentException sendEx) {
          // Ignore the error.
        }
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
    return true;
  }
}
