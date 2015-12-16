package data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author igiagante on 19/11/15.
 */
public class DbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 25;
    public static final String DATABASE_NAME = "soccer.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_SEASON_TABLE = "CREATE TABLE " + SoccerContract.SeasonEntry.TABLE_NAME + " (" +
                SoccerContract.SeasonEntry._ID + " INTEGER PRIMARY KEY," +
                SoccerContract.SeasonEntry.CAPTION + " TEXT NOT NULL," +
                SoccerContract.SeasonEntry.LEAGUE + " TEXT ," +
                SoccerContract.SeasonEntry.YEAR + " INTEGER ," +
                "UNIQUE (" + SoccerContract.SeasonEntry._ID + ") ON CONFLICT IGNORE)";

        final String SQL_CREATE_TEAM_TABLE = "CREATE TABLE " + SoccerContract.TeamEntry.TABLE_NAME + " (" +
                SoccerContract.TeamEntry._ID + " INTEGER PRIMARY KEY," +
                SoccerContract.TeamEntry.NAME + " TEXT NOT NULL," +
                SoccerContract.TeamEntry.SHORT_NAME + " TEXT NOT NULL," +
                SoccerContract.TeamEntry.THUMBNAIL + " INTEGER ," +
                "UNIQUE (" + SoccerContract.TeamEntry._ID + ") ON CONFLICT IGNORE)";

        final String SQL_CREATE_MATCH_TABLE = "CREATE TABLE " + SoccerContract.MatchEntry.TABLE_NAME + " (" +
                SoccerContract.MatchEntry._ID + " INTEGER," +
                SoccerContract.MatchEntry.SEASON_ID + " INTEGER," +
                SoccerContract.MatchEntry.HOME_TEAM_ID + " INTEGER," +
                SoccerContract.MatchEntry.AWAY_TEAM_ID + " INTEGER," +
                SoccerContract.MatchEntry.DATE + " TEXT," +
                SoccerContract.MatchEntry.TIME + " TEXT," +
                SoccerContract.MatchEntry.MATCH_DAY + " TEXT," +
                SoccerContract.MatchEntry.HOME_GOALS + " TEXT," +
                SoccerContract.MatchEntry.AWAY_GOALS + " TEXT," +
                SoccerContract.MatchEntry.STATUS + " TEXT," +
                " FOREIGN KEY (" + SoccerContract.MatchEntry.SEASON_ID + ") REFERENCES " +
                SoccerContract.SeasonEntry.TABLE_NAME + " (" + SoccerContract.SeasonEntry._ID + ")," +
                " FOREIGN KEY (" + SoccerContract.MatchEntry.HOME_TEAM_ID + ") REFERENCES " +
                SoccerContract.TeamEntry.TABLE_NAME + " (" + SoccerContract.TeamEntry._ID + ")," +
                " FOREIGN KEY (" + SoccerContract.MatchEntry.AWAY_TEAM_ID + ") REFERENCES " +
                SoccerContract.TeamEntry.TABLE_NAME + " (" + SoccerContract.TeamEntry._ID + "))";

        Log.d("sql-statments", SQL_CREATE_SEASON_TABLE);
        Log.d("sql-statments", SQL_CREATE_TEAM_TABLE);
        Log.d("sql-statments", SQL_CREATE_MATCH_TABLE);

        db.execSQL(SQL_CREATE_SEASON_TABLE);
        db.execSQL(SQL_CREATE_TEAM_TABLE);
        db.execSQL(SQL_CREATE_MATCH_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SoccerContract.SeasonEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SoccerContract.TeamEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SoccerContract.MatchEntry.TABLE_NAME);
        onCreate(db);
    }
}