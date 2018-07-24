package org.opensandiego.webikesd.views.record;

import com.google.common.base.Optional;

import org.opensandiego.webikesd.data.model.CyclePoint;
import org.opensandiego.webikesd.data.model.Trip;
import org.opensandiego.webikesd.data.model.TripCyclePoint;
import org.opensandiego.webikesd.data.source.annotations.Repo;
import org.opensandiego.webikesd.data.source.cyclepoint.CyclePointDataSource;
import org.opensandiego.webikesd.data.source.trip.TripDataSource;
import org.opensandiego.webikesd.data.source.tripcyclepoint.TripCyclePointDataSource;
import org.opensandiego.webikesd.util.schedulers.SchedulerProvider;

import java.util.UUID;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

@Singleton
class TrackingPresenter implements TrackingContract.Presenter {

  // Services (Invisible View)
  @Nullable
  private TrackingContract.Service mService;

  // Internal states
  @NonNull
  private TrackingContract.TripState mCurrentTripState;
  @NonNull
  private TrackingContract.TripState mNoTripTripState;
  @NonNull
  private TrackingContract.TripState mTripStartedTripState;
  @NonNull
  private TrackingContract.TripState mTripPausedTripState;

  // Data
  @NonNull
  private final TripCyclePointDataSource mTripCyclePtRepo;
  @NonNull
  private final TripDataSource mTripDataRepo;
  @NonNull
  private final CyclePointDataSource mCyclePtRepo;
  @NonNull
  private final SchedulerProvider mSchedulerProvider;
  @NonNull
  private final CompositeDisposable mCompositeDisposable;
  @NonNull
  private final String mTripId;
  @Nullable
  private Trip mTrip;

  @Inject
  TrackingPresenter(@NonNull @Repo TripCyclePointDataSource tripCyclePtRepo,
                    @NonNull @Repo TripDataSource tripDataRepo,
                    @NonNull @Repo CyclePointDataSource cyclePtRepo,
                    @NonNull SchedulerProvider schedulerProvider,
                    @NonNull String tripId) {
    mTripCyclePtRepo = tripCyclePtRepo;
    mTripDataRepo = tripDataRepo;
    mCyclePtRepo = cyclePtRepo;
    mSchedulerProvider = schedulerProvider;
    mCompositeDisposable = new CompositeDisposable();
    mTripId = tripId;

    // Setup internal states
    mNoTripTripState = new NoTripState();
    mTripStartedTripState = new TripStartedState();
    mTripPausedTripState = new TripPausedState();
    mCurrentTripState = mNoTripTripState;
  }

  @Override
  public void setView(TrackingContract.Service view) { mService = view; }

  @Override
  public void dropView() {
    mCompositeDisposable.clear();
    mService = null;
  }

  @Override
  public void loadTrip() {
    Disposable disposable = mTripDataRepo
        .get(mTripId)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .subscribeOn(mSchedulerProvider.io())
        .observeOn(mSchedulerProvider.ui())
        .subscribe(
            // onNext
            this::setTripData,
            // onError
            Throwable::printStackTrace
        );

    mCompositeDisposable.add(disposable);
  }

  private void setTripData(@NonNull Trip trip) {
    mTrip = trip;
  }

  @Override
  public void startTrip() { mCurrentTripState.startTrip(); }

  @Override
  public void updateTrip(double latitude, double longitude) {
    mCurrentTripState.updateTrip
        (latitude, longitude);
  }

  @Override
  public void pauseTrip() { mCurrentTripState.pauseTrip(); }

  @Override
  public void cancelTrip() { mCurrentTripState.cancelTrip(); }

  @Override
  public void completeTrip() { mCurrentTripState.completeTrip(); }

  /**
   * Concrete internal {@link TrackingContract.TripState} when there is no
   * trip or trip has stopped.
   */
  private class NoTripState implements TrackingContract.TripState {
    @Override
    public void startTrip() {
      // create and startTrip a new trip
      Trip trip = new Trip(mTripId);

      Disposable disposable = mTripDataRepo
          .add(trip)
          .subscribeOn(mSchedulerProvider.io())
          .observeOn(mSchedulerProvider.ui())
          .subscribe(() -> {
            mCurrentTripState = mTripStartedTripState;
            if (mService == null || !mService.isActive()) { return; }
            mService.startLocationUpdates();
          }, Throwable::printStackTrace);

      // add to execution queue
      mCompositeDisposable.add(disposable);
    }

    @Override
    public void updateTrip(double latitude, double longitude) {
      // Ignore invalid request, no trip to update
    }

    @Override
    public void pauseTrip() {
      // Ignore invalid request, no trip to pause
    }

    @Override
    public void cancelTrip() {
      if (mService == null || !mService.isActive()) { return; }
      mService.stopLocationUpdates();
      mService.stopService();
    }

    @Override
    public void completeTrip() {
      if (mService == null || !mService.isActive()) { return; }
      mService.stopLocationUpdates();
      mService.stopService();
    }
  }

  /**
   * Concrete internal {@link TrackingContract.TripState} when trip
   * is active and started
   */
  private class TripStartedState implements TrackingContract.TripState {
    @Nullable
    private CyclePoint mLastKnownCyclePoint;

    @Override
    public void startTrip() {
      // Ignore invalid request, trip already started
    }

    @Override
    public void updateTrip(double latitude, double longitude) {
      // Create pt object from location data
      String id = UUID.randomUUID().toString();
      long timestamp = System.currentTimeMillis();
      CyclePoint currentPt = new CyclePoint(id, latitude, longitude, timestamp);

      if (mLastKnownCyclePoint == null) {
        // first pt, no speed
        mLastKnownCyclePoint = currentPt;
        currentPt.setSpeed(0);
      } else {
        // TODO calculate speed base on distance and time elapsed
        double elapsedTime = currentPt.getTime() - mLastKnownCyclePoint.getTime();
        currentPt.setSpeed(10);
      }

      // Create trip <-> association record
      String tripCyclePtUid = UUID.randomUUID().toString();
      TripCyclePoint tripCyclePt = new TripCyclePoint(tripCyclePtUid, mTripId, currentPt.getUid());

      // Update the cycle point and then add the trip <-> point association record
      Disposable disposable = mCyclePtRepo
          .add(currentPt)
          .andThen(mTripCyclePtRepo.add(tripCyclePt))
          .subscribeOn(mSchedulerProvider.io())
          .observeOn(mSchedulerProvider.io())
          .subscribe();

      // add to execution queue
      mCompositeDisposable.add(disposable);
    }

    @Override
    public void pauseTrip() {
      mCurrentTripState = mTripPausedTripState;
      if (mService == null || !mService.isActive()) { return; }
      mService.stopLocationUpdates();
    }

    @Override
    public void cancelTrip() {
      mCurrentTripState = mTripPausedTripState;
      mCurrentTripState.cancelTrip();
    }

    @Override
    public void completeTrip() {
      mCurrentTripState = mTripPausedTripState;
      mCurrentTripState.completeTrip();
    }
  }

  /**
   * Concrete internal {@link TrackingContract.TripState} when trip
   * is inactive and paused.
   */
  private class TripPausedState implements TrackingContract.TripState {

    @Override
    public void startTrip() {
      mCurrentTripState = mTripStartedTripState;
      if (mService == null || !mService.isActive()) { return; }
      mService.startLocationUpdates();
    }

    @Override
    public void updateTrip(double latitude, double longitude) {
      // Ignore invalid request, can't updateTrip a paused trip.
    }

    @Override
    public void pauseTrip() {
      // Ignore invalid request, trip already paused.
    }

    @Override
    public void cancelTrip() {
      // cancel trip by deleting from data source
      Disposable disposable = mTripDataRepo
          .delete(mTripId)
          .subscribeOn(mSchedulerProvider.io())
          .observeOn(mSchedulerProvider.ui())
          .subscribe(() -> {
            mCurrentTripState = mNoTripTripState;
            mCurrentTripState.cancelTrip();
          }, Throwable::printStackTrace);

      // add to execution queue
      mCompositeDisposable.add(disposable);
    }

    @Override
    public void completeTrip() {
      // complete trip iff there is data to save
      if (mTrip != null) {
        mTrip.setEndTime(System.currentTimeMillis());
        Disposable disposable = mTripDataRepo
            .add(mTrip)
            .subscribeOn(mSchedulerProvider.io())
            .observeOn(mSchedulerProvider.ui())
            .subscribe(() -> {
              mCurrentTripState = mNoTripTripState;
              mCurrentTripState.completeTrip();
            }, Throwable::printStackTrace);

        // add to execution queue
        mCompositeDisposable.add(disposable);
      }
    }
  }
}
