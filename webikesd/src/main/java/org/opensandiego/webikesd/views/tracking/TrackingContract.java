package org.opensandiego.webikesd.views.tracking;

import android.support.annotation.NonNull;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;

import org.opensandiego.webikesd.views.BasePresenter;
import org.opensandiego.webikesd.views.BaseView;

interface TrackingContract {

  interface View extends BaseView {
    void checkLocationSettings();

    void checkLocationPermissions();
  }

  interface Service extends BaseView, TripState {
    void setView(View view);

    void dropView();

    void startLocationUpdates();

    void stopLocationUpdates();

    void stopService();

    @NonNull
    LocationRequest getLocationRequest();

    @NonNull
    LocationCallback getLocationCallback();

  }

  // ================ End Framework Dependencies ====================== /

  interface TripState {
    /**
     * Command to start the trip
     */
    void startTrip();

    /**
     * Command to update an existing trip
     *
     * @param latitude  latitude of the current location
     * @param longitude longitude of the current location
     */
    void updateTrip(double latitude, double longitude);

    /**
     * Command to pause the trip, this state is mutable
     */
    void pauseTrip();

    /**
     * Command to cancel (delete) the trip, this state is consider terminal
     * for a particular trip.
     */
    void cancelTrip();

    /**
     * Command to complete the trip, this state is consider terminal
     * for a particular trip.
     */
    void completeTrip();
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
