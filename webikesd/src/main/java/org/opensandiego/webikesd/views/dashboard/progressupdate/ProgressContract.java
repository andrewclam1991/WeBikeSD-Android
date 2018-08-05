package org.opensandiego.webikesd.views.dashboard.progressupdate;

import org.opensandiego.webikesd.views.BasePresenter;
import org.opensandiego.webikesd.views.BaseView;

public interface ProgressContract {

  interface View extends BaseView {
    void showCurrentProgress(int percentage);

    void showGetCurrentProgressError();

    void showSetCurrentProgressSuccess();

    void showSetCurrentProgressFailure();
  }

  interface Presenter extends BasePresenter<View> {
    void getProgress();

    void setProgress(int progress);

    void incrementProgress();
  }

}
