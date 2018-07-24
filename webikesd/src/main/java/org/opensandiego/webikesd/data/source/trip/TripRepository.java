package org.opensandiego.webikesd.data.source.trip;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import org.opensandiego.webikesd.data.model.Trip;
import org.opensandiego.webikesd.data.source.Repository;
import org.opensandiego.webikesd.data.source.annotations.Local;
import org.opensandiego.webikesd.data.source.annotations.Remote;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Repository implementation responsible for managing caching,
 * local and remote instances of {@link TripDataSource}
 */

@Singleton
class TripRepository extends Repository<Trip> implements TripDataSource {

  @NonNull
  private final TripDataSource mLocalDataSource;

  @NonNull
  private final TripDataSource mRemoteDataSource;

  @VisibleForTesting
  @Inject
  TripRepository(@NonNull @Local TripDataSource localDataSource,
                 @NonNull @Remote TripDataSource remoteDataSource) {
    super(localDataSource, remoteDataSource);
    mLocalDataSource = checkNotNull(localDataSource);
    mRemoteDataSource = checkNotNull(remoteDataSource);
  }

}
