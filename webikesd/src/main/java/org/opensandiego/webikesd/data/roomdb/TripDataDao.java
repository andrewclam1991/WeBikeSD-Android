package org.opensandiego.webikesd.data.roomdb;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.google.common.base.Optional;

import org.opensandiego.webikesd.data.model.TripData;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface TripDataDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insert(TripData tripData);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertAll(List<TripData> tripData);

  @Update(onConflict = OnConflictStrategy.REPLACE)
  void update(TripData tripData);

  @Query("SELECT * from trips ORDER BY end_time ASC")
  Flowable<List<TripData>> getTrips();

  @Query("SELECT * FROM trips WHERE uid == :uid LIMIT 1")
  Flowable<Optional<TripData>> getTrip(String uid);

  @Query("DELETE FROM trips WHERE uid == :uid")
  void delete(String uid);

  @Query("DELETE FROM trips")
  void deleteAll();
}
