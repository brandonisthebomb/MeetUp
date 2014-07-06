package sharan.internalstorage;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button b= (Button) findViewById(R.id.button);
        final Context con= getApplicationContext();
        final int duration= Toast.LENGTH_SHORT;
        String string= "good";
        b.setOnClickListener(new Button.OnClickListener(){
        @Override
        public void onClick(View arg0)
        {
            FileOutputStream fos= null;
            try {
                fos = openFileOutput("manny", Context.MODE_PRIVATE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String s= "IT'S ALIIIIVEEEEE!!!";
            if (fos != null) {
                try {
                    fos.write(s.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileInputStream fis=null;
            try {
                fis= openFileInput("manny");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            byte[] bite=new byte[20];
            try {
                fis.read(bite,0, bite.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String string= new String(bite);
            final Toast toast= Toast.makeText(con, string,duration);
            toast.show();
            TextView tv= (TextView) findViewById(R.id.textview);
            tv.setText(string);
        }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
        return super.onOptionsItemSelected(item);
    }
}
