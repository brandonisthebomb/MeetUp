package tjhs.meet.meetupversion10.meetupversion10;

import android.app.ActionBar;
import android.app.LauncherActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.SearchView;
import android.widget.TextView;

public class ContactActivity extends FragmentActivity implements ContactFragment.OnContactsInteractionListener {

    private ActionBar mActionBar;

    private ContactFragment mContactsFragment;

    private SearchView mSearchView;

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
        mSearchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mActionBar.setDisplayShowHomeEnabled(true);
                return false;
            }
        });

        return true;
    }

    @Override
    public void onContactSelected(Uri contactUri) {

    }

    @Override
    public void onSelectionCleared(){
    }
}
