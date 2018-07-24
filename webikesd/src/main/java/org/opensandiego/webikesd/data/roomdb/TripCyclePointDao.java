package org.opensandiego.webikesd.data.roomdb;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.google.common.base.Optional;

import org.opensandiego.webikesd.data.model.CyclePoint;
import org.opensandiego.webikesd.data.model.TripCyclePoint;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface TripCyclePointDao extends BaseDao<TripCyclePoint> {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insert(TripCyclePoint pt);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertAll(List<TripCyclePoint> pts);

  @Update(onConflict = OnConflictStrategy.REPLACE)
  void update(TripCyclePoint pt);

  @Query("SELECT * FROM trip_cycle_pts WHERE uid == :uid LIMIT 1")
  Flowable<Optional<TripCyclePoint>> get(String uid);

  @Query("SELECT * FROM trip_cycle_pts")
  Flowable<List<TripCyclePoint>> getAll();

  @Query("DELETE FROM trip_cycle_pts WHERE uid == :itemId")
  void delete(String itemId);

  @Query("DELETE FROM trip_cycle_pts")
  void deleteAll();

  @Query("SELECT cycle_pts.uid, cycle_pts.time, cycle_pts.accuracy, cycle_pts.altitude, " +
      " cycle_pts.lat, cycle_pts.lgt, cycle_pts.speed " +
      "FROM cycle_pts " +
      "INNER JOIN trip_cycle_pts on trip_cycle_pts.tripUid == cycle_pts.uid " +
      "INNER JOIN trips ON trip_cycle_pts.tripUid == trips.uid " +
      "WHERE trips.uid LIKE :tripId ")
  Flowable<List<CyclePoint>> getCyclePtsByTripId(String tripId);

  @Query("DELETE FROM trip_cycle_pts " +
      "WHERE trip_cycle_pts.tripUid == :tripId " +
      "AND trip_cycle_pts.cyclePtUid == :cyclePtId")
  void delete(String tripId, String cyclePtId);

  @Query("DELETE FROM trip_cycle_pts " +
      "WHERE trip_cycle_pts.tripUid == :tripId ")
  void deleteAll(String tripId);
}
