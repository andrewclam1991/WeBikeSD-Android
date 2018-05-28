package org.opensandiego.webikesd.data.roomdb;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * This is used by Dagger to inject the App {@link RoomDatabase}
 */
@Module
public abstract class AppDatabaseModule {

  @Singleton
  @Provides
  static AppDatabase provideAppDatabase(Application context) {
    return Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "app.db")
        .build();
  }

  @Singleton
  @Provides
  static CyclePointDao provideCyclePointDao(AppDatabase appDatabase)
  { return appDatabase.cyclePointDao(); }

  @Singleton
  @Provides
  static TripDataDao provideTripDataDao(AppDatabase appDatabase)
  { return appDatabase.tripDataDao(); }

  @Singleton
  @Provides
  static TripCyclePointDao provideTripCyclePointDao(AppDatabase appDatabase)
  { return appDatabase.tripCyclePointDao(); }

}
