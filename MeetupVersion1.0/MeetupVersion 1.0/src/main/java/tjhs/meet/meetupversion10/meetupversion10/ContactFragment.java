package tjhs.meet.meetupversion10.meetupversion10;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LauncherActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Contacts.Photo;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AlphabetIndexer;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.SearchView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

public class ContactFragment extends ListFragment implements
        AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "ContactsListFragment";

    private static final String STATE_PREVIOUSLY_SELECTED_KEY = "tjhs.meet.meetupversion10.meetupversion10.SELECTEDITEM";

    private ContactsAdapter mAdapter;

    private String mSearchTerm;

    private OnContactsInteractionListener mOnContactSelectedListener;

    private int mPreviouslySelectedSearchItem = 0;

    private boolean mIsSearchResultView = false;

    private Uri[] mContactsArray;

    public ContactFragment() {}

    public void setSearchQuery(String query) {
        if (TextUtils.isEmpty(query)) {
            mIsSearchResultView = false;
        } else {
            mSearchTerm = query;
            mIsSearchResultView = true;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        mAdapter = new ContactsAdapter(getActivity());

        if (savedInstanceState != null) {
            mSearchTerm = savedInstanceState.getString(SearchManager.QUERY);
            mPreviouslySelectedSearchItem =
                    savedInstanceState.getInt(STATE_PREVIOUSLY_SELECTED_KEY, 0);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the list fragment layout
        return inflater.inflate(R.layout.contact_list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListAdapter(mAdapter);
        getListView().setOnItemClickListener(this);

        if (mPreviouslySelectedSearchItem == 0) {
            getLoaderManager().initLoader(ContactsQuery.QUERY_ID, null, this);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mOnContactSelectedListener = (OnContactsInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnContactsInteractionListener");
        }
    }

    @Override
    public void onStop(){
        SparseBooleanArray checked = getListView().getCheckedItemPositions();

        super.onStop();
    }

    public Uri[] getContactsArray(){
        return mContactsArray;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        final Cursor cursor = mAdapter.getCursor();

        cursor.moveToPosition(position);

        final Uri uri = Contacts.getLookupUri(
                cursor.getLong(ContactsQuery.ID),
                cursor.getString(ContactsQuery.LOOKUP_KEY));

        mOnContactSelectedListener.onContactSelected(uri);
    }

    private void onSelectionCleared() {
        mOnContactSelectedListener.onSelectionCleared();
        getListView().clearChoices();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.contact_list_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);

        if (mIsSearchResultView) {
            searchItem.setVisible(false);
        }

        final SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryText) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String newFilter = !TextUtils.isEmpty(newText) ? newText : null;
                if (mSearchTerm == null && newFilter == null) {
                    return true;
                }
                if (mSearchTerm != null && mSearchTerm.equals(newFilter)) {
                    return true;
                }
                mSearchTerm = newFilter;
                getLoaderManager().restartLoader(ContactsQuery.QUERY_ID, null, ContactFragment.this);
                return true;
            }
        });

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                // Nothing to do when the action item is expanded
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                // When the user collapses the SearchView the current search string is
                // cleared and the loader restarted.
                if (!TextUtils.isEmpty(mSearchTerm)) {
                    onSelectionCleared();
                }
                mSearchTerm = null;
                getLoaderManager().restartLoader(
                        ContactsQuery.QUERY_ID, null, ContactFragment.this);
                return true;
            }
        });

        if (mSearchTerm != null) {
            final String savedSearchTerm = mSearchTerm;

            searchItem.expandActionView();

            searchView.setQuery(savedSearchTerm, false);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!TextUtils.isEmpty(mSearchTerm)) {
            outState.putString(SearchManager.QUERY, mSearchTerm);

            outState.putInt(STATE_PREVIOUSLY_SELECTED_KEY, getListView().getCheckedItemPosition());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_contact:
                final Intent intent = new Intent(Intent.ACTION_INSERT, Contacts.CONTENT_URI);
                startActivity(intent);
                break;
            case R.id.menu_search:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (id == ContactsQuery.QUERY_ID) {
            Uri contentUri;

            if (mSearchTerm == null) {
                contentUri = ContactsQuery.CONTENT_URI;
            } else {
                contentUri = Uri.withAppendedPath(ContactsQuery.FILTER_URI, Uri.encode(mSearchTerm));
            }

            return new CursorLoader(getActivity(),
                    contentUri,
                    ContactsQuery.PROJECTION,
                    ContactsQuery.SELECTION,
                    null,
                    ContactsQuery.SORT_ORDER);
        }

        Log.e(TAG, "onCreateLoader - incorrect ID provided (" + id + ")");
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == ContactsQuery.QUERY_ID) {
            mAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == ContactsQuery.QUERY_ID) {
            mAdapter.swapCursor(null);
        }
    }

    private class ContactsAdapter extends CursorAdapter implements SectionIndexer {

        private LayoutInflater mInflater;

        private AlphabetIndexer mAlphabetIndexer;

        private TextAppearanceSpan highlightTextSpan;

        public ContactsAdapter(Context context) {
            super(context, null, 0);

            mInflater = LayoutInflater.from(context);

            final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

            mAlphabetIndexer = new AlphabetIndexer(null, ContactsQuery.SORT_KEY, alphabet);

            highlightTextSpan = new TextAppearanceSpan(getActivity(), R.style.searchTextHiglight);
        }

        private int indexOfSearchQuery(String displayName) {
            if (!TextUtils.isEmpty(mSearchTerm)) {
                return displayName.toLowerCase(Locale.getDefault()).indexOf(
                        mSearchTerm.toLowerCase(Locale.getDefault()));
            }
            return -1;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            final View itemLayout = mInflater.inflate(R.layout.contact_list_item, viewGroup, false);
            final ViewHolder holder = new ViewHolder();
            holder.text1 = (TextView) itemLayout.findViewById(android.R.id.text1);
            holder.text2 = (TextView) itemLayout.findViewById(android.R.id.text2);
            holder.icon = (QuickContactBadge) itemLayout.findViewById(android.R.id.icon);

            itemLayout.setTag(holder);

            return itemLayout;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            final ViewHolder holder = (ViewHolder) view.getTag();

            final String displayName = cursor.getString(ContactsQuery.DISPLAY_NAME);

            final int startIndex = indexOfSearchQuery(displayName);

            if (startIndex == -1) {
                holder.text1.setText(displayName);

                if (TextUtils.isEmpty(mSearchTerm)) {
                    holder.text2.setVisibility(View.GONE);
                } else {
                    holder.text2.setVisibility(View.VISIBLE);
                }
            } else {
                final SpannableString highlightedName = new SpannableString(displayName);

                highlightedName.setSpan(highlightTextSpan, startIndex,
                        startIndex + mSearchTerm.length(), 0);

                holder.text1.setText(highlightedName);

                holder.text2.setVisibility(View.GONE);
            }
        }


        @Override
        public Cursor swapCursor(Cursor newCursor) {
            mAlphabetIndexer.setCursor(newCursor);
            return super.swapCursor(newCursor);
        }

        @Override
        public int getCount() {
            if (getCursor() == null) {
                return 0;
            }
            return super.getCount();
        }

        @Override
        public Object[] getSections() {
            return mAlphabetIndexer.getSections();
        }

        @Override
        public int getPositionForSection(int i) {
            if (getCursor() == null) {
                return 0;
            }
            return mAlphabetIndexer.getPositionForSection(i);
        }

        @Override
        public int getSectionForPosition(int i) {
            if (getCursor() == null) {
                return 0;
            }
            return mAlphabetIndexer.getSectionForPosition(i);
        }

        private class ViewHolder {
            TextView text1;
            TextView text2;
            QuickContactBadge icon;
        }
    }

    public interface OnContactsInteractionListener {

        public void onContactSelected(Uri contactUri);
        public void onSelectionCleared();
    }

    public interface ContactsQuery {

        final static int QUERY_ID = 1;

        final static Uri CONTENT_URI = Contacts.CONTENT_URI;

        final static Uri FILTER_URI = Contacts.CONTENT_FILTER_URI;

        @SuppressLint("InlinedApi")
        final static String SELECTION = (Contacts.DISPLAY_NAME_PRIMARY) + "<>''" + " AND " + Contacts.IN_VISIBLE_GROUP + "=1";

        @SuppressLint("InlinedApi")
        final static String SORT_ORDER = Contacts.SORT_KEY_PRIMARY;

        final static String[] PROJECTION = {

                Contacts._ID,

                Contacts.LOOKUP_KEY,

                Contacts.DISPLAY_NAME_PRIMARY,

                Contacts.PHOTO_THUMBNAIL_URI,

                SORT_ORDER,
        };

        final static int ID = 0;
        final static int LOOKUP_KEY = 1;
        final static int DISPLAY_NAME = 2;
        final static int SORT_KEY = 4;
    }
}
