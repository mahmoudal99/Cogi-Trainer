package onipractice.mahmoud.com.fitnessapp;

import android.content.Context;
import android.content.Intent;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import onipractice.mahmoud.com.fitnessapp.Models.User;
import onipractice.mahmoud.com.fitnessapp.Models.UserDetails;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    String email, username, firstname, lastname, password;
    EditText emailET, usernameET, firstnameET, lastnameET, passwordET;
    Button signUpBTN;
    TextView signInText;
    Context context;
    String userID;

    //firebase
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        context = SignUpActivity.this;

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();

        setWidgets();
        setUpFirebaseAuth();
        init();
    }

    private void init(){
        email = emailET.getText().toString();
        firstname = firstnameET.getText().toString();
        lastname = lastnameET.getText().toString();
        username = usernameET.getText().toString();
        password = passwordET.getText().toString();

        signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        signUpBTN.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                email = emailET.getText().toString();
                username = usernameET.getText().toString();
                password = passwordET.getText().toString();
                firstname = firstnameET.getText().toString();
                lastname = lastnameET.getText().toString();

                email = emailET.getText().toString();
                password = passwordET.getText().toString();

                registerNewEmail(email, password);

            }

        });

    }

    private void addUser(String id, String username,String firstname,String lastname){

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("user_account_settings");

        if(auth.getCurrentUser() != null){
            userID = auth.getCurrentUser().getUid();
        }

        UserDetails user = new UserDetails(username, firstname, lastname, "false", "Null");

        uidRef.child(id).setValue(user);
    }

    private void setWidgets(){
        emailET = (EditText) findViewById(R.id.emailET);
        firstnameET = (EditText) findViewById(R.id.firstnameET);
        lastnameET = (EditText) findViewById(R.id.lastnameET);
        usernameET = (EditText) findViewById(R.id.usernameET);
        passwordET = (EditText) findViewById(R.id.passwordET);

        signInText = (TextView) findViewById(R.id.signInText);
        signUpBTN = (Button) findViewById(R.id.signUpBTN);
    }

    private boolean checkInputs(String email, String username, String firstname, String lastname, String password){
        Log.d(TAG, "checkInputs: checking inputs for null values.");
        if(email.equals("") || username.equals("") || password.equals("") || firstname.equals("") || lastname.equals("")){

            Toast.makeText(context, "All fields must be filled out.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

        /*
    ------------------------------------------Firebase----------------------------------------------------
     */

    public void registerNewEmail(String email, String password){

        auth.createUserWithEmailAndPassword(email, password)

                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override

                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }

                        else if(task.isSuccessful()){
                            //send verificaton email

                            FirebaseUser user = auth.getCurrentUser();
                            String id = user.getUid();

                            Toast.makeText(SignUpActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            addUser(id, username, firstname, lastname);
                            sendVerificationEmail();
                        }
                    }

                });
    }

    public void sendVerificationEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override

                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                            }else{
                                Toast.makeText(SignUpActivity.this, "couldn't send verification email.", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
        }
    }

    private void setUpFirebaseAuth()
    {
        auth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null)
                {
                    Log.d(TAG, "Connected");

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

























