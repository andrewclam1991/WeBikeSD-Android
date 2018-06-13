package org.opensandiego.webikesd.views.record;

import android.support.annotation.NonNull;

import org.opensandiego.webikesd.data.model.CyclePoint;
import org.opensandiego.webikesd.data.model.TripData;
import org.opensandiego.webikesd.views.BasePresenter;
import org.opensandiego.webikesd.views.BaseView;


interface RecordContract {
  interface View extends BaseView {
    void showTripTime(long duration);
    void showTripSpeed(double speed);
    void showTripDistance(double distance);
    void showTripStatus(String status);
    void showLocationStatus(String status);
  }

  interface ViewService extends BaseView, RecordState{
    RecordState getState();
    void setState(RecordState state);
    void setView(View view);
    void dropView();
    void showTrip(@NonNull TripData tripData);
  }

  interface RecordState{
    void start();
    void update();
    void pause();
    void cancel();
    void complete();
  }

  interface Presenter extends BasePresenter<ViewService>{
    void loadTrip();
    void saveTrip(TripData tripData);
    void updateTrip(CyclePoint pt);
  }

}
