package org.opensandiego.webikesd.views.dashboard.monitor;


import org.opensandiego.webikesd.di.ActivityScoped;
import org.opensandiego.webikesd.di.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link MonitorPresenter}.
 */
@Module
public abstract class MonitorModule {
  @FragmentScoped
  @ContributesAndroidInjector
  abstract MonitorFragment monitorFragment();

  @ActivityScoped
  @Binds
  abstract MonitorContract.Presenter monitorPresenter(MonitorPresenter presenter);
}
