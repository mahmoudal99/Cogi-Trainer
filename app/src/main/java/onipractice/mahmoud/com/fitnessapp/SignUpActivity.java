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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import onipractice.mahmoud.com.fitnessapp.Models.UserDetails;
import onipractice.mahmoud.com.fitnessapp.Utils.FirebaseMethods;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    String email, firstname, lastname, password;
    EditText emailET, firstnameET, lastnameET, passwordET;
    Button signUpBTN;
    TextView signInText;
    Context context;
    FirebaseMethods firebaseMethods;
    String userSignedUp;

    //firebase
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        context = SignUpActivity.this;
        firebaseMethods = new FirebaseMethods(context);

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();

        initialize();
        setUpFirebaseAuth();
        setWidgets();
    }

    private void initialize(){

        emailET = (EditText) findViewById(R.id.emailET);
        firstnameET = (EditText) findViewById(R.id.firstnameET);
        lastnameET = (EditText) findViewById(R.id.lastnameET);
        passwordET = (EditText) findViewById(R.id.passwordET);
        signInText = (TextView) findViewById(R.id.signInText);
        signUpBTN = (Button) findViewById(R.id.signUpBTN);
    }

    private void clearWidgets(){

        emailET.getText().clear();
        passwordET.getText().clear();
        firstnameET.getText().clear();
        lastnameET.getText().clear();
    }

    private void setWidgets(){

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
                firstname = firstnameET.getText().toString();
                lastname = lastnameET.getText().toString();

                email = emailET.getText().toString();

                if(passwordET.getText().toString().length()<8 &&!isValidPassword(passwordET.getText().toString())){

                    Toast.makeText(context,
                            "Password must be over 8 characters & contain atleast\n one lowercase, one uppercase, one number & a special character (@!#$)",
                            Toast.LENGTH_SHORT).show();

                    passwordET.getText().clear();

                }else{

                    password = passwordET.getText().toString();

                    firebaseMethods.registerNewEmail(email, password, firstname, lastname);
                    Toast.makeText(context, "Check your inbox", Toast.LENGTH_SHORT).show();

                    clearWidgets();

                    Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }


            }

        });

    }

    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "[a-zA-Z0-9\\!\\@\\#\\$]{8,24}";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

        /*
    ------------------------------------------Firebase----------------------------------------------------
     */

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

























