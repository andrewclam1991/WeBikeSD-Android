package org.opensandiego.webikesd.data.source.cyclepoint;

import android.support.annotation.NonNull;

import org.opensandiego.webikesd.data.source.annotations.Local;
import org.opensandiego.webikesd.data.source.annotations.Remote;
import org.opensandiego.webikesd.data.source.annotations.Repo;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

/**
 * This is used by Dagger to inject the required arguments into the {@link CyclePointRepository}.
 */
@Module
abstract public class CyclePointRepositoryModule {

  @Binds
  @NonNull
  @Singleton
  @Repo
  abstract CyclePointDataSource providesRepository(@NonNull CyclePointRepository repository);

  @NonNull
  @Singleton
  @Binds
  @Local
  abstract CyclePointDataSource providesLocalDataSource(@NonNull CyclePointLocalDataSource
                                                            dataSource);

  @NonNull
  @Singleton
  @Binds
  @Remote
  abstract CyclePointDataSource providesRemoteDataSource(@NonNull CyclePointLocalDataSource
                                                             dataSource);

}
