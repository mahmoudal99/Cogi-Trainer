package onipractice.mahmoud.com.fitnessapp.Profile;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import onipractice.mahmoud.com.fitnessapp.R;
import onipractice.mahmoud.com.fitnessapp.SignInActivity;
import onipractice.mahmoud.com.fitnessapp.Utils.FirebaseMethods;

public class ChangePasswordActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    EditText currentPassEt, newPassEt, repeatPassEt;;
    String currentPassword, newPassword, repeatPassword, user;
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
        user = getIntent().getStringExtra("user");

        setUpFirebaseAuth();
        initialize();
        setWidgets();
    }

    private void initialize(){

        currentPassEt = (EditText) findViewById(R.id.currentPassword);
        newPassEt = (EditText) findViewById(R.id.newPassword);
        repeatPassEt = (EditText) findViewById(R.id.repeatPassword);
        backArrowIv = (ImageView) findViewById(R.id.backArrow);
        changePassBtn = (Button) findViewById(R.id.changePassBtn);
    }

    private void setWidgets(){

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
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }

    //---------- Changing Password ----------//

    private void getPassword(){

        currentPassword = currentPassEt.getText().toString();
        newPassword = newPassEt.getText().toString();
        repeatPassword = repeatPassEt.getText().toString();

        if(!newPassword.equals(repeatPassword)){

            Toast.makeText(context, "Passwords Dont Match", Toast.LENGTH_SHORT).show();

        }

        firebaseMethods.changePassword(currentPassword, newPassword);
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