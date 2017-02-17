package com.example.peter.myapplication2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;

public class MainActivity extends AppCompatActivity{

    private Button button;
    private EditText txtMobile;
    private EditText txtPassword;
    private String mobile;
    private String password;
    private ProgressDialog progressDialog = null;

    //@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                txtMobile = (EditText) findViewById(R.id.editText);
                txtPassword = (EditText) findViewById(R.id.editText2);
                mobile = txtMobile.getText().toString();
                password = txtPassword.getText().toString();

                if(mobile.length() != 10){
                    Toast.makeText(MainActivity.this,"Invalid phone number!" + mobile, Toast.LENGTH_SHORT).show();
                }else  if(password.isEmpty()){
                    Toast.makeText(MainActivity.this, "Password can not be empty!", Toast.LENGTH_SHORT).show();

                }else if(!isNetworkAvailable()){
                    Toast.makeText(MainActivity.this,"Please check your internet connection! ", Toast.LENGTH_SHORT).show();

                }
                else{

                    AsyncTaskRunner runner = new AsyncTaskRunner();
                    runner.execute("1000",mobile,password);

                }//end if

            }//end onclick

        });



    }

    /**
     *
     * @return
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }






    /**
     * @author Prabu
     * Private class which runs the long operation. ( Sleeping for some time )
     */
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {



        private String resp;

        @Override
        protected String doInBackground(String... params) {

            try {
                // Do your long operations here and return the result
                int time = Integer.parseInt(params[0]);
                String mobileStr = params[1];
                String passwordStr = params[2];
                resp = "Sent...";

               // sendHTTPRequest(mobileStr,passwordStr);


                postData(mobileStr,passwordStr);


            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
           // Toast.makeText(MainActivity.this, "result:" + result, Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
            progressDialog = new ProgressDialog(MainActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onProgressUpdate(Progress[])
         */
        @Override
        protected void onProgressUpdate(String... text) {
            Toast.makeText(MainActivity.this, text[0], Toast.LENGTH_SHORT).show();
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }



    }


    public void postData(String mobile , String password) {
        HttpClient httpclient = new DefaultHttpClient();
        String getUrl = "http://4a0fb6ec.ngrok.io/CarTracker/user/driverLogin?";

        // specify the URL you want to post to
        HttpPost httppost = new HttpPost(getUrl);
        try {
            // create a list to store HTTP variables and their values
            String encodeMobile = URLEncoder.encode(mobile, "UTF-8");
            String encodePassword = URLEncoder.encode(password, "UTF-8");
            List nameValuePairs = new ArrayList();
            // add an HTTP variable and value pair
            nameValuePairs.add(new BasicNameValuePair("mobile", encodeMobile));
            nameValuePairs.add(new BasicNameValuePair("password", encodePassword));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            // send the variable and value, in other words post, to the URL
            HttpResponse response = httpclient.execute(httppost);

            //String responseStr = new String(response.toString());
            String responseStr = EntityUtils.toString(response.getEntity());
            String status = "";
            String message = "";


            JSONObject obj = new JSONObject(responseStr);
            status = (String) obj.get("status");
            status = status.trim();
            message = (String) obj.get("message");

            if(status.equals("OK")){

                Intent intent = new Intent(MainActivity.this,ScheduleActivity.class);
                startActivity(intent);
                finish();

            }

          //  return message;


        } catch (ClientProtocolException e) {
            // process execption
        } catch (IOException e) {
            // process execption
        } catch (JSONException e) {
            e.printStackTrace();
        }

       // return resp;
    }

}



