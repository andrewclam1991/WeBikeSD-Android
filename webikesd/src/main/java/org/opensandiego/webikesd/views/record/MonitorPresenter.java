package org.opensandiego.webikesd.views.record;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MonitorPresenter implements MonitorContract.Presenter {

  @Nullable
  private MonitorContract.View mView;

  @Inject
  MonitorPresenter() {}

  @Override
  public void loadTrip() {

  }

  @Override
  public void setView(MonitorContract.View view) {
    mView = view;
  }

  @Override
  public void dropView() {
    mView = null;
  }
}
