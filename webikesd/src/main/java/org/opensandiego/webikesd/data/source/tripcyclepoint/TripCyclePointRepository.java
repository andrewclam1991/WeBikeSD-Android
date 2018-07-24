package org.opensandiego.webikesd.data.source.tripcyclepoint;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import org.opensandiego.webikesd.data.model.TripCyclePoint;
import org.opensandiego.webikesd.data.source.Repository;
import org.opensandiego.webikesd.data.source.annotations.Local;
import org.opensandiego.webikesd.data.source.annotations.Remote;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Repository implementation responsible for managing caching,
 * local and remote instances of {@link TripCyclePointDataSource}
 */

@Singleton
class TripCyclePointRepository extends Repository<TripCyclePoint> implements
    TripCyclePointDataSource {

  @NonNull
  private final TripCyclePointDataSource mLocalDataSource;

  @NonNull
  private final TripCyclePointDataSource mRemoteDataSource;

  @VisibleForTesting
  @Inject
  TripCyclePointRepository(@NonNull @Local TripCyclePointDataSource localDataSource,
                           @NonNull @Remote TripCyclePointDataSource remoteDataSource) {
    super(localDataSource, remoteDataSource);
    mLocalDataSource = checkNotNull(localDataSource);
    mRemoteDataSource = checkNotNull(remoteDataSource);
  }

}
