/*
* CrawlActivity - PubCrawler Applicaiton
* TCSS450 - Fall 2016
*
*/
package edu.uw.tacoma.jwolf059.pubcrawler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * The LoginActivity Activity will allows for the authetication of a single user. It provides each
 * user the oppurtunity to register for a pubCrawler account, sign in using the users pubCrawler
 * credentials, or sign in using facebook.
 * @version 2 Nov 2016
 * @author Jeremy Wolf
 *
 */
public class LoginActivity extends AppCompatActivity {

    /** String containing the URL to the authenticate.php code */
    public final static String SIGNIN_URL = "http://cssgate.insttech.washington.edu/~jwolf059/authenticate.php?";
    // Contains the SharedPreference object
    private SharedPreferences mSharedPreferences;
    // Facebook Callback manager
    CallbackManager callbackManager;
    //String containing the LoginId
    private String mloginID;
    // String containing the password
    private String mPassword;
    // Facebook login button
    private LoginButton loginButton;


    /**
     * Creates the Login Activity.
     *{@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);

        //Checks to see if the device has an active login.
        if (!mSharedPreferences.getBoolean(getString(R.string.LOGGEDIN), false)) {
            setContentView(R.layout.activity_login);
            final EditText userIdText = (EditText) findViewById(R.id.user_name);
            final EditText pwdText = (EditText) findViewById(R.id.password);

            //Creats the Facebook loginbutton
            loginButton = (LoginButton) findViewById(R.id.login_button);
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.i("Logged in", "Yep");
                    mSharedPreferences
                            .edit()
                            .putBoolean(getString(R.string.LOGGEDIN), true)
                            .commit();
                    Intent intent = new Intent(getApplicationContext(), PubCrawler_Main.class);
                    startActivity(intent);


                    finish();
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException e) {

                }

            });

            //loginButton.clearPermissions();


            //Creates the custom signinButton
            Button signInButton = (Button) findViewById(R.id.sign_in);
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mloginID = userIdText.getText().toString();
                    mPassword = pwdText.getText().toString();

                    //Validates that the Username Field
                    if (TextUtils.isEmpty(mloginID)) {
                        Toast.makeText(v.getContext(), "Enter your Email Address"
                                , Toast.LENGTH_SHORT)
                                .show();
                        userIdText.requestFocus();
                        return;
                    }
                    //Verifies that an email is used
                    if (!mloginID.contains("@")) {
                        Toast.makeText(v.getContext(), "Enter a valid email address"
                                , Toast.LENGTH_SHORT)
                                .show();
                        userIdText.requestFocus();
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
                    String url = buildSignInURL(v);
                    logIn(url);
                }
            });
        } else {
            //Launches Main Activity if Active loggin is found.
            Intent i = new Intent(this, PubCrawler_Main.class);
            startActivity(i);
            finish();
        }
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Creates the Login URL to be sent to the PHP server code that will Autheticate the users
     * credentails
     * @param v the Current View
     * @return String that contains the URL to inlude login ID and Password.
     */
    private String buildSignInURL(View v) {

        StringBuilder sb = new StringBuilder(SIGNIN_URL);

        try {

            sb.append("id='");
            sb.append(mloginID.toLowerCase());
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
     * Transfers the Loging URL to the methods that will execute the authetication.
     * @param url String that contains the URL to inlude login ID and Password.
     */
    public void logIn(String url) {
        LoginTask task = new LoginTask();
        task.execute(new String[]{url.toString()});
    }

    /**
     * Takes the user to the Register Activity.
     * @param view the current view..
     */
    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * Creates the LoginTask that executes the authetification and verifys the returned results.
     */
    private class LoginTask extends AsyncTask<String, Void, String> {

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
                    response = "Unable to Login, Reason: "
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
            Log.e("Results: ", result);
            // Something wrong with the network or the URL.
            try {
                Log.e("Result contains", result);
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("sucess")) {
                    Toast.makeText(getApplicationContext(), "You have successfully Logged In"
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
                    Toast.makeText(getApplicationContext(), "Incorrect Username or Password: "
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






