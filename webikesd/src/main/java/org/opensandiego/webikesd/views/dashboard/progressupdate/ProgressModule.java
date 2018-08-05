package org.opensandiego.webikesd.views.dashboard.progressupdate;


import android.support.annotation.NonNull;

import org.opensandiego.webikesd.di.ActivityScoped;
import org.opensandiego.webikesd.di.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link ProgressPresenter}.
 */
@Module
public abstract class ProgressModule {
  @FragmentScoped
  @ContributesAndroidInjector
  abstract ProgressFragment providesFragment();

  @NonNull
  @ActivityScoped
  @Binds
  abstract ProgressContract.Presenter providesPresenter(ProgressPresenter presenter);

}
