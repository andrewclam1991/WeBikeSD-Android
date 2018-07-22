/**
 * Cycle Philly, Copyright 2014 Code for Philly
 *
 * @author Lloyd Emelle <lloyd@codeforamerica.org>
 * @author Christopher Le Dantec <ledantec@gatech.edu>
 * @author Anhong Guo <guoanhong15@gmail.com>
 * <p>
 * Updated/Modified for Philly's app deployment. Based on the
 * CycleTracks codebase for SFCTA and Cycle Atlanta.
 * <p>
 * CycleTracks, Copyright 2009,2010 San Francisco County Transportation Authority
 * San Francisco, CA, USA
 * @author Billy Charlton <billy.charlton@sfcta.org>
 * <p>
 * This file is part of CycleTracks.
 * <p>
 * CycleTracks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * CycleTracks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with CycleTracks.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.opensandiego.webikesd.data.model;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

/**
 * Reference class between a {@link TripData} and its {@link CyclePoint}s
 */
@Entity(
    primaryKeys = {"uid", "cyclePtUid", "tripUid"},
    tableName = "trip_cycle_pts")
public class TripCyclePoint implements BaseModel {

  @NonNull
  private String uid;
  @NonNull
  private String cyclePtUid;
  @NonNull
  private String tripUid;

  public TripCyclePoint(@NonNull String uid, @NonNull String tripUid, @NonNull String cyclePtUid) {
    this.uid = uid;
    this.tripUid = tripUid;
    this.cyclePtUid = cyclePtUid;
  }

  @NonNull
  @Override
  public String getUid() { return uid; }

  @Override
  public void setUid(@NonNull String id) { this.uid = id; }

  @NonNull
  public String getCyclePtUid() {
    return cyclePtUid;
  }

  public void setCyclePtUid(@NonNull String cyclePtUid) {
    this.cyclePtUid = cyclePtUid;
  }

  @NonNull
  public String getTripUid() {
    return tripUid;
  }

  public void setTripUid(@NonNull String tripUid) {
    this.tripUid = tripUid;
  }
}

