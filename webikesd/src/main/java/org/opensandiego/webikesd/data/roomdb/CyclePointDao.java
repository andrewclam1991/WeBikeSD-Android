package org.opensandiego.webikesd.data.roomdb;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.google.common.base.Optional;

import org.opensandiego.webikesd.data.model.CyclePoint;
import org.opensandiego.webikesd.data.model.TripData;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface CyclePointDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void put(CyclePoint pt);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void putAll(List<CyclePoint> pts);

  @Update(onConflict = OnConflictStrategy.REPLACE)
  void update(CyclePoint pt);

  @Query("SELECT * from cycle_pts ORDER BY time ASC")
  Flowable<List<CyclePoint>> getAll();

  @Query("SELECT * FROM cycle_pts WHERE uid == :uid LIMIT 1")
  Flowable<Optional<CyclePoint>> get(String uid);

  @Query("DELETE FROM cycle_pts WHERE uid == :uid")
  void delete(String uid);

  @Query("DELETE FROM trips")
  void deleteAll();

}
