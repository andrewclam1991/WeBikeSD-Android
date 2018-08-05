package org.opensandiego.webikesd.views.dashboard.tracking;


import android.support.annotation.NonNull;

import org.opensandiego.webikesd.data.model.TripId;
import org.opensandiego.webikesd.di.FragmentScoped;
import org.opensandiego.webikesd.di.ServiceScoped;
import org.opensandiego.webikesd.views.dashboard.monitor.MonitorPresenter;

import java.util.UUID;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link MonitorPresenter}.
 */
@Module
public abstract class TrackingModule {
  @FragmentScoped
  @ContributesAndroidInjector
  abstract TrackingFragment providesFragment();

  @NonNull
  @ServiceScoped
  @Binds
  abstract TrackingContract.Presenter providesPresenter(TrackingPresenter presenter);

  @NonNull
  @Provides
  @ServiceScoped
  @TripId
  static String provideTripId() {
    return UUID.randomUUID().toString();
  }

}
