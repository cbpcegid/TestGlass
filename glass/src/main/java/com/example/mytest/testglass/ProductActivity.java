package com.example.mytest.testglass;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.InputStream;
import java.net.URL;


public class ProductActivity extends Activity {

    ImageView img;
    Bitmap bitmap;
    ProgressDialog pDialog;
    JSONObject json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the message from the intent
        Intent intent = getIntent();
        String message = intent.getStringExtra(LiveCardMenuActivity.EXTRA_MESSAGE);
        try {
            json = new JSONObject(message);
            Log.i(ProductActivity.class.getName(), json.toString());
        } catch (Exception e)
        {
            Log.e(ProductActivity.class.getName(), e.getMessage());
        }
        setContentView(R.layout.activity_product);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
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

        @Override
        public void onViewCreated(android.view.View view, android.os.Bundle savedInstanceState)
        {
            try {
                View productView = view;
                ImageView img = (ImageView)view.findViewById(R.id.product_image);
                ProductActivity activity = (ProductActivity)view.getContext();
                new LoadImage(view).execute(activity.json.getString("ImageUri"));
                TextView product_label = (TextView) productView.findViewById(R.id.product_label);
                if (product_label != null)
                    product_label.setText(activity.json.getString("Name"));
                TextView price_value = (TextView) productView.findViewById(R.id.price_value);
                if (price_value != null)
                    price_value.setText(activity.json.getString("Price"));
                TextView stock_value = (TextView) productView.findViewById(R.id.stock_value);
                if (stock_value != null)
                    stock_value.setText(activity.json.getString("Stock"));
            } catch (Exception e)
            {
                Log.e(ProductActivity.class.getName(), e.getMessage());
            }
        }

        private class LoadImage extends AsyncTask<String, String, Bitmap> {
            View _view;
            LoadImage(View view) {
                _view = view;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //pDialog = new ProgressDialog(ProductActivity.this);
                //pDialog.setMessage("@string/loading_image_msg");
                //pDialog.show();
            }

            protected Bitmap doInBackground(String... args) {
                try {

                    Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
                    return bitmap;
                } catch (Exception e) {
                    Log.e("LoadImage", e.getMessage());
                }
                return null;
            }
            protected void onPostExecute(Bitmap image) {
                if(image != null){
                    ImageView img = (ImageView)_view.findViewById(R.id.product_image);
                    img.setImageBitmap(image);
                    img.setImageBitmap(image);
                    //pDialog.dismiss();
                }else{
                    //pDialog.dismiss();
                    Toast.makeText((ProductActivity)_view.getContext(), "@string/image_not_found", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
