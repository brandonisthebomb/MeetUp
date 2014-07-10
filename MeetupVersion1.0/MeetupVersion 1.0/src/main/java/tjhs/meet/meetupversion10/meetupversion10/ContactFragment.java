package tjhs.meet.meetupversion10.meetupversion10;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

import tjhs.meet.meetupversion10.meetupversion10.R;

/**
 * Created by Brandon on 7/5/14.
 */
public class ContactFragment extends DialogFragment implements
    LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    private static final String[] PROJECTION = {ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};
    private static final int CONTACT_ID_INDEX = 0;
    private static final int LOOKUP_KEY_INDEX = 0;

    private static final String[] FROM_COLUMNS = {ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};
    private static final int[] TO_IDS = {android.R.id.text1};
    ListView mContactsList;
    long mContactId;
    String mContactKey;
    Uri mContactUri;
    private SimpleCursorAdapter mCursorAdapter;

    private static final String SELECTION = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?";
    private String mSearchString;
    SearchView mSearchView;
    private String[] mSelectionArgs = {mSearchString};

    private static final String STATE_PREVIOUSLY_SELECTED_KEY = "tjhs.meet.meetupversion10.meetupversion10.SELECTED_ITEM";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the list fragment layout
        View view =  inflater.inflate(R.layout.contact_list_fragment, container, false);
        mContactsList = (ListView)view.findViewById(R.id.list);
        mCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.contact_list_item, null,
                FROM_COLUMNS, TO_IDS, 0);
        mContactsList.setAdapter(mCursorAdapter);
        mContactsList.setOnItemClickListener(this);
        getLoaderManager().initLoader(0, null, this);

        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View item, int position, long rowID){
        Cursor cursor = ((SimpleCursorAdapter)parent.getAdapter()).getCursor();
        cursor.moveToPosition(position);
        mContactId = cursor.getLong(CONTACT_ID_INDEX);
        mContactKey = cursor.getString(LOOKUP_KEY_INDEX);
        mContactUri = ContactsContract.Contacts.getLookupUri(mContactId, mContactKey);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args){
        mSelectionArgs[0] = "%" + mSearchString + "%";
        return new CursorLoader(getActivity(),
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,
                SELECTION,
                mSelectionArgs,
                null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor){
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        mCursorAdapter.swapCursor(null);
    }

    public static class CustomSearchView extends SearchView{
        public CustomSearchView (Context context){
            super(context);
        }

        @Override
        public void onActionViewCollapsed(){
            setQuery("", false);
            super.onActionViewCollapsed();
        }
    }

    public void setSearchQuery(String query){
        mSearchString = query;
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){

        inflater.inflate(R.menu.contact_list_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        final SearchManager searchManager = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView)searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String newSearchString = !TextUtils.isEmpty(newText) ? newText : null;
                if (mSearchString == null && newSearchString == null){
                    return true;
                }
                if (mSearchString != null && mSearchString.equals(newSearchString)){
                    return true;
                }
                mSearchString = newSearchString;
                getLoaderManager().restartLoader(0, null, ContactFragment.this);
                return true;
            }
        });

        if(mSearchString != null){
            final String savedSearchString = mSearchString;
            searchView.setQuery(savedSearchString, false);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        if(!TextUtils.isEmpty(mSearchString)){
            outState.putString(SearchManager.QUERY, mSearchString);
     //       outState.putInt(STATE_PREVIOUSLY_SELECTED_KEY, getListView().getChecked );

        }
    }

}
