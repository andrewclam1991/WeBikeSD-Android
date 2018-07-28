package org.opensandiego.webikesd.views.tracking;


import org.opensandiego.webikesd.di.FragmentScoped;
import org.opensandiego.webikesd.di.ServiceScoped;
import org.opensandiego.webikesd.views.monitor.MonitorPresenter;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link MonitorPresenter}.
 */
@Module
public abstract class TrackingModule {
  @FragmentScoped
  @ContributesAndroidInjector
  abstract TrackingFragment trackingFragment();

  @ServiceScoped
  @Binds
  abstract TrackingContract.Presenter trackingPresenter(TrackingPresenter presenter);
}
