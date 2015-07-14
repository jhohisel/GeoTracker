package edu.uw.ProjectMayhem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brian on 4/29/2015.
 */
public class MovementDBHandler extends SQLiteOpenHelper {


    private static int DATABASE_VERSION = 3;

    private static final String DATABASE_NAME = "PeopleTracker";
    private static final String MOVEMENT = "movement";

    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String SPEED = "speed";
    private static final String HEADING = "heading";
    private static final String PK_SOURCE_ID = "source_id";
    private static final String TIMESTAMP = "timestamp";

    public MovementDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void addMovement(MovementData aMove) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(LATITUDE, aMove.getLatitude());
        values.put(LONGITUDE, aMove.getLongitude());
        values.put(SPEED, aMove.getSpeed());
        values.put(HEADING, aMove.getHeading());
        values.put(PK_SOURCE_ID, aMove.getSourceID());
        values.put(TIMESTAMP, aMove.getTimeStamp());

        db.insert(MOVEMENT, null, values);

        db.close();
    }

    public MovementData getMovement(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(MOVEMENT, new String[] {LATITUDE,
                LONGITUDE, SPEED, HEADING, PK_SOURCE_ID, TIMESTAMP}, PK_SOURCE_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        MovementData move = new MovementData(Double.parseDouble(cursor.getString(0)),
                Double.parseDouble(cursor.getString(1)), Double.parseDouble(cursor.getString(2)),
                cursor.getString(3),Integer.parseInt(cursor.getString(4)), cursor.getString(5));

        return move;
    }

    public List<MovementData> getAllMovement() {

        List<MovementData> movementList = new ArrayList<MovementData>();

        String selectQuery = "SELECT  * FROM " + MOVEMENT;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MovementData move = new MovementData();

                move.setLatitude(Double.parseDouble(cursor.getString(0)));
                move.setLongitude(Double.parseDouble(cursor.getString(1)));
                move.setSpeed(Double.parseDouble(cursor.getString(2)));
                move.setHeading(cursor.getString(3));
                move.setSourceID(Integer.parseInt(cursor.getString(4)));
                move.setTimeStamp(cursor.getString(5));

                movementList.add(move);

            } while (cursor.moveToNext());
        }

        return movementList;
    }

    public int getMovementCount() {

        String countQuery = "SELECT  * FROM " + MOVEMENT;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();
    }

    public int updateMovement(MovementData update) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(LATITUDE, update.getLatitude());
        values.put(LONGITUDE, update.getLongitude());
        values.put(SPEED, update.getSpeed());
        values.put(HEADING, update.getHeading());
        values.put(PK_SOURCE_ID, update.getSourceID());
        values.put(TIMESTAMP, update.getTimeStamp());

        return db.update(MOVEMENT, values, PK_SOURCE_ID + " = ?",
                new String[] { String.valueOf(update.getSourceID())});
    }

    public void deleteMovement(MovementData deleteMove) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(MOVEMENT, PK_SOURCE_ID + " = ?",
                new String[] { String.valueOf(deleteMove.getSourceID())});

        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_MOVEMENT_TABLE = "create table " + MOVEMENT + " (" + LATITUDE + " DOUBLE,"
                + LONGITUDE + " DOUBLE," + SPEED + " DOUBLE," + HEADING + " TEXT," +
                PK_SOURCE_ID + " INTEGER PRIMARY KEY," + TIMESTAMP + " TEXT" + ")";

        db.execSQL(CREATE_MOVEMENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldDB, int newDB) {

        db.execSQL("DROP TABLE IF EXISTS " + MOVEMENT);

        DATABASE_VERSION++;

        onCreate(db);
    }
}
