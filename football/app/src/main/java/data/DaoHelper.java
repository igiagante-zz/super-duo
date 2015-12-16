package data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.igiagante.football.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import model.Match;
import model.Season;
import model.Team;

/**
 * @author igiagante on 20/11/15.
 */
public class DaoHelper {

    private static final String TAG = DaoHelper.class.getSimpleName();

    private Context mContext;
    private DbHelper dbHelper;

    public DaoHelper(Context context) {
        mContext = context;
        dbHelper = new DbHelper(mContext);
    }

    /**
     * Gets the number of team from the database.
     *
     * @return count.
     */
    public int getTeamCount() {
        String countQuery = "SELECT  * FROM " + SoccerContract.TeamEntry.TABLE_NAME;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    /**
     * Gets the last id from Team Table.
     *
     * @return lastId.
     */
    public long getLastItemId() {
        long lastId = 0;
        String query = "SELECT ROWID from " + SoccerContract.TeamEntry.TABLE_NAME + " order by ROWID DESC limit 1";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {
            lastId = c.getLong(0); //The 0 is the column index, we only have 1 column, so the index is 0
            c.close();
        }
        return lastId;
    }

    /**
     * Verifies if a match is already persisted in the database.
     *
     * @param id Match id.
     * @return boolean.
     */
    public boolean checkIfMatchExist(String id) {
        Cursor matchEntry = mContext.getContentResolver().query(
                SoccerContract.MatchEntry.buildMatchUri(Long.parseLong(id)),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        if (matchEntry.getCount() > 0) {
            return true;
        }

        matchEntry.close();
        return false;
    }

    /**
     * Verifies if a season is already persisted in the database.
     *
     * @param id Season id.
     * @return boolean.
     */
    public boolean checkIfSeasonExist(String id) {
        Cursor seasonEntry = mContext.getContentResolver().query(
                SoccerContract.SeasonEntry.buildSeasonUri(Long.parseLong(id)),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        if (seasonEntry.getCount() > 0) {
            return true;
        }

        seasonEntry.close();
        return false;
    }

    /**
     * Gets matches for today.
     *
     * @return ArrayList<Match>.
     */
    public ArrayList<Match> getMatchesForToday() {

        SimpleDateFormat format = new SimpleDateFormat(mContext.getString(R.string.format_yyyy_MM_dd), Locale.US);
        String query = "SELECT * from " + SoccerContract.MatchEntry.TABLE_NAME + " where "
                + SoccerContract.MatchEntry.DATE + " = '" + format.format(new Date()) + "'";

        return getMatches(query);
    }

    /**
     * Gets one Match.
     *
     * @param id Match id.
     * @return Match.
     */
    public Match getMatch(String id) {

        String query = "SELECT * from " + SoccerContract.MatchEntry.TABLE_NAME + " where "
                + SoccerContract.MatchEntry._ID + " = " + id;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        if (c != null && c.moveToNext()) {
            Match matchFromCursor = getMatchFromCursor(c);
            c.close();
            return matchFromCursor;
        }
        return null;
    }

    /**
     * Gets all seasons.
     *
     * @return seasons.
     */
    public ArrayList<Season> getAllSeasons() {

        String query = "SELECT * from " + SoccerContract.SeasonEntry.TABLE_NAME;

        ArrayList<Season> seasons = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if (c != null && c.getCount() > 0) {

            Log.d(TAG, "Seasons found: " + c.getCount());

            c.moveToFirst();
            while (!c.isAfterLast()) {
                Season season = new Season();
                season.setId(c.getString(c.getColumnIndex(SoccerContract.SeasonEntry._ID)));
                season.setLeague(c.getString(c.getColumnIndex(SoccerContract.SeasonEntry.LEAGUE)));
                season.setCaption(c.getString(c.getColumnIndex(SoccerContract.SeasonEntry.CAPTION)));
                season.setYear(c.getString(c.getColumnIndex(SoccerContract.SeasonEntry.YEAR)));
                seasons.add(season);
                c.moveToNext();
            }
            c.close();
        }

        return seasons;
    }

    /**
     * Gets all matches for one season.
     *
     * @param seasonId Season id.
     * @return ArrayList<Match>.
     */
    public ArrayList<Match> getAllMatchForOneSeason(String seasonId) {

        String query = "SELECT * from " + SoccerContract.MatchEntry.TABLE_NAME + " where "
                + SoccerContract.MatchEntry.SEASON_ID + " = " + seasonId;
        return getMatches(query);
    }

    public ArrayList<Match> getAllMatch() {

        String query = "SELECT * from " + SoccerContract.MatchEntry.TABLE_NAME;
        return getMatches(query);
    }

    /**
     * Execute a query which could retrieve more than one Match.
     *
     * @param query Query to be executed.
     * @return matches.
     */
    private ArrayList<Match> getMatches(String query) {

        ArrayList<Match> matches = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if (c != null && c.getCount() > 0) {

            Log.d(TAG, "found: " + c.getCount());

            c.moveToFirst();
            while (!c.isAfterLast()) {
                matches.add(getMatchFromCursor(c));
                c.moveToNext();
            }
            c.close();
        }

        for (Match match : matches) {
            match.setHomeTeam(getTeam(match.getHomeTeamId()));
            match.setAwayTeam(getTeam(match.getAwayTeamId()));
        }

        return matches;
    }

    /**
     * Gets a match form Cursor.
     *
     * @param c Cursor.
     * @return match.
     */
    public Match getMatchFromCursor(Cursor c) {

        Match match = new Match();

        if (c != null) {
            match.setMatchId(String.valueOf(c.getInt(c.getColumnIndex(SoccerContract.MatchEntry._ID))));
            match.setSeasonId(c.getString(c.getColumnIndex(SoccerContract.MatchEntry.SEASON_ID)));
            match.setMatchDay(c.getString(c.getColumnIndex(SoccerContract.MatchEntry.MATCH_DAY)));
            match.setStatus(c.getString(c.getColumnIndex(SoccerContract.MatchEntry.STATUS)));
            match.setDate(c.getString(c.getColumnIndex(SoccerContract.MatchEntry.DATE)));
            match.setTime(c.getString(c.getColumnIndex(SoccerContract.MatchEntry.TIME)));
            match.setHomeGoals(c.getString(c.getColumnIndex(SoccerContract.MatchEntry.HOME_GOALS)));
            match.setAwayGoals(c.getString(c.getColumnIndex(SoccerContract.MatchEntry.AWAY_GOALS)));
            match.setHomeTeamId(c.getString(c.getColumnIndex(SoccerContract.MatchEntry.HOME_TEAM_ID)));
            match.setAwayTeamId(c.getString(c.getColumnIndex(SoccerContract.MatchEntry.AWAY_TEAM_ID)));
        }
        return match;
    }

    public Team getTeam(String id) {

        Team team = new Team();

        String query = "SELECT * from " + SoccerContract.TeamEntry.TABLE_NAME + " where "
                + SoccerContract.TeamEntry._ID + " = " + id;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {
            team.setId(c.getInt(c.getColumnIndex(SoccerContract.TeamEntry._ID)));
            team.setName(c.getString(c.getColumnIndex(SoccerContract.TeamEntry.NAME)));
            team.setShortName(c.getString(c.getColumnIndex(SoccerContract.TeamEntry.SHORT_NAME)));
            team.setThumbnail(c.getString(c.getColumnIndex(SoccerContract.TeamEntry.THUMBNAIL)));
            c.close();
        } else {
            Log.d(TAG, "Team with: " + id + " was not found.");
        }
        return team;
    }
}
