/*
* CrawlActivity - PubCrawler Applicaiton
* TCSS450 - Fall 2016
*
*/
package edu.uw.tacoma.jwolf059.pubcrawler.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.uw.tacoma.jwolf059.pubcrawler.OptionScreens.PubCrawler_Main;
import edu.uw.tacoma.jwolf059.pubcrawler.R;


/**
 * An Activity to register new users.
 * @version 6 November 2016
 * @author Thang
 *
 */
public class RegisterActivity extends AppCompatActivity {

    /** URL used to contect to the User Database */
    private final static String REGISTER_USER_URL =
            "http://cssgate.insttech.washington.edu/~jwolf059/register.php?";
    //To keep user logged in when they use the app the next time.
    private SharedPreferences mSharedPreferences;
    //User's login ID. Is used to build the register request url.
    private String mLoginID;
    // User's login password. Is used to build the register request url.
    private String mPassword;


    /**
     * Creates view for the Register Activity
     * and implements the action listener for the Register button.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Hide the floating action button
        FloatingActionButton floatingActionButton = (FloatingActionButton)
                findViewById(R.id.fab);
        floatingActionButton.hide();

        final EditText userIDText = (EditText) findViewById(R.id.userid_edit);
        final EditText pwdText = (EditText) findViewById(R.id.pwd_edit);
        final EditText pwdVerText = (EditText) findViewById(R.id.pwdVer_edit);

        Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginID = userIDText.getText().toString();
                mPassword = pwdText.getText().toString();
                String pwdVerString = pwdVerText.getText().toString();

                //Validates that the Username Field
                if (TextUtils.isEmpty(mLoginID)) {
                    Toast.makeText(v.getContext(), "Enter your Email Address"
                            , Toast.LENGTH_SHORT)
                            .show();
                    userIDText.requestFocus();
                    return;
                }
                //Verifies that an email is used
                if (!mLoginID.contains("@")) {
                    Toast.makeText(v.getContext(), "Enter a valid email address"
                            , Toast.LENGTH_SHORT)
                            .show();
                    userIDText.requestFocus();
                    return;
                }
                //Validates that the password field is not empty
                if (TextUtils.isEmpty(mPassword)) {
                    Toast.makeText(v.getContext(), "Enter password"
                            , Toast.LENGTH_SHORT)
                            .show();
                    pwdText.requestFocus();
                    return;
                }
                //Validates that the password is not less the 6 Characters.
                if (mPassword.length() < 6) {
                    Toast.makeText(v.getContext()
                            , "Enter password of at least 6 characters"
                            , Toast.LENGTH_SHORT)
                            .show();
                    pwdText.requestFocus();
                    return;
                }
                //check for matching password and password confirmation
                if (!mPassword.equals(pwdVerString)) {
                    Toast.makeText(v.getContext(), "Passwords don't match. Please retype passwords",
                            Toast.LENGTH_SHORT).show();
                    pwdText.requestFocus();
                    return;
                }
                // Build the register request url
                String url = buildRegisterURL(v);
                // Launch the AsyncTask to run the register process in background
                new RegisterTask().execute(new String[]{url});
            }
        });


    }

    /**
     * Creates the Register URL to be sent to the PHP server code that will register the user
     * @param v the Current View
     * @return String that contains the URL to include login ID and Password.
     */
    private String buildRegisterURL(View v) {

        StringBuilder sb = new StringBuilder(REGISTER_USER_URL);

        try {
            sb.append("id='");
            sb.append(mLoginID.toLowerCase());
            sb.append("'");

            String pass = mPassword;
            sb.append("&pass='");
            sb.append(mPassword);
            sb.append("'");
        }
        catch(Exception e) {
            Toast.makeText(v.getContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
            Log.e("SB message", e.getMessage());
        }

        return sb.toString();
    }

    /**
     * Creates the RegisterTask that executes the register process and verifies the returned results.
     */
    private class RegisterTask extends AsyncTask<String, Void, String> {

        /**
         *{@inheritDoc}
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         *{@inheritDoc}
         */
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to Register, Reason: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }


        /**
         * It checks to see if there was a problem with the URL(Network) which is when an
         * exception is caught. It tries to call the parse Method and checks to see if it was successful.
         * If not, it displays the exception.
         *
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
            Log.e("Results: ", result);
            // Something wrong with the network or the URL.
            try {
                Log.e("Result contains", result);
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("sucess")) {
                    Toast.makeText(getApplicationContext(), "You have successfully registered and logged in"
                            , Toast.LENGTH_LONG)
                            .show();
                    //Adds active login boolean to the Shared Preferences.
                    mSharedPreferences
                            .edit()
                            .putBoolean(getString(R.string.LOGGEDIN), true)
                            .commit();
                    Intent intent = new Intent(getApplicationContext(), PubCrawler_Main.class);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(getApplicationContext(), "Unable to register: "
                                    + jsonObject.get("error")
                            , Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("Wrong Data", e.getMessage());
            }

        }
    }

}
