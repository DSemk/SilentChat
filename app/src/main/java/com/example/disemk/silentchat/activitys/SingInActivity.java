package com.example.disemk.silentchat.activitys;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.disemk.silentchat.R;
import com.example.disemk.silentchat.engine.BaseDataMaster;
import com.example.disemk.silentchat.engine.SingletonCM;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;

/**
 * Created by disemk on 11.01.17.
 */

public class SingInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "SingInActivity";
    private static final String APP_PREFERENCES = "silent_pref";
    private static final String APP_PREFERENCES_BACKGROUND_ID = "backgroundId";


    private SharedPreferences mSharedPreferences;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private BaseDataMaster dataMaster;

    private SignInButton mSignInButton;
    private CheckBox checkInfo;


    /**
     * @metod - loadSharPrefData() - load user settings chenges;
     * @metod initialize() - init all items & all ;
     * @metod checkUserAuth() - check user is already singIn or no. If singIn, go to RoomsFragment;
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singin);

        // START config_singin
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(AppIndex.API).build();

        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mFirebaseUser = firebaseAuth.getCurrentUser();
                if (mFirebaseUser != null) {
                    pushFBaseUserInfo();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + mFirebaseUser.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }

        };
        initialize();
        loadSharPrefData();
        checkUserAuth();
    }

    // Check you are a new user,or not.
    private void checkUserAuth() {
        if (mFirebaseUser != null) {
            pushFBaseUserInfo();
            startActivity(new Intent(SingInActivity.this, MainActivity.class).putExtra("status", false));
            finish();
        }
    }


    private void initialize() {
        checkInfo = (CheckBox) findViewById(R.id.as_checkBox);
        mSignInButton = (SignInButton) findViewById(R.id.singIn_btn);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInfo.isChecked()) {
                    authorize();
                } else {
                    Toast.makeText(SingInActivity.this, "Сначала приймите правила лиц. соглашения", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();


    }

    private void loadSharPrefData() {
        try {
            mSharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
            // load favorite rooms from db
        } catch (Resources.NotFoundException e) {
            SingletonCM.getInstance().setBackgroundID(R.drawable.back_4);
        }

        if (mSharedPreferences.contains(APP_PREFERENCES_BACKGROUND_ID)) {
            SingletonCM.getInstance().setBackgroundID(mSharedPreferences.getInt(APP_PREFERENCES_BACKGROUND_ID, 0));
            Log.d(TAG, "Data is load");
        } else Log.d(TAG, "Data load faild");

    }

    private void authorize() {
        Intent authorizeIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(authorizeIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult signInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (signInResult.isSuccess()) {
                GoogleSignInAccount signInAccount = signInResult.getSignInAccount();
                firebaseAuthWithGoogle(signInAccount);
            } else {
                Toast.makeText(getApplicationContext(), "SingIn Faild!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d(TAG, " onActivityResult : warm requestCode");
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SingInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {

                            // status == true,use in first run app on devise
                            showAlrtDialog();
                        }

                    }
                });
    }

    private void showAlrtDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(SingInActivity.this);
        View view = layoutInflater.inflate(R.layout.alert_dialog_singin, null);


        AlertDialog.Builder builder = new AlertDialog
                .Builder(new ContextThemeWrapper(SingInActivity.this, R.style.myDialog));

        builder.setView(view);
        final EditText editName = (EditText) view.findViewById(R.id.ad_addNewRoom_et);

        builder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Предупреждение!");
        alertDialog.show();

    }

    private void pushFBaseUserInfo() {
        if (mFirebaseAuth != null && mFirebaseUser != null) {
            SingletonCM.getInstance().
                    setUserName(mFirebaseAuth.getCurrentUser().getDisplayName());
            SingletonCM.getInstance().setUserIcon(mFirebaseUser.getPhotoUrl().toString());
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Services Error", Toast.LENGTH_SHORT).show();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("SingIn Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient.connect();
        AppIndex.AppIndexApi.start(mGoogleApiClient, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(mGoogleApiClient, getIndexApiAction());
        mGoogleApiClient.disconnect();
    }

}
