package org.opensandiego.webikesd.views.record;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import javax.inject.Inject;

/**
 * Foreground Service
 * Class Responsibilities:
 * - maintain client {@link FusedLocationProviderClient} to tracking device location
 * - delegate saving the location updates to its {@link TrackingContract.Presenter}
 */
class TrackingService extends Service implements TrackingContract.Service {

  @Inject
  TrackingContract.Presenter mPresenter;

  // View
  @Nullable
  private TrackingContract.View mView;

  // Service binder given to client View
  private final IBinder mBinder = new ServiceBinder();

  // Google Play Service Location Provider
  private FusedLocationProviderClient mLocationClient;
  private LocationRequest mLocationRequest;
  private LocationCallback mLocationCallback;

  @Override
  public void setView(TrackingContract.View view) { mView = view; }

  @Override
  public void dropView() { mView = null; }

  @Override
  public void onCreate() {
    super.onCreate();
    mLocationClient = LocationServices.getFusedLocationProviderClient(this);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    mPresenter.setView(this);
    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public boolean onUnbind(Intent intent) {
    mPresenter.dropView();
    return super.onUnbind(intent);
  }

  @Override
  public boolean isActive() { return true; }

  @Override
  public void onTripStart() { mPresenter.onTripStart(); }

  @Override
  public void onTripUpdate(double latitude, double longitude) {
    mPresenter.onTripUpdate(latitude,
        longitude);
  }

  @Override
  public void onTripPaused() { mPresenter.onTripPaused(); }

  @Override
  public void onTripCancelled() { mPresenter.onTripCancelled(); }

  @Override
  public void onTripComplete() { mPresenter.onTripComplete(); }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) { return mBinder; }

  @NonNull
  @Override
  public LocationRequest getLocationRequest() {
    if (mLocationRequest == null) {
      mLocationRequest = new LocationRequest()
          .setInterval(10000)
          .setFastestInterval(5000)
          .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    return mLocationRequest;
  }

  @NonNull
  @Override
  public LocationCallback getLocationCallback() {
    if (mLocationCallback == null) {
      mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
          if (locationResult == null) { return; }
          for (Location location : locationResult.getLocations()) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            onTripUpdate(latitude, longitude);
          }
        }
      };
    }
    return mLocationCallback;
  }


  @Override
  public void startLocationUpdates() {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest
        .permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      // permission not granted, must explicitly request from user
      if (mView != null && mView.isActive()) {
        mView.checkLocationPermissions();
      }
      return;
    }
    mLocationClient.requestLocationUpdates(getLocationRequest(), getLocationCallback(), null);
  }

  @Override
  public void stopLocationUpdates() {
    mLocationClient.removeLocationUpdates(getLocationCallback());
  }

  @Override
  public void dropService() {
    stopSelf();
  }

  /**
   * Class used for the client Binder.  Because we know this service always
   * runs in the same process as its clients, we don't need to deal with IPC.
   */
  public class ServiceBinder extends Binder {
    TrackingContract.Service getService() { return TrackingService.this; }
  }
}
