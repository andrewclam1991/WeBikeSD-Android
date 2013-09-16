/**  Cycle Altanta, Copyright 2012 Georgia Institute of Technology
 *                                    Atlanta, GA. USA
 *
 *   @author Christopher Le Dantec <ledantec@gatech.edu>
 *   @author Anhong Guo <guoanhong15@gmail.com>
 *
 *   Updated/Modified for Atlanta's app deployment. Based on the
 *   CycleTracks codebase for SFCTA.
 *
 *   CycleTracks, Copyright 2009,2010 San Francisco County Transportation Authority
 *                                    San Francisco, CA, USA
 *
 *   @author Billy Charlton <billy.charlton@sfcta.org>
 *
 *   This file is part of CycleTracks.
 *
 *   CycleTracks is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   CycleTracks is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with CycleTracks.  If not, see <http://www.gnu.org/licenses/>.
 */
//
package edu.gatech.ppl.cycleatlanta;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.MarkerOptionsCreator;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class ShowMap extends FragmentActivity {
	List<Polyline> mapTracks;
	Drawable drawable;
	Polyline gpspoints;
	PolylineOptions gpsoptions;
	float[] lineCoords;

	private GoogleMap mMap;
	private LinearLayout layout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mapview);

		try {
			Log.d("got far enough to instantiate map...", "building map");
			if (mapTracks != null) {
				mapTracks.clear();
			} else {
				mapTracks = new ArrayList<Polyline>();
			}

	        Bundle cmds = getIntent().getExtras();
            long tripid = cmds.getLong("showtrip");
            TripData trip = TripData.fetchTrip(this, tripid);

			LatLng northEastBound = new LatLng(trip.lathigh, trip.lgtlow);
            LatLng southWestBound = new LatLng(trip.latlow, trip.lgthigh);

            Log.e("Bounds Points", "NE pt:" + trip.lathigh + " " + trip.lgtlow + " SW pt: " +
            		trip.latlow + " " + trip.lgthigh);

            // Add 500 to map span, to guarantee pins fit on map
			//final LatLngBounds bounds = new LatLngBounds(southWestBound, northEastBound);
            final LatLngBounds bounds = new LatLngBounds.Builder()
            	.include(northEastBound)
            	.include(southWestBound)
            	.build();

			// check if already instantiated
			if (mMap == null) {
				mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
				layout = (LinearLayout)findViewById(R.id.LinearLayout01);
				ViewTreeObserver vto = layout.getViewTreeObserver();
				vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
					@Override
				    public void onGlobalLayout() {
				        layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				        // Center & zoom the map
						// TODO: do this after map layout completes
				        Log.d("using bounds", "NE: " + bounds.northeast.toString() + " SW: " + bounds.southwest.toString());
				        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 5));
				    }
				});


			} else {
				mMap.clear();
			}

			// check if got map
			if (mMap == null) {
				// TODO:
				Log.d("Couldn't get map fragment!", "No map fragment");
				return;
			}

            // Show trip details
            TextView t1 = (TextView) findViewById(R.id.TextViewT1);
            TextView t2 = (TextView) findViewById(R.id.TextViewT2);
            TextView t3 = (TextView) findViewById(R.id.TextViewT3);
            t1.setText(trip.purp);
            t2.setText(trip.info);
            t3.setText(trip.fancystart);

			if (gpspoints == null) {
				// TODO:
				AddPointsToMapLayerTask maptask = new AddPointsToMapLayerTask();
				maptask.execute(trip);
			} else {
				mapTracks.add(gpspoints);
			}

			if (trip.status < TripData.STATUS_SENT
					&& cmds != null
					&& cmds.getBoolean("uploadTrip", false)) {
			    // And upload to the cloud database, too!  W00t W00t!
                TripUploader uploader = new TripUploader(ShowMap.this);
                uploader.execute(trip.tripid);
			}

		} catch (Exception e) {
			Log.d("Map error",e.toString(), e);
		}
	}

	private class AddPointsToMapLayerTask extends AsyncTask<TripData, Integer, PolylineOptions> {
		TripData trip;

		@Override
		protected PolylineOptions doInBackground(TripData... trips) {
			trip = trips[0]; // always get just the first trip

			ShowMap.this.gpsoptions = new PolylineOptions();
			ShowMap.this.gpsoptions.addAll(trip.getPoints());

			return ShowMap.this.gpsoptions;
		}

		@Override
		protected void onPostExecute(PolylineOptions opts) {
			// Add the polylines
			ShowMap.this.gpspoints = ShowMap.this.mMap.addPolyline(opts);
			mapTracks.add(ShowMap.this.gpspoints);

			// Add start & end pins
			if (trip.startpoint != null) {
				mMap.addMarker(new MarkerOptions()
						.position(trip.startpoint.coords)
						.title("start")
						.snippet(trip.fancystart)
						.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
			}
			if (trip.endpoint != null) {
				mMap.addMarker(new MarkerOptions()
						.position(trip.endpoint.coords)
						.title("end")
						.snippet(DateFormat.getInstance().format(trip.endTime))
						.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
			}
		}
	}
}