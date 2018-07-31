package org.opensandiego.webikesd.views;

import android.app.Service;
import android.content.ServiceConnection;
import android.support.annotation.NonNull;

/**
 * Fragment interaction listener that delegates bind/unbind service request to
 * its attach parent.
 * <p>
 * Note: Simple pattern that instead of checking and using getActivity()
 * uses standard Android Fragment -> Activity listener based communication
 */
public interface FragmentServiceBinderListener {
  /**
   * Delegates bind service request
   *
   * @param serviceClass      the class that is asked to be bound
   * @param serviceConnection connection callback that defines connection behaviors.
   * @param flags             bind service flag
   * @param <S>               Type of {@link Service}
   */
  <S extends Service> void onRequestBindService(@NonNull Class<S> serviceClass,
                                                @NonNull ServiceConnection serviceConnection,
                                                int flags);

  /**
   * Delegates unbind service request
   *
   * @param serviceConnection connection callback that defines connection behaviors.
   */
  void onRequestUnbindService(@NonNull ServiceConnection serviceConnection);
}
