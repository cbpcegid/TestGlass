package com.example.mytest.testglass;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;

import org.json.JSONObject;
import org.json.JSONArray;


public class ProductActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        // Get the message from the intent
        Intent intent = getIntent();
        String message = intent.getStringExtra(LiveCardMenuActivity.EXTRA_MESSAGE);
        try {
            JSONObject json = new JSONObject(message);
            Log.i(ProductActivity.class.getName(), json.toString());
            Fragment productFragment = getFragmentManager().findFragmentById(R.id.product_fragment);
            if (productFragment!=null) {
                View productView = productFragment.getView();
                TextView product_label = (TextView) productView.findViewById(R.id.product_label);
                if (product_label != null)
                    product_label.setText(json.getString("Name"));
                TextView price_value = (TextView) productView.findViewById(R.id.price_value);
                if (price_value != null)
                    price_value.setText(json.getString("Price"));
                TextView stock_value = (TextView) productView.findViewById(R.id.stock_value);
                if (stock_value != null)
                    stock_value.setText(json.getString("Stock"));
            }

        } catch (Exception e)
        {
            Log.e(ProductActivity.class.getName(), e.getMessage());
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
 //       }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_product, container, false);
            return rootView;
        }
    }
}
