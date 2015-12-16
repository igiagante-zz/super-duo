package it.jaschke.alexandria.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

import it.jaschke.alexandria.model.Book;

/**
 * @author igiagante on 10/11/15.
 */
public class DaoHelper {

    private Context mContext;

    public DaoHelper(Context context) {
        mContext = context;
    }

    public void deleteBook(Book book) {
        mContext.getContentResolver().delete(AlexandriaContract.BookEntry
                .buildBookUri(Long.parseLong(book.getId())), null, null);
    }

    /**
     * Persists a book with its authors and categories
     *
     * @param book Book object.
     */
    public void persistBook(Book book) {

        ContentValues values = new ContentValues();
        values.put(AlexandriaContract.BookEntry._ID, book.getId());
        values.put(AlexandriaContract.BookEntry.TITLE, book.getTitle());
        values.put(AlexandriaContract.BookEntry.IMAGE_URL, book.getThumbnail());
        values.put(AlexandriaContract.BookEntry.SUBTITLE, book.getSubtitle());
        values.put(AlexandriaContract.BookEntry.DESC, book.getDescription());
        mContext.getContentResolver().insert(AlexandriaContract.BookEntry.CONTENT_URI, values);

        persistAuthors(book.getId(), book.getAuthors());

        persistCategories(book.getId(), book.getCategories());

    }

    private void persistAuthors(String bookId, String authorsParam) {

        if(authorsParam != null && !authorsParam.equals("")){
            ContentValues values = new ContentValues();
            String[] authors = authorsParam.split(",");

            for (int i = 0; i < authors.length; i++) {
                values.put(AlexandriaContract.AuthorEntry._ID, bookId);
                values.put(AlexandriaContract.AuthorEntry.AUTHOR, authors[i]);
                mContext.getContentResolver().insert(AlexandriaContract.AuthorEntry.CONTENT_URI, values);
                values = new ContentValues();
            }
        }
    }

    private void persistCategories(String bookId, String categoriesParam) {

        if(categoriesParam != null && !categoriesParam.equals("")) {
            ContentValues values = new ContentValues();
            String[] categories = categoriesParam.split(",");

            for (int i = 0; i < categories.length; i++) {
                values.put(AlexandriaContract.CategoryEntry._ID, bookId);
                values.put(AlexandriaContract.CategoryEntry.CATEGORY, categories[i]);
                mContext.getContentResolver().insert(AlexandriaContract.CategoryEntry.CONTENT_URI, values);
                values = new ContentValues();
            }
        }
    }

    public boolean checkIfBookExistInFavoriteList(String ean) {
        Cursor bookEntry = mContext.getContentResolver().query(
                AlexandriaContract.BookEntry.buildBookUri(Long.parseLong(ean)),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        if (bookEntry.getCount() > 0) {
            return true;
        }

        bookEntry.close();
        return false;
    }

    public Cursor getBookCursorById(String ean) {
        return mContext.getContentResolver().query(
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(ean)),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
    }


    public Book getBookFromDatabase(String ean) {
        Cursor data = mContext.getContentResolver().query(
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(ean)),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        if (!data.moveToFirst()) {
            return null;
        }

        return createBookFromCursor(data);
    }

    public CursorLoader getCursorLoaderForBookEntry() {
        return new CursorLoader(
                mContext,
                AlexandriaContract.BookEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    public Cursor getCursorForBookEntry() {
        return mContext.getContentResolver().query(
                AlexandriaContract.BookEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    /**
     * Creates an object book from one Cursor.
     *
     * @param cursor Cursor object.
     * @return Book object.
     */
    public Book createBookFromCursor(Cursor cursor) {

        Book book = new Book();
        book.setId(cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry._ID)));
        book.setTitle(cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.TITLE)));
        book.setSubtitle(cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE)));
        book.setDescription(cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.DESC)));
        book.setThumbnail(cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL)));

        String authors = cursor.getString(cursor.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        book.setAuthors(authors);

        String categories = cursor.getString(cursor.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        book.setCategories(categories);

        book.setFavorite(true);

        cursor.close();

        return book;
    }
}
