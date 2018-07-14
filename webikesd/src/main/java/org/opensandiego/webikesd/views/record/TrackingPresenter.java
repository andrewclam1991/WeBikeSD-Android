package org.opensandiego.webikesd.views.record;

import com.google.common.base.Optional;

import org.opensandiego.webikesd.data.model.CyclePoint;
import org.opensandiego.webikesd.data.model.TripCyclePoint;
import org.opensandiego.webikesd.data.model.TripData;
import org.opensandiego.webikesd.data.source.DataSource;
import org.opensandiego.webikesd.data.source.Repo;
import org.opensandiego.webikesd.util.schedulers.SchedulerProvider;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

@Singleton
class TrackingPresenter implements TrackingContract.Presenter {

  // Service (Invisible View)
  private TrackingContract.Service mService;

  // Internal states
  private TrackingContract.State mCurrentState;
  private TrackingContract.State mNoTripState;
  private TrackingContract.State mTripStartedState;
  private TrackingContract.State mTripPausedState;

  // Data Source
  @NonNull private final DataSource<TripCyclePoint> mTripCyclePtRepo;
  @NonNull private final DataSource<TripData> mTripDataRepo;
  @NonNull private final DataSource<CyclePoint> mCyclePtRepo;
  @NonNull private final SchedulerProvider mSchedulerProvider;
  @NonNull private final CompositeDisposable mCompositeDisposable;
  @NonNull private final String mTripId;
  @Nullable private TripData mTripData;

  @Inject
  TrackingPresenter(@NonNull @Repo DataSource<TripCyclePoint> tripCyclePtRepo,
                    @NonNull @Repo DataSource<TripData> tripDataRepo,
                    @NonNull @Repo DataSource<CyclePoint> cyclePtRepo,
                    @NonNull SchedulerProvider schedulerProvider,
                    @NonNull String tripId){
    mTripCyclePtRepo = tripCyclePtRepo;
    mTripDataRepo = tripDataRepo;
    mCyclePtRepo = cyclePtRepo;
    mSchedulerProvider = schedulerProvider;
    mCompositeDisposable = new CompositeDisposable();
    mTripId = tripId;
    setupInternalStates();
  }

  /**
   * Setup this class's internal state components
   * {@link NoTripState}
   */
  private void setupInternalStates() {
    mNoTripState = new NoTripState();
    mTripStartedState = new TripStartedState();
    mTripPausedState = new TripPausedState();
    mCurrentState = mNoTripState;
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
            tripData -> {
              mTripData = tripData;
              mService.showTrip(mTripData);
            },
            // onError
            Throwable::printStackTrace);

    mCompositeDisposable.add(disposable);
  }

  @Override
  public void start() { mCurrentState.start(); }

  @Override
  public void update(CyclePoint pt) { mCurrentState.update(pt); }

  @Override
  public void pause() { mCurrentState.pause(); }

  @Override
  public void cancel() { mCurrentState.cancel(); }

  @Override
  public void complete() { mCurrentState.complete(); }

  /**
   * Concrete internal {@link TrackingContract.State} when there is no
   * trip or trip has stopped.
   */
  private class NoTripState implements TrackingContract.State {
    @Override
    public void start() {
      // TODO create trip, and on created async do following


      mCurrentState = mTripStartedState;
      mService.startLocationUpdates();
    }

    @Override
    public void update(CyclePoint pt) { /* invalid request, ignore*/ }

    @Override
    public void pause() { /* invalid request, ignore */}

    @Override
    public void cancel() {
      mService.stopLocationUpdates();
      mService.dropService();
    }

    @Override
    public void complete() {
      mService.stopLocationUpdates();
      mService.dropService();
    }
  }

  /**
   * Concrete internal {@link TrackingContract.State} when trip
   * is active and started
   */
  private class TripStartedState implements TrackingContract.State {

    @Override
    public void start() { /*invalid request, ignore*/}

    @Override
    public void update(CyclePoint pt) {
      // TODO update trip with repository
    }

    @Override
    public void pause() {
      mCurrentState = mTripPausedState;
      mService.stopLocationUpdates();
    }

    @Override
    public void cancel() {
      mCurrentState = mNoTripState;
      mCurrentState.cancel();
    }

    @Override
    public void complete() {
      // TODO impl complete trip at started state
      Disposable disposable = mTripDataRepo
          .put(new TripData("randomid"))
          .subscribeOn(mSchedulerProvider.io())
          .observeOn(mSchedulerProvider.ui())
          .subscribe(()->{
            mCurrentState = mNoTripState;
            mCurrentState.complete();
          }, Throwable::printStackTrace);

      // add to execution queue
      mCompositeDisposable.add(disposable);
    }
  }

  /**
   * Concrete internal {@link TrackingContract.State} when trip
   * is inactive and paused, but not completed.
   */
  private class TripPausedState implements TrackingContract.State {

    @Override
    public void start() {
      mCurrentState = mTripStartedState;
      mService.startLocationUpdates();
    }

    @Override
    public void update(CyclePoint pt) { /* Invalid request, ignore */}

    @Override
    public void pause() { /* Already paused, ignore */ }

    @Override
    public void cancel() {
      // TODO impl delete trip at paused state
      Disposable disposable = mTripDataRepo
          .delete("randomId")
          .subscribeOn(mSchedulerProvider.io())
          .observeOn(mSchedulerProvider.ui())
          .subscribe(()->{
            mCurrentState = mNoTripState;
            mCurrentState.cancel();
          },e -> {
            // TODO impl complete trip error
            e.printStackTrace();
          });

      // add to execution queue
      mCompositeDisposable.add(disposable);
    }

    @Override
    public void complete() {
      // TODO impl complete trip at paused state
      Disposable disposable = mTripDataRepo
          .put(new TripData("randomid"))
          .subscribeOn(mSchedulerProvider.io())
          .observeOn(mSchedulerProvider.ui())
          .subscribe(()->{
            mCurrentState = mNoTripState;
            mCurrentState.complete();
          },e -> {
            // TODO impl complete trip error
            e.printStackTrace();
          });

      // add to execution queue
      mCompositeDisposable.add(disposable);
    }
  }
}
