package onipractice.mahmoud.com.fitnessapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import onipractice.mahmoud.com.fitnessapp.Utils.FirebaseMethods;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    // Variables
    private String email, firstname, lastname, password;

    // Widgets
    private EditText emailEditText, firstnameEditText, lastnameEditText, passwordEditText;
    private Button signUpButton;
    private TextView signInText;
    private Context context;

    //firebase
    private FirebaseAuth authentication;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseMethods firebaseMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initialize();
        setUpFirebaseAuthentication();
        setWidgets();
    }

    private void initialize() {

        context = SignUpActivity.this;
        firebaseMethods = new FirebaseMethods(context);

        emailEditText = (EditText) findViewById(R.id.emailET);
        firstnameEditText = (EditText) findViewById(R.id.firstnameET);
        lastnameEditText = (EditText) findViewById(R.id.lastnameET);
        passwordEditText = (EditText) findViewById(R.id.passwordET);
        signInText = (TextView) findViewById(R.id.signInText);
        signUpButton = (Button) findViewById(R.id.signUpBTN);
    }

    private void clearWidgets() {

        emailEditText.getText().clear();
        passwordEditText.getText().clear();
        firstnameEditText.getText().clear();
        lastnameEditText.getText().clear();
    }

    private void setWidgets() {

        signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                email = emailEditText.getText().toString();
                firstname = firstnameEditText.getText().toString();
                lastname = lastnameEditText.getText().toString();

                if (passwordEditText.getText().toString().length() < 8 && !isValidPassword(passwordEditText.getText().toString())) {
                    Toast.makeText(context,
                            "Password must be over 8 characters & contain atleast\n one lowercase, one uppercase, one number & a special character (@!#$)",
                            Toast.LENGTH_SHORT).show();

                    passwordEditText.getText().clear();

                } else {
                    password = passwordEditText.getText().toString();

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

    //---------- Firebase ----------//
    private void setUpFirebaseAuthentication() {
        authentication = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Log.d(TAG, "Connected");

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

























