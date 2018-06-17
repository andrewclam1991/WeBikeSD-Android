package org.opensandiego.webikesd.views.record;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.opensandiego.webikesd.data.model.CyclePoint;
import org.opensandiego.webikesd.data.model.TripData;

import java.util.UUID;

/**
 * Foreground Service that is responsible for tracking trip location
 * and delegate saving the location updates to its presenter
 */
class TrackingService extends Service implements TrackingContract.Service {
  // Binder given to clients
  private final IBinder mBinder = new ServiceBinder();

  // View
  @Nullable
  private TrackingContract.View mView;

  // TODO inject presenter
  private TrackingContract.Presenter mPresenter;

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
    setupLocationProvider();
  }

  @Override
  public void showTrip(@NonNull TripData tripData) {
    if (mView != null && mView.isActive()){
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

  /**
   * Setup Google Play Service location provider components
   */
  private void setupLocationProvider() {
    mLocationClient = LocationServices.getFusedLocationProviderClient(this);
    mLocationRequest = new LocationRequest()
        .setInterval(10000)
        .setFastestInterval(5000)
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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

  @SuppressLint("MissingPermission")
  public void startLocationUpdates(){
    mLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
  }

  @Override
  public void stopLocationUpdates() {
    mLocationClient.removeLocationUpdates(mLocationCallback);
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
