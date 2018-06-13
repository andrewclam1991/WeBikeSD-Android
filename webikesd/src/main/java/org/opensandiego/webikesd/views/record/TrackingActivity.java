package org.opensandiego.webikesd.views.record;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.opensandiego.webikesd.R;

/**
 * This {@link TrackingContract.View} class is responsible for
 * 1. Request necessary user permissions with prompts.
 * 2. Starting a background service for tracking location updates.
 * 3. Delegate user start/pause/cancel/complete trip requests to a background service.
 * 4. React to service updates (eg. Trip data) and show user trip status.
 */
public class TrackingActivity extends AppCompatActivity implements TrackingContract.View {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_record);
    createLocationRequest();
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
    mService.dropView();
    unbindService(mConnection);
    mBound = false;
  }

  /** Details that defines callbacks for service binding, passed to bindService() */
  private boolean mBound;
  private TrackingContract.Service mService;
  private ServiceConnection mConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {
      TrackingService.ServiceBinder binder = (TrackingService.ServiceBinder) service;
      mService = binder.getService();
      mService.setView(TrackingActivity.this);
      mBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) { mBound = false; }
  };

  /**
   * Details checking if location settings are satisfied before starting
   * a location update session
   */
  private LocationRequest mLocationRequest;
  private static final int REQUEST_CHECK_SETTINGS = 1001;
  protected void createLocationRequest() {
    mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval(10000);
    mLocationRequest.setFastestInterval(5000);
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
        .addLocationRequest(mLocationRequest);

    SettingsClient client = LocationServices.getSettingsClient(this);
    Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

    task.addOnSuccessListener(response -> { /* location setting satisfied */ });

    task.addOnFailureListener(e -> {
      if (e instanceof ResolvableApiException) {
        // Location settings are not satisfied, but this can be fixed
        // by showing the user a dialog.
        try {
          // Show the dialog by calling startResolutionForResult(),
          // and check the result in onActivityResult().
          ResolvableApiException resolvable = (ResolvableApiException) e;
          resolvable.startResolutionForResult(TrackingActivity.this,
              REQUEST_CHECK_SETTINGS);
        } catch (IntentSender.SendIntentException sendEx) {
          // Ignore the error.
        }
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == RESULT_OK){
      createLocationRequest();
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void showTripTime(long duration) {
    // TODO impl formatting duration to user
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
    return false;
  }
}
