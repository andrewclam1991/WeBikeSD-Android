package org.opensandiego.webikesd.views.record;

import android.support.annotation.NonNull;

import org.opensandiego.webikesd.data.model.CyclePoint;
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

//    void checkLocationSettings();
//    void checkLocationPermissions();
  }

  interface Service extends BaseView, State {
    void setView(View view);
    void dropView();
    void showTrip(@NonNull TripData tripData);
    void startLocationUpdates();
    void stopLocationUpdates();
    void dropService();
  }

  interface Presenter extends BasePresenter<Service>, State{
    void loadTrip();
  }

  interface State {
    void start();
    void update(CyclePoint pt);
    void pause();
    void cancel();
    void complete();
  }
}
