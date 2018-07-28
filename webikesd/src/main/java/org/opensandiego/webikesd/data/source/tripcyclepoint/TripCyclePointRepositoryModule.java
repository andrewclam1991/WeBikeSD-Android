package org.opensandiego.webikesd.data.source.tripcyclepoint;

import android.support.annotation.NonNull;

import org.opensandiego.webikesd.data.source.annotations.Local;
import org.opensandiego.webikesd.data.source.annotations.Remote;
import org.opensandiego.webikesd.data.source.annotations.Repo;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

/**
 * This is used by Dagger to inject the required arguments into the
 * {@link TripCyclePointRepository}.
 */
@Module
abstract public class TripCyclePointRepositoryModule {

  @Binds
  @NonNull
  @Singleton
  @Repo
  abstract TripCyclePointDataSource providesRepository(@NonNull TripCyclePointRepository
                                                           repository);

  @NonNull
  @Singleton
  @Binds
  @Local
  abstract TripCyclePointDataSource providesLocalDataSource(@NonNull TripCyclePointLocalDataSource
                                                                dataSource);

  @NonNull
  @Singleton
  @Binds
  @Remote
  abstract TripCyclePointDataSource providesRemoteDataSource(@NonNull TripCyclePointLocalDataSource
                                                                 dataSource);

}
