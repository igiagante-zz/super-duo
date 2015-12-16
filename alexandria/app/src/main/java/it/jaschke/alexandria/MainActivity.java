package it.jaschke.alexandria;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import it.jaschke.alexandria.api.Callback;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.data.DaoHelper;
import it.jaschke.alexandria.model.Book;
import it.jaschke.alexandria.services.BookService;

/**
 * @author igiagante on 6/11/15.
 */
public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        Callback,
        SearchFragment.FavoriteBookListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment navigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence title;

    /**
     * Used to get data book service.
     */
    private BroadcastReceiver messageReceiver;

    public static final String MESSAGE_KEY = "MESSAGE_EXTRA";
    public static final String EAN = "EAN";

    /**
     * Identifies the search fragment.
     */
    private static final String TAG_FRAGMENT_SEARCH = "TAG_FRAGMENT_SEARCH";
    /**
     * Identifies the list fragment.
     */
    private static final String TAG_FRAGMENT_LIST = "TAG_FRAGMENT_LIST";
    /**
     * Identifies the about fragment.
     */
    private static final String TAG_FRAGMENT_ABOUT = "TAG_FRAGMENT_ABOUT";

    /**
     * Fragment which represents the management of list of books.
     */
    private FragmentLibrary mLibraryFragment;
    /**
     * Fragment which represents the search of one book and its details.
     */
    private SearchFragment mSearchFragment;

    /**
     * Simple object which allows the access to the provider more easily.
     */
    private DaoHelper mDaoHelper;

    /**
     * Used to save the last id of the book which was selected.
     */
    private String lastBookIdSelected;

    private class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getStringExtra(MESSAGE_KEY) != null) {
                Toast.makeText(MainActivity.this, intent.getStringExtra(MESSAGE_KEY), Toast.LENGTH_LONG).show();
            } else if (intent.getAction().equals(BookService.NOTIFICATION_GET_BOOK)) {

                Book book = intent.getParcelableExtra(BookService.PARAM_BOOK);
                View viewBookDetail = findViewById(R.id.section_book_detail_container);

                if (null != book) {
                    viewBookDetail.setVisibility(View.VISIBLE);

                    mSearchFragment = (SearchFragment) getFragmentManager().findFragmentByTag(TAG_FRAGMENT_SEARCH);
                    mSearchFragment.setBook(book);
                } else {
                    viewBookDetail.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDaoHelper = new DaoHelper(this);

        if (null != savedInstanceState) {
            lastBookIdSelected = savedInstanceState.getString(EAN);
        }

        navigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        title = getTitle();

        if (null != findViewById(R.id.right_container)) {

            mSearchFragment = new SearchFragment();
            mLibraryFragment = new FragmentLibrary();

            FragmentTransaction fragmentListTransaction = getFragmentManager().beginTransaction();

            fragmentListTransaction.replace(R.id.container, mLibraryFragment, TAG_FRAGMENT_LIST);
            fragmentListTransaction.addToBackStack(null);
            fragmentListTransaction.commit();

            //In two-pane the trailer fragment is added by a fragment transaction.
            FragmentTransaction fragmentSearchTransaction = getFragmentManager().beginTransaction();

            fragmentSearchTransaction.replace(R.id.right_container, mSearchFragment, TAG_FRAGMENT_SEARCH);
            fragmentSearchTransaction.addToBackStack(null);
            fragmentSearchTransaction.commit();

        } else {
            // Set up the drawer.
            navigationDrawerFragment.setUp(R.id.navigation_drawer,
                    (DrawerLayout) findViewById(R.id.drawer_layout));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EAN, lastBookIdSelected);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        FragmentManager fragmentManager = getFragmentManager();
        Fragment nextFragment;
        String tag = "";

        switch (position) {
            default:
            case 0:
                nextFragment = new FragmentLibrary();
                tag = TAG_FRAGMENT_LIST;
                break;
            case 1:
                nextFragment = new SearchFragment();
                tag = TAG_FRAGMENT_SEARCH;
                break;
            case 2:
                nextFragment = new About();
                tag = TAG_FRAGMENT_ABOUT;
                break;

        }

        fragmentManager.beginTransaction()
                .replace(R.id.container, nextFragment, tag)
                .addToBackStack((String) title)
                .commit();
    }

    public void setTitle(int titleId) {
        title = getString(titleId);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title);
    }

    @Override
    public void updateFavoriteBook() {
        mLibraryFragment.updateBookList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!navigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        messageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BookService.NOTIFICATION_GET_BOOK);
        filter.addAction(BookService.MESSAGE_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, filter);

        //In case it rotates the phone, it needs to set the last book
        Cursor cursor = mDaoHelper.getCursorForBookEntry();

        if (lastBookIdSelected != null && null != findViewById(R.id.right_container)) {
            Book firstBook = getFirstBook(lastBookIdSelected);
            mSearchFragment.setBook(firstBook);
        } else if (null != findViewById(R.id.right_container) && cursor != null) {
            if (cursor.moveToFirst()) {
                String id = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry._ID));
                mSearchFragment.setBook(getFirstBook(id));
            }
        }
    }

    /**
     * Gets the first row of book entries.
     *
     * @param id the book's id.
     * @return Book to be set default.
     */
    private Book getFirstBook(String id) {

        Cursor cursor = mDaoHelper.getBookCursorById(id);
        if (!cursor.moveToFirst()) {
            return null;
        }

        return mDaoHelper.createBookFromCursor(cursor);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        super.onDestroy();
    }

    @Override
    public void onItemSelected(String ean) {

        lastBookIdSelected = ean;

        if (null != findViewById(R.id.right_container)) {
            mSearchFragment = (SearchFragment) getFragmentManager().findFragmentByTag(TAG_FRAGMENT_SEARCH);
        } else {
            mSearchFragment = new SearchFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, mSearchFragment, TAG_FRAGMENT_SEARCH)
                    .addToBackStack((String) title)
                    .commit();

            getFragmentManager().executePendingTransactions();
        }

        DaoHelper mDaoHelper = new DaoHelper(this);

        if (mSearchFragment.isVisible()) {
            mSearchFragment.setBook(mDaoHelper.getBookFromDatabase(ean));
        }
    }

    @Override
    public void onBackPressed() {

        FragmentManager fm = getFragmentManager();

        if (fm.getBackStackEntryCount() > 1) {
            fm.popBackStack();
        }

        if (fm.getBackStackEntryCount() == 1) {
            super.onBackPressed();
        }
    }
}