package tjhs.meet.meetupversion10.meetupversion10;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ContactActivity extends FragmentActivity implements ContactFragment.OnContactsInteractionListener {

    private ContactFragment mContactsFragment;

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
    }

    @Override
    public void onContactSelected(Uri contactUri) {
    }

    @Override
    public void onSelectionCleared(){
    }
}
