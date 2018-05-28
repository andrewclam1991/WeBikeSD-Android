package org.opensandiego.webikesd.data.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.opensandiego.webikesd.data.model.TripStatus.STATUS_COMPLETE;
import static org.opensandiego.webikesd.data.model.TripStatus.STATUS_INCOMPLETE;
import static org.opensandiego.webikesd.data.model.TripStatus.STATUS_SENT;

/**
 * IntDef to enforce {@link TripData}'s status
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({STATUS_INCOMPLETE, STATUS_COMPLETE, STATUS_SENT})
public @interface TripStatus {
  // Declare the constants
  int STATUS_INCOMPLETE = 0;
  int STATUS_COMPLETE = 1;
  int STATUS_SENT = 2;
}