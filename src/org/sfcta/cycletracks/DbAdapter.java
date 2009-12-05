package org.sfcta.cycletracks;

import java.text.DateFormat;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple database access helper class. Defines the basic CRUD operations,
 * and gives the ability to list all trips as well as retrieve or modify 
 * a specific trip.
 * 
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 * 
 * **This code borrows heavily from Google demo app "Notepad" in the Android SDK**
 */
public class DbAdapter {
    private static final int DATABASE_VERSION = 11;

    public static final String K_TRIP_ROWID = "_id";
    public static final String K_TRIP_PURP  = "purp";
    public static final String K_TRIP_START = "start";
    public static final String K_TRIP_FANCYSTART = "fancystart";
    public static final String K_TRIP_NOTE  = "note";
    public static final String K_TRIP_LATHI  = "lathi";
    public static final String K_TRIP_LATLO  = "latlo";
    public static final String K_TRIP_LGTHI  = "lgthi";
    public static final String K_TRIP_LGTLO  = "lgtlo";

    public static final String K_POINT_ROWID = "_id";
    public static final String K_POINT_TRIP  = "trip";
    public static final String K_POINT_SEQ   = "seq";
    public static final String K_POINT_LAT   = "lat";
    public static final String K_POINT_LGT   = "lgt";
    public static final String K_POINT_TIME  = "time";
    public static final String K_POINT_ACC   = "acc";

    private static final String TAG = "DbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private int seq = 1;
    
    /**
     * Database creation sql statement
     */
    private static final String TABLE_CREATE_TRIPS =
            "create table trips (_id integer primary key autoincrement, "
                    + "purp text, start double, fancystart text, note text,"
                    + "lathi integer, latlo integer, lgthi integer, lgtlo integer);";
    
    private static final String TABLE_CREATE_COORDS =
            "create table coords (_id integer primary key autoincrement, "
          			+ "trip integer, seq integer, lat integer, lgt integer, "
          			+ "time double, acc integer);";

    private static final String DATABASE_NAME = "data";
    private static final String DATA_TABLE_TRIPS = "trips";
    private static final String DATA_TABLE_COORDS = "coords";

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(TABLE_CREATE_TRIPS);
            db.execSQL(TABLE_CREATE_COORDS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS "+DATA_TABLE_TRIPS);
            db.execSQL("DROP TABLE IF EXISTS "+DATA_TABLE_COORDS);
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public DbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public DbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }

    // #### Coordinate table methods ####
    
    /**
     * Create a set of coords for a trip using the data provided. If the coords are
     * all successfully created, return true; otherwise return false.
     * TODO: This method is really slow! :-(
     */
    public boolean createCoordsForTrip(long tripid, Vector <CyclePoint> points) {
    	boolean success = true;
    	mDb.beginTransaction();
    	
    	for (int i=0; i<points.size(); i++) {
            ContentValues rowValues = new ContentValues();
            rowValues.put(K_POINT_TRIP, tripid);
            rowValues.put(K_POINT_SEQ, i);
            
    		CyclePoint pt = points.elementAt(i);
    		rowValues.put(K_POINT_LAT, pt.getLatitudeE6());
    		rowValues.put(K_POINT_LGT, pt.getLongitudeE6());
    		rowValues.put(K_POINT_TIME, pt.time);
    		//rowValues.put(K_POINT_ACC, 0); //TODO: Store accuracy!
            
            long rtn = mDb.insert(DATA_TABLE_COORDS, null, rowValues);
            if (rtn<0) {
            	success = false;
            	break;    		
            }
    	}
    	mDb.endTransaction();
    	return success;
    }

    public boolean addCoordToTrip(long tripid, CyclePoint pt) {
        ContentValues rowValues = new ContentValues();
        rowValues.put(K_POINT_TRIP, tripid);
        rowValues.put(K_POINT_SEQ, seq++);
        
		rowValues.put(K_POINT_LAT, pt.getLatitudeE6());
		rowValues.put(K_POINT_LGT, pt.getLongitudeE6());
		rowValues.put(K_POINT_TIME, pt.time);
		//rowValues.put(K_POINT_ACC, 0); //TODO: Store accuracy!
            
        return mDb.insert(DATA_TABLE_COORDS, null, rowValues) > 0;
    }
    
    public boolean deleteAllCoordsForTrip(long tripid) {
        return mDb.delete(DATA_TABLE_COORDS, K_POINT_TRIP + "=" + tripid, null) > 0;
    }
    
    public Cursor fetchAllCoordsForTrip(long tripid) {
        Cursor mCursor =mDb.query(true, DATA_TABLE_COORDS, 
        		new String[] {K_POINT_SEQ, K_POINT_LAT, K_POINT_LGT, K_POINT_TIME},
        		K_POINT_TRIP + "=" + tripid, 
        		null, null, null, null, null);
        
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    // #### Trip table methods ####

    /**
     * Create a new trip using the data provided. If the trip is
     * successfully created return the new rowId for that trip, otherwise return
     * a -1 to indicate failure.
     */
    public long createTrip(String purp, double starttime, String fancystart, String note) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(K_TRIP_PURP, purp);
        initialValues.put(K_TRIP_START, starttime);
        initialValues.put(K_TRIP_FANCYSTART, fancystart);
        initialValues.put(K_TRIP_NOTE, note);
        
        // Start numbering points for this trip from 1 
        seq = 1;
        
        return mDb.insert(DATA_TABLE_TRIPS, null, initialValues);
    }
    
    public long createTrip() {
    	return createTrip("",0,"","");
    }

    /**
     * Delete the trip with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteTrip(long rowId) {
        return mDb.delete(DATA_TABLE_TRIPS, K_TRIP_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllTrips() {
    	//TODO: These are not all strings! How does this work?
        return mDb.query(DATA_TABLE_TRIPS,
        		new String[] {K_TRIP_ROWID, K_TRIP_PURP, K_TRIP_START, K_TRIP_FANCYSTART, K_TRIP_NOTE},
        		null, null, null, null, "-"+K_TRIP_START);
    }

    /**
     * Return a Cursor positioned at the trip that matches the given rowId
     * 
     * @param rowId id of trip to retrieve
     * @return Cursor positioned to matching trip, if found
     * @throws SQLException if trip could not be found/retrieved
     */
    public Cursor fetchTrip(long rowId) throws SQLException {
        Cursor mCursor =mDb.query(true, DATA_TABLE_TRIPS, 
        		new String[] {K_TRIP_ROWID, K_TRIP_PURP, K_TRIP_START, K_TRIP_FANCYSTART, K_TRIP_NOTE, K_TRIP_LATHI, K_TRIP_LATLO, K_TRIP_LGTHI, K_TRIP_LGTLO},
        		K_TRIP_ROWID + "=" + rowId, 
        		null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public boolean updateTrip(long tripid, String purp, double starttime, String fancystart, String note,
    		int lathigh, int latlow, int lgthigh, int lgtlow) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(K_TRIP_PURP, purp);
        initialValues.put(K_TRIP_START, starttime);
        initialValues.put(K_TRIP_FANCYSTART, fancystart);
        initialValues.put(K_TRIP_NOTE, note);
        initialValues.put(K_TRIP_LATHI, lathigh);
        initialValues.put(K_TRIP_LATLO, latlow);
        initialValues.put(K_TRIP_LGTHI, lgthigh);
        initialValues.put(K_TRIP_LGTLO, lgtlow);

        return mDb.update(DATA_TABLE_TRIPS, initialValues, K_TRIP_ROWID + "=" + tripid, null) > 0;
    }
}