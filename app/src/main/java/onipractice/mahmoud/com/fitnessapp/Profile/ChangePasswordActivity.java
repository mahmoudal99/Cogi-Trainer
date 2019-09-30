package onipractice.mahmoud.com.fitnessapp.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import onipractice.mahmoud.com.fitnessapp.R;
import onipractice.mahmoud.com.fitnessapp.Utils.FirebaseMethods;

public class ChangePasswordActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    EditText currentPassEt, newPassEt, repeatPassEt;
    String currentPassword, newPassword, repeatPassword, userType;
    Button changePassBtn;
    ImageView backArrowIv;

    Context context;

    //firebase
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    FirebaseMethods firebaseMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        context = ChangePasswordActivity.this;
        userType = getIntent().getStringExtra("user");

        setUpFirebaseAuthentication();
        initialize();
        setWidgets();
    }

    private void initialize() {

        currentPassEt = findViewById(R.id.currentPassword);
        newPassEt = findViewById(R.id.newPassword);
        repeatPassEt = findViewById(R.id.repeatPassword);
        backArrowIv = findViewById(R.id.backArrow);
        changePassBtn = findViewById(R.id.changePassBtn);
    }

    private void setWidgets() {

        changePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPassword();
            }
        });

        backArrowIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("user", userType);
                startActivity(intent);
            }
        });
    }

    //---------- Changing Password ----------//

    private void getPassword() {

        currentPassword = currentPassEt.getText().toString();
        newPassword = newPassEt.getText().toString();
        repeatPassword = repeatPassEt.getText().toString();

        if (!newPassword.equals(repeatPassword)) {

            Toast.makeText(context, "Passwords Dont Match", Toast.LENGTH_SHORT).show();

        }

        firebaseMethods.changePassword(currentPassword, newPassword);
    }


    //---------- Firebase ----------//

    private void setUpFirebaseAuthentication() {
        auth = FirebaseAuth.getInstance();

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
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {

        super.onStop();
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }
    }
}
