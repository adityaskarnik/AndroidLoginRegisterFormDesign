package com.aditya.adityapc.androidloginregisterformdesign;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * Created by Aditya PC on 5/31/2016.
 */
public class ActivityRegistration extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    private AutoCompleteTextView registerActivityEmail;
    EditText registerFirstName, registerLastName, registerContact;
    ShowHidePasswordEditText registerActivityPassword, registerConfirmPassword;
    TextView loginLink;
    private View registerProgressView;
    private View registerFormView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerFirstName = (EditText) findViewById(R.id.registerFirstName);
        registerLastName = (EditText) findViewById(R.id.registerLastName);
        registerConfirmPassword = (ShowHidePasswordEditText) findViewById(R.id.registerConfirmPassword);
        registerContact = (EditText) findViewById(R.id.registerContact);

        registerActivityEmail = (AutoCompleteTextView) findViewById(R.id.registerEmail);
        populateAutoComplete();

        loginLink = (TextView)findViewById(R.id.link_login);
        registerActivityPassword = (ShowHidePasswordEditText) findViewById(R.id.registerPassword);
        /*registerActivityPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.registerEmail || id == EditorInfo.IME_ACTION_GO) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });*/

        Button registrationButton = (Button) findViewById(R.id.register_button);
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        registerFormView = findViewById(R.id.register_form);
        registerProgressView = findViewById(R.id.register_progress);

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(),ActivityLogin.class);
                startActivity(intent);
            }
        });
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

            registerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            registerFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    registerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            registerProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            registerProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    registerProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            registerProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            registerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(registerActivityEmail, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        List<String> emails = new ArrayList<>();
        data.moveToFirst();
        while (!data.isAfterLast()) {
            emails.add(data.getString(ProfileQuery.ADDRESS));
            data.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(ActivityRegistration.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        registerActivityEmail.setAdapter(adapter);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    private void attemptLogin() {

        //Reset errors
        registerFirstName.setError(null);
        registerLastName.setError(null);
        registerActivityEmail.setError(null);
        registerActivityPassword.setError(null);
        registerConfirmPassword.setError(null);
        registerContact.setError(null);

        //Store values at the time of registration
        String registrationfname = registerFirstName.getText().toString();
        String registrationlname = registerLastName.getText().toString();
        String registrationemail = registerActivityEmail.getText().toString();
        String registrationpassword = registerActivityPassword.getText().toString();
        String registrationconfirmpassword = registerConfirmPassword.getText().toString();
        String registrationcontact = registerContact.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check if the user entered first name.
        if (TextUtils.isEmpty(registrationfname)) {
            registerFirstName.setError(getString(R.string.error_field_required));
            focusView = registerFirstName;
            cancel = true;
        }

        // Check if the user entered last name.
        if (TextUtils.isEmpty(registrationlname)) {
            registerLastName.setError(getString(R.string.error_field_required));
            focusView = registerLastName;
            cancel = true;
        }

        // Check for a valid email, if the user entered one.
        if (TextUtils.isEmpty(registrationemail)) {
            registerActivityEmail.setError(getString(R.string.error_field_required));
            focusView = registerActivityEmail;
            cancel = true;
        } else if (!isEmailValid(registrationemail)) {
            registerActivityEmail.setError(getString(R.string.error_invalid_email));
            focusView = registerActivityEmail;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(registrationpassword)) {
            registerActivityPassword.setError(getString(R.string.error_field_required));
            focusView = registerActivityPassword;
            cancel = true;
        } else if(!isPasswordValid(registrationpassword)) {
            registerActivityPassword.setError(getString(R.string.error_invalid_password));
            focusView = registerActivityPassword;
            cancel = true;
        }

        // Check for a valid confirm password, if the user entered one.
        if (TextUtils.isEmpty(registrationconfirmpassword)) {
            registerConfirmPassword.setError(getString(R.string.error_field_required));
            focusView = registerConfirmPassword;
            cancel = true;
        } else if(!isConfirmPasswordValid(registrationconfirmpassword)) {
            registerConfirmPassword.setError(getString(R.string.error_incorrect_confirm_password));
            focusView = registerConfirmPassword;
            cancel = true;
        }

        //check if password and confirm password are the same
        if(!registrationpassword.equals(registrationconfirmpassword)) {
            registerConfirmPassword.setError(getString(R.string.error_incorrect_confirm_password));
            focusView = registerConfirmPassword;
            cancel = true;
        }

        // check for valid phone number, if the user entered one.
        if(TextUtils.isEmpty(registrationcontact)) {
            registerContact.setError(getString(R.string.error_field_required));
            focusView = registerContact;
            cancel = true;
        } else if(!isContactValid(registrationcontact)) {
            registerContact.setError(getString(R.string.error_invalid_contact));
            focusView = registerContact;
            cancel = true;
        }




        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            showProgress(true);
        }


    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return isValidEmail(email);
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 7;
    }

    private boolean isConfirmPasswordValid(String confirmpassword) {
        return confirmpassword.length() > 7;
    }

    private boolean isContactValid(String contact) {
        return contact.length() == 10;
    }
}
