package org.opensandiego.webikesd.data.source.progress;

import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

/**
 * This is used by Dagger to inject the App
 */
@Module
public abstract class ProgressDataSourceModule {

  @NonNull
  @Singleton
  @Binds
  abstract ProgressDataSource provideProgressDataSource(ProgressModel progressModel);

  @NonNull
  @Provides
  @ProgressKey
  static String providesProgressKey() {
    return "PROGRESS_KEY";
  }
}
