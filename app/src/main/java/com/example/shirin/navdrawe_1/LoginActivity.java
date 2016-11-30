package com.example.shirin.navdrawe_1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.lock.AuthenticationCallback;
import com.auth0.android.lock.Lock;
import com.auth0.android.lock.LockCallback;
import com.auth0.android.lock.utils.LockException;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserProfile;
import com.example.shirin.navdrawe_1.application.App;
import com.example.shirin.navdrawe_1.utils.CredentialsManager;

import java.util.HashMap;
import java.util.Map;

import static com.example.shirin.navdrawe_1.utils.CredentialsManager.saveCredentials;

public class LoginActivity extends AppCompatActivity {
    EditText email, pass;
    Button btn;
    String eMail;

    private Lock mLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //make a new Autho object with client id and domain
        Auth0 auth0 = new Auth0(getString(R.string.auth0_client_id), getString(R.string.auth0_domain));
        //request a refresh a token along with the id token
        //refersh token will keep the user logged in
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("scope", "openid offline_access");
        //create a lock with autho object and a lockcallback
        mLock = Lock.newBuilder(auth0, mCallback).build(this); //add parameters to the build
        if(CredentialsManager.getCredentials(this).getIdToken() == null){
            //auto login
            startActivity(mLock.newIntent(this));
            return;
        }
        //get the authetication client with auth0
        AuthenticationAPIClient apiClient = new AuthenticationAPIClient(auth0);
        //client's token info which make accounts user specific
        apiClient.tokenInfo(CredentialsManager.getCredentials(this).getIdToken())
                            .start(new BaseCallback<UserProfile, AuthenticationException>() {
                                //basec callback with user profile and autheticantion exceptin
                                @Override
                                public void onSuccess(UserProfile payload) {
                                    Log.d("Login Success", "Great");
                                    LoginActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(LoginActivity.this, "Auto Login",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    //logo successfull login go to MainActivity
                                    //need to send the id to retrive userspecific information
                                    Log.d("id", payload.getId());
                                    //get user email from the token which is payload
                                    Log.d("email", payload.getEmail());
                                    Intent intent = new Intent(getApplicationContext(),
                                            MainActivity.class);
                                    //sending user info to mainActivity as a bundle
                                    //of extras
                                    Bundle extras = new Bundle();
                                    extras.putString("TOKEN", payload.getId());
                                    extras.putString("EMAIL", payload.getEmail());
                                    intent.putExtras(extras);
                                    startActivity(intent);
                                    //close loginActivity
                                    finish();
                                }

                                @Override
                                public void onFailure(AuthenticationException error) {
                                    Log.d("Login Failed", "TryAgain");
                                    LoginActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(LoginActivity.this, "Session Expired", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                    //delete credentials and stay in the same login screen
                                    CredentialsManager.deleteCredentials(getApplicationContext());
                                    startActivity(mLock.newIntent(LoginActivity.this));
                                }
                            });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // destroy the lock
        mLock.onDestroy(this);
        mLock = null;
    }
    //callback. for auth0 we need onCreate, onDestroy, callback. we will fetch user profile
    //with the AutheticationAPI
    private final LockCallback mCallback = new AuthenticationCallback() {
        @Override
        public void onAuthentication(Credentials credentials) {
            Toast.makeText(LoginActivity.this, "Log In - Success", Toast.LENGTH_SHORT).show();
            //to request data saved in user's credentials
            App newapp = new App();
            Log.d("User's Credentials",String.valueOf(credentials));
            //save cresentials from login activity
            saveCredentials(LoginActivity.this,credentials);
            //  newapp.getInstance().setUserCredentials(credentials);
            //logo login go to whereever intent takes
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            Log.d("Success","MainActivity");
            finish();
        }

        @Override
        public void onCanceled() {
            Toast.makeText(LoginActivity.this, "Log In - Cancelled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(LockException error) {
            Toast.makeText(LoginActivity.this, "Log In - Error Occurred", Toast.LENGTH_SHORT).show();
        }
    };
}
