package onipractice.mahmoud.com.fitnessapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import onipractice.mahmoud.com.fitnessapp.Trainer.PersonalTrainerHomeActivity;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";
    Intent intent;

    // Widgets
    private Context context;
    private Button loginBTN;
    private EditText emailLogin, passwordLogin;
    private TextView CreateAccountText;
    private String email, password, userID;
    private String result;
    String userSignedUp;

    //Shared Preference
    SharedPreferences.Editor editor;
    SharedPreferences cacheData;

    // Firebase
    private DatabaseReference rootRef;
    private DatabaseReference uidRef;
    private DatabaseReference tokenReference;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        context = SignInActivity.this;

        tokenReference = FirebaseDatabase.getInstance().getReference().child("user_account_settings");

        // Shared Preferences
        cacheData = getSharedPreferences("Preferences", ChoosePreferenceActivity.MODE_PRIVATE);
        editor = cacheData.edit();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        if (user != null) {
            // User is signed in
           checkPreference();
        } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }

        initialize();
        setUpWidgets();
        setUpFirebaseAuth();
        signIn();


    }

    private void initialize(){
        emailLogin = (EditText) findViewById(R.id.email);
        passwordLogin = (EditText) findViewById(R.id.password);
        CreateAccountText = (TextView) findViewById(R.id.CreateAccountText);
        loginBTN = (Button) findViewById(R.id.loginBTN);
    }

    private void setUpWidgets(){

        CreateAccountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    public boolean isStringNull(String string)
    {
        if(string.equals(""))
        {
            return true;
        }else
        {
            return false;
        }
    }

    private void setPreference(){

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("user_account_settings");

        if(auth.getCurrentUser() != null){
            userID = auth.getCurrentUser().getUid();
        }

        uidRef.child(userID).child("prefChosen").setValue("true");
    }

    private void getReference(String id){

        rootRef = FirebaseDatabase.getInstance().getReference();
        uidRef = rootRef.child("user_account_settings").child(id).child("prefChosen");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                editor.putString("value", dataSnapshot.getValue(String.class));
                editor.apply();
//                Toast.makeText(context, cacheData.getString("value", ""), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);
    }

    private void checkPreference(){

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null){
            userID = auth.getCurrentUser().getUid();
        }

        rootRef = FirebaseDatabase.getInstance().getReference();
        uidRef = rootRef.child("user_account_settings").child(userID).child("preference");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                result = dataSnapshot.getValue(String.class);

                if(result.equals("Personal Trainer")){

                    Intent intent = new Intent(context, PersonalTrainerHomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                }else if (result.equals("Trainee")){

                    Intent intent = new Intent(context, TraineeHomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);

    }

    private void signIn(){

        auth = FirebaseAuth.getInstance();

        loginBTN.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                email = emailLogin.getText().toString();
                password = passwordLogin.getText().toString();

                if(isStringNull(email) && isStringNull(password))
                {
                    Toast.makeText(context, "You must fill out all the fields", Toast.LENGTH_SHORT).show();

                }else {

                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    FirebaseUser user = auth.getCurrentUser();

                                    userID = user.getUid();

                                    if (!task.isSuccessful()) {

                                        Log.w(TAG, "signInWithEmail:failed", task.getException());

                                        Toast.makeText(SignInActivity.this, getString(R.string.auth_failed),
                                                Toast.LENGTH_SHORT).show();
                                    }else {

                                        if(user.isEmailVerified()){
                                            Log.d(TAG, "onComplete: success. email is verified.");

                                            String user_id = user.getUid();
                                            String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                            tokenReference.child(user_id).child("token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    getReference(userID);
                                                    if(cacheData.getString("value", "").equals("false")){

                                                        Intent intent = new Intent(SignInActivity.this, ChoosePreferenceActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(intent);
                                                        finish();

                                                        setPreference();

                                                    }else {

                                                        checkPreference();
                                                    }
                                                }
                                            });

                                        }else{
                                            Toast.makeText(context, "Email is not verified \n check your email inbox.", Toast.LENGTH_SHORT).show();
                                            auth.signOut();
                                        }
                                    }

                                }
                            });
                }

            }
        });

    }

    //---------- Firebase ----------//

    private void setUpFirebaseAuth()
    {
        auth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null)
                {
                    Log.d(TAG, "Success");

                }else {
                    Log.d(TAG, "signed out");
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null)
        {
            auth.removeAuthStateListener(authStateListener);
        }
    }
}
































