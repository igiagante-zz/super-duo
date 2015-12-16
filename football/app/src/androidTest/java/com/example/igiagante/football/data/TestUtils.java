package com.example.igiagante.football.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import data.DbHelper;
import data.SoccerContract;
import data.SoccerContract.SeasonEntry;
import data.SoccerContract.TeamEntry;

/**
 * @author igiagante on 19/11/15.
 */
public class TestUtils extends AndroidTestCase {

    static void cleanDB(Context mContext) {
        mContext.getContentResolver().delete(
                SoccerContract.SeasonEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                SoccerContract.MatchEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                SoccerContract.TeamEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                SoccerContract.SeasonEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                SoccerContract.MatchEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                SoccerContract.TeamEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    static long insertSeasonValues(Context mContext) {

        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = createSeasonValues();

        long seasonRowId = db.insert(SeasonEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Movie Values", seasonRowId != -1);

        return seasonRowId;
    }

    static ContentValues createSeasonValues() {

        ContentValues seasonValues = new ContentValues();

        seasonValues.put(SeasonEntry._ID, 398);
        seasonValues.put(SeasonEntry.CAPTION, "Bundesliga 2");
        seasonValues.put(SeasonEntry.LEAGUE, "BL2");
        seasonValues.put(SeasonEntry.YEAR, 2015);


        return seasonValues;
    }

    static long insertTeamValues(Context mContext) {

        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = createTeamValues();

        long teamRowId = db.insert(TeamEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Team Values", teamRowId != -1);

        return teamRowId;
    }

    static ContentValues createTeamValues() {

        ContentValues seasonValues = new ContentValues();

        seasonValues.put(TeamEntry._ID, 90);
        seasonValues.put(TeamEntry.NAME, "Borussia Dortmund");
        seasonValues.put(TeamEntry.SHORT_NAME, "Borussia");
        seasonValues.put(TeamEntry.THUMBNAIL, "https://commons.wikimedia.org/wiki/File%3ABorussia_Dortmund_logo.svg");

        return seasonValues;
    }

    static long insertMatchValues(Context mContext) {

        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = createMatchValues("123", "10-05-2015");

        long matchRowId = db.insert(SoccerContract.MatchEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Movie Values", matchRowId != -1);

        return matchRowId;
    }

    static long insertBulkTeamValues(Context mContext) {

        ContentValues[] testValues = getTeams();

        long teamRowId = mContext.getContentResolver().bulkInsert(TeamEntry.CONTENT_URI, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Movie Values", teamRowId != -1);

        return teamRowId;
    }

    static ContentValues createMatchValues(String id, String date) {

        ContentValues matchValues = new ContentValues();

        matchValues.put(SoccerContract.MatchEntry._ID, id);
        matchValues.put(SoccerContract.MatchEntry.DATE, date);
        matchValues.put(SoccerContract.MatchEntry.TIME, "19:00");
        matchValues.put(SoccerContract.MatchEntry.MATCH_DAY, 10);
        matchValues.put(SoccerContract.MatchEntry.HOME_TEAM_ID, 7);
        matchValues.put(SoccerContract.MatchEntry.HOME_GOALS, 2);
        matchValues.put(SoccerContract.MatchEntry.AWAY_TEAM_ID, 8);
        matchValues.put(SoccerContract.MatchEntry.AWAY_GOALS, 0);
        matchValues.put(SoccerContract.MatchEntry.STATUS, "FINISHED");
        matchValues.put(SoccerContract.MatchEntry.SEASON_ID, 398);

        return matchValues;
    }

    static ContentValues createTeamHomeValues() {

        ContentValues seasonValues = new ContentValues();

        seasonValues.put(TeamEntry._ID, 7);
        seasonValues.put(TeamEntry.NAME, "Borussia Dortmund");
        seasonValues.put(TeamEntry.SHORT_NAME, "Borussia");
        seasonValues.put(TeamEntry.THUMBNAIL, "https://commons.wikimedia.org/wiki/File%3ABorussia_Dortmund_logo.svg");

        return seasonValues;
    }

    static ContentValues createTeamAwayValues() {

        ContentValues seasonValues = new ContentValues();

        seasonValues.put(TeamEntry._ID, 8);
        seasonValues.put(TeamEntry.NAME, "Munich");
        seasonValues.put(TeamEntry.SHORT_NAME, "Munich");
        seasonValues.put(TeamEntry.THUMBNAIL, "Munich.svg");

        return seasonValues;
    }

    static ContentValues[] getTeams() {

        ContentValues[] values = new ContentValues[3];

        ContentValues teamOne = new ContentValues();
        teamOne.put(TeamEntry._ID, 7);
        teamOne.put(TeamEntry.NAME, "Borussia Dortmund");
        teamOne.put(TeamEntry.SHORT_NAME, "Borussia");
        teamOne.put(TeamEntry.THUMBNAIL, "picture");

        values[0] = teamOne;

        ContentValues teamTwo = new ContentValues();
        teamTwo.put(TeamEntry._ID, 8);
        teamTwo.put(TeamEntry.NAME, "Hamburger SV");
        teamTwo.put(TeamEntry.SHORT_NAME, "HSV");
        teamTwo.put(TeamEntry.THUMBNAIL, "picture");

        values[1] = teamTwo;

        ContentValues teamThree = new ContentValues();
        teamThree.put(TeamEntry._ID, 3);
        teamThree.put(TeamEntry.NAME, "Munich");
        teamThree.put(TeamEntry.SHORT_NAME, "Munich");
        teamThree.put(TeamEntry.THUMBNAIL, "picture");

        values[2] = teamThree;

        return values;
    }
}
