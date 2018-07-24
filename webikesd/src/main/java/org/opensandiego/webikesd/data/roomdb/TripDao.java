package org.opensandiego.webikesd.data.roomdb;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.google.common.base.Optional;

import org.opensandiego.webikesd.data.model.Trip;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface TripDao extends BaseDao<Trip> {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insert(Trip trip);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertAll(List<Trip> tripData);

  @Update(onConflict = OnConflictStrategy.REPLACE)
  void update(Trip trip);

  @Query("SELECT * from trips ORDER BY end_time ASC")
  Flowable<List<Trip>> getAll();

  @Query("SELECT * FROM trips WHERE uid == :uid LIMIT 1")
  Flowable<Optional<Trip>> get(String uid);

  @Query("DELETE FROM trips WHERE uid == :uid")
  void delete(String uid);

  @Query("DELETE FROM trips")
  void deleteAll();
}
