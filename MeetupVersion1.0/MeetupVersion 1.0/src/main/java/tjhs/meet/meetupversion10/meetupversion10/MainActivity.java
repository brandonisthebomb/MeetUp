package tjhs.meet.meetupversion10.meetupversion10;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import tjhs.meet.meetupversion10.meetupversion10.UI.Sphere;

public class MainActivity extends FragmentActivity{

    final String tag = "MainActivity";
    private ActionBar myActionBar;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] mDrawerListItems;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private RelativeLayout mLayout;

    private TextView dummyView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set up the Actionbar and the drawerlist for menus
        myActionBar = getActionBar();

        mDrawerListItems = getResources().getStringArray(R.array.nav_drawer_items);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.ic_navigation_drawer,
                R.string.drawer_open,
                R.string.drawer_close);

        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
        myActionBar.setDisplayHomeAsUpEnabled(true);
        myActionBar.setHomeButtonEnabled(true);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mDrawerListItems));
        // Set the list's click listener
        //mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mLayout = (RelativeLayout)findViewById(R.id.main_layout);

        init();
    }

    private void init(){
        Sphere whoSphere = new Sphere(this, 150, 150, 70, getResources().getColor(R.color.colorPrimary),null);
        whoSphere.setText("Who");
        whoSphere.setOnClickListener(new whoSphereOnClickListener());
        mLayout.addView(whoSphere, whoSphere.getParams());

        dummyView = new TextView(this);
        dummyView.setLayoutParams(new RelativeLayout.LayoutParams(0,0));
        mLayout.addView(dummyView);

        setupUI(mLayout);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
        }
    }

    public void saveFunction(View view){

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds` items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        if(mActionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(MainActivity.this);
                    dummyView.requestFocus();
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }



    private class whoSphereOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view){
            Intent intent = new Intent(MainActivity.this, ContactsActivity.class);
            startActivity(intent);
        }
    }

}
