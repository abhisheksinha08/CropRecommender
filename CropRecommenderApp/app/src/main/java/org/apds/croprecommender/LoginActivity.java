package org.apds.croprecommender;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apds.croprecommender.models.AuthUser;
import org.apds.croprecommender.utils.GpsTracker;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mUserIdView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private Location location;

    private GpsTracker tracker;

    private static final int REQUEST_PERMISSION_LOCATION = 255;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tracker = new GpsTracker(this);
        // Set up the login form.
        mUserIdView = (EditText) findViewById(R.id.userId);


        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_LOCATION);
        } else {
            location=tracker.getLocation();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                location=tracker.getLocation();
            }
        }
    }


    public void OnRegisterClick(View v)
    {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
        this.finish();
    }


    public void OnSignIn(View view) {
        attemptLogin();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_LOCATION);
        } else {
            location=tracker.getLocation();
        }

        // Reset errors.
        mUserIdView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String userId = mUserIdView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(userId)) {
            mUserIdView.setError(getString(R.string.error_field_required));
            focusView = mUserIdView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(userId, password, location, "");
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void onLoginSuccess()
    {
        Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show();
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, AuthUser> {

        private final String mUserId;
        private final String mPassword;
        private final Location mLocation;
        private final String mOtherInfo;

        final private static String AUTH_AUTH_SERVICE_API = "http://projectapds.ecompliancesuite.com/Api/Authentication/Login?";

        UserLoginTask(String userId, String password, Location location, String otherInfo) {
            mUserId = userId;
            mPassword = password;
            mLocation = location;
            mOtherInfo = otherInfo;
        }

        @Override
        protected AuthUser doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return (new AuthUser());
            }

            AuthUser authUser = Authenticate();

            return authUser;
        }

        @Override
        protected void onPostExecute(final AuthUser authuser) {
            mAuthTask = null;
            showProgress(false);

            if (authuser.Message.contains("1,")) {
                onLoginSuccess();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }


        private AuthUser Authenticate() {

            AuthUser senUser = new AuthUser();
            senUser.Message = "2,Error connecting!";

            String lat = "";
            String lon = "";
            try
            {
                lat = String.valueOf(mLocation.getLatitude());
                lon = String.valueOf(mLocation.getLongitude());
            }
            catch (Exception e1){}
            try {

                URL url = new URL(AUTH_AUTH_SERVICE_API + "userId="
                        + mUserId + "&password=" + mPassword + "&latitude=" + lat+ "&longitude=" + lon + "&otherInfo=" + mOtherInfo);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();

                int contentLength = (int) connection.getContentLength();

                if (contentLength != 0) {
                    char[] buffer = new char[contentLength];
                    InputStream stream = connection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(stream);

                    int hasRead = 0;
                    while (hasRead < contentLength)
                        hasRead += reader.read(buffer, hasRead, contentLength
                                - hasRead);
                    stream.close();

                    // JsonReader reader = new JsonReader(new
                    // InputStreamReader(stream));
                    Gson gson = new Gson();
                    senUser = gson.fromJson(new String(buffer), AuthUser.class);
                }
            } catch (Exception e) {
                // any exception show the error layout
                e.printStackTrace();
            }
            return senUser;
        }
    }
}

