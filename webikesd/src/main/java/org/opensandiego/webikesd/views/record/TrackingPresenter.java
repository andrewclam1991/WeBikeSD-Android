package org.opensandiego.webikesd.views.record;

import org.opensandiego.webikesd.data.model.CyclePoint;

class TrackingPresenter implements TrackingContract.Presenter {

  // Service (View)
  private TrackingContract.Service mService;

  // Internal states
  private TrackingContract.State mCurrentState;
  private TrackingContract.State mNoTripState;
  private TrackingContract.State mTripStartedState;
  private TrackingContract.State mTripPausedState;

  TrackingPresenter(){
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
  public void dropView() { mService = null; }

  @Override
  public void loadTrip() {
    // todo subscribe to flowable trip from repository,
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
      mCurrentState = mNoTripState;
      mCurrentState.complete();
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
      // todo delete trip
      mCurrentState = mNoTripState;
      mCurrentState.cancel();
    }

    @Override
    public void complete() {
      // todo save trip
      mCurrentState = mNoTripState;
      mCurrentState.complete();
    }
  }
}
