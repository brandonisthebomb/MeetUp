package tjhs.meet.meetupversion10.meetupversion10;

import android.app.ActionBar;
import android.app.LauncherActivity;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.SimpleArrayMap;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;

public class ContactActivity extends FragmentActivity implements ContactFragment.OnContactsInteractionListener, ContactFragment.DataPasser {

    private ActionBar mActionBar;

    private ContactFragment mContactsFragment;

    private SearchView mSearchView;

    private ArrayList<String> PhoneNumbers;

    private boolean isSearchResultView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        if (Intent.ACTION_SEARCH.equals(getIntent().getAction())){
            String searchQuery = getIntent().getStringExtra(SearchManager.QUERY);
            mContactsFragment = (ContactFragment)getSupportFragmentManager().findFragmentById(R.id.contact_list);
            isSearchResultView = true;
            mContactsFragment.setSearchQuery(searchQuery);
        }

        mActionBar = getActionBar();

        mActionBar.setDisplayShowHomeEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contact_list_menu, menu);
        mSearchView = (SearchView)menu.findItem(R.id.menu_search).getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public void onContactSelected(Uri contactUri) {
    }

    @Override
    public void onContactUnselected(Uri contactUri){
    }

    @Override
    public void onSelectionCleared(){
    }

    @Override
    public void onDataPass(ArrayList<String> data) {
        PhoneNumbers = data;
        Log.w("onDataPass", "got here");
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, MainActivity.class);
        Log.w("onBackPressed", "got here");
        intent.putStringArrayListExtra("PhoneNumbers", PhoneNumbers);
        setResult(RESULT_OK, intent);
        finish();
    }
}
