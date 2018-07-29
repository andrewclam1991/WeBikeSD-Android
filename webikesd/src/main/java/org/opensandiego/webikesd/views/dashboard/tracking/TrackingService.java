package org.opensandiego.webikesd.views.dashboard.tracking;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.opensandiego.webikesd.BuildConfig;
import org.opensandiego.webikesd.di.ServiceScoped;

import javax.inject.Inject;

import dagger.android.DaggerService;

import static org.opensandiego.webikesd.views.dashboard.tracking.TrackingNotification
    .TRACKING_NOTIFICATION_ID;

/**
 * Service Class Responsibilities:
 * - maintain client {@link FusedLocationProviderClient} to tracking device location
 * - delegate saving the location updates to its {@link TrackingContract.Presenter}
 */
@ServiceScoped
public class TrackingService extends DaggerService implements TrackingContract.Service {

  // Debug Log tag
  private static final String LOG_TAG = TrackingService.class.getSimpleName();

  @Inject
  TrackingContract.Presenter mPresenter;

  // View
  @Nullable
  private TrackingContract.View mView;

  // Service binder given to client View
  @NonNull
  private final IBinder mBinder = new ServiceBinder();

  // Google Play Service Location Provider
  @Nullable
  private FusedLocationProviderClient mLocationClient;

  @Nullable
  private LocationRequest mLocationRequest;

  @Nullable
  private LocationCallback mLocationCallback;

  /**
   * Helper method to start an instance of this Service
   */
  @Override
  public void startService() {
    if (BuildConfig.DEBUG) {
      Log.d(LOG_TAG, "startSelf() called to start background service");
    }

    Intent intent = new Intent(getApplicationContext(), TrackingService.class);
    // Check android version for foreground notification requirement
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      startForegroundService(intent);
    } else {
      startService(intent);
    }
  }

  @Override
  public void setView(TrackingContract.View view) { mView = view; }

  @Override
  public void dropView() { mView = null; }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    mPresenter.setView(this);
    // Check android version for foreground notification requirement
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      startForeground(TRACKING_NOTIFICATION_ID, TrackingNotification.build(this));
    }
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
  public void startTrip() { mPresenter.startTrip(); }

  @Override
  public void updateTrip(double latitude, double longitude) {
    mPresenter.updateTrip(latitude, longitude);
  }

  @Override
  public void pauseTrip() { mPresenter.pauseTrip(); }

  @Override
  public void cancelTrip() { mPresenter.cancelTrip(); }

  @Override
  public void completeTrip() { mPresenter.completeTrip(); }

  @NonNull
  @Override
  public IBinder onBind(Intent intent) {
    mPresenter.setView(this);
    return mBinder;
  }

  @NonNull
  @Override
  public final LocationRequest getLocationRequest() {
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
  public final LocationCallback getLocationCallback() {
    if (mLocationCallback == null) {
      mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
          if (locationResult == null) { return; }
          for (Location location : locationResult.getLocations()) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            updateTrip(latitude, longitude);
          }
        }
      };
    }
    return mLocationCallback;
  }

  @NonNull
  private FusedLocationProviderClient getLocationProviderClient() {
    if (mLocationClient == null) {
      mLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }
    return mLocationClient;
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
    getLocationProviderClient().requestLocationUpdates(getLocationRequest(), getLocationCallback
        (), null);
  }

  @Override
  public void stopLocationUpdates() {
    getLocationProviderClient().removeLocationUpdates(getLocationCallback());
  }

  @Override
  public void stopService() {
    stopForeground(true);
    stopSelf();
  }

  /**
   * Class used for the client Binder.  Because we know this service always
   * runs in the same process as its clients, we don't need to deal with IPC.
   */
  public final class ServiceBinder extends Binder {
    TrackingContract.Service getService() { return TrackingService.this; }
  }
}
