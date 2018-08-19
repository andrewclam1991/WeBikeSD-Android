package org.opensandiego.webikesd.di.modules;

/*
  Created by lamch on 12/30/2017.
 */

import android.support.annotation.NonNull;

import org.opensandiego.webikesd.util.schedulers.BaseSchedulerProvider;
import org.opensandiego.webikesd.util.schedulers.SchedulerProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
abstract public class SchedulerProviderModule {

  @NonNull
  @Singleton
  @Provides
  static BaseSchedulerProvider provideSchedulerProvider() {
    return SchedulerProvider.getInstance();
  }
}
