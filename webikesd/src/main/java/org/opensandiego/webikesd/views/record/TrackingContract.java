package org.opensandiego.webikesd.views.record;

import android.support.annotation.NonNull;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;

import org.opensandiego.webikesd.data.model.TripData;
import org.opensandiego.webikesd.views.BasePresenter;
import org.opensandiego.webikesd.views.BaseView;

interface TrackingContract {

  interface View extends BaseView {
    void showTripTime(long duration);
    void showTripSpeed(double speed);
    void showTripDistance(double distance);
    void showTripStatus(String status);
    void showLocationStatus(String status);
    void checkLocationSettings();
    void checkLocationPermissions();
  }

  interface Service extends BaseView, TripState {
    void setView(View view);
    void dropView();
    void startLocationUpdates();
    void stopLocationUpdates();
    void dropService();

    @NonNull
    LocationRequest getLocationRequest();

    @NonNull
    LocationCallback getLocationCallback();

  }

  // ================ End Framework Dependencies ====================== /

  interface TripState {
    void onTripStart();
    void onTripUpdate(double latitude, double longitude);
    void onTripPaused();
    void onTripCancelled();
    void onTripComplete();
  }

  /**
   * Defines presenter responsibilities
   * Note: Within the context of {@link TrackingContract}, this
   * presenter is solely responsible for
   * - managing {@link TripState}
   * - react to service updates, uses model to persist the updates
   */
  interface Presenter extends BasePresenter<Service>, TripState {
    void loadTrip();
  }

}
