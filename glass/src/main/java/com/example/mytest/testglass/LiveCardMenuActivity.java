package com.example.mytest.testglass;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.net.http.AndroidHttpClient;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * A transparent {@link Activity} displaying a "Stop" options menu to remove the {@link LiveCard}.
 */
public class LiveCardMenuActivity extends Activity {

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Open the options menu right away.
        openOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.live_card, menu);
        return true;
    }

    private class SendPostTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // Make your request POST here. Example:
            myRequestPost();
            return null;
        }

        protected void onPostExecute(Void result) {
            // Do something when finished.
        }

        private int myRequestPost() {

            int resultCode = 0;

            String url = "http://192.168.0.26/kanguru/api/product/current";

            HttpClient client = new DefaultHttpClient();
            HttpGet post = new HttpGet(url);

            // add headers you want, example:
            // post.setHeader("Authorization", "YOUR-TOKEN");

            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            urlParameters.add(new BasicNameValuePair("id", "111111"));
            urlParameters.add(new BasicNameValuePair("otherField", "your-other-data"));

            try {
                //post.setEntity(new UrlEncodedFormEntity(urlParameters));

                HttpResponse response = client.execute(post);
                System.out.println("\nSending 'GET' request to URL : " + url);
                //System.out.println("Post parameters : " + post.getEntity());
                System.out.println("Response Code : " +
                        response.getStatusLine().getStatusCode());

                resultCode = response.getStatusLine().getStatusCode();

                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));

                StringBuffer result = new StringBuffer();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }

                System.out.println(result.toString());
            } catch (Exception e) {
                Log.e("POST", e.getMessage());
            }

            return resultCode;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_httpget:
                new SendPostTask().execute();
                return true;
            case R.id.action_stop:
                // Stop the service which will unpublish the live card.
                stopService(new Intent(this, LiveCardService.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
        // Nothing else to do, finish the Activity.
        finish();
    }
}

