package onipractice.mahmoud.com.fitnessapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.codetroopers.betterpickers.recurrencepicker.EventRecurrence;
import com.codetroopers.betterpickers.recurrencepicker.EventRecurrenceFormatter;
import com.codetroopers.betterpickers.recurrencepicker.RecurrencePickerDialogFragment;
import com.codetroopers.betterpickers.timepicker.TimePickerBuilder;
import com.codetroopers.betterpickers.timepicker.TimePickerDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import onipractice.mahmoud.com.fitnessapp.Models.WorkoutsObject;
import onipractice.mahmoud.com.fitnessapp.Utils.FirebaseMethods;

public class TimetableActivity extends AppCompatActivity implements RecurrencePickerDialogFragment.OnRecurrenceSetListener,
        TimePickerDialogFragment.TimePickerDialogHandler{

    private EventRecurrence mEventRecurrence = new EventRecurrence();

    private static final String FRAG_TAG_RECUR_PICKER = "recurrencePickerDialogFragment";
    private static final String TAG = "TimetableActivity";

    //Firebase
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private FirebaseAuth authentication;
    private FirebaseUser currentUser;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseMethods firebaseMethods;


    //Variables
    private char icon;
    private String value, mRrule, day, time, workout;

    //Widgets
    private TextView  rowIconTextView;
    private EditText workoutTypeEditText;
    private ListView list;
    private UsersAdapter adapter;
    private ArrayList<WorkoutsObject> arrayList;
    private WorkoutsObject object;
    private FloatingActionButton floatingActionButton;
    private ImageView backArrow, deleteTrainingDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        backArrow = (ImageView) findViewById(R.id.backArrow);
        firebaseMethods = new FirebaseMethods(TimetableActivity.this);
        setUpFirebaseAuth();
        floatingActionButton = findViewById(R.id.fab);

        list = (ListView) findViewById(R.id.list);
        rowIconTextView = (TextView) findViewById(R.id.row_icon);

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();

        //icon = cacheData.getString("workoutDay", "").charAt(0);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDayDialog();
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimetableActivity.this, TraineeHomeActivity.class);
                startActivity(intent);
            }
        });

        createObject();

    }


    private void createObject()
    {
        arrayList = new ArrayList<WorkoutsObject>();
        adapter = new UsersAdapter(TimetableActivity.this, arrayList);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        authentication = FirebaseAuth.getInstance();
        FirebaseUser user = authentication.getCurrentUser();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("workout_timetable").child(user.getUid());

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds: dataSnapshot.getChildren()) {

                    object = new WorkoutsObject();
                    object.day = ds.child("day").getValue(String.class);
                    object.workout = ds.child("workout").getValue(String.class).substring(0, 1).toUpperCase() +
                                                                                ds.child("workout").getValue(String.class).substring(1);
                    object.time = ds.child("time").getValue(String.class);
                    arrayList.add(object);
                    adapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);

    }

    public class UsersAdapter extends ArrayAdapter<WorkoutsObject> {

        public UsersAdapter(Context context, ArrayList<WorkoutsObject> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final WorkoutsObject user = getItem(position);

            if (convertView == null) {

                convertView = LayoutInflater.from(getContext()).inflate(R.layout.training_day_item, parent, false);

            }

            TextView trainingDayTextView = convertView.findViewById(R.id.training_day_Tv);
            TextView trainingWorkoutTextView = convertView.findViewById(R.id.training_workout_Tv);
            TextView trainingTimeTextView = convertView.findViewById(R.id.training_time_Tv);
            rowIconTextView = convertView.findViewById(R.id.row_icon);
            ImageView deleteTrainingDay = convertView.findViewById(R.id.delete);

            trainingDayTextView.setText(user.day);
            trainingTimeTextView.setText(user.time);
            trainingWorkoutTextView.setText(user.workout);
            rowIconTextView.setText(String.valueOf(icon).toUpperCase());

            authentication = FirebaseAuth.getInstance();
            currentUser = authentication.getCurrentUser();

            deleteTrainingDay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    arrayList.remove(user);
                    adapter.notifyDataSetChanged();
                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference uidRef = rootRef.child("workout_timetable").child(currentUser.getUid());
                }
            });

            return convertView;

        }

    }

    private void addDayDialog(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(TimetableActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_add_training_day, null);

        TextView confirmDialogTV = (TextView) mView.findViewById(R.id.dialogConfirm);
        TextView cancelDialogTV = (TextView) mView.findViewById(R.id.dialogCancel);
        TextView dayTv = (TextView) mView.findViewById(R.id.dayTv);
        TextView timeTv = (TextView) mView.findViewById(R.id.timeTv);
        workoutTypeEditText = (EditText) mView.findViewById(R.id.workoutEt);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setAttributes(lp);
        dialog.show();

        dayTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dayDialog();
            }
        });

        timeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeDialog();
            }
        });

        confirmDialogTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workout = workoutTypeEditText.getText().toString();
                authentication = FirebaseAuth.getInstance();
                FirebaseUser user = authentication.getCurrentUser();
                firebaseMethods.addWorkoutDay(user.getUid(), day, time, workout);
                createObject();
                dialog.dismiss();
            }
        });

        cancelDialogTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void dayDialog(){
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
        rpd.setOnRecurrenceSetListener(TimetableActivity.this);
        rpd.show(fm, FRAG_TAG_RECUR_PICKER);
    }

    private void timeDialog(){

        TimePickerBuilder tpb = new TimePickerBuilder()
                .setFragmentManager(getSupportFragmentManager())
                .setStyleResId(R.style.BetterPickersDialogFragment_Light);
        tpb.show();
    }

    @Override
    public void onRecurrenceSet(String rrule) {

        mRrule = rrule;

        if (mRrule != null) {

            mEventRecurrence.parse(mRrule);

        }

        populateRepeats();
    }

    private void populateRepeats() {

        Resources r = getResources();

        String repeatString = "";

        boolean enabled;

        if (!TextUtils.isEmpty(mRrule)) {

            repeatString = EventRecurrenceFormatter.getRepeatString(this, r, mEventRecurrence, true);

        }


        day = repeatString;

    }


    @Override
    public void onDialogTimeSet(int reference, int hourOfDay, int minute) {

        time = String.valueOf(hourOfDay) + ":" + String.valueOf(minute);
    }

            /*
    ------------------------------------------Firebase----------------------------------------------------
     */

    private void setUpFirebaseAuth()
    {
        authentication = FirebaseAuth.getInstance();

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



























