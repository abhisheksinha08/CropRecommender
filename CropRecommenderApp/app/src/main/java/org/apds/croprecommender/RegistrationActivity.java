package org.apds.croprecommender;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apds.croprecommender.models.AuthUser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;




public class RegistrationActivity extends AppCompatActivity {

    private UserRegistrationTask mAuthTask = null;

    private EditText mUserIdView;
    private EditText mPasswordView;
    private EditText mNameView;
    private EditText mEmailView;
    private EditText mPhoneView;
    private EditText mRePasswordView;

    private View mProgressView;
    private View mRegFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRegFormView = findViewById(R.id.regform);
        mProgressView = findViewById(R.id.reg_progress);


        mUserIdView = (EditText) findViewById(R.id.userId);
        mPasswordView = (EditText) findViewById(R.id.password);
        mRePasswordView = (EditText) findViewById(R.id.repassword);
        mNameView = (EditText) findViewById(R.id.name);
        mEmailView = (EditText) findViewById(R.id.email);
        mPhoneView = (EditText) findViewById(R.id.phone);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }


    public void onRegisterClick(View view){
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUserIdView.setError(null);
        mPasswordView.setError(null);
        mRePasswordView.setError(null);
        mEmailView.setError(null);
        mNameView.setError(null);
        mPhoneView.setError(null);

        // Store values at the time of the login attempt.
        String userId = mUserIdView.getText().toString();
        String password = mPasswordView.getText().toString();
        String repassword = mRePasswordView.getText().toString();
        String name = mNameView.getText().toString();
        String email =mEmailView.getText().toString();
        String phone = mPhoneView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

// Check for a valid password, if the user entered one.
        if (!password.equals(repassword)) {
            mRePasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mRePasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(userId)) {
            mUserIdView.setError(getString(R.string.error_field_required));
            focusView = mUserIdView;
            cancel = true;
        }

        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(phone)) {
            mPhoneView.setError(getString(R.string.error_field_required));
            focusView = mPhoneView;
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
            mAuthTask = new UserRegistrationTask(userId, password, name, email, phone);
            mAuthTask.execute((Void) null);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void onRegistrationSuccess()
    {
        Toast.makeText(this, "Registration complete!", Toast.LENGTH_LONG).show();
        showAlert();
    }

    public void showAlert()
    {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Registration")
                .setMessage("Registration complete. Please check your email for verification.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                        startActivity(intent);
                        RegistrationActivity.this.finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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

            mRegFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mRegFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegistrationTask extends AsyncTask<Void, Void, AuthUser> {

        private final String mUserId;
        private final String mPassword;
        private final String mName;
        private final String mEmail;
        private final String mPhoneNumber;

        final private static String AUTH_AUTH_SERVICE_API = "http://projectapds.ecompliancesuite.com/Api/Authentication/Register?";

        UserRegistrationTask(String userId, String password, String name, String email, String phoneNumber) {
            mUserId = userId;
            mPassword = password;
            mName = name;
            mEmail = email;
            mPhoneNumber = phoneNumber;
        }

        @Override
        protected AuthUser doInBackground(Void... params) {
            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return (new AuthUser());
            }

            AuthUser authUser = Registration();

            return authUser;
        }

        @Override
        protected void onPostExecute(final AuthUser authuser) {
            mAuthTask = null;
            showProgress(false);

            if (authuser.Message.contains("1,")) {
                onRegistrationSuccess();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }


        private AuthUser Registration() {

            AuthUser senUser = new AuthUser();
            senUser.Message = "2,Error connecting!";

            try {

                URL url = new URL(AUTH_AUTH_SERVICE_API + "userId="
                        + mUserId + "&password=" + mPassword + "&userName=" + mName+ "&emailId=" + mEmail + "&phoneNumber=" + mPhoneNumber);
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
