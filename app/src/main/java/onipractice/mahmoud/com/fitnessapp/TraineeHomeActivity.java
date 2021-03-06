package onipractice.mahmoud.com.fitnessapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.codetroopers.betterpickers.recurrencepicker.EventRecurrence;
import com.codetroopers.betterpickers.recurrencepicker.EventRecurrenceFormatter;
import com.codetroopers.betterpickers.recurrencepicker.RecurrencePickerDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import de.hdodenhof.circleimageview.CircleImageView;
import onipractice.mahmoud.com.fitnessapp.Jogging.JoggingActivity;
import onipractice.mahmoud.com.fitnessapp.Messaging.MessengerActivity;
import onipractice.mahmoud.com.fitnessapp.Trainer.AddTrainerActivity;
import onipractice.mahmoud.com.fitnessapp.Trainer.MyTrainerActivity;
import onipractice.mahmoud.com.fitnessapp.Training.StretchingActivity;

public class TraineeHomeActivity extends AppCompatActivity implements RecurrencePickerDialogFragment.OnRecurrenceSetListener {

    private static final String FRAG_TAG_RECUR_PICKER = "recurrencePickerDialogFragment";

    // Widgets
    private CircleImageView profileImg;
    private CardView progressCardView, trainerCardView, myDietCardView, timetableCardView, stretchesCardView, joggingCardView;
    private ImageView messaging;

    // Variables
    private String userID, userHasTrainer, user_id, mRrule;
    Context context;

    // Firebase
    private DatabaseReference userDatabase;
    private FirebaseAuth authentication;
    private DatabaseReference rootReference;
    private DatabaseReference uidReference;

    private EventRecurrence mEventRecurrence = new EventRecurrence();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainee_home);

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        float density  = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth  = outMetrics.widthPixels / density;

        Toast.makeText(this, String.valueOf(dpHeight) + "+" + String.valueOf(dpWidth), Toast.LENGTH_LONG).show();

        authentication = FirebaseAuth.getInstance();

        context = TraineeHomeActivity.this;

        authentication = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference().child("user_account_settings").child(authentication.getCurrentUser().getUid());
        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userDatabase.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
                userDatabase.child("online").setValue("true");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        userID = authentication.getCurrentUser().getUid();
        setWidgets();
        init();
    }

    private void setWidgets() {
        profileImg = (CircleImageView) findViewById(R.id.profileImg);
//        progressCardView = (CardView) findViewById(R.id.progressCardView);
        trainerCardView = (CardView) findViewById(R.id.trainerCard);
        timetableCardView = (CardView) findViewById(R.id.timetableCard);
        myDietCardView = (CardView) findViewById(R.id.dietCardView);
        stretchesCardView = (CardView) findViewById(R.id.stretchesCard);
        joggingCardView = (CardView) findViewById(R.id.joggingCard);
        messaging = (ImageView) findViewById(R.id.messageIv);
    }

    private void init() {

        rootReference = FirebaseDatabase.getInstance().getReference();
        uidReference = rootReference.child("Friends").child(userID);

        uidReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("trainerId")) {
                    user_id = dataSnapshot.child("trainerId").getValue().toString();
                    userHasTrainer = "true";
                } else {
                    Toast.makeText(TraineeHomeActivity.this, "You do not have a trainer yet", Toast.LENGTH_SHORT).show();
                    userHasTrainer = "false";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        messaging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userHasTrainer.equals("false")) {
                    Intent intent = new Intent(TraineeHomeActivity.this, AddTrainerActivity.class);
                    intent.putExtra("user", "trainee");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(context, MessengerActivity.class);
                    intent.putExtra("user_id", user_id);
                    intent.putExtra("user", "trainee");
                    startActivity(intent);
                }
            }
        });

        trainerCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userHasTrainer.equals("false")) {
                    Intent intent = new Intent(TraineeHomeActivity.this, AddTrainerActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(context, MyTrainerActivity.class);
                    intent.putExtra("user_id", user_id);
                    intent.putExtra("reference", "trainer");
                    startActivity(intent);
                }
            }
        });

        timetableCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TimetableActivity.class);
                startActivity(intent);
            }
        });

        myDietCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TraineeHomeActivity.this, DietActivity.class);
                startActivity(intent);
            }
        });

        stretchesCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TraineeHomeActivity.this, StretchingActivity.class);
                startActivity(intent);
            }
        });

        joggingCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, JoggingActivity.class);
                startActivity(intent);
            }
        });
    }

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
        // Example of reattaching to the fragment
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

        if (!TextUtils.isEmpty(mRrule)) {
            repeatString = EventRecurrenceFormatter.getRepeatString(this, r, mEventRecurrence, true);
        }
        Toast.makeText(context, repeatString, Toast.LENGTH_SHORT).show();
    }
}
