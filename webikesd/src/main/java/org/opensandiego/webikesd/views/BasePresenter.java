package org.opensandiego.webikesd.views;

/**
 * API of a {@link BasePresenter} class in the MVP architecture
 */
public interface BasePresenter<V extends BaseView> {
  /**
   * Sets the {@link BaseView} to onTripStart handling user interaction
   *
   * @param view reference to the {@link BaseView} class
   */
  void setView(V view);

  /**
   * Drops reference of the corresponding {@link BaseView}
   */
  void dropView();
}
