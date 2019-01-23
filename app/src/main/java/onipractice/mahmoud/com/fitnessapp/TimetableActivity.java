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

    private static final String TAG = "TimetableActivity";

    //Firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    private FirebaseAuth auth;
    FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener authStateListener;
    FirebaseMethods firebaseMethods;
    ImageView delete;

    //Variables
    char icon;
    int itemCounter;

    private EventRecurrence mEventRecurrence = new EventRecurrence();
    private static final String FRAG_TAG_RECUR_PICKER = "recurrencePickerDialogFragment";

    //Widgets
    TextView tvModuleDay, tvModuleWorkout, tvModuleIcon, tvModuleTime, timeTv, dayTv;
    EditText workoutEt;
    ListView list;
    String value, mRrule, day, time, workout;
    UsersAdapter adapter;
    ArrayList<WorkoutsObject> arrayList;
    WorkoutsObject object;
    FloatingActionButton floatingActionButton;
    TextView confirm, cancel;
    ImageView backArrow;

    //Shared Preference
    SharedPreferences.Editor editor;
    SharedPreferences cacheData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        backArrow = (ImageView) findViewById(R.id.backArrow);
        firebaseMethods = new FirebaseMethods(TimetableActivity.this);
        setUpFirebaseAuth();
        floatingActionButton = findViewById(R.id.fab);
        itemCounter = 0;

        list = (ListView) findViewById(R.id.list);
        tvModuleIcon = (TextView) findViewById(R.id.row_icon);

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();

        cacheData = getSharedPreferences("Workouts", TimetableActivity.MODE_PRIVATE);
        editor = cacheData.edit();

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

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

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

            tvModuleDay = convertView.findViewById(R.id.training_day_Tv);
            tvModuleWorkout = convertView.findViewById(R.id.training_workout_Tv);
            tvModuleTime = convertView.findViewById(R.id.training_time_Tv);
            tvModuleIcon = convertView.findViewById(R.id.row_icon);
            delete = convertView.findViewById(R.id.delete);

            tvModuleDay.setText(user.day);
            tvModuleTime.setText(user.time);
            tvModuleWorkout.setText(user.workout);
            tvModuleIcon.setText(String.valueOf(icon).toUpperCase());

            auth = FirebaseAuth.getInstance();
            mUser = auth.getCurrentUser();

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    arrayList.remove(user);
                    adapter.notifyDataSetChanged();
                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference uidRef = rootRef.child("workout_timetable").child(mUser.getUid());
                }
            });

            return convertView;

        }

    }

    private void addDayDialog(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(TimetableActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_add_training_day, null);

        confirm = (TextView) mView.findViewById(R.id.dialogConfirm);
        cancel = (TextView) mView.findViewById(R.id.dialogCancel);
        dayTv = (TextView) mView.findViewById(R.id.dayTv);
        timeTv = (TextView) mView.findViewById(R.id.timeTv);
        workoutEt = (EditText) mView.findViewById(R.id.workoutEt);

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

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workout = workoutEt.getText().toString();
                auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();
                firebaseMethods.addWorkoutDay(user.getUid(), day, time, workout);
                createObject();
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



























