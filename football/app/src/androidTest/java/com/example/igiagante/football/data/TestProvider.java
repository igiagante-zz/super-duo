package com.example.igiagante.football.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.example.igiagante.football.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import data.DaoHelper;
import data.DbHelper;
import data.SoccerContract;
import data.SoccerContract.MatchEntry;
import data.SoccerContract.SeasonEntry;
import data.SoccerContract.TeamEntry;
import model.Match;
import model.Team;

/**
 * @author igiagante on 19/11/15.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void deleteAllRecords() {
        TestUtils.cleanDB(mContext);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        deleteAllRecords();
    }

    public void testGetType() {

        String type = mContext.getContentResolver().getType(SeasonEntry.CONTENT_URI);
        assertEquals(SeasonEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(SoccerContract.TeamEntry.CONTENT_URI);
        assertEquals(TeamEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(MatchEntry.CONTENT_URI);
        assertEquals(MatchEntry.CONTENT_TYPE, type);

        long id = 398;
        type = mContext.getContentResolver().getType(SeasonEntry.buildSeasonUri(id));
        assertEquals(SeasonEntry.CONTENT_ITEM_TYPE, type);

        type = mContext.getContentResolver().getType(SeasonEntry.buildSeasonWithMatchesUri(id));
        assertEquals(SeasonEntry.CONTENT_ITEM_TYPE, type);

        type = mContext.getContentResolver().getType(TeamEntry.buildEntryUri(id));
        assertEquals(TeamEntry.CONTENT_ITEM_TYPE, type);

    }

    public void testSeasonQuery() {
        // insert our test records into the database
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long seasonRowId = TestUtils.insertSeasonValues(mContext);

        assertTrue("Unable to Insert SeasonEntry into the Database", seasonRowId != -1);
        db.close();
    }

    public void testTeamQuery() {
        // insert our test records into the database
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long teamRowId = TestUtils.insertTeamValues(mContext);

        assertTrue("Unable to Insert TeamEntry into the Database", teamRowId != -1);
        db.close();
    }

    public void testMatchQuery() {

        long matchRowId = TestUtils.insertMatchValues(mContext);
        assertTrue("Unable to Insert MatchEntry into the Database", matchRowId != -1);
    }

    public void testGetMatchWithTeams() {

        DaoHelper daoHelper = new DaoHelper(mContext);

        Match match;

        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues matchValues = TestUtils.createMatchValues("123", "10-05-2015");

        long matchRowId = db.insert(SoccerContract.MatchEntry.TABLE_NAME, null, matchValues);

        if (matchRowId != -1) {
            ContentValues teamHomeValues = TestUtils.createTeamHomeValues();
            long teamHomeRowId = db.insert(TeamEntry.TABLE_NAME, null, teamHomeValues);

            ContentValues teamAwayValues = TestUtils.createTeamAwayValues();
            long teamAwayRowId = db.insert(TeamEntry.TABLE_NAME, null, teamAwayValues);

            if (teamHomeRowId != -1 && teamAwayRowId != -1) {
                match = daoHelper.getMatch(String.valueOf(12341));
                if (match != null) {
                    Team teamHome = daoHelper.getTeam(match.getHomeTeamId());
                    Team awayTeam = daoHelper.getTeam(match.getAwayTeamId());

                    assertNotNull(teamHome);
                    assertNotNull(awayTeam);
                    assertEquals(teamHome.getName(), "Borussia Dortmund");
                    assertEquals(teamHome.getShortName(), "Borussia");
                }
            }
        }
    }

    public void testGetMatchesForToday() {

        DaoHelper daoHelper = new DaoHelper(mContext);

        Match match;

        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        SimpleDateFormat format = new SimpleDateFormat(mContext.getString(R.string.format_yyyy_MM_dd), Locale.US);

        ContentValues matchValuesOne = TestUtils.createMatchValues("123", format.format(new Date()));
        db.insert(SoccerContract.MatchEntry.TABLE_NAME, null, matchValuesOne);

        ContentValues matchValuesTwo = TestUtils.createMatchValues("234", format.format(new Date()));
        db.insert(SoccerContract.MatchEntry.TABLE_NAME, null, matchValuesTwo);

        ContentValues matchValuesThree = TestUtils.createMatchValues("456", "2015-12-10");
        db.insert(SoccerContract.MatchEntry.TABLE_NAME, null, matchValuesThree);

        ArrayList<Match> matchesAll = daoHelper.getAllMatch();
        assertEquals(matchesAll.size(), 3);

        ArrayList<Match> matches = daoHelper.getMatchesForToday();
        assertEquals(matches.size(), 2);
    }

    public void testBulkInsertTeam() {
        ContentValues[] testValues = TestUtils.getTeams();

        long count = mContext.getContentResolver().bulkInsert(TeamEntry.CONTENT_URI, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Team Values", count != -1);
        assertEquals(3, count);
    }
}