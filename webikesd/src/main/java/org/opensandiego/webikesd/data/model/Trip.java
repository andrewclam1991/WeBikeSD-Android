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

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Store a cycle trip's data in the model layer
 */
@Entity(tableName = "trips")
public final class Trip implements BaseModel {

  @PrimaryKey
  @NonNull
  private String uid;

  @ColumnInfo(name = "start_time")
  private double startTime;

  @ColumnInfo(name = "end_time")
  private double endTime;

  @ColumnInfo(name = "lat_high")
  private double latHigh;

  @ColumnInfo(name = "lgt_high")
  private double lgtHigh;

  @ColumnInfo(name = "lat_low")
  private double latLow;

  @ColumnInfo(name = "lgt_low")
  private double lgtLow;

  @ColumnInfo(name = "latest_lat")
  private double latestLat;

  @ColumnInfo(name = "latest_lgt")
  private double latestLgt;

  @ColumnInfo(name = "status")
  private int status;

  @ColumnInfo(name = "distance")
  private double distance;

  @ColumnInfo(name = "purpose")
  private String purpose;

  @ColumnInfo(name = "fancy_start")
  private String fancyStart;

  @ColumnInfo(name = "info")
  private String info;

  @Embedded(prefix = "start_pt_")
  private CyclePoint startPt;

  @Embedded(prefix = "end_pt_")
  private CyclePoint endPt;

  @ColumnInfo(name = "total_pause_time")
  private double totalPauseTime = 0;

  @ColumnInfo(name = "pause_started_at")
  private double pauseStartedAt = 0;

  public Trip(@NonNull String uid) {
    this.uid = uid;
    this.startTime = System.currentTimeMillis();
    this.endTime = System.currentTimeMillis();
    this.latestLat = 800;
    this.latestLgt = 800;
    this.latHigh = (int) (-100 * 1E6);
    this.latLow = (int) (100 * 1E6);
    this.lgtLow = (int) (180 * 1E6);
    this.lgtHigh = (int) (-180 * 1E6);
  }

  @NonNull
  @Override
  public String getUid() { return uid; }

  @Override
  public void setUid(@NonNull String id) { this.uid = id; }

  public double getStartTime() {
    return startTime;
  }

  public void setStartTime(double startTime) {
    this.startTime = startTime;
  }

  public double getEndTime() {
    return endTime;
  }

  public void setEndTime(double endTime) {
    this.endTime = endTime;
  }

  public double getLatHigh() {
    return latHigh;
  }

  public void setLatHigh(double latHigh) {
    this.latHigh = latHigh;
  }

  public double getLgtHigh() {
    return lgtHigh;
  }

  public void setLgtHigh(double lgtHigh) {
    this.lgtHigh = lgtHigh;
  }

  public double getLatLow() {
    return latLow;
  }

  public void setLatLow(double latLow) {
    this.latLow = latLow;
  }

  public double getLgtLow() {
    return lgtLow;
  }

  public void setLgtLow(double lgtLow) {
    this.lgtLow = lgtLow;
  }

  public double getLatestLat() {
    return latestLat;
  }

  public void setLatestLat(double latestLat) {
    this.latestLat = latestLat;
  }

  public double getLatestLgt() {
    return latestLgt;
  }

  public void setLatestLgt(double latestLgt) {
    this.latestLgt = latestLgt;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(@TripStatus int status) {
    this.status = status;
  }

  public double getDistance() {
    return distance;
  }

  public void setDistance(double distance) {
    this.distance = distance;
  }

  public String getPurpose() {
    return purpose;
  }

  public void setPurpose(String purpose) {
    this.purpose = purpose;
  }

  public String getFancyStart() {
    return fancyStart;
  }

  public void setFancyStart(String fancyStart) {
    this.fancyStart = fancyStart;
  }

  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info;
  }

  public CyclePoint getStartPt() {
    return startPt;
  }

  public void setStartPt(CyclePoint startPt) {
    this.startPt = startPt;
  }

  public CyclePoint getEndPt() {
    return endPt;
  }

  public void setEndPt(CyclePoint endPt) {
    this.endPt = endPt;
  }

  public double getTotalPauseTime() {
    return totalPauseTime;
  }

  public void setTotalPauseTime(double totalPauseTime) {
    this.totalPauseTime = totalPauseTime;
  }

  public double getPauseStartedAt() {
    return pauseStartedAt;
  }

  public void setPauseStartedAt(double pauseStartedAt) {
    this.pauseStartedAt = pauseStartedAt;
  }
}

