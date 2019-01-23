package onipractice.mahmoud.com.fitnessapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import onipractice.mahmoud.com.fitnessapp.Trainer.TrainerPersonalDetailsActivity;

public class ChoosePreferenceActivity extends AppCompatActivity {

    private static final String TAG = "ChooseOption";

    // Widgets
    RelativeLayout personalTrainerRL;
    RelativeLayout traineeRL;

    // Variables
    String userID,pref;

    //Shared Preference
    SharedPreferences.Editor editor;
    SharedPreferences cacheData;
    private DatabaseReference rootRef;
    private DatabaseReference uidRef;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_position);

        context = ChoosePreferenceActivity.this;

        cacheData = getSharedPreferences("Position", ChoosePreferenceActivity.MODE_PRIVATE);
        editor = cacheData.edit();

        rootRef = FirebaseDatabase.getInstance().getReference();
        uidRef = rootRef.child("user_account_settings");

        setUpFirebaseAuth();
        initialize();
        setUpWidgets();

    }

    private void initialize(){

        personalTrainerRL = (RelativeLayout) findViewById(R.id.personalTrainerRL);
        traineeRL = (RelativeLayout) findViewById(R.id.traineeRL);
    }

    private void setUpWidgets(){

        personalTrainerRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(auth.getCurrentUser() != null){
                    userID = auth.getCurrentUser().getUid();
                }

                uidRef.child(userID).child("preference").setValue("Personal Trainer");
                checkPreference();
            }
        });

        traineeRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(auth.getCurrentUser() != null){
                    userID = auth.getCurrentUser().getUid();
                }

                uidRef.child(userID).child("preference").setValue("Trainee");
                checkPreference();
            }
        });
    }

    private void checkPreference(){

        if(auth.getCurrentUser() != null){
            userID = auth.getCurrentUser().getUid();
        }

        rootRef = FirebaseDatabase.getInstance().getReference();
        uidRef = rootRef.child("user_account_settings").child(userID).child("preference");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                pref = dataSnapshot.getValue(String.class);

                Toast.makeText(context, pref, Toast.LENGTH_SHORT).show();


                if(pref.equals("Personal Trainer")){

                    Intent intent = new Intent(context, TrainerPersonalDetailsActivity.class);
                    intent.putExtra("user_id", userID);
                    startActivity(intent);

                }else if (pref.equals("Trainee")){

                    Intent intent = new Intent(context, TraineePersonalDetailsActivity.class);
                    startActivity(intent);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);

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
