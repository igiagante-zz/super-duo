package it.jaschke.alexandria.services;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import it.jaschke.alexandria.MainActivity;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.model.Book;
import it.jaschke.alexandria.parsers.BookParser;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class BookService extends IntentService {

    private final String LOG_TAG = BookService.class.getSimpleName();

    //Params
    public static final String PARAM_BOOK = "PARAM_BOOK";

    //Actions
    public static final String ACTION_GET_BOOK = "ACTION_GET_BOOK";
    public static final String ACTION_DELETE_BOOK = "ACTION_DELETE_BOOK";

    //Notifications
    public static final String NOTIFICATION_GET_BOOK = "NOTIFICATION_GET_BOOK";
    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";

    public static final String EAN = "EAN";

    public BookService() {
        super("Alexandria");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_BOOK.equals(action)) {
                final String ean = intent.getStringExtra(EAN);
                Book book = getBook(ean);
                publishResults(book);
            } else if (ACTION_DELETE_BOOK.equals(action)) {
                final String ean = intent.getStringExtra(EAN);
                deleteBook(ean);
            }
        }
    }

    private void publishResults(Book book) {
        Intent intent = new Intent();
        intent.putExtra(PARAM_BOOK, book);
        intent.setAction(NOTIFICATION_GET_BOOK);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void deleteBook(String ean) {
        if (ean != null) {
            getContentResolver().delete(AlexandriaContract.BookEntry.buildBookUri(Long.parseLong(ean)), null, null);
        }
    }

    /**
     * Handle action fetchBook in the provided background thread with the provided
     * parameters.
     */
    private Book getBook(String ean) {

        if (ean.length() != 13) {
            return null;
        }

        String ITEMS = "items";

        try {
            URL bookUrl = createBookUrl(ean);

            Log.d("URL ", bookUrl.toString());

            String result = getData(bookUrl.toString());

            JSONObject bookJson = new JSONObject(result);

            if (!bookJson.has(ITEMS)) {
                Intent messageIntent = new Intent(MESSAGE_EVENT);
                messageIntent.putExtra(MainActivity.MESSAGE_KEY, getResources().getString(R.string.not_found));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(messageIntent);
                return null;
            } else {
                Book book = BookParser.parseBookJSON(result);
                book.setId(ean);

                return book;
            }
        } catch (MalformedURLException urlException) {
            Log.d(LOG_TAG, urlException.toString());
        } catch (JSONException je) {

        }
        return null;
    }

    @NonNull
    private URL createBookUrl(String ean) throws MalformedURLException {
        final String FORECAST_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
        final String QUERY_PARAM = "q";

        final String ISBN_PARAM = "isbn:" + ean;

        Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, ISBN_PARAM)
                .build();

        Log.d("url", builtUri.toString());

        return new URL(builtUri.toString());
    }

    /**
     * Returns the data asked by a request.
     *
     * @param url the address where the data is requested.
     * @return String contains the streamed information.
     */
    private String getData(String url) {

        BufferedReader reader = null;
        HttpURLConnection urlConnection = null;

        try {
            //get connection
            urlConnection = connect(url);

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            return buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    /**
     * Create an url connection.
     *
     * @param path url address.
     * @return HttpURLConnection
     * @throws IOException if the address is wrong or there is not internet connection.
     */
    private HttpURLConnection connect(String path) throws IOException {

        URL url = new URL(path);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();

        return urlConnection;
    }

}