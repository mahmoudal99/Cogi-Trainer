package onipractice.mahmoud.com.fitnessapp.Trainer;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import onipractice.mahmoud.com.fitnessapp.Messaging.ChatActivity;
import onipractice.mahmoud.com.fitnessapp.R;
import onipractice.mahmoud.com.fitnessapp.TraineeHomeActivity;

public class MyTrainerActivity extends AppCompatActivity {

    private static final String TAG = "MyTrainerActivity";

    // Widgets
    ImageView chat_img, back_arrow;
    TextView nameTv, lastnameTv, availabilityTv, specialityTv;

    Button sendClientRequestBtn, deleteClientRequestBtn;
    String name, surname, id, availability, speciality, currentState;

    //firebase
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    DatabaseReference rootRef;
    DatabaseReference uidRef;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trainer);

        user = FirebaseAuth.getInstance().getCurrentUser();

        Toast.makeText(MyTrainerActivity.this, String.valueOf(user.getUid()), Toast.LENGTH_SHORT).show();

        id = getIntent().getStringExtra("user_id");
        initialize();
        setUpWidgets();
        setUpFirebaseAuth();
        setUserInfo(id);


    }

    private void initialize(){

        nameTv = (TextView) findViewById(R.id.nameTv);
        lastnameTv = (TextView) findViewById(R.id.surnameTv);
        chat_img = (ImageView) findViewById(R.id.chat_img);
        back_arrow = (ImageView) findViewById(R.id.backArrow);
        specialityTv = (TextView) findViewById(R.id.specialityTv);
        availabilityTv = (TextView) findViewById(R.id.availabilityTv);
        deleteClientRequestBtn = (Button) findViewById(R.id.deleteFriendRequest);
    }

    private void setUpWidgets(){

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyTrainerActivity.this, TraineeHomeActivity.class);
                startActivity(intent);
            }
        });

        chat_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyTrainerActivity.this , ChatActivity.class);
                intent.putExtra("user_id", id);
                intent.putExtra("user", "trainee");
                startActivity(intent);
            }
        });

        deleteClientRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRequestState();
            }
        });

    }

    private void deleteRequestState(){

        user = FirebaseAuth.getInstance().getCurrentUser();
        Toast.makeText(MyTrainerActivity.this, String.valueOf(id), Toast.LENGTH_SHORT).show();

        Map unfriendMap = new HashMap();
        unfriendMap.put("Friends/" + user.getUid() + "/" + "trainerId", null);

        rootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                if(databaseError == null){

                    currentState = "not_friends";

                    deleteClientRequestBtn.setVisibility(View.INVISIBLE);
                    deleteClientRequestBtn.setEnabled(false);
                    Intent intent = new Intent(MyTrainerActivity.this, TraineeHomeActivity.class);
                    startActivity(intent);


                }else {

                    String error = databaseError.getMessage();
                    Toast.makeText(MyTrainerActivity.this, error, Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    private void init(String name, String surname, String availability, String speciality){

        nameTv.setText(name);
        lastnameTv.setText(surname);
        availabilityTv.setText(availability);
        specialityTv.setText(speciality);

    }

    //------------ Query ----------//

    private void setUserInfo(final String id){

        rootRef = FirebaseDatabase.getInstance().getReference();
        uidRef = rootRef.child("trainer").child(id);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                name = dataSnapshot.child("firstname").getValue(String.class);
                surname = dataSnapshot.child("lastname").getValue(String.class);
                availability = dataSnapshot.child("availibility").getValue(String.class);
                speciality = dataSnapshot.child("specialitiesString").getValue(String.class);

                init(name, surname, availability, speciality);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);
    }


    //------------ Firebase ----------//

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }
}
