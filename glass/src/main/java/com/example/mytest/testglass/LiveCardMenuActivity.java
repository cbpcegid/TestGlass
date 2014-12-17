package com.example.mytest.testglass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.Toast;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.view.WindowUtils;
import com.jaxbot.glass.barcode.scan.CaptureActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

//interface du handler de méthode
interface IPostExecuteHandler {

    void execute(String result);
}

/**
 * A transparent {@link Activity} displaying a "Stop" options menu to remove the {@link com.example.mytest.testglass.LiveCardMenuActivity}.
 */
public class LiveCardMenuActivity extends Activity {

    private GestureDetector mGestureDetector;

    public final static String EXTRA_MESSAGE = "com.example.mytest.testglass.PRODUCTS_MESSAGE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);
        //getWindow().requestFeature(Window.FEATURE_OPTIONS_PANEL);
        mGestureDetector = createGestureDetector(this);
        setContentView(R.layout.live_card);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Open the options menu right away.
        //openOptionsMenu();
    }


    private GestureDetector createGestureDetector(Context context) {
        GestureDetector gestureDetector = new GestureDetector(context);

        //Create a base listener for generic gestures
        gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                if (gesture == Gesture.TAP) {
                    openOptionsMenu();
                    return true;
                } else if (gesture == Gesture.TWO_TAP) {
                    // do something on two finger tap
                    return true;
                } else if (gesture == Gesture.SWIPE_RIGHT) {
                    // do something on right (forward) swipe
                    return true;
                } else if (gesture == Gesture.SWIPE_LEFT) {
                    // do something on left (backwards) swipe
                    return true;
                } else if (gesture == Gesture.SWIPE_DOWN){
                    return true;
                }
                return false;
            }
        });

        gestureDetector.setFingerListener(new GestureDetector.FingerListener() {
            @Override
            public void onFingerCountChanged(int previousCount, int currentCount) {
                // do something on finger count changes
            }
        });

        gestureDetector.setScrollListener(new GestureDetector.ScrollListener() {
            @Override
            public boolean onScroll(float displacement, float delta, float velocity) {
                // do something on scrolling
                return true;
            }
        });

        return gestureDetector;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS ||
                featureId == Window.FEATURE_OPTIONS_PANEL) {
            getMenuInflater().inflate(R.menu.live_card, menu);
            return true;
        }
        // Pass through to super to setup touch menu.
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.live_card, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2)
        {
            String qrType = data.getStringExtra("qr_type");
            String qrData = data.getStringExtra("qr_data");
            if (qrData.startsWith("http://product:")) {
                new HttpAsyncTask(new CaptureProductHandler()).execute("http://192.168.0.26/kanguru/api/product/open?id=" + qrData.substring(15));
                //Intent productIntent = new Intent(this, ProductActivity.class);
                //productIntent.putExtra(EXTRA_MESSAGE, )
                //startActivityForResult(productIntent, 2);

                //http://192.168.0.26/kanguru/api/product/open?id=123456
            }
            if (qrData.startsWith("http://customer:")) {
                new HttpAsyncTask(new CaptureCustomerHandler()).execute("http://192.168.0.26/kanguru/api/customer/open?id=" + qrData.substring(16));

            }
            //this.findViewById(1).requestFocus()
        }
    }

    private boolean doCommand(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.action_capture:
                Intent captureIntent = new Intent(this, CaptureActivity.class);
                startActivityForResult(captureIntent, 2);

                return true;
            case R.id.action_httpget:
                new HttpAsyncTask(new CaptureProductHandler()).execute("http://192.168.0.26/kanguru/api/product/current");
                return true;
            case R.id.action_stop:
                // Stop the service which will unpublish the live card.
                stopService(new Intent(this, LiveCardService.class));
                finish();
                return true;
        }
        return false;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (!doCommand(item)) {
            return super.onMenuItemSelected(featureId, item);
        }
        return true;
    }

  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      if (!doCommand(item))
          return super.onOptionsItemSelected(item);
      return true;
    }

    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

       private class HttpAsyncTask extends AsyncTask<String, Void, String> {

       IPostExecuteHandler _handler;

        public HttpAsyncTask(IPostExecuteHandler handler)
        {
            _handler = handler;
        }
        private String productItems = "";

        @Override
        protected String doInBackground(String... urls) {

            productItems = GET(urls[0]);
            return productItems;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            _handler.execute(productItems);
        }
    }

    private class CaptureProductHandler implements IPostExecuteHandler
    {
        public void execute(String result) {
            Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
            Intent products = new Intent(getApplicationContext(), ProductActivity.class);
            products.putExtra(EXTRA_MESSAGE, result);
            startActivityForResult(products, 1);
        }
    }

    private class CaptureCustomerHandler implements IPostExecuteHandler
    {
        public void execute(String result) {
            Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
        }
    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append((line + "\n"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
        // Nothing else to do, finish the Activity.
        //finish();
    }
}
