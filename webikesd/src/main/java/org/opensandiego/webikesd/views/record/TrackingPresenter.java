package org.opensandiego.webikesd.views.record;

import com.google.common.base.Optional;

import org.opensandiego.webikesd.data.model.CyclePoint;
import org.opensandiego.webikesd.data.model.TripCyclePoint;
import org.opensandiego.webikesd.data.model.TripData;
import org.opensandiego.webikesd.data.source.DataSource;
import org.opensandiego.webikesd.data.source.Repo;
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
  private final DataSource<TripCyclePoint> mTripCyclePtRepo;
  @NonNull
  private final DataSource<TripData> mTripDataRepo;
  @NonNull
  private final DataSource<CyclePoint> mCyclePtRepo;
  @NonNull
  private final SchedulerProvider mSchedulerProvider;
  @NonNull
  private final CompositeDisposable mCompositeDisposable;
  @NonNull
  private final String mTripId;
  @Nullable
  private TripData mTripData;

  @Inject
  TrackingPresenter(@NonNull @Repo DataSource<TripCyclePoint> tripCyclePtRepo,
                    @NonNull @Repo DataSource<TripData> tripDataRepo,
                    @NonNull @Repo DataSource<CyclePoint> cyclePtRepo,
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

  private void setTripData(@NonNull TripData tripData){
    mTripData = tripData;
  }

  @Override
  public void onTripStart() { mCurrentTripState.onTripStart(); }

  @Override
  public void onTripUpdate(double latitude, double longitude) { mCurrentTripState.onTripUpdate(latitude, longitude); }

  @Override
  public void onTripPaused() { mCurrentTripState.onTripPaused(); }

  @Override
  public void onTripCancelled() { mCurrentTripState.onTripCancelled(); }

  @Override
  public void onTripComplete() { mCurrentTripState.onTripComplete(); }

  /**
   * Concrete internal {@link TrackingContract.TripState} when there is no
   * trip or trip has stopped.
   */
  private class NoTripState implements TrackingContract.TripState {
    @Override
    public void onTripStart() {
      // create and onTripStart a new trip
      TripData tripData = new TripData(mTripId);

      Disposable disposable = mTripDataRepo
          .put(tripData)
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
    public void onTripUpdate(double latitude, double longitude) {
      // Ignore invalid request, no trip to update
    }

    @Override
    public void onTripPaused() {
      // Ignore invalid request, no trip to pause
    }

    @Override
    public void onTripCancelled() {
      if (mService == null || !mService.isActive()) { return; }
      mService.stopLocationUpdates();
      mService.dropService();
    }

    @Override
    public void onTripComplete() {
      if (mService == null || !mService.isActive()) { return; }
      mService.stopLocationUpdates();
      mService.dropService();
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
    public void onTripStart() {
      // Ignore invalid request, trip already started
    }

    @Override
    public void onTripUpdate(double latitude, double longitude) {
      // Create pt object from location data
      String id = UUID.randomUUID().toString();
      long timestamp = System.currentTimeMillis();
      CyclePoint currentPt = new CyclePoint(id, latitude, longitude, timestamp);

      if (mLastKnownCyclePoint == null){
        // first pt, no speed
        mLastKnownCyclePoint = currentPt;
        currentPt.setSpeed(0);
      }else{
        // TODO calculate speed base on distance and time elapsed
        double elapsedTime = currentPt.getTime() - mLastKnownCyclePoint.getTime();
        currentPt.setSpeed(10);
      }

      // Create trip <-> association record
      String tripCyclePtUid = UUID.randomUUID().toString();
      TripCyclePoint tripCyclePt = new TripCyclePoint(tripCyclePtUid,mTripId, currentPt.getUid());

      // Update the cycle point and then add the trip <-> point association record
      Disposable disposable = mCyclePtRepo
          .put(currentPt)
          .andThen(mTripCyclePtRepo.put(tripCyclePt))
          .subscribeOn(mSchedulerProvider.io())
          .observeOn(mSchedulerProvider.io())
          .subscribe();

      // add to execution queue
      mCompositeDisposable.add(disposable);
    }

    @Override
    public void onTripPaused() {
      mCurrentTripState = mTripPausedTripState;
      if (mService == null || !mService.isActive()) { return; }
      mService.stopLocationUpdates();
    }

    @Override
    public void onTripCancelled() {
      mCurrentTripState = mTripPausedTripState;
      mCurrentTripState.onTripCancelled();
    }

    @Override
    public void onTripComplete() {
      mCurrentTripState = mTripPausedTripState;
      mCurrentTripState.onTripComplete();
    }
  }

  /**
   * Concrete internal {@link TrackingContract.TripState} when trip
   * is inactive and paused.
   */
  private class TripPausedState implements TrackingContract.TripState {

    @Override
    public void onTripStart() {
      mCurrentTripState = mTripStartedTripState;
      if (mService == null || !mService.isActive()) { return; }
      mService.startLocationUpdates();
    }

    @Override
    public void onTripUpdate(double latitude, double longitude) {
      // Ignore invalid request, can't onTripUpdate a paused trip.
    }

    @Override
    public void onTripPaused() {
      // Ignore invalid request, trip already paused.
    }

    @Override
    public void onTripCancelled() {
      // cancel trip by deleting from data source
      Disposable disposable = mTripDataRepo
          .delete(mTripId)
          .subscribeOn(mSchedulerProvider.io())
          .observeOn(mSchedulerProvider.ui())
          .subscribe(() -> {
            mCurrentTripState = mNoTripTripState;
            mCurrentTripState.onTripCancelled();
          }, Throwable::printStackTrace);

      // add to execution queue
      mCompositeDisposable.add(disposable);
    }

    @Override
    public void onTripComplete() {
      // complete trip iff there is data to save
      if (mTripData != null) {
        mTripData.setEndTime(System.currentTimeMillis());
        Disposable disposable = mTripDataRepo
            .put(mTripData)
            .subscribeOn(mSchedulerProvider.io())
            .observeOn(mSchedulerProvider.ui())
            .subscribe(() -> {
              mCurrentTripState = mNoTripTripState;
              mCurrentTripState.onTripComplete();
            }, Throwable::printStackTrace);

        // add to execution queue
        mCompositeDisposable.add(disposable);
      }
    }
  }
}
