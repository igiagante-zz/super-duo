package it.jaschke.alexandria.parsers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.jaschke.alexandria.model.Book;

/**
 * @author igiagante on 6/11/15.
 */
public class BookParser {

    /**
     * Parses the call to the service book's result into the Book object.
     *
     * @param result JSON to be parsed.
     * @return Book object.
     */
    public static Book parseBookJSON(String result) {

        Book book = new Book();

        String ITEMS = "items";

        String VOLUME_INFO = "volumeInfo";

        String TITLE = "title";
        String SUBTITLE = "subtitle";
        String DESC = "description";

        String IMG_URL_PATH = "imageLinks";
        String IMG_URL = "thumbnail";
        String AUTHORS = "authors";
        String CATEGORIES = "categories";

        try {

            JSONObject bookJson = new JSONObject(result);
            JSONArray bookArray = bookJson.getJSONArray(ITEMS);
            JSONObject bookInfo = ((JSONObject) bookArray.get(0)).getJSONObject(VOLUME_INFO);

            book.setTitle(bookInfo.getString(TITLE));

            if (bookInfo.has(SUBTITLE)) {
                book.setSubtitle(bookInfo.getString(SUBTITLE));
            }

            if (bookInfo.has(DESC)) {
                book.setDescription(bookInfo.getString(DESC));
            }

            if (bookInfo.has(IMG_URL_PATH) && bookInfo.getJSONObject(IMG_URL_PATH).has(IMG_URL)) {
                book.setThumbnail(bookInfo.getJSONObject(IMG_URL_PATH).getString(IMG_URL));
            }

            if(bookInfo.has(AUTHORS)) {
                addAuthors(book, bookInfo.getJSONArray(AUTHORS));
            }

            if(bookInfo.has(CATEGORIES)) {
                addCategories(book, bookInfo.getJSONArray(CATEGORIES));
            }

        } catch (JSONException e) {
            Log.e("Error ", e.toString());
        }

        return book;
    }

    private static void addAuthors(Book book, JSONArray jsonArray) throws JSONException {

        StringBuilder authors = new StringBuilder();

        for (int i = 0; i < jsonArray.length(); i++) {
            authors.append(jsonArray.getString(i));

            if (i != jsonArray.length() - 1) {
                authors.append(", ");
            }
        }

        book.setAuthors(authors.toString());
    }

    private static void addCategories(Book book, JSONArray jsonArray) throws JSONException {

        StringBuilder categories = new StringBuilder();

        for (int i = 0; i < jsonArray.length(); i++) {
            categories.append(jsonArray.getString(i));
            if (i != jsonArray.length() - 1) {
                categories.append(", ");
            }
        }

        book.setCategories(categories.toString());
    }
}
