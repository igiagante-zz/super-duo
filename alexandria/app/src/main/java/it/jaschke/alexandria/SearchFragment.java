package it.jaschke.alexandria;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import it.jaschke.alexandria.barcode.BarcodeActivity;
import it.jaschke.alexandria.connections.Connection;
import it.jaschke.alexandria.data.DaoHelper;
import it.jaschke.alexandria.model.Book;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.services.DownloadImage;

/**
 * @author igiagante on 6/11/15.
 */
public class SearchFragment extends Fragment {

    public static final String PREFS_NAME = "MyPrefsFile";
    private StringBuilder queryList; //String list with comma
    private String[] queries; //list
    private AutoCompleteTextView searchView;

    private static final String BARCODE = "BARCODE";
    private static final String BOOK = "BOOK";
    private static final String SEARCH_QUERY = "SEARCH_QUERY";
    private static final String LIST_OF_QUERIES = "LIST_OF_QUERIES";
    private static final int BARCODE_REQUEST = 1;

    private View mContainerView;

    private TextView mBookTitle;
    private TextView mBookSubtitle;
    private TextView mBookDescription;
    private TextView mBookCategories;
    private TextView mBookAuthors;
    private ImageView mBookThumbnail;
    private ToggleButton mFavoriteBookButton;

    private Book mBook;
    private boolean buttonFavoritePressed = false;

    private DaoHelper mDaoHelper;
    private FavoriteBookListener mFavoriteBookListener;

    public interface FavoriteBookListener {
        void updateFavoriteBook();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContainerView = inflater.inflate(R.layout.fragment_search, container, false);

        mDaoHelper = new DaoHelper(getActivity());

        searchView = (AutoCompleteTextView) mContainerView.findViewById(R.id.search_query);

        mBookTitle = (TextView) mContainerView.findViewById(R.id.book_title);
        mBookSubtitle = (TextView) mContainerView.findViewById(R.id.book_subTitle);
        mBookDescription = (TextView) mContainerView.findViewById(R.id.book_description);
        mBookCategories = (TextView) mContainerView.findViewById(R.id.categories);
        mBookAuthors = (TextView) mContainerView.findViewById(R.id.authors);
        mBookThumbnail = (ImageView) mContainerView.findViewById(R.id.book_cover);

        if (savedInstanceState != null) {
            searchView.setText(savedInstanceState.getString(SEARCH_QUERY));
            mBook = savedInstanceState.getParcelable(BOOK);
        }

        initSearchQueryList();

        final Button searchButton = (Button) mContainerView.findViewById(R.id.alexandria_search_button);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();

                TextView textView = (TextView) mContainerView.findViewById(R.id.search_query);
                String query = textView.getText().toString();
                if (query.equals("")) {
                    textView.setError(getString(R.string.valid_query));
                } else {
                    if (!existQuery(query)) {
                        queryList.append(query);
                        queryList.append(",");
                        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(LIST_OF_QUERIES, queryList.toString());
                        editor.apply();
                    }

                    String ean = searchView.getText().toString();

                    if (!mDaoHelper.checkIfBookExistInFavoriteList(ean)) {
                        if (Connection.checkInternet(getActivity())) {
                            callBookService(ean);
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.internet_not_available), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        setBook(mDaoHelper.getBookFromDatabase(ean));
                    }
                }
            }
        });

        Button buttonScan = (Button) mContainerView.findViewById(R.id.alexandria_scan_button);

        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BarcodeActivity.class);
                startActivity(intent);
                startActivityForResult(intent, BARCODE_REQUEST);
            }
        });

        mFavoriteBookButton = (ToggleButton) mContainerView.findViewById(R.id.add_book_button);
        mFavoriteBookButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (Utils.isLandScape(getActivity()) || Utils.getSmallWithDisplay(getActivity()) > 550) {
                    if (isChecked && !mBook.isFavorite()) {
                        mDaoHelper.persistBook(mBook);
                        mFavoriteBookListener.updateFavoriteBook();
                    }

                    if (!isChecked && mBook.isFavorite()) {
                        mDaoHelper.deleteBook(mBook);
                        mFavoriteBookListener.updateFavoriteBook();
                        clearFields();
                        hideComponents();
                    }
                }

                if (isChecked) {
                    mBook.setFavorite(true);
                } else {
                    mBook.setFavorite(false);
                }

                buttonFavoritePressed = true;
            }
        });

        return mContainerView;
    }

    public void showComponents() {
        getActivity().findViewById(R.id.section_book_detail_container).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.container_book_description).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.add_book_button).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.book_cover).setVisibility(View.VISIBLE);
    }

    public void hideComponents() {
        getActivity().findViewById(R.id.section_book_detail_container).setVisibility(View.INVISIBLE);
        getActivity().findViewById(R.id.container_book_description).setVisibility(View.INVISIBLE);
        getActivity().findViewById(R.id.add_book_button).setVisibility(View.INVISIBLE);
        getActivity().findViewById(R.id.book_cover).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        hideKeyboard();
    }

    private void hideKeyboard() {
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        updateFavoriteBookData();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (searchView != null) {
            outState.putString(SEARCH_QUERY, searchView.getText().toString());
        }

        if (mBook != null) {
            outState.putParcelable(BOOK, mBook);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == BARCODE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                String barcode = data.getStringExtra(BARCODE);
                if (barcode != null) {
                    searchView.setText(barcode);
                    searchView.setSelection(searchView.getText().length());
                    callBookService(searchView.getText().toString());
                } else {
                    Toast.makeText(getActivity(), getString(R.string.barcode_not_found), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            if (activity instanceof MainActivity) {
                mFavoriteBookListener = (MainActivity) activity;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement FavoriteMovieListener");
        }
    }

    /**
     * Updates the favorite book in the database.
     */
    private void updateFavoriteBookData() {

        if (mBook != null && buttonFavoritePressed) {
            if (!mDaoHelper.checkIfBookExistInFavoriteList(mBook.getId()) && mBook.isFavorite()) {
                Log.d("Persist", "New book was marked as favorite");
                mDaoHelper.persistBook(mBook);
            } else if (!mBook.isFavorite() && buttonFavoritePressed) {
                Log.d("Delete", "New book was unmarked as favorite");
                mDaoHelper.deleteBook(mBook);
            }
        }
    }

    /**
     * Sets the values of the book in the view.
     *
     * @param book Book object.
     */
    public void setBook(Book book) {

        //saveS the last favorite book data
        updateFavoriteBookData();
        showComponents();
        clearFields();

        if (null != book) {

            getActivity().findViewById(R.id.section_book_detail_container).setVisibility(View.VISIBLE);
            this.mBook = book;

            mBookTitle.setText(book.getTitle());

            if (book.getSubtitle() != null) {
                mBookSubtitle.setText(book.getSubtitle());
                mBookSubtitle.setVisibility(View.VISIBLE);
            }

            if (book.getCategories() != null) {
                mBookCategories.setText(book.getCategories() + ".");
            }

            if (book.getAuthors() != null) {
                mBookAuthors.setText(book.getAuthors() + ".");
            }
            if (book.getDescription() != null) {
                getActivity().findViewById(R.id.container_book_description).setVisibility(View.VISIBLE);
                Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "Courgette-Regular.ttf");
                mBookDescription.setTypeface(typeface);
                mBookDescription.setText(book.getDescription());
            }

            if (book.getThumbnail() != null && !book.getThumbnail().isEmpty()) {
                new DownloadImage((ImageView) mContainerView.findViewById(R.id.book_cover)).execute(book.getThumbnail());
            }

            mFavoriteBookButton.setChecked(mDaoHelper.checkIfBookExistInFavoriteList(mBook.getId()));
        }
    }

    private void clearFields() {
        ((TextView) mContainerView.findViewById(R.id.book_title)).setText("");
        ((TextView) mContainerView.findViewById(R.id.book_subTitle)).setText("");
        ((TextView) mContainerView.findViewById(R.id.authors)).setText("");
        ((TextView) mContainerView.findViewById(R.id.categories)).setText("");

        if (mContainerView.findViewById(R.id.book_description).getVisibility() == View.VISIBLE) {
            ((TextView) mContainerView.findViewById(R.id.book_description)).setText("");
        }
    }

    /**
     * Calls the book's service to get the book's information.
     *
     * @param ean the book's code.
     */
    private void callBookService(String ean) {

        if ((ean.length() < 13 && ean.length() > 10)
                || ean.length() > 13
                || ean.length() < 10) {

            Toast.makeText(getActivity(), "The isbn should be a number of 10 or 13 digits",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (ean.length() == 10 && !ean.startsWith("978")) {
            ean = "978" + ean;
        }

        //Once we have an ISBN, start a book intent
        Intent bookIntent = new Intent(getActivity(), BookService.class);
        bookIntent.putExtra(BookService.EAN, ean);
        bookIntent.setAction(BookService.ACTION_GET_BOOK);
        getActivity().startService(bookIntent);
    }

    /**
     * Initializes the list of the last searches.
     */
    private void initSearchQueryList() {

        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        String list = settings.getString(LIST_OF_QUERIES, "");

        queryList = new StringBuilder(list);

        if (!list.isEmpty()) {
            queries = list.split(",");
        } else {
            queries = new String[1];
            queries[0] = "";
        }

        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, queries);
        searchView.setAdapter(adapter);
        searchView.setThreshold(1);

    }

    private boolean existQuery(String query) {
        if (queries != null && queries.length > 0) {
            for (int i = 0; i < queries.length; i++) {
                if (queries[i].equals(query))
                    return true;
            }
        }
        return false;
    }
}
