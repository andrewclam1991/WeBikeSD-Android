package org.opensandiego.webikesd.data.roomdb;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import org.opensandiego.webikesd.data.model.CyclePoint;
import org.opensandiego.webikesd.data.model.Trip;
import org.opensandiego.webikesd.data.model.TripCyclePoint;

/**
 * Base class for Android Room to generate an App SQLite database.
 */
@Database(entities = {CyclePoint.class, Trip.class, TripCyclePoint.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
  public abstract CyclePointDao cyclePointDao();

  public abstract TripDao tripDataDao();

  public abstract TripCyclePointDao tripCyclePointDao();
}
