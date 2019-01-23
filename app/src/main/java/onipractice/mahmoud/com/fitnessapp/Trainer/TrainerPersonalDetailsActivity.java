package onipractice.mahmoud.com.fitnessapp.Trainer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codetroopers.betterpickers.recurrencepicker.EventRecurrence;
import com.codetroopers.betterpickers.recurrencepicker.EventRecurrenceFormatter;
import com.codetroopers.betterpickers.recurrencepicker.RecurrencePickerDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import onipractice.mahmoud.com.fitnessapp.Models.TrainerPersonalDetails;
import onipractice.mahmoud.com.fitnessapp.R;

public class TrainerPersonalDetailsActivity extends AppCompatActivity implements View.OnClickListener, RecurrencePickerDialogFragment.OnRecurrenceSetListener{

    private static final String TAG = "TrainerPersonalDetails";

    private static final String FRAG_TAG_RECUR_PICKER = "recurrencePickerDialogFragment";


    // Widgets
    Button maleBtn, femaleBtn, nextBtn;
    TextView confirm, cancel, educationEt;

    // Variables
    String gender, mRrule, userId, education, specialitiesString, availibility, user_id, name, surname;
    boolean[] checkedItems = new boolean[10];
    ArrayList<String> selectedSpecialities = new ArrayList<>();

    private EventRecurrence mEventRecurrence = new EventRecurrence();

    //firebase
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    DatabaseReference rootRef;
    DatabaseReference uidRef;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_personal_details);

        context = TrainerPersonalDetailsActivity.this;

        // Get Intent
        userId = getIntent().getStringExtra("user_id");

        retreiveTrainerDetails(userId);
        initialize();
        setUpWidgets();
        setUpFirebaseAuth();

    }

    private void initialize(){

        nextBtn = (Button) findViewById(R.id.nextBtn);
        maleBtn = (Button) findViewById(R.id.maleBtn);
        femaleBtn = (Button) findViewById(R.id.femaleBtn);

    }

    private void setUpWidgets(){

        maleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                maleBtn.setBackgroundColor(getResources().getColor(R.color.outline));
                femaleBtn.setBackgroundColor(getResources().getColor(R.color.transparent));

                gender = "male";
            }
        });

        femaleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                maleBtn.setBackgroundColor(getResources().getColor(R.color.transparent));
                femaleBtn.setBackgroundColor(getResources().getColor(R.color.outline));

                gender = "female";
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addPersonalDetails(name, surname, education, specialitiesString, availibility, gender);

                Intent intent = new Intent(context, PersonalTrainerHomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void retreiveTrainerDetails(String user_id){

        rootRef = FirebaseDatabase.getInstance().getReference();
        uidRef = rootRef.child("user_account_settings").child(user_id);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                name = dataSnapshot.child("firstname").getValue(String.class);
                surname = dataSnapshot.child("lastname").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);
    }



    //---------------- Add Personal Trainer Details to Firebase ----------------//


    private void addPersonalDetails(String firstname, String lastname, String education, String specialitiesString, String availibility, String gender){

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("trainer");

        if(auth.getCurrentUser() != null){
            userId = auth.getCurrentUser().getUid();
        }

        TrainerPersonalDetails trainerPersonalDetails = new TrainerPersonalDetails(firstname, lastname, education, specialitiesString, availibility, gender);
        uidRef.child(userId).setValue(trainerPersonalDetails);
    }

    //---------------- Speciality Dialog ----------------//

    private void specialityDialog(){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(TrainerPersonalDetailsActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_insert_speciality, null);

        confirm = (TextView) mView.findViewById(R.id.dialogConfirm);
        cancel = (TextView) mView.findViewById(R.id.dialogCancel);

        final String[] specialities = {"Conditioning", "Functional Training", "Core Strength", "Proprioception/Balance Training",
                "Olympic Lifting", "Power Lifting", "Sport-specific Training", "Strength Training", "Rehabilitation",
                "High Intensity Training"};
        mBuilder.setMultiChoiceItems(specialities, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                if (isChecked) {
                    selectedSpecialities.add(specialities[which]);
                }
            }
        });

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringBuilder sb = new StringBuilder();

                for(String s: selectedSpecialities){
                     sb.append(s);
                     sb.append(" ");
                }

                specialitiesString = String.valueOf(sb);
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    //---------------- Education Dialog ----------------//

    private void educationDialog(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(TrainerPersonalDetailsActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_insert_education, null);

        educationEt = (EditText) mView.findViewById(R.id.educationEt);
        confirm = (TextView) mView.findViewById(R.id.dialogConfirm);
        cancel = (TextView) mView.findViewById(R.id.dialogCancel);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                education = educationEt.getText().toString();
                Log.d("AVALA", education);
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    //---------------- Availability Dialog ----------------//

    private void availabilityDialog(){
        FragmentManager fm = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        Time time = new Time();
        time.setToNow();
        bundle.putLong(RecurrencePickerDialogFragment.BUNDLE_START_TIME_MILLIS, time.toMillis(false));
        bundle.putString(RecurrencePickerDialogFragment.BUNDLE_TIME_ZONE, time.timezone);
        bundle.putString(RecurrencePickerDialogFragment.BUNDLE_RRULE, mRrule);
        bundle.putBoolean(RecurrencePickerDialogFragment.BUNDLE_HIDE_SWITCH_BUTTON, true);

        RecurrencePickerDialogFragment rpd = new RecurrencePickerDialogFragment();
        rpd.setArguments(bundle);
        rpd.setOnRecurrenceSetListener(TrainerPersonalDetailsActivity.this);
        rpd.show(fm, FRAG_TAG_RECUR_PICKER);
    }

    //---------------- Implemented Methods ----------------//

    @Override
    public void onRecurrenceSet(String rrule) {

        mRrule = rrule;

        if (mRrule != null) {

            mEventRecurrence.parse(mRrule);

        }

        populateRepeats();

    }

    @Override
    public void onResume() {

        super.onResume();

        RecurrencePickerDialogFragment rpd = (RecurrencePickerDialogFragment) getSupportFragmentManager().findFragmentByTag(

                FRAG_TAG_RECUR_PICKER);

        if (rpd != null) {

            rpd.setOnRecurrenceSetListener(this);

        }

    }

    private void populateRepeats() {

        Resources r = getResources();

        String repeatString = "";

        boolean enabled;

        if (!TextUtils.isEmpty(mRrule)) {

            repeatString = EventRecurrenceFormatter.getRepeatString(this, r, mEventRecurrence, true);

        }


        availibility = repeatString;
        Log.d("AVALA", availibility);

    }

    // On Click Listener

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.insertAvailibility:
                availabilityDialog();
                break;
            case R.id.insertEducation:
                educationDialog();
                break;
            case R.id.insertSpeciality:
                specialityDialog();
                break;

        }

    }


    //---------------- Firebase ----------------//

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
                    userId = user.getUid();

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
