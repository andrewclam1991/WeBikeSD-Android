/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensandiego.webikesd.data.source.tripcyclepoint;

import android.support.annotation.NonNull;

import org.opensandiego.webikesd.data.model.CyclePoint;
import org.opensandiego.webikesd.data.model.TripCyclePoint;
import org.opensandiego.webikesd.data.roomdb.TripCyclePointDao;
import org.opensandiego.webikesd.data.source.LocalDataSource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Flowable;


/**
 * Concrete implementation of a data source as a db.
 */
@Singleton
class TripCyclePointLocalDataSource extends LocalDataSource<TripCyclePoint> implements
    TripCyclePointDataSource {

  @NonNull
  private final TripCyclePointDao mTripCyclePointDao;

  @Inject
  TripCyclePointLocalDataSource(@NonNull TripCyclePointDao tripCyclePointDao) {
    super(tripCyclePointDao);
    this.mTripCyclePointDao = tripCyclePointDao;
  }

  @NonNull
  @Override
  public Flowable<List<CyclePoint>> getCyclePtsByTripId(@NonNull String tripId) {
    return mTripCyclePointDao.getCyclePtsByTripId(tripId);
  }

  @NonNull
  @Override
  public Completable deleteAll(@NonNull String tripId) {
    return Completable.create(emitter -> {
      mTripCyclePointDao.deleteAll(tripId);
      emitter.onComplete();
    });
  }
}
