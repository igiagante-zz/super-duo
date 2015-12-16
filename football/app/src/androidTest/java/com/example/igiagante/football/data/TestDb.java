package com.example.igiagante.football.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

import data.DbHelper;
import data.SoccerContract;
import data.SoccerContract.MatchEntry;
import data.SoccerContract.SeasonEntry;
import data.SoccerContract.TeamEntry;

/**
 * @author igiagante on 19/11/15.
 */
public class TestDb extends AndroidTestCase {

    void deleteTheDatabase() {
        mContext.deleteDatabase(DbHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(SoccerContract.SeasonEntry.TABLE_NAME);
        tableNameHashSet.add(SoccerContract.TeamEntry.TABLE_NAME);
        tableNameHashSet.add(SoccerContract.MatchEntry.TABLE_NAME);

        mContext.deleteDatabase(DbHelper.DATABASE_NAME);

        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly", c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created wrong.", tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + SoccerContract.SeasonEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> seasonColumnHashSet = new HashSet<String>();
        seasonColumnHashSet.add(SeasonEntry._ID);
        seasonColumnHashSet.add(SeasonEntry.CAPTION);
        seasonColumnHashSet.add(SeasonEntry.LEAGUE);
        seasonColumnHashSet.add(SeasonEntry.YEAR);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            seasonColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required season entry columns",
                seasonColumnHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + SoccerContract.TeamEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> teamColumnHashSet = new HashSet<String>();
        teamColumnHashSet.add(TeamEntry._ID);
        teamColumnHashSet.add(TeamEntry.NAME);
        teamColumnHashSet.add(TeamEntry.SHORT_NAME);
        teamColumnHashSet.add(TeamEntry.THUMBNAIL);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            teamColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required team entry columns",
                teamColumnHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + SoccerContract.MatchEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> matchColumnHashSet = new HashSet<String>();
        matchColumnHashSet.add(MatchEntry._ID);
        matchColumnHashSet.add(MatchEntry.SEASON_ID);
        matchColumnHashSet.add(MatchEntry.DATE);
        matchColumnHashSet.add(MatchEntry.TIME);
        matchColumnHashSet.add(MatchEntry.MATCH_DAY);
        matchColumnHashSet.add(MatchEntry.HOME_TEAM_ID);
        matchColumnHashSet.add(MatchEntry.HOME_GOALS);
        matchColumnHashSet.add(MatchEntry.AWAY_TEAM_ID);
        matchColumnHashSet.add(MatchEntry.AWAY_GOALS);
        matchColumnHashSet.add(MatchEntry.STATUS);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            matchColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required match entry columns",
                matchColumnHashSet.isEmpty());
        db.close();
    }
}