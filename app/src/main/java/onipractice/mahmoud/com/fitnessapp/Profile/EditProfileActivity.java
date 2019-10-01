package onipractice.mahmoud.com.fitnessapp.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import onipractice.mahmoud.com.fitnessapp.R;
import onipractice.mahmoud.com.fitnessapp.Utils.Dialogs;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "ProfileActivity";

    // Widgets
    TextView emailTv, nameTv, surnameTv, genderTv, weightTv, heightTv, weightLabel, heightLabel, genderLabel;
    ImageView backArrow;
    Dialogs dialogs;

    // Variables
    String name, surname, userId, weight, gender, height, newGender, userType;
    Context context;

    //firebase
    private FirebaseAuth authentication;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference rootRef;
    private DatabaseReference uidRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        context = EditProfileActivity.this;
        userType = getIntent().getStringExtra("user");

        initialize();
        setUpWidgets();
        setUpFirebaseAuthentication();

        if(userType.equals("trainer")){
            weightLabel.setVisibility(View.INVISIBLE);
            genderLabel.setVisibility(View.INVISIBLE);
            heightLabel.setVisibility(View.INVISIBLE);
        }
    }

    private void initialize(){

        emailTv = findViewById(R.id.emailTv);
        nameTv =  findViewById(R.id.nameTv);
        surnameTv = findViewById(R.id.surnameTv);
        backArrow = (ImageView) findViewById(R.id.backArrow);
        weightTv = findViewById(R.id.weightTv);
        genderTv = findViewById(R.id.genderTv);
        heightTv = findViewById(R.id.heightTv);
        weightLabel = findViewById(R.id.weight);
        heightLabel = findViewById(R.id.height);
        genderLabel = findViewById(R.id.gender);
    }

    private void setUpWidgets(){

        emailTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogs = new Dialogs(context);
                dialogs.emailDialog();
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("user", userType);
                startActivity(intent);
            }
        });
    }

    private void setUserSettings(String name, String surname){
        nameTv.setText(name);
        surnameTv.setText(surname);
        emailTv.setText(authentication.getCurrentUser().getEmail());
    }

    private void setUserPersonalDetails(String gender, String weight, String height){
        genderTv.setText(gender);
        weightTv.setText(weight);
        heightTv.setText(height);
    }


    private void getUserSettings(String id){
        rootRef = FirebaseDatabase.getInstance().getReference();
        uidRef = rootRef.child("user_account_settings").child(id);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    name = dataSnapshot.child("firstname").getValue(String.class);
                    surname = dataSnapshot.child("lastname").getValue(String.class);
                    setUserSettings(name, surname);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);
    }

    private void getUserPersonalDetails(String id){

        rootRef = FirebaseDatabase.getInstance().getReference();
        uidRef = rootRef.child("personal_details").child(id);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    gender = dataSnapshot.child("gender").getValue(String.class);
                    height = dataSnapshot.child("height").getValue(String.class);
                    weight = dataSnapshot.child("weight").getValue(String.class);

                    setUserPersonalDetails(gender, weight, height);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);
    }

    //---------- Firebase ----------//

    private void setUpFirebaseAuthentication()
    {
        authentication = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null)
                {
                    Log.d(TAG, "Connected");
                    userId = user.getUid();
                    getUserSettings(userId);
                    getUserPersonalDetails(userId);
                }else {
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
        if (authStateListener != null)
        {
            authentication.removeAuthStateListener(authStateListener);
        }
    }

    //---------- On Click Listeners ----------//
    @Override
    public void onClick(View v) {

        dialogs = new Dialogs(context);

        switch (v.getId()) {
            case R.id.genderTv:
                dialogs.genderDialog();
                break;
            case R.id.heightTv:
                dialogs.heightDialog();
                break;
            case R.id.weightTv:
                dialogs.weightDialog();
                break;
            default:
                break;
        }
    }
}

















