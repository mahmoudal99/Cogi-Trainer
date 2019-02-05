package onipractice.mahmoud.com.fitnessapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import onipractice.mahmoud.com.fitnessapp.Trainer.PersonalTrainerHomeActivity;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";
    Intent intent;

    // Widgets
    private Context context;
    private Button loginButton;
    private EditText emailEditText, passwordEditText;
    private TextView createAccountTextView;

    //Variables
    private String result, user_id, deviceToken, email, password, userID;

    //Shared Preference
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreference;

    // Firebase
    private DatabaseReference rootReference;
    private DatabaseReference uidReference;
    private DatabaseReference tokenReference;
    private FirebaseAuth authentication;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        context = SignInActivity.this;

        initializeVariables();
        checkUserSignedIn();
        setUpWidgets();
        setUpFirebaseAuthentication();
    }

    private void initializeVariables() {
        tokenReference = FirebaseDatabase.getInstance().getReference().child("user_account_settings");
        // Shared Preferences
        sharedPreference = getSharedPreferences("Preferences", ChooseUserTypeActivity.MODE_PRIVATE);
        editor = sharedPreference.edit();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        emailEditText = (EditText) findViewById(R.id.email);
        passwordEditText = (EditText) findViewById(R.id.password);
        createAccountTextView = (TextView) findViewById(R.id.CreateAccountText);
        loginButton = (Button) findViewById(R.id.loginBTN);
    }

    private void setUpWidgets() {
        createAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    private void checkUserSignedIn() {
        if (currentUser != null) {
            checkUserType();
        } else {
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }
    }

    public boolean isStringNull(String string) {
        if (string.equals("")) {
            return true;
        } else {
            return false;
        }
    }

    private void setUserTypePicked() {
        rootReference = FirebaseDatabase.getInstance().getReference();
        uidReference = rootReference.child("user_account_settings");

        if (authentication.getCurrentUser() != null) {
            userID = authentication.getCurrentUser().getUid();
        }
        uidReference.child(userID).child("prefChosen").setValue("true");
    }

    private void getUserType(String id) {
        rootReference = FirebaseDatabase.getInstance().getReference();
        uidReference = rootReference.child("user_account_settings").child(id).child("prefChosen");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                editor.putString("value", dataSnapshot.getValue(String.class));
                editor.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        uidReference.addListenerForSingleValueEvent(valueEventListener);
    }

    private void checkUserType() {
        authentication = FirebaseAuth.getInstance();

        if (authentication.getCurrentUser() != null) {
            userID = authentication.getCurrentUser().getUid();
        }

        rootReference = FirebaseDatabase.getInstance().getReference();
        uidReference = rootReference.child("user_account_settings").child(userID).child("preference");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                result = dataSnapshot.getValue(String.class);
                if (result.equals("Personal Trainer")) {
                    Intent intent = new Intent(context, PersonalTrainerHomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else if (result.equals("Trainee")) {
                    Intent intent = new Intent(context, TraineeHomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        uidReference.addListenerForSingleValueEvent(valueEventListener);
    }

    private void signIn() {

        authentication = FirebaseAuth.getInstance();

        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();

        if (isStringNull(email) && isStringNull(password)) {
            Toast.makeText(context, "You must fill out all the fields", Toast.LENGTH_SHORT).show();
        } else {

            authentication.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            currentUser = authentication.getCurrentUser();
                            userID = currentUser.getUid();

                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                Toast.makeText(SignInActivity.this, getString(R.string.auth_failed),
                                        Toast.LENGTH_SHORT).show();
                            } else {

                                if (currentUser.isEmailVerified()) {

                                    Log.d(TAG, "onComplete: success. email is verified.");
                                    user_id = currentUser.getUid();
                                    deviceToken = FirebaseInstanceId.getInstance().getToken();
                                    tokenReference.child(user_id).child("token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            getUserType(userID);
                                            if (sharedPreference.getString("value", "").equals("false")) {
                                                intent = new Intent(SignInActivity.this, ChooseUserTypeActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                                finish();
                                                setUserTypePicked();
                                            } else {
                                                checkUserType();
                                            }
                                        }
                                    });

                                } else {

                                    Toast.makeText(context, "Email is not verified \n check your email inbox.", Toast.LENGTH_SHORT).show();
                                    authentication.signOut();

                                }

                            }

                        }
                    });

        }
    }

    //---------- Firebase ----------//
    private void setUpFirebaseAuthentication() {
        authentication = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    Log.d(TAG, "Success");
                } else {
                    Log.d(TAG, "signed out");
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        authentication.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            authentication.removeAuthStateListener(authStateListener);
        }
    }
}































