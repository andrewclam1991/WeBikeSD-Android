package org.opensandiego.webikesd.views.dashboard.tracking;


import android.support.annotation.NonNull;

import org.opensandiego.webikesd.di.FragmentScoped;
import org.opensandiego.webikesd.views.dashboard.monitor.MonitorPresenter;

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

  @NonNull
  @Binds
  abstract TrackingContract.Presenter trackingPresenter(TrackingPresenter presenter);

//  @NonNull
//  @ServiceScoped
//  @Provides
//  @TripId
//  static String provideTripId() {
//    return UUID.randomUUID().toString();
//  }

}
