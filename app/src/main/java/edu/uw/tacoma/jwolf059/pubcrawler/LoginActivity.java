package edu.uw.tacoma.jwolf059.pubcrawler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInstaller;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.R.attr.data;

public class LoginActivity extends AppCompatActivity {

    private final static String REGISTER_USER_URL = "http://cssgate.insttech.washington.edu/~jwolf059/register.php?";
    private SharedPreferences mSharedPreferences;
    private final static String SIGNIN_URL = "http://cssgate.insttech.washington.edu/~jwolf059/authenticate.php?";
    CallbackManager callbackManager;
    private String mloginID;
    private String mPassword;
    private LoginButton loginButton;

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
        if (!mSharedPreferences.getBoolean(getString(R.string.LOGGEDIN), false)) {
            setContentView(R.layout.activity_login);
            final EditText userIdText = (EditText) findViewById(R.id.user_name);
            final EditText pwdText = (EditText) findViewById(R.id.password);
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

            Log.i("Past", "Facebook");
            loginButton.clearPermissions();

            Button signInButton = (Button) findViewById(R.id.sign_in);
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mloginID = userIdText.getText().toString();
                    mPassword = pwdText.getText().toString();
                    if (TextUtils.isEmpty(mloginID)) {
                        Toast.makeText(v.getContext(), "Enter your Email Address"
                                , Toast.LENGTH_SHORT)
                                .show();
                        userIdText.requestFocus();
                        return;
                    }
                    if (!mloginID.contains("@")) {
                        Toast.makeText(v.getContext(), "Enter a valid email address"
                                , Toast.LENGTH_SHORT)
                                .show();
                        userIdText.requestFocus();
                        return;
                    }

                    if (TextUtils.isEmpty(mPassword)) {
                        Toast.makeText(v.getContext(), "Enter password"
                                , Toast.LENGTH_SHORT)
                                .show();
                        pwdText.requestFocus();
                        return;
                    }
                    if (mPassword.length() < 6) {
                        Toast.makeText(v.getContext()
                                , "Enter password of at least 6 characters"
                                , Toast.LENGTH_SHORT)
                                .show();
                        pwdText.requestFocus();
                        return;
                    }
                    //NEED THIS
                    String url = buildSignInURL(v);
                    Log.i("URL", url);
                    logIn(url, v);
                }
            });
        } else {
            Intent i = new Intent(this, PubCrawler_Main.class);
            startActivity(i);
            finish();
        }
    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    //NEED THIS
    private String buildSignInURL(View v) {

        StringBuilder sb = new StringBuilder(SIGNIN_URL);

        try {
            Log.i("Login ID ", mloginID);
            sb.append("id='");
            sb.append(mloginID);
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


    public void logIn(String url, View v) {
        LoginTask task = new LoginTask();
        task.execute(new String[]{url.toString()});
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    //NEED this
    private class LoginTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

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






