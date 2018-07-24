package org.opensandiego.webikesd.views.record;


import org.opensandiego.webikesd.di.ActivityScoped;

import dagger.Binds;
import dagger.Module;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link MonitorPresenter}.
 */
@Module
public abstract class TrackingModule {
  @ActivityScoped
  @Binds
  abstract TrackingContract.Presenter taskPresenter(TrackingPresenter presenter);
}
