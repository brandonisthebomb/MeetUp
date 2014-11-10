package tjhs.meet.meetupversion10.meetupversion10;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
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
import android.telephony.SmsManager;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity{

    final String tag = "MainActivity";

    private ActionBar myActionBar;

    private DrawerLayout mDrawerLayout;

    private ListView mDrawerList;

    private String[] mDrawerListItems;

    private ActionBarDrawerToggle mActionBarDrawerToggle;

    private RelativeLayout mLayout;

    private TextView dummyView;

    private GridView myWhoGridView;

    private EditText mEditText;

    private String[] GroupPreferences;

    private ArrayList<String> mContactNumbers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set up the Actionbar and the drawerlist for menus
        myActionBar = getActionBar();
        myActionBar.hide();

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

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mDrawerListItems));
        // Set the list's click listener
        //mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mLayout = (RelativeLayout)findViewById(R.id.main_layout);

        myWhoGridView = (GridView)findViewById(R.id.who_gridview);

        int mLength = 1;
//        if (savedInstanceState != null){
//            mLength = savedInstanceState.getInt("num_contacts");
//            mContactNumbers = savedInstanceState.getStringArrayList("mContactsPhoneArray");
//        }
//        else{
//            mContactNumbers = new ArrayList<String>();
//        }
        mContactNumbers = new ArrayList<String>();


        //Create who grid
        GroupPreferences = new String[mLength];
        myWhoGridView.setAdapter(new GroupAdapter(this, GroupPreferences));
        myWhoGridView.setOnItemClickListener(new GridItemListener());

        //Associate Edit text
        mEditText = (EditText)findViewById(R.id.main_edittext);

        //Set up UI------------------------------------------------------
        dummyView = new TextView(this);
        dummyView.setLayoutParams(new RelativeLayout.LayoutParams(0,0));
        mLayout.addView(dummyView);
        setupUI(mLayout);
        //-----------------------------------------------------------------
        Log.w("MainActivity", "onCreate");
    }


    @Override
    protected void onSaveInstanceState(Bundle outstate){
        super.onSaveInstanceState(outstate);
        outstate.putStringArrayList("mContactsPhoneArray", mContactNumbers);
        Log.w("MainActivity", "onSaveInstanceState");

    }


    public void send(View view){
        if (mContactNumbers.isEmpty()) {
            Toast.makeText(this, "No Contacts selected", Toast.LENGTH_SHORT).show();
        } else {

            // get the message from the message text box
            String msg = mEditText.getText().toString();

            for (int i = 0; i < mContactNumbers.size(); i++) {
                String phoneNumber = mContactNumbers.get(i);
                Toast.makeText(this, phoneNumber, Toast.LENGTH_SHORT).show();
                // call the sms manager
                PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
                SmsManager sms = SmsManager.getDefault();
                // this is the function that does all the magic
                sms.sendTextMessage(phoneNumber, null, msg, pi, null);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.w("onActivityResult", "gets here");
        if (requestCode == 1){
            if (resultCode == RESULT_OK){
                Log.w("onActivityResult", "gets here");
                mContactNumbers = data.getStringArrayListExtra("PhoneNumbers");
            }
        }
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

    private class GroupAdapter extends BaseAdapter{

        private LayoutInflater mInflater;

        private String[] mContactsArray;

        public GroupAdapter(Context context, String[] array){
            mInflater = LayoutInflater.from(context);
            mContactsArray = array;
        }

        public int getCount(){
            int count = mContactsArray.length;
            if(count == 0){
                return 1;
            }
            return count;
        }

        @Override
        public Object getItem(int position) {
            return mContactsArray[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = mInflater.inflate(R.layout.group_list_item, parent, false);
            if(position == 0){
                convertView = mInflater.inflate(R.layout.group_add_item, parent, false);
            }

            return convertView;

        }
    }

    private class GridItemListener implements GridView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(position == 0){
                Intent intent = new Intent(MainActivity.this, ContactActivity.class);
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, 1);
            }
        }
    }


}
