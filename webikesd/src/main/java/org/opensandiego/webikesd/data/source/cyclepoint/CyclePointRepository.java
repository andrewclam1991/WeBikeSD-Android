package org.opensandiego.webikesd.data.source.cyclepoint;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import org.opensandiego.webikesd.data.model.CyclePoint;
import org.opensandiego.webikesd.data.source.Repository;
import org.opensandiego.webikesd.data.source.annotations.Local;
import org.opensandiego.webikesd.data.source.annotations.Remote;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Repository implementation responsible for managing caching,
 * local and remote instances of {@link CyclePointDataSource}
 */

@Singleton
class CyclePointRepository extends Repository<CyclePoint> implements CyclePointDataSource {

  @NonNull
  private final CyclePointDataSource mLocalDataSource;

  @NonNull
  private final CyclePointDataSource mRemoteDataSource;

  @VisibleForTesting
  @Inject
  CyclePointRepository(@NonNull @Local CyclePointDataSource localDataSource,
                       @NonNull @Remote CyclePointDataSource remoteDataSource) {
    super(localDataSource, remoteDataSource);
    mLocalDataSource = checkNotNull(localDataSource);
    mRemoteDataSource = checkNotNull(remoteDataSource);
  }

}
