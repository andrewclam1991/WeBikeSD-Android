package org.opensandiego.webikesd.data.source.tripcyclepoint;


import android.support.annotation.NonNull;

import org.opensandiego.webikesd.data.model.CyclePoint;
import org.opensandiego.webikesd.data.model.Trip;
import org.opensandiego.webikesd.data.model.TripCyclePoint;
import org.opensandiego.webikesd.data.source.DataSource;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * API exposes {@link TripCyclePoint} specific data source requirements
 */
public interface TripCyclePointDataSource extends DataSource<TripCyclePoint> {

  /* Define specific methods for future extension*/

  /**
   * Get a list of {@link CyclePoint}s that are associated with a {@link Trip}
   *
   * @param tripId the unique id that identifies the {@link Trip}
   * @return observable emission of {@link CyclePoint}s that is associated with the
   * particular {@link Trip} by id
   */
  @NonNull
  Flowable<List<CyclePoint>> getCyclePtsByTripId(@NonNull String tripId);

  /**
   * Deletes all {@link CyclePoint}s that are associated with a {@link Trip} by id
   *
   * @param tripId the unique id that identifies the {@link Trip}
   * @return observable completable event
   */
  @NonNull
  Completable deleteAll(@NonNull String tripId);

}
