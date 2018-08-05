package org.opensandiego.webikesd.views.dashboard.monitor;

import org.opensandiego.webikesd.views.BasePresenter;
import org.opensandiego.webikesd.views.BaseView;

interface MonitorContract {

  interface View extends BaseView {
    void showTripTime(long duration);

    void showTripSpeed(double speed);

    void showTripDistance(double distance);

    void showTripStatus(String status);

    void showLocationStatus(String status);
  }

  interface Presenter extends BasePresenter<View> {
    void loadTrip();
  }

}
