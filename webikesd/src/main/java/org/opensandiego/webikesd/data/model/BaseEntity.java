package org.opensandiego.webikesd.data.model;

import android.support.annotation.NonNull;

/**
 * Base class for each Model class
 */
public interface BaseEntity {

  @NonNull
  String getUid();

  void setUid(@NonNull String id);
}
