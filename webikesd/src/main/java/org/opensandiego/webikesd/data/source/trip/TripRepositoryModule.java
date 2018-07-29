package org.opensandiego.webikesd.data.source.trip;

import android.support.annotation.NonNull;

import org.opensandiego.webikesd.data.source.annotations.Local;
import org.opensandiego.webikesd.data.source.annotations.Remote;
import org.opensandiego.webikesd.data.source.annotations.Repo;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

/**
 * This is used by Dagger to inject the required arguments into the {@link TripRepository}.
 */
@Module
abstract public class TripRepositoryModule {

  @NonNull
  @Singleton
  @Binds
  @Repo
  abstract TripDataSource providesRepository(@NonNull TripRepository repository);

  @NonNull
  @Singleton
  @Binds
  @Local
  abstract TripDataSource providesLocalDataSource(@NonNull TripLocalDataSource dataSource);

  @NonNull
  @Singleton
  @Binds
  @Remote
  abstract TripDataSource providesRemoteDataSource(@NonNull TripLocalDataSource dataSource);

}
