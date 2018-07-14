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

import org.opensandiego.webikesd.data.model.CyclePoint;
import org.opensandiego.webikesd.data.model.TripData;

import java.util.UUID;

import javax.inject.Inject;

/**
 * Foreground Service that is responsible for tracking trip location
 * and delegate saving the location updates to its presenter
 */
class TrackingService extends Service implements TrackingContract.Service {
  // Binder given to clients
  private final IBinder mBinder = new ServiceBinder();

  @Inject
  TrackingContract.Presenter mPresenter;

  @Nullable
  private TrackingContract.View mView;

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
  public void showTrip(@NonNull TripData tripData) {
    if (mView != null && mView.isActive()) {
      mView.showTripDistance(tripData.getDistance());
    }
  }

  @Override
  public boolean isActive() { return true; }

  @Override
  public void start() { mPresenter.start(); }

  @Override
  public void update(CyclePoint pt) { mPresenter.update(pt); }

  @Override
  public void pause() { mPresenter.pause(); }

  @Override
  public void cancel() { mPresenter.cancel(); }

  @Override
  public void complete() { mPresenter.complete(); }

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
            String id = UUID.randomUUID().toString();
            double lat = location.getLatitude();
            double lgt = location.getLongitude();
            long timestamp = System.currentTimeMillis();
            CyclePoint pt = new CyclePoint(id, lat, lgt, timestamp);
            update(pt);
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
