package data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * @author igiagante on 19/11/15.
 */
public class SoccerProvider extends ContentProvider {

    private static final int SEASON_ID = 100;
    private static final int SEASON = 101;
    private static final int SEASON_FULL = 102;

    private static final int TEAM_ID = 200;
    private static final int TEAM = 201;

    private static final int MATCH_ID = 300;
    private static final int MATCH = 301;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private DbHelper dbHelper;

    private static final SQLiteQueryBuilder bookFull;

    static {
        bookFull = new SQLiteQueryBuilder();
        bookFull.setTables(
                SoccerContract.SeasonEntry.TABLE_NAME + " LEFT OUTER JOIN " +
                        SoccerContract.MatchEntry.TABLE_NAME + " USING (" + SoccerContract.MatchEntry._ID + ")");
    }

    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = SoccerContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, SoccerContract.PATH_SEASONS + "/#", SEASON_ID);
        matcher.addURI(authority, SoccerContract.PATH_TEAMS + "/#", TEAM_ID);
        matcher.addURI(authority, SoccerContract.PATH_MATCHES + "/#", MATCH_ID);

        matcher.addURI(authority, SoccerContract.PATH_SEASONS, SEASON);
        matcher.addURI(authority, SoccerContract.PATH_TEAMS, TEAM);
        matcher.addURI(authority, SoccerContract.PATH_MATCHES, MATCH);

        matcher.addURI(authority, SoccerContract.PATH_SEASON_WITH_MATCHES + "/#", SEASON_FULL);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (uriMatcher.match(uri)) {
            case SEASON:
                retCursor = dbHelper.getReadableDatabase().query(
                        SoccerContract.SeasonEntry.TABLE_NAME,
                        projection,
                        selection,
                        selection == null ? null : selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case TEAM:
                retCursor = dbHelper.getReadableDatabase().query(
                        SoccerContract.TeamEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case MATCH:
                retCursor = dbHelper.getReadableDatabase().query(
                        SoccerContract.MatchEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case SEASON_ID:
                retCursor = dbHelper.getReadableDatabase().query(
                        SoccerContract.SeasonEntry.TABLE_NAME,
                        projection,
                        SoccerContract.SeasonEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case TEAM_ID:
                retCursor = dbHelper.getReadableDatabase().query(
                        SoccerContract.TeamEntry.TABLE_NAME,
                        projection,
                        SoccerContract.TeamEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case MATCH_ID:
                retCursor = dbHelper.getReadableDatabase().query(
                        SoccerContract.MatchEntry.TABLE_NAME,
                        projection,
                        SoccerContract.MatchEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case SEASON_FULL:
                retCursor = bookFull.query(dbHelper.getReadableDatabase(),
                        null,
                        null,
                        selectionArgs,
                        SoccerContract.SeasonEntry.TABLE_NAME + "." + SoccerContract.SeasonEntry._ID,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);

        switch (match) {
            case SEASON_ID:
                return SoccerContract.SeasonEntry.CONTENT_ITEM_TYPE;
            case TEAM_ID:
                return SoccerContract.TeamEntry.CONTENT_ITEM_TYPE;
            case MATCH_ID:
                return SoccerContract.MatchEntry.CONTENT_ITEM_TYPE;
            case SEASON:
                return SoccerContract.SeasonEntry.CONTENT_TYPE;
            case TEAM:
                return SoccerContract.TeamEntry.CONTENT_TYPE;
            case MATCH:
                return SoccerContract.MatchEntry.CONTENT_TYPE;
            case SEASON_FULL:
                return SoccerContract.SeasonEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case SEASON_ID: {
                long _id = db.insert(SoccerContract.SeasonEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = SoccerContract.SeasonEntry.buildSeasonUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                getContext().getContentResolver().notifyChange(SoccerContract.SeasonEntry.buildSeasonUri(_id), null);
                break;
            }
            case MATCH_ID: {
                long _id = db.insert(SoccerContract.MatchEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = SoccerContract.MatchEntry.buildMatchUri(values.getAsLong("_id"));
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case SEASON:
                rowsDeleted = db.delete(
                        SoccerContract.SeasonEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MATCH:
                rowsDeleted = db.delete(
                        SoccerContract.MatchEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TEAM:
                rowsDeleted = db.delete(
                        SoccerContract.TeamEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SEASON_ID:
                rowsDeleted = db.delete(
                        SoccerContract.SeasonEntry.TABLE_NAME,
                        SoccerContract.SeasonEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case SEASON:
                rowsUpdated = db.update(SoccerContract.SeasonEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case MATCH:
                rowsUpdated = db.update(SoccerContract.MatchEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int count = 0;

        switch (match) {
            case MATCH:
                db.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(SoccerContract.MatchEntry.TABLE_NAME, null, value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            count++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return count;

            case TEAM:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(SoccerContract.TeamEntry.TABLE_NAME, null, value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            count++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}